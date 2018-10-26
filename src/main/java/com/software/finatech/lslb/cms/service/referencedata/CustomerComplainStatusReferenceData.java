package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CustomerComplainStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CustomerComplainStatusReferenceData {
    public static final String OPEN_ID = "1";
    public static final String CLOSED_ID = "2";
    public static final String PENDING_ID = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        CustomerComplainStatus customerComplainStatus1 = (CustomerComplainStatus) mongoRepositoryReactive.findById(OPEN_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus1 == null) {
            customerComplainStatus1 = new CustomerComplainStatus();
            customerComplainStatus1.setId(OPEN_ID);
        }
        customerComplainStatus1.setName("OPEN");

        CustomerComplainStatus customerComplainStatus2 = (CustomerComplainStatus) mongoRepositoryReactive.findById(CLOSED_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus2 == null) {
            customerComplainStatus2 = new CustomerComplainStatus();
            customerComplainStatus2.setId(CLOSED_ID);
        }
        customerComplainStatus2.setName("CLOSED");

        CustomerComplainStatus customerComplainStatus3 = (CustomerComplainStatus) mongoRepositoryReactive.findById(PENDING_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus3 == null) {
            customerComplainStatus3 = new CustomerComplainStatus();
            customerComplainStatus3.setId(PENDING_ID);
        }
        customerComplainStatus3.setName("PENDING");

        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus1);
        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus2);
        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus3);
    }
}
