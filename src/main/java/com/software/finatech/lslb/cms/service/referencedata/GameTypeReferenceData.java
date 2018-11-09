package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class GameTypeReferenceData {

    public static String OSB_GAME_TYPE_ID = "02";
    public static String POL_GAME_TYPE_ID = "01";
    public static String GAMING_MACHINE_ID = "03";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        GameType gameType1 = (GameType) mongoRepositoryReactive.findById(POL_GAME_TYPE_ID, GameType.class).block();
        if (gameType1 == null) {
            gameType1 = new GameType();
            gameType1.setId(POL_GAME_TYPE_ID);

        }
        gameType1.setDescription("Public Online Lottery");
        gameType1.setName("Public Online Lottery");
        gameType1.setShortCode("POL");
        gameType1.setAipDurationMonths(3);
        gameType1.setAllowsGamingTerminal(true);
        gameType1.setAllowsGamingMachine(false);
        gameType1.setInstitutionLicenseDurationMonths(12);
        gameType1.setGamingMachineLicenseDurationMonths(12);
        gameType1.setAgentLicenseDurationMonths(12);


        GameType gameType2 = (GameType) mongoRepositoryReactive.findById(OSB_GAME_TYPE_ID, GameType.class).block();
        if (gameType2 == null) {
            gameType2 = new GameType();
            gameType2.setId(OSB_GAME_TYPE_ID);

        }
        gameType2.setDescription("Online Sport Betting");
        gameType2.setName("Online Sport Betting");
        gameType2.setShortCode("OSB");
        gameType2.setAllowsGamingTerminal(false);
        gameType2.setAllowsGamingMachine(false);
        gameType2.setAipDurationMonths(3);
        gameType2.setInstitutionLicenseDurationMonths(12);
        gameType2.setGamingMachineLicenseDurationMonths(12);
        gameType2.setAgentLicenseDurationMonths(12);

        GameType gameType3 = (GameType) mongoRepositoryReactive.findById(GAMING_MACHINE_ID, GameType.class).block();
        if (gameType3 == null) {
            gameType3 = new GameType();
            gameType3.setId(GAMING_MACHINE_ID);
        }
        gameType3.setDescription("Gaming Machine");
        gameType3.setName("Gaming Machine");
        gameType3.setShortCode("CS");
        gameType3.setAipDurationMonths(3);
        gameType3.setAllowsGamingMachine(true);
        gameType3.setAllowsGamingTerminal(false);
        gameType3.setInstitutionLicenseDurationMonths(12);
        gameType3.setGamingMachineLicenseDurationMonths(12);
        gameType3.setAgentLicenseDurationMonths(12);


        mongoRepositoryReactive.saveOrUpdate(gameType1);
        mongoRepositoryReactive.saveOrUpdate(gameType2);
        mongoRepositoryReactive.saveOrUpdate(gameType3);
    }
}
