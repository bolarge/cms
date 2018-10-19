package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeeApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class FeeApprovalRequestTypeReferenceData {

    public static final String CREATE_FEE_ID = "1";
    public static final String SET_FEE_END_DATE_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        FeeApprovalRequestType feeApprovalRequestType = (FeeApprovalRequestType) mongoRepositoryReactive.findById(CREATE_FEE_ID, FeeApprovalRequestType.class).block();
        if (feeApprovalRequestType == null) {
            feeApprovalRequestType = new FeeApprovalRequestType();
            feeApprovalRequestType.setId(CREATE_FEE_ID);
        }
        feeApprovalRequestType.setName("CREATE FEE");
        mongoRepositoryReactive.saveOrUpdate(feeApprovalRequestType);


        feeApprovalRequestType = (FeeApprovalRequestType) mongoRepositoryReactive.findById(SET_FEE_END_DATE_ID, FeeApprovalRequestType.class).block();
        if (feeApprovalRequestType == null) {
            feeApprovalRequestType = new FeeApprovalRequestType();
            feeApprovalRequestType.setId(SET_FEE_END_DATE_ID);
        }
        feeApprovalRequestType.setName("SET FEE END DATE");
        mongoRepositoryReactive.saveOrUpdate(feeApprovalRequestType);
    }
}
