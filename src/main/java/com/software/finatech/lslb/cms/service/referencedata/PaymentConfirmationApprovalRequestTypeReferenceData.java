package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.domain.PaymentConfirmationApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.OutsideSystemPaymentService;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PaymentConfirmationApprovalRequestTypeReferenceData {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConfirmationApprovalRequestTypeReferenceData.class);

    public static final String CONFIRM_FULL_PAYMENT_ID = "1";
    public static final String CONFIRM_PARTIAL_PAYMENT_ID = "2";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(CONFIRM_FULL_PAYMENT_ID, mongoRepositoryReactive, "CONFIRM FULL PAYMENT");
        loadForIdAndName(CONFIRM_PARTIAL_PAYMENT_ID, mongoRepositoryReactive, "CONFIRM PARTIAL PAYMENT");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        PaymentConfirmationApprovalRequestType type = (PaymentConfirmationApprovalRequestType) mongoRepositoryReactive.findById(id, PaymentConfirmationApprovalRequestType.class).block();
        if (type == null) {
            type = new PaymentConfirmationApprovalRequestType();
            type.setId(id);
        }
        type.setName(name);
        mongoRepositoryReactive.saveOrUpdate(type);
    }

    //POINT OF FAILURE
    private static PaymentConfirmationApprovalRequestType getTypeById(MongoRepositoryReactiveImpl mongoRepositoryReactive, String typeId) {
        //Map<String, FactObject> typeMap = Mapstore.STORE.get("PaymentConfirmationApprovalRequestType");
        //logger.info("Type Object is " + typeMap.get(typeId));
        logger.info("Approval Request ID  is " +  typeId);

        PaymentConfirmationApprovalRequestType type = null;
        //if (typeMap != null) {
            //NULL POINTER EXCEPTION THROWN AS OBJECT ID COULD NOT BE OBTAINED FROM INTERNAL CACHE
            //type = (PaymentConfirmationApprovalRequestType) typeMap.get(typeId);
        //}
        if (type == null) {
            type = (PaymentConfirmationApprovalRequestType) mongoRepositoryReactive.findById(typeId, PaymentConfirmationApprovalRequestType.class).block();
            //if (typeMap != null && type != null) {
            //    typeMap.put(typeId, type);
           // }
        }
        return type;
    }


    public static String getTypeNameById(MongoRepositoryReactiveImpl mongoRepositoryReactive, String id) {
        PaymentConfirmationApprovalRequestType type = getTypeById(mongoRepositoryReactive, id);
        if (type != null) {
            return type.getName();
        }
        return null;
    }
}
