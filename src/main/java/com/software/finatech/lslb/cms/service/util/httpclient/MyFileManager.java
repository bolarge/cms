package com.software.finatech.lslb.cms.service.util.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class MyFileManager {
    private static final Logger logger = LoggerFactory.getLogger(MyFileManager.class);

    /**
     * u see the file name , you should change it to the name of the file
     * that you want to save most likely the name of the biller
     * (e.g)let them pass  the name of the biller into the function from the ui
     */
    public void writeImageToFile(MultipartFile multipartFile) {
        /**
         File file = new File("smile-networks.jpg");
         try {
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         fileOutputStream.write(multipartFile.getBytes());
         fileOutputStream.close();

         } catch (IOException e) {
         logger.error("IO error occurred while  saving file", e);
         } catch (Exception e) {
         logger.error("An error occurred while saving file", e);
         }
         */
    }

    public String readImage(String billerId) {
        /**
         Biller biller = billerRepository.findById(billerId);
         if (biller == null){
         return null;
         }
         File file = new File(biller.getFileName());
         try {
         if (file.exists() && file.isFile()) {
         try {
         byte[] fileContent = FileUtils.readFileToByteArray(new File(fileName));
         return Base64.getEncoder().encodeToString(fileContent);
         } catch (IOException e) {
         logger.error("An io error occurred while reading file", e);
         return null;
         }
         }
         return null;
         } catch (Exception e) {
         logger.error("An error occurred while reading file", e);
         return null;
         }
         */return null;
    }

}
