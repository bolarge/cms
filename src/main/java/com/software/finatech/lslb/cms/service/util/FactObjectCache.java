package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class FactObjectCache {

    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;

    /**
     *
     * @param key
     * @param objectId
     * @return
     */
    public Optional<FactObject> get(String key, String objectId) {

        FactObject factObject = Mapstore.STORE.get(key).get(objectId);

        if(factObject == null)
            return Optional.empty();

        factObject = loadFactObject(key, objectId);

        if(factObject == null)
            return  Optional.empty();

        return Optional.of(factObject);
    }

    public void reloadFactObjectForKey(String key) {

        HashSet<FactObject> factObjects =
                (HashSet<FactObject>) mongoRepositoryReactive.findAll(Mapstore.FACT_ENUM.get(key)).toStream().collect(Collectors.toSet());

        ConcurrentHashMap<String, FactObject> facts = new ConcurrentHashMap<>();
        factObjects.forEach(fact -> {
            facts.put(fact.getId(), fact);
        });

        Mapstore.STORE.put(key, facts);

    }



    /**
     *
     * @param key
     * @param objId
     * @return
     */
    private FactObject loadFactObject(String key, String objId) {

        return mongoRepositoryReactive.findById(objId, Mapstore.FACT_ENUM.get(key)).block();

    }

}
