package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class LicenseStatusReferenceData {

    public static final String AIP_LICENSE_STATUS_ID = "01";
    public static final String LICENSED_LICENSE_STATUS_ID = "02";
    public static final String LICENSE_REVOKED_LICENSE_STATUS_ID = "03";
    public static final String RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID = "04";
    public static final String LICENSE_EXPIRED_STATUS_ID = "05";
    public static final String AIP_DOCUMENT_STATUS_ID = "06";
    public static final String AIP_COMPLETED = "07";
    public static final String LICENSE_RUNNING = "09";
    public static final String RENEWAL_LICENSE_IN_REVIEW = "08";
    public static final String RENEWED_ID = "10";
    public static final String LICENSE_TRANSFERED = "11";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(AIP_LICENSE_STATUS_ID, mongoRepositoryReactive, "APPROVAL IN PRINCIPLE");
        loadForIdAndName(LICENSED_LICENSE_STATUS_ID, mongoRepositoryReactive, "LICENSED");
        loadForIdAndName(LICENSE_REVOKED_LICENSE_STATUS_ID, mongoRepositoryReactive, "REVOKED");
        loadForIdAndName(RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID, mongoRepositoryReactive, "RENEWAL IN PROGRESS");
        loadForIdAndName(LICENSE_EXPIRED_STATUS_ID, mongoRepositoryReactive, "EXPIRED");
        loadForIdAndName(AIP_DOCUMENT_STATUS_ID, mongoRepositoryReactive, "AIP DOCUMENT UPLOADED");
        loadForIdAndName(AIP_COMPLETED, mongoRepositoryReactive, "AIP COMPLETED");
        loadForIdAndName(LICENSE_RUNNING, mongoRepositoryReactive, "LICENSE RUNNING");
        loadForIdAndName(RENEWAL_LICENSE_IN_REVIEW, mongoRepositoryReactive, "RENEWAL IN REVIEW");
        loadForIdAndName(RENEWED_ID, mongoRepositoryReactive, "RENEWED");
        loadForIdAndName(LICENSE_TRANSFERED, mongoRepositoryReactive, "LICENSE TRANSFERRED");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        LicenseStatus licenseStatus = (LicenseStatus) mongoRepositoryReactive.findById(id, LicenseStatus.class).block();
        if (licenseStatus == null) {
            licenseStatus = new LicenseStatus();
            licenseStatus.setId(id);
        }
        licenseStatus.setName(name);
        mongoRepositoryReactive.saveOrUpdate(licenseStatus);
    }
}
