package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LicenseUpdater {

    private static final Logger logger = LoggerFactory.getLogger(LicenseUpdater.class);

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        Query query = new Query();
        query.addCriteria(Criteria.where("licenseTypeId").in(Arrays.asList("1", "INSTITUTION")));
        ArrayList<License> institutionLicenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
        if (institutionLicenses != null && !institutionLicenses.isEmpty()) {
            for (License license : institutionLicenses) {
                license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            logger.info("Updated institution licenses");
        }

        query = new Query();
        query.addCriteria(Criteria.where("licenseTypeId").in(Arrays.asList("2", "AGENT")));
        ArrayList<License> agentLicenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
        if (agentLicenses != null && !agentLicenses.isEmpty()) {
            for (License license : agentLicenses) {
                license.setLicenseTypeId(LicenseTypeReferenceData.AGENT_ID);
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            logger.info("Updated agent licenses");
        }

        query = new Query();
        query.addCriteria(Criteria.where("licenseTypeId").in(Arrays.asList("3", "GAMING_MACHINE")));
        ArrayList<License> machineLicense = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
        if (machineLicense != null && !machineLicense.isEmpty()) {
            for (License license : machineLicense) {
                license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_MACHINE_ID);
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            logger.info("Updated gaming machine licenses");
        }


        query = new Query();
        query.addCriteria(Criteria.where("licenseTypeId").is(null));
        ArrayList<PaymentRecord> paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
        if (paymentRecords != null && !paymentRecords.isEmpty()) {
            for (PaymentRecord paymentRecord : paymentRecords) {
                paymentRecord.setLicenseTypeId(paymentRecord.getRevenueNameId());
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            }
        }

        ArrayList<Fee> fees = (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
        if (fees != null && !fees.isEmpty()) {
            for (Fee fee : fees) {
                fee.setLicenseTypeId(fee.getRevenueNameId());
                mongoRepositoryReactive.saveOrUpdate(fee);
            }
        }
    }
}
