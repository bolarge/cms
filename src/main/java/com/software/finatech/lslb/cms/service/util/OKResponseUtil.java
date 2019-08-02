package com.software.finatech.lslb.cms.service.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * @author adeyi.adebolu
 * created on 19/06/2019
 */
public class OKResponseUtil {

    public static Mono<ResponseEntity> OKResponse(Object object) {
        return Mono.just(new ResponseEntity<>(object, HttpStatus.OK));
    }
}
