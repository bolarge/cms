package com.software.finatech.lslb.cms.service.util;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ErrorResponseUtil {

    public static Mono<ResponseEntity> logAndReturnError(Logger logger, String errorMsg, Throwable e) {
        logger.error(errorMsg, e);
        return Mono.just(new ResponseEntity<>(String.format("%s", errorMsg), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
