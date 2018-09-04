package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class GameTypeReferenceData {

    public static String OSB_GAME_TYPE_ID = "02";
    public static String POL_GAME_TYPE_ID = "01";
    public static String GAMING_MACHINE_OPERATOR_ID = "03";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        GameType gameType1 = (GameType) mongoRepositoryReactive.findById(POL_GAME_TYPE_ID, GameType.class).block();
        if (gameType1 == null) {
            gameType1 = new GameType();
            gameType1.setId(POL_GAME_TYPE_ID);

        }
        gameType1.setDescription("Public Online Lottery");
        gameType1.setName("POL");
        gameType1.setAipDuration("3");
        gameType1.setLicenseDuration("12");
        gameType1.setGamingMachineLicenseDuration("12");
        gameType1.setAgentLicenseDuration("12");


        GameType gameType2 = (GameType) mongoRepositoryReactive.findById(OSB_GAME_TYPE_ID, GameType.class).block();
        if (gameType2 == null) {
            gameType2 = new GameType();
            gameType2.setId(OSB_GAME_TYPE_ID);

        }
        gameType2.setDescription("Online Sport Betting");
        gameType2.setName("OSB");
        gameType2.setAipDuration("3");
        gameType2.setLicenseDuration("12");
        gameType2.setGamingMachineLicenseDuration("12");
        gameType2.setAgentLicenseDuration("12");

        GameType gameType3 = (GameType) mongoRepositoryReactive.findById(GAMING_MACHINE_OPERATOR_ID, GameType.class).block();
        if (gameType3 == null) {
            gameType3 = new GameType();
            gameType3.setId(GAMING_MACHINE_OPERATOR_ID);

        }
        gameType3.setDescription("A gaming machine operator (operator that has gaming machines)");
        gameType3.setName("GAMING_MACHINE");
        gameType3.setAipDuration("3");
        gameType3.setLicenseDuration("12");
        gameType3.setGamingMachineLicenseDuration("12");
        gameType3.setAgentLicenseDuration("12");


        mongoRepositoryReactive.saveOrUpdate(gameType1);
        mongoRepositoryReactive.saveOrUpdate(gameType2);
        mongoRepositoryReactive.saveOrUpdate(gameType3);
    }
}
