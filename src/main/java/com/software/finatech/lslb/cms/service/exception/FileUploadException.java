package com.software.finatech.lslb.cms.service.exception;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */
public class FileUploadException extends Exception {

    public FileUploadException() {
        super();
    }

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
