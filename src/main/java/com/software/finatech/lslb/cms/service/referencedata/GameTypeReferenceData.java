package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class GameTypeReferenceData {

    public static String OSB_GAME_TYPE_ID = "02";
    public static String POL_GAME_TYPE_ID = "01";
    public static String GAMING_MACHINE_ID = "03";
    public static String SCRATCH_CARD_ID = "04";
    public static String CASINO_ID = "05";
    public static String POOLS_BETTING_ID = "06";
    public static String OTHER_LICENSE_ID = "07";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        GameType gameType = (GameType) mongoRepositoryReactive.findById(POL_GAME_TYPE_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(POL_GAME_TYPE_ID);

        }
        gameType.setDescription("Public Online Lottery");
        gameType.setName("Public Online Lottery");
        gameType.setShortCode("POL");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingTerminal(true);
        gameType.setAllowsGamingMachine(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);


        gameType = (GameType) mongoRepositoryReactive.findById(OSB_GAME_TYPE_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(OSB_GAME_TYPE_ID);
        }
        gameType.setDescription("Online Sport Betting");
        gameType.setName("Online Sport Betting");
        gameType.setShortCode("OSB");
        gameType.setAllowsGamingTerminal(false);
        gameType.setAllowsGamingMachine(false);
        gameType.setAipDurationMonths(3);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);

        gameType = (GameType) mongoRepositoryReactive.findById(GAMING_MACHINE_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(GAMING_MACHINE_ID);
        }
        gameType.setDescription("Gaming Machine");
        gameType.setName("Gaming Machine");
        gameType.setShortCode("GMO");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingMachine(true);
        gameType.setAllowsGamingTerminal(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);

        gameType = (GameType) mongoRepositoryReactive.findById(SCRATCH_CARD_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(SCRATCH_CARD_ID);
        }
        gameType.setDescription("Scratch Card Operator");
        gameType.setName("Scratch Card");
        gameType.setShortCode("SC");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingMachine(false);
        gameType.setAllowsGamingTerminal(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);

        gameType = (GameType) mongoRepositoryReactive.findById(POOLS_BETTING_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(POOLS_BETTING_ID);
        }
        gameType.setDescription("Pools Betting");
        gameType.setName("Pools Betting");
        gameType.setShortCode("PB");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingMachine(false);
        gameType.setAllowsGamingTerminal(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);


        gameType = (GameType) mongoRepositoryReactive.findById(CASINO_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(CASINO_ID);
        }
        gameType.setDescription("Casino");
        gameType.setName("Casino");
        gameType.setShortCode("CS");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingMachine(true);
        gameType.setAllowsGamingTerminal(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);

        gameType = (GameType) mongoRepositoryReactive.findById(OTHER_LICENSE_ID, GameType.class).block();
        if (gameType == null) {
            gameType = new GameType();
            gameType.setId(OTHER_LICENSE_ID);
        }
        gameType.setDescription("Other Licence");
        gameType.setName("Other Licence");
        gameType.setShortCode("O-L");
        gameType.setAipDurationMonths(3);
        gameType.setAllowsGamingMachine(false);
        gameType.setAllowsGamingTerminal(false);
        gameType.setInstitutionLicenseDurationMonths(12);
        gameType.setGamingMachineLicenseDurationMonths(12);
        gameType.setAgentLicenseDurationMonths(12);
        mongoRepositoryReactive.saveOrUpdate(gameType);

    }
}
