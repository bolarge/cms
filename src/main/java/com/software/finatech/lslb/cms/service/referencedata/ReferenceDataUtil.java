package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.EnumeratedFact;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

public class ReferenceDataUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDataUtil.class);

    public static Mono<ResponseEntity> getAllEnumeratedEntity(String entityMapName) {
        try {
            Map<String, FactObject> entityMap = Mapstore.STORE.get(entityMapName);
            if (entityMap == null) {
                return Mono.just(new ResponseEntity<>("Entity Map does not exist", HttpStatus.BAD_REQUEST));
            }
            Collection<FactObject> factObjects = entityMap.values();
            if (factObjects.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            List<EnumeratedFactDto> enumeratedFactDtoList = new ArrayList<>();
            for (FactObject factObject : factObjects) {
                EnumeratedFact enumeratedFact = (EnumeratedFact) factObject;
                enumeratedFactDtoList.add(enumeratedFact.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(enumeratedFactDtoList, HttpStatus.OK));
        } catch (Exception e) {
          return   logAndReturnError(logger,String.format("An error occurred while getting all values of %s from map store", entityMapName), e);
        }
    }
}
