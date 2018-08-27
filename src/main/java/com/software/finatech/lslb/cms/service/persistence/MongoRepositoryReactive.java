package com.software.finatech.lslb.cms.service.persistence;

import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@SuppressWarnings("hiding")
public interface MongoRepositoryReactive<FactObject> {
	Mono<FactObject> saveOrUpdate(FactObject fact);
    void delete(FactObject fact);
    Mono<FactObject> save(FactObject fact);
    Mono<FactObject> find(Query query, Class<?> fact);
	Flux<FactObject> findAll(Class fact);
	Mono<Long> count(Query query, Class<?> fact) ;
	Flux<? extends FactObject> findAll(Query query, Class fact);
	Boolean saveAll(ArrayList<FactObject> facts);
	Mono<FactObject> findById(String id, Class<?> fact);

}
