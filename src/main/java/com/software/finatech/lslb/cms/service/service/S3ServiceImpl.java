package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.S3Service;
import com.software.finatech.lslb.cms.service.util.EnvironmentUtils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.async.AsyncRequestProvider;
import software.amazon.awssdk.async.AsyncResponseHandler;
import software.amazon.awssdk.http.async.SimpleSubscriber;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static com.software.finatech.lslb.cms.service.util.NumberUtil.getRandomNumberInRange;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * @author adeyi.adebolu
 * created on 30/04/2019
 */
@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    private S3AsyncClient client;
    private EnvironmentUtils environmentUtils;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public S3ServiceImpl(S3AsyncClient client,
                         EnvironmentUtils environmentUtils,
                         MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.client = client;
        this.environmentUtils = environmentUtils;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public void uploadMultipartForDocument(MultipartFile multipartFile, Document document) throws Exception {

        String key = getDecoratedKeyWithFolderName(String.format("%s-%s.%s", removeExtension(multipartFile.getOriginalFilename()),
                getRandomNumberInRange(0, 1000),
                getExtension(multipartFile.getOriginalFilename())));
        try {

            PutObjectResponse response = putObjectInBucket(key, new AsyncRequestProvider() {

                @Override
                public long contentLength() {
                    return multipartFile.getSize();
                }

                @Override
                public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
                    try {
                        subscriber.onSubscribe(new UploadSubscription(multipartFile.getInputStream(), subscriber));
                    } catch (IOException e) {
                        logger.error("error occured in subscribe exception", e);
                        throw new UncheckedIOException(e);
                    }

                }
            }).get();

            if (response != null) {
                logger.info(" Response ==> {}", response);
                document.setAwsObjectKey(key);
                mongoRepositoryReactive.saveOrUpdate(document);

            } else {
                logger.error("Invalid response");

            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error occurred while working with completable future", e);
        }
    }


    @Override
    public void downloadFileToHttpResponse(String objectKey, String fileName, HttpServletResponse httpServletResponse) {
        try {
            getObjectFromBucket(objectKey, new AsyncResponseHandler<GetObjectResponse, Object>() {
                @Override
                public void responseReceived(GetObjectResponse getObjectResponse) {
                    httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                    httpServletResponse.setContentType(getObjectResponse.contentType());
                    httpServletResponse.setContentLength(Math.toIntExact(getObjectResponse.contentLength()));
                }

                @Override
                public void onStream(Publisher<ByteBuffer> publisher) {
                    try {
                        publisher.subscribe(createSubscriber(httpServletResponse.getOutputStream()));
                    } catch (IOException e) {
                        logger.error("IO error occurred on stream", e);
                    }
                }

                @Override
                public void exceptionOccurred(Throwable throwable) {
                    logger.error("Error occurred ", throwable);
                    try {
                        httpServletResponse.getOutputStream().close();
                    } catch (IOException e) {
                        logger.error("IO error while closing output stream", e);
                    }
                }

                @Override
                public Object complete() {
                    try {
                        httpServletResponse.getOutputStream().close();
                    } catch (IOException e) {
                        logger.error("IO error while closing output stream", e);
                    }
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error occurred while working with completable future", e);
        }
    }

    private CompletableFuture<Object> getObjectFromBucket(String objectKey, AsyncResponseHandler<GetObjectResponse, Object> handler) {
        return client.getObject(GetObjectRequest.builder().bucket(bucketName).key(objectKey).build(), handler);
    }

    private CompletableFuture<PutObjectResponse> putObjectInBucket(String objectKey, AsyncRequestProvider provider) {
        return client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build(), provider);
    }

    private SimpleSubscriber createSubscriber(OutputStream out) {
        WritableByteChannel channel = Channels.newChannel(out);
        return new SimpleSubscriber(byteBuffer -> {
            try {
                channel.write(byteBuffer);
            } catch (IOException e) {
                logger.error("IO error occurred on subscribe", e);
            }
        });
    }

    private String getDecoratedKeyWithFolderName(String key) throws Exception {
        if (environmentUtils.isTestEnvironment()) {
            return String.format("lslb-cms/test/%s", key);
        }
        if (environmentUtils.isStagingEnvironment()) {
            return String.format("lslb-cms/staging/%s", key);
        }
        if (environmentUtils.isProductionEnvironment()) {
            return String.format("lslb-cms/production/%s", key);
        }
        if (environmentUtils.isDevelopmentEnvironment()) {
            return String.format("lslb-cms/development/%s", key);
        }
        throw new Exception("Invalid Environment");
    }

    private class UploadSubscription implements Subscription {

        private ReadableByteChannel inputChannel;
        private Subscriber<? super ByteBuffer> subscriber;
        private AtomicLong outstandingRequests;
        private boolean writeInProgress;

        public UploadSubscription(InputStream inputStream, Subscriber<? super ByteBuffer> subscriber) {

            this.writeInProgress = false;
            inputChannel = Channels.newChannel(inputStream);
            this.subscriber = subscriber;
            this.outstandingRequests = new AtomicLong(0L);

        }

        @Override
        public void request(long n) {
            this.outstandingRequests.addAndGet(n);
            synchronized (this) {
                if (!this.writeInProgress) {
                    this.writeInProgress = true;
                    this.readData();
                }
            }
        }

        private void readData() {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                while (inputChannel.read(buffer) != -1 || buffer.position() > 0) {
                    buffer.flip();
                    subscriber.onNext(buffer);
                    buffer.compact();
                }
            } catch (IOException ex) {
                logger.error("IOException occured in readData()", ex);
                throw new UncheckedIOException(ex);
            }

            UploadSubscription.this.writeInProgress = false;

        }

        private void closeFile() {
            try {
                this.inputChannel.close();
                subscriber.onComplete();
            } catch (IOException ex) {
                logger.error("IOException occured in closeFile()", ex);
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void cancel() {
            this.closeFile();
        }

    }
}
