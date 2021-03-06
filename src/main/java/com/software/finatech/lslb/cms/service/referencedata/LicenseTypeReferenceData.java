package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.Mapstore;

import java.util.Map;

public class LicenseTypeReferenceData {

    public static final String INSTITUTION_ID = "01";
    public static final String AGENT_ID = "02";
    public static final String GAMING_MACHINE_ID = "03";
    public static final String GAMING_TERMINAL_ID = "04";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        LicenseType licenseType = (LicenseType) mongoRepositoryReactive.findById(INSTITUTION_ID, LicenseType.class).block();
        if (licenseType == null) {
            licenseType = new LicenseType();
            licenseType.setId(INSTITUTION_ID);
        }
        licenseType.setName("GAMING OPERATOR");
        LicenseType licenseType1 = (LicenseType) mongoRepositoryReactive.findById(AGENT_ID, LicenseType.class).block();
        if (licenseType1 == null) {
            licenseType1 = new LicenseType();
            licenseType1.setId(AGENT_ID);
        }
        licenseType1.setName("AGENT");
        LicenseType licenseType2 = (LicenseType) mongoRepositoryReactive.findById(GAMING_MACHINE_ID, LicenseType.class).block();
        if (licenseType2 == null) {
            licenseType2 = new LicenseType();
            licenseType2.setId(GAMING_MACHINE_ID);
        }
        licenseType2.setName("GAMING MACHINE");

        LicenseType licenseType3 = (LicenseType) mongoRepositoryReactive.findById(GAMING_TERMINAL_ID, LicenseType.class).block();
        if (licenseType3 == null) {
            licenseType3 = new LicenseType();
            licenseType3.setId(GAMING_TERMINAL_ID);
        }
        licenseType3.setName("GAMING TERMINAL");

        mongoRepositoryReactive.saveOrUpdate(licenseType);
        mongoRepositoryReactive.saveOrUpdate(licenseType1);
        mongoRepositoryReactive.saveOrUpdate(licenseType2);
        mongoRepositoryReactive.saveOrUpdate(licenseType3);
    }

    public static LicenseType getLicenseTypeById(MongoRepositoryReactiveImpl mongoRepositoryReactive, String licenseTypeId) {
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");

        LicenseType licenseType = null;
        if (licenseTypeMap != null) {
            licenseType = (LicenseType) licenseTypeMap.get(licenseTypeId);
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(licenseTypeId, licenseType);
            }
        }
        return licenseType;
    }

    public static String getLicenseTypeNameById(MongoRepositoryReactiveImpl mongoRepositoryReactive, String id){
        LicenseType licenseType = getLicenseTypeById(mongoRepositoryReactive, id);
        if (licenseType != null){
            return licenseType.getName();
        }
        return null;
    }
}
