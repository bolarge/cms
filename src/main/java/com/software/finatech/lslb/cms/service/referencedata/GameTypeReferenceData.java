package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class GameTypeReferenceData {

    public static String OSB_GAME_TYPE_ID= "02";
    public static String POL_GAME_TYPE_ID ="01";
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        GameType gameType1 = (GameType) mongoRepositoryReactive.findById(POL_GAME_TYPE_ID,GameType.class).block();
        if(gameType1==null){
            gameType1 = new GameType();
            gameType1.setId(POL_GAME_TYPE_ID);

        }
        gameType1.setDescription("Public Online Lottery");
        gameType1.setName("Pol");
        gameType1.setAipDuration("3");
        gameType1.setLicenseDuration("12");
        gameType1.setGamingMachineLicenseDuration("12");
        gameType1.setAgentLicenseDuration("12");



        GameType gameType2 = (GameType) mongoRepositoryReactive.findById(OSB_GAME_TYPE_ID,GameType.class).block();
        if(gameType2==null){
            gameType2 = new GameType();
            gameType2.setId(OSB_GAME_TYPE_ID);

        }
        gameType2.setDescription("Online Sport Betting");
        gameType2.setName("Osb");
        gameType2.setAipDuration("3");
        gameType2.setLicenseDuration("12");
        gameType2.setGamingMachineLicenseDuration("12");
        gameType2.setAgentLicenseDuration("12");

        mongoRepositoryReactive.saveOrUpdate(gameType1);
        mongoRepositoryReactive.saveOrUpdate(gameType2);
    }
}
