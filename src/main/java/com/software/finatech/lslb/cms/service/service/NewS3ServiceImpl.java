package com.software.finatech.lslb.cms.service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.exception.FileUploadException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.S3Service;
import com.software.finatech.lslb.cms.service.util.EnvironmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.utils.IoUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.software.finatech.lslb.cms.service.util.NumberUtil.getRandomNumberInRange;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */

@Component
public class NewS3ServiceImpl implements S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    private static final Logger logger = LoggerFactory.getLogger(NewS3ServiceImpl.class);

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;
    @Autowired
    private EnvironmentUtils environmentUtils;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;


    @Override
    public void uploadMultipartForDocument(MultipartFile multipartFile, Document document) throws FileUploadException {
        try {
            String key = getDecoratedKeyWithFolderName(String.format("%s-%s.%s", removeExtension(multipartFile.getOriginalFilename()),
                    getRandomNumberInRange(0, 1000),
                    getExtension(multipartFile.getOriginalFilename())));
            File file = multipartFileToFile(multipartFile);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);

            PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
            if (putObjectResult == null) {
                throw new FileUploadException("No response received");
            }
            document.setAwsObjectKey(key);
            mongoRepositoryReactive.saveOrUpdate(document);
            file.delete();
            logger.info("{}", putObjectResult);
        } catch (Exception e) {
            throw new FileUploadException("Unable to upload File", e);
        }
    }

    @Override
    public void downloadFileToHttpResponseSync(String objectKey, String fileName, HttpServletResponse res) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);
        //   res.getOutputStream().write(IOUtils.toByteArray(s3Object.getObjectContent()));
        res.setHeader("filename", fileName);
        res.setContentType(s3Object.getObjectMetadata().getContentType());
        res.setContentLengthLong(s3Object.getObjectMetadata().getContentLength());
        IoUtils.copy(s3Object.getObjectContent(), res.getOutputStream());
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


    @Override
    public void downloadFileToHttpResponse(String objectKey, String fileName, HttpServletResponse httpServletResponse) {

    }

    private File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        if (StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            throw new FileNotFoundException("Invalid file name");
        }
        File convFile = new File(multipartFile.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
