package com.software.finatech.lslb.cms.service.persistence;

import com.software.finatech.lslb.cms.service.domain.FactObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.TimeZone;

@Repository("mongoRepositoryReactive")
public class MongoRepositoryReactiveImpl implements MongoRepositoryReactive<FactObject> {
	private static final Logger log           = LoggerFactory.getLogger(MongoRepositoryReactiveImpl.class);


	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public ReactiveMongoTemplate getReactiveMongoTemplate() {
		return reactiveMongoTemplate;
	}

	public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
		this.reactiveMongoTemplate = reactiveMongoTemplate;
	}


	@Override
	public Mono<FactObject> saveOrUpdate(FactObject fact) {

			//Document dbObject = new Document();
			//.getConverter().write(fact, dbObject);
		//reactiveMongoTemplate.upsert(new Query(Criteria.where("_id").is(fact.getId())), Update.fromDocument(dbObject, "_id"), PERSON_COLLECTION);

		//set timezone because of mongodb date
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		reactiveMongoTemplate.save(fact).block();//.subscribe();
		return null;
	}

	@Override
	public void delete(FactObject fact) {
		reactiveMongoTemplate.remove(fact).block();//.subscribe();
	}

	@Override
	public Mono<FactObject> save(FactObject fact) {
		try {
			 reactiveMongoTemplate.insert(fact);
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean saveAll(ArrayList<FactObject> facts) {
		try {
			 //Flux<FactObject> saves =
			//reactiveMongoTemplate.insertAll(facts);//.subscribe();
					//.then() //
					 //.block();;

			reactiveMongoTemplate.insertAll(facts).then().block();
					//.then() //
					//.block();
			//reactiveMongoTemplate.insertAll(Flux.just(facts));

			return true;
		}catch(Throwable e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Mono<FactObject> findById(String id, Class<?> fact) {
		try {
			//FactObject factObject = (FactObject) reactiveMongoTemplate.findById(id, fact);
			return (Mono<FactObject>)reactiveMongoTemplate.findById(id, fact);
		}catch(Throwable e){
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Mono<FactObject> find(Query query, Class<?> fact) {
		try {
			//FactObject factObject = (FactObject) mongoTemplate.findOne(query, fact);
			return (Mono<FactObject>) reactiveMongoTemplate.findOne(query, fact);
		}catch(Throwable e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Flux<FactObject> findAll(Class fact) {
		//ArrayList<FactObject> factObjects = (ArrayList<FactObject>) mongoTemplate.findAll(fact);
		return reactiveMongoTemplate.findAll(fact);
	}

	public Flux<? extends FactObject> findAll(Query query, Class fact) {
		return reactiveMongoTemplate.find(query, fact);
	}

	@Override
	public Mono<Long> count(Query query, Class<?> fact) {
		return reactiveMongoTemplate.count(query, fact);
	}
	
}
