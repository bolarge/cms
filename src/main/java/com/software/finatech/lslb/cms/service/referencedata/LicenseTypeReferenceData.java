package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.domain.GamingMachine;
import com.software.finatech.lslb.cms.service.domain.LicenseType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class LicenseTypeReferenceData {
    public static final String INSTITUTION = "INSTITUTION";
    public static final String AGENT= "AGENT";
    public static final String GAMING_MACHINE= "GAMING_MACHINE";


        public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
            LicenseType licenseType = (LicenseType) mongoRepositoryReactive.findById(INSTITUTION, LicenseType.class).block();
            if (licenseType == null) {
                licenseType = new LicenseType();
                licenseType.setId(INSTITUTION);
            }
            licenseType.setName("INSTITUTION");
                LicenseType licenseType1 = (LicenseType) mongoRepositoryReactive.findById(AGENT, LicenseType.class).block();
            if (licenseType1 == null) {
                licenseType1 = new LicenseType();
                licenseType1.setId(AGENT);
            }
            licenseType1.setName("AGENT");
            LicenseType licenseType2 = (LicenseType) mongoRepositoryReactive.findById(GAMING_MACHINE, LicenseType.class).block();
            if (licenseType2 == null) {
                licenseType2 = new LicenseType();
                licenseType2.setId(GAMING_MACHINE);
            }
            licenseType2.setName("GAMING MACHINE");

            mongoRepositoryReactive.saveOrUpdate(licenseType);
        mongoRepositoryReactive.saveOrUpdate(licenseType1);
        mongoRepositoryReactive.saveOrUpdate(licenseType2);
    }
}
