package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.EnumeratedFact;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.domain.IntSortedEnumeratedFact;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.GlobalApplicationContext;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

public class ReferenceDataUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDataUtil.class);
    public static Comparator<EnumeratedFact> enumeratedFactComparator = (o1, o2) -> StringUtils.compare(o1.toString(), o2.toString());
    public static Comparator<Object> objectComparator = (o1, o2) -> StringUtils.compare(o1.toString(), o2.toString());
    private static Comparator<IntSortedEnumeratedFact> intSortedEnumeratedFactComparator = (o1, o2) -> o1.getSortInt() - o2.getSortInt();

    private static MongoRepositoryReactiveImpl mongoRepositoryReactive = GlobalApplicationContext.ctx.getBean(MongoRepositoryReactiveImpl.class);

    public static Mono<ResponseEntity> getAllEnumeratedEntity(String entityMapName, Class clazz) {
        try {
            Map<String, FactObject> entityMap = Mapstore.STORE.get(entityMapName);
            Collection<FactObject> factObjects;
            if (entityMap == null) {
                factObjects = (Collection<FactObject>) mongoRepositoryReactive.findAll(new Query(), clazz).toStream().collect(Collectors.toList());
            } else {
                factObjects = entityMap.values();
            }
            if (factObjects.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            Collection<EnumeratedFact> enumeratedFacts = toSortedEnumeratedFacts(factObjects);
            List<EnumeratedFactDto> enumeratedFactDtoList = new ArrayList<>();
            for (EnumeratedFact enumeratedFact : enumeratedFacts) {
                enumeratedFactDtoList.add(enumeratedFact.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(enumeratedFactDtoList, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while getting all values of %s from map store", entityMapName), e);
        }
    }

    public static Mono<ResponseEntity> getAllIntSortedEnumeratedEntity(String entityMapName, Class clazz) {
        try {
            Map<String, FactObject> entityMap = Mapstore.STORE.get(entityMapName);
            Collection<FactObject> factObjects;
            if (entityMap == null) {
                factObjects = (Collection<FactObject>) mongoRepositoryReactive.findAll(new Query(), clazz).toStream().collect(Collectors.toList());
            } else {
                factObjects = entityMap.values();
            }

            if (factObjects.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            Collection<IntSortedEnumeratedFact> intSortedEnumeratedFacts = toIntSortedEnumeratedFacts(factObjects);
            List<EnumeratedFactDto> enumeratedFactDtoList = new ArrayList<>();
            for (EnumeratedFact enumeratedFact : intSortedEnumeratedFacts) {
                enumeratedFactDtoList.add(enumeratedFact.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(enumeratedFactDtoList, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while getting all values of %s from map store", entityMapName), e);
        }
    }

    public static List<EnumeratedFact> getAllEnumeratedFacts(String entityMapName) {
        try {
            Map<String, FactObject> entityMap = Mapstore.STORE.get(entityMapName);
            if (entityMap == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(toSortedEnumeratedFacts(entityMap.values()));
        } catch (Exception e) {
            logger.error(String.format("An error occurred while getting all values of %s from map store", entityMapName));
            return new ArrayList<>();
        }
    }

    private static Collection<EnumeratedFact> toSortedEnumeratedFacts(Collection<FactObject> factObjects) {
        List<EnumeratedFact> enumeratedFacts = new ArrayList<>();
        for (FactObject factObject : factObjects) {
            EnumeratedFact enumeratedFact = (EnumeratedFact) factObject;
            enumeratedFacts.add(enumeratedFact);
        }
        enumeratedFacts.sort(enumeratedFactComparator);
        return enumeratedFacts;
    }

    private static Collection<IntSortedEnumeratedFact> toIntSortedEnumeratedFacts(Collection<FactObject> factObjects) {
        List<IntSortedEnumeratedFact> enumeratedFacts = new ArrayList<>();
        for (FactObject factObject : factObjects) {
            IntSortedEnumeratedFact enumeratedFact = (IntSortedEnumeratedFact) factObject;
            enumeratedFacts.add(enumeratedFact);
        }
        enumeratedFacts.sort(intSortedEnumeratedFactComparator);
        return enumeratedFacts;
    }
}
