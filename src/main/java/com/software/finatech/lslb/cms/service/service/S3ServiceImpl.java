package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    private Environment environment;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public S3ServiceImpl(S3AsyncClient client,
                         Environment environment,
                         MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.client = client;
        this.environment = environment;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public void uploadMultipartForDocument(MultipartFile multipartFile, Document document) throws Exception {
        String key = getDecoratedKeyWithFolderName(String.format("%s-%s.%s", removeExtension(multipartFile.getOriginalFilename()),
                getRandomNumberInRange(0, 1000),
                getExtension(multipartFile.getOriginalFilename())));
        try {
            File temp_file = writeToTempFile(multipartFile);
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(multipartFile.getContentType())
                    .contentLength(temp_file.length()).build();
            multipartFile.transferTo(temp_file);
            PutObjectResponse response = client.putObject(putObjectRequest,
                    AsyncRequestProvider.fromFile(temp_file.toPath())).get();
            if (response != null) {
                logger.info(" Response ==> {}", response);
                document.setAwsObjectKey(key);
                mongoRepositoryReactive.saveOrUpdate(document);
                if (!temp_file.delete()) {
                    logger.error("Could not delete temp file {}", temp_file.getName());
                }
            } else {
                logger.error("Invalid response");
                if (!temp_file.delete()) {
                    logger.error("Could not delete temp file {}", temp_file.getName());
                }
            }
        } catch (IOException e) {
            logger.error("IO Error occurred while trying to upload file", e);
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


    private File writeToTempFile(MultipartFile file) throws Exception {
        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw new Exception("File Name must not be empty");
        }
        Path filepath = Paths.get(file.getOriginalFilename());

        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
            return filepath.toFile();
        }
    }

    private String getDecoratedKeyWithFolderName(String key) throws Exception {
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        if (activeProfiles.contains("test")) {
            return String.format("lslb-cms/test/%s", key);
        }
        if (activeProfiles.contains("staging")) {
            return String.format("lslb-cms/staging/%s", key);
        }
        if (activeProfiles.contains("production")) {
            return String.format("lslb-cms/production/%s", key);
        }
        if (activeProfiles.contains("development")) {
            return String.format("lslb-cms/development/%s", key);
        }
        throw new Exception("Invalid Environment");
    }
}
