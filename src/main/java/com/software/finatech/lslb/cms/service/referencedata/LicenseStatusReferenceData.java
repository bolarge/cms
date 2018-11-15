package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.ArrayList;
import java.util.List;

public class LicenseStatusReferenceData {
    public static final String AIP_LICENSE_STATUS_ID = "01";
    public static final String LICENSED_LICENSE_STATUS_ID = "02";
    public static final String LICENSE_REVOKED_ID = "03";
    public static final String RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID = "04";
    public static final String LICENSE_EXPIRED_STATUS_ID = "05";
    public static final String AIP_DOCUMENT_STATUS_ID = "06";
    public static final String AIP_COMPLETED = "07";
    public static final String LICENSE_RUNNING = "09";
    public static final String RENEWAL_LICENSE_IN_REVIEW = "08";
    public static final String RENEWED_ID = "10";
    public static final String LICENSE_TRANSFERRED = "11";
    public static final String LICENSE_TERMINATED_ID = "12";
    public static final String LICENSE_SUSPENDED_ID = "13";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(AIP_LICENSE_STATUS_ID, mongoRepositoryReactive, "APPROVAL IN PRINCIPLE");
        loadForIdAndName(LICENSED_LICENSE_STATUS_ID, mongoRepositoryReactive, "LICENCED");
        loadForIdAndName(LICENSE_REVOKED_ID, mongoRepositoryReactive, "LICENCE REVOKED");
        loadForIdAndName(RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID, mongoRepositoryReactive, "RENEWAL IN PROGRESS");
        loadForIdAndName(LICENSE_EXPIRED_STATUS_ID, mongoRepositoryReactive, "EXPIRED");
        loadForIdAndName(AIP_DOCUMENT_STATUS_ID, mongoRepositoryReactive, "AIP DOCUMENT UPLOADED");
        loadForIdAndName(AIP_COMPLETED, mongoRepositoryReactive, "AIP COMPLETED");
        loadForIdAndName(LICENSE_RUNNING, mongoRepositoryReactive, "LICENCE RUNNING");
        loadForIdAndName(RENEWAL_LICENSE_IN_REVIEW, mongoRepositoryReactive, "RENEWAL IN REVIEW");
        loadForIdAndName(RENEWED_ID, mongoRepositoryReactive, "RENEWED");
        loadForIdAndName(LICENSE_TRANSFERRED, mongoRepositoryReactive, "LICENCE TRANSFERRED");
        loadForIdAndName(LICENSE_TERMINATED_ID, mongoRepositoryReactive, "LICENCE TERMINATED");
        loadForIdAndName(LICENSE_SUSPENDED_ID, mongoRepositoryReactive, "LICENCE SUSPENDED");
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

    public static List<String> getAllowedLicensedStatusIds() {
        List<String> allowedLicenseStatusIds = new ArrayList<>();
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.AIP_COMPLETED);
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.RENEWED_ID);
        return allowedLicenseStatusIds;
    }
}
