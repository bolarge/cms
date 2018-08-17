package com.software.finatech.lslb.cms.userservice.persistence;

import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@SuppressWarnings("hiding")
public interface MongoRepositoryReactive<FactObject> {
	public Mono<FactObject> saveOrUpdate(FactObject fact);
    public void delete(FactObject fact);
    public Mono<FactObject> save(FactObject fact);
    public Mono<FactObject> find(Query query, Class<?> fact);
	public Flux<FactObject> findAll(Class fact);
	public Mono<Long> count(Query query, Class<?> fact) ;
	public Flux<? extends FactObject> findAll(Query query, Class fact);
	public Boolean saveAll(ArrayList<FactObject> facts);
	public Mono<FactObject> findById(String id, Class<?> fact);

}
