package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.referencedata.LicenseTransferStatusReferenceData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "LicenseTransferStatuses")
public class LicenseTransferStatus extends EnumeratedFact {

    public boolean isPendingNewInstitutionAddition() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_NEW_INSTITUTION_ADDITION_ID, this.id);
    }

    public boolean isPendingInitialApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_INITIAL_APPROVAL_ID, this.id);
    }

    public boolean isPendingAddInstitutionApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_ADD_INSTITUTION_APPROVAL_ID, this.id);
    }

    public boolean isPendingFinalApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_FINAL_APPROVAL_ID, this.id);
    }
}
