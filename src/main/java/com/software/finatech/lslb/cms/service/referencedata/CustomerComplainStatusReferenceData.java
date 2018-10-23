package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CustomerComplainStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CustomerComplainStatusReferenceData {
    public static final String OPEN_ID = "1";
    public static final String RESOLVED_ID = "2";
    public static final String CLOSED_ID = "3";
    public static final String UNRESOLVED_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        CustomerComplainStatus customerComplainStatus1 = (CustomerComplainStatus) mongoRepositoryReactive.findById(OPEN_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus1 == null) {
            customerComplainStatus1 = new CustomerComplainStatus();
            customerComplainStatus1.setId(OPEN_ID);
        }
        customerComplainStatus1.setName("OPEN");

        CustomerComplainStatus customerComplainStatus2 = (CustomerComplainStatus) mongoRepositoryReactive.findById(RESOLVED_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus2 == null) {
            customerComplainStatus2 = new CustomerComplainStatus();
            customerComplainStatus2.setId(RESOLVED_ID);
        }
        customerComplainStatus2.setName("RESOLVED");

        CustomerComplainStatus customerComplainStatus3 = (CustomerComplainStatus) mongoRepositoryReactive.findById(CLOSED_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus3 == null) {
            customerComplainStatus3 = new CustomerComplainStatus();
            customerComplainStatus3.setId(CLOSED_ID);
        }
        customerComplainStatus3.setName("CLOSED");

        CustomerComplainStatus customerComplainStatus4 = (CustomerComplainStatus) mongoRepositoryReactive.findById(UNRESOLVED_ID, CustomerComplainStatus.class).block();
        if (customerComplainStatus4 == null) {
            customerComplainStatus4 = new CustomerComplainStatus();
            customerComplainStatus4.setId(UNRESOLVED_ID);
        }
        customerComplainStatus4.setName("UNRESOLVED");

        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus1);
        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus2);
        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus3);
        mongoRepositoryReactive.saveOrUpdate(customerComplainStatus4);
    }
}
