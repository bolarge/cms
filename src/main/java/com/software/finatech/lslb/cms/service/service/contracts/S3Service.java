package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Document;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author adeyi.adebolu
 * created on 30/04/2019
 */
public interface S3Service {
    void uploadMultipartForDocument(MultipartFile multipartFile, Document document) throws Exception;

    void downloadFileToHttpResponse(String objectKey, String fileName, HttpServletResponse httpServletResponse);

    void downloadFileToHttpResponseSync(String objectKey, String fileName, HttpServletResponse res) throws IOException;

}
