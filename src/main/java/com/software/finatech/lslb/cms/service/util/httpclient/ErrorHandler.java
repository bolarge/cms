package com.software.finatech.lslb.cms.service.util.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class ErrorHandler implements ResponseErrorHandler {
  private static  final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    @Override
    public boolean hasError(ClientHttpResponse response) {
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        logger.error("%s\n%s\n%s",response.getStatusText(), response.getStatusCode(),response.getRawStatusCode());
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) {

    }
}
