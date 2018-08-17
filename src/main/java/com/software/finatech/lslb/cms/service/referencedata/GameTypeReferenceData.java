package com.software.finatech.lslb.cms.userservice.referencedata;

import com.software.finatech.lslb.cms.userservice.domain.GameType;
import com.software.finatech.lslb.cms.userservice.persistence.MongoRepositoryReactiveImpl;

public class GameTypeReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        GameType gameType1 = (GameType) mongoRepositoryReactive.findById("01",GameType.class).block();
        if(gameType1==null){
            gameType1 = new GameType();
            gameType1.setId("01");

        }
        gameType1.setDescription("Public Online Lottery");
        gameType1.setName("Pol");



        GameType gameType2 = (GameType) mongoRepositoryReactive.findById("02",GameType.class).block();
        if(gameType2==null){
            gameType2 = new GameType();
            gameType2.setId("02");

        }
        gameType2.setDescription("Online Sport Betting");
        gameType2.setName("Osb");

        mongoRepositoryReactive.saveOrUpdate(gameType1);
        mongoRepositoryReactive.saveOrUpdate(gameType2);
    }
}
