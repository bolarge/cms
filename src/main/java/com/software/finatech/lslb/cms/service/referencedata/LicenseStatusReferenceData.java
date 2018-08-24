package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class LicenseStatusReferenceData {

    private static final String ONE = "01";
    private static  final String TWO = "02";
    private static final String THREE = "03";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        LicenseStatus licenseStatus1 = (LicenseStatus)mongoRepositoryReactive.findById(ONE, LicenseStatus.class).block();
        if (licenseStatus1 == null){
            licenseStatus1 = new LicenseStatus();
            licenseStatus1.setId(ONE);
        }
        licenseStatus1.setName("APPROVAL IN PRINCIPLE");


        LicenseStatus licenseStatus2 = (LicenseStatus)mongoRepositoryReactive.findById(TWO, LicenseStatus.class).block();
        if (licenseStatus2 == null){
            licenseStatus2 = new LicenseStatus();
            licenseStatus2.setId(TWO);
        }
        licenseStatus2.setName("LICENSED");

        LicenseStatus licenseStatus3 = (LicenseStatus)mongoRepositoryReactive.findById(THREE, LicenseStatus.class).block();
        if (licenseStatus3 == null){
            licenseStatus3 = new LicenseStatus();
            licenseStatus3.setId(THREE);
        }
        licenseStatus3.setName("LICENSE REVOKED");

        mongoRepositoryReactive.saveOrUpdate(licenseStatus1);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus2);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus3);

    }
}
