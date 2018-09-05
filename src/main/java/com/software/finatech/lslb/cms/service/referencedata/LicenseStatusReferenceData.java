package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class LicenseStatusReferenceData {

    public static final String AIP_LICENSE_STATUS_ID = "01";
    public static  final String LICENSED_LICENSE_STATUS_ID = "02";
    public static final String LICENSE_REVOKED_LICENSE_STATUS_ID = "03";
    public static final String LICENSE_IN_PROGRESS_LICENSE_STATUS_ID = "04";
    public static final String LICENSE_EXPIRED_STATUS_ID = "05";
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        LicenseStatus licenseStatus1 = (LicenseStatus)mongoRepositoryReactive.findById(AIP_LICENSE_STATUS_ID, LicenseStatus.class).block();
        if (licenseStatus1 == null){
            licenseStatus1 = new LicenseStatus();
            licenseStatus1.setId(AIP_LICENSE_STATUS_ID);
        }
        licenseStatus1.setName("APPROVAL IN PRINCIPLE");


        LicenseStatus licenseStatus2 = (LicenseStatus)mongoRepositoryReactive.findById(LICENSED_LICENSE_STATUS_ID, LicenseStatus.class).block();
        if (licenseStatus2 == null){
            licenseStatus2 = new LicenseStatus();
            licenseStatus2.setId(LICENSED_LICENSE_STATUS_ID);
        }
        licenseStatus2.setName("LICENSED");

        LicenseStatus licenseStatus3 = (LicenseStatus)mongoRepositoryReactive.findById(LICENSE_REVOKED_LICENSE_STATUS_ID, LicenseStatus.class).block();
        if (licenseStatus3 == null){
            licenseStatus3 = new LicenseStatus();
            licenseStatus3.setId(LICENSE_REVOKED_LICENSE_STATUS_ID);
        }
        licenseStatus3.setName("LICENSE REVOKED");

        LicenseStatus licenseStatus4 = (LicenseStatus)mongoRepositoryReactive.findById(LICENSE_IN_PROGRESS_LICENSE_STATUS_ID, LicenseStatus.class).block();
        if (licenseStatus4 == null){
            licenseStatus4 = new LicenseStatus();
            licenseStatus4.setId(LICENSE_IN_PROGRESS_LICENSE_STATUS_ID);
        }
        licenseStatus4.setName("LICENSE IN PROGRESS");


        LicenseStatus licenseStatus5 = (LicenseStatus)mongoRepositoryReactive.findById(LICENSE_EXPIRED_STATUS_ID, LicenseStatus.class).block();
        if (licenseStatus5 == null){
            licenseStatus5 = new LicenseStatus();
            licenseStatus5.setId(LICENSE_EXPIRED_STATUS_ID);
        }
        licenseStatus5.setName("LICENSE EXPIRED");

        mongoRepositoryReactive.saveOrUpdate(licenseStatus1);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus2);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus3);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus4);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus5);

    }
}
