package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.exception.VigiPayServiceException;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateCustomer;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateInvoice;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayRecipient;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.httpclient.VigipayHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VigipayServiceImpl implements VigipayService {
    private String customerCorporateCode = "";

    @Value("${vigipay.country-code}")
    private String countryCode;
    @Value("${vigipay.corporate-code}")
    private String corporateCode;

    @Value("${vigipay.location-code}")
    private String locationCode;
    @Value("${vigipay.currency-code}")
    private String currencyCode;
    @Value("${vigipay.corporate-revenue-code}")
    private String corporateRevenueCode;

    private VigipayHttpClient vigipayHttpClient;
    private AuthInfoService authInfoService;

    @Autowired
    public VigipayServiceImpl(VigipayHttpClient vigipayHttpClient,
                              AuthInfoService authInfoService) {
        this.vigipayHttpClient = vigipayHttpClient;
        this.authInfoService = authInfoService;
    }

    @Override
    public String createCustomerCodeForAgent(Agent agent) {
        VigipayCreateCustomer vigipayCreateCustomer = createCustomerFromAgent(agent);
        return vigipayHttpClient.createCustomerCode(vigipayCreateCustomer);
    }

    @Override
    public String createCustomerCodeForInstitution(Institution institution) {
        VigipayCreateCustomer vigipayCreateCustomer = createCustomerFromInstitution(institution);
        return vigipayHttpClient.createCustomerCode(vigipayCreateCustomer);
    }

    @Override
    public String createInBranchInvoiceForAgent(Agent agent, List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = createInvoiceForAgent(agent, vigipayInvoiceItems);
        return vigipayHttpClient.createInvoice(vigipayCreateInvoice);
    }

    @Override
    public String createInBranchInvoiceForInstitution(Institution institution,
                                                      List<AuthInfo> adminsForInstitution,
                                                      List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = createInvoiceFromInstitution(institution, adminsForInstitution, vigipayInvoiceItems);
        return vigipayHttpClient.createInvoice(vigipayCreateInvoice);
    }

    @Override
    public String createInBranchMultipleItemInvoiceForInstitution(Institution institution,
                                                      List<AuthInfo> adminsForInstitution,
                                                      List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = createMultipleItemInvoiceFromInstitution(institution, adminsForInstitution, vigipayInvoiceItems);
        return vigipayHttpClient.createInvoice(vigipayCreateInvoice);
    }



    @Override
    public boolean isConfirmedInvoicePayment(String invoiceNumber) throws VigiPayServiceException {
        return vigipayHttpClient.validateInvoicePaid(invoiceNumber);
    }

    private VigipayCreateCustomer createCustomerFromAgent(Agent agent) {
        VigipayCreateCustomer vigipayCreateCustomer = new VigipayCreateCustomer();
        vigipayCreateCustomer.setAddressLine1(agent.getResidentialAddress() != null ? agent.getResidentialAddress() : "42 Local Airport Road");
        vigipayCreateCustomer.setContactPersonEmail(agent.getEmailAddress());
        vigipayCreateCustomer.setName(agent.getFullName());
        vigipayCreateCustomer.setCustomerCorporateCode(customerCorporateCode);
        vigipayCreateCustomer.setContactPersonTitle("Mr");
        vigipayCreateCustomer.setContactPersonFirstName(agent.getFirstName());
        vigipayCreateCustomer.setContactPersonLastName(agent.getLastName());
        vigipayCreateCustomer.setContactPersonPhone(agent.getPhoneNumber());
        vigipayCreateCustomer.setCountryCode(countryCode);
        vigipayCreateCustomer.setCorporateCode(corporateCode);
        return vigipayCreateCustomer;
    }

    private VigipayCreateCustomer createCustomerFromInstitution(Institution institution) {
        VigipayCreateCustomer vigipayCreateCustomer = new VigipayCreateCustomer();
        List<AuthInfo> gamingOperatorAdmins = authInfoService.findAllEnabledUsersForInstitution(institution.getId());
        AuthInfo admin1 = gamingOperatorAdmins.get(0);
        vigipayCreateCustomer.setAddressLine1(institution.getAddress() != null ? institution.getAddress() : "42 local airport road");
        vigipayCreateCustomer.setContactPersonEmail(admin1.getEmailAddress());
        vigipayCreateCustomer.setContactPersonLastName(admin1.getLastName());
        vigipayCreateCustomer.setName(institution.getInstitutionName());
        vigipayCreateCustomer.setCustomerCorporateCode(customerCorporateCode);
        vigipayCreateCustomer.setContactPersonTitle("Mr");
        vigipayCreateCustomer.setContactPersonFirstName(admin1.getFirstName());
        vigipayCreateCustomer.setContactPersonLastName(admin1.getLastName());
        vigipayCreateCustomer.setContactPersonPhone(admin1.getPhoneNumber());
        vigipayCreateCustomer.setCountryCode(countryCode);
        vigipayCreateCustomer.setCorporateCode(corporateCode);
        return vigipayCreateCustomer;
    }


    private VigipayCreateInvoice createInvoiceFromInstitution(Institution institution, List<AuthInfo> authInfos,
                                                              List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = new VigipayCreateInvoice();
        vigipayCreateInvoice.setCustomerCode(institution.getVgPayCustomerCode());
        vigipayCreateInvoice.setRecipients(vigipayRecipientListFromAdmins(authInfos));
        vigipayCreateInvoice.setLocationCode(locationCode);
        vigipayCreateInvoice.setCurrencyCode(currencyCode);
        vigipayCreateInvoice.setNote("From Lagos State Lotteries Board");
        vigipayCreateInvoice.setInvoiceItems(vigipayInvoiceItems);
        vigipayCreateInvoice.setCorporateRevenueCode(corporateRevenueCode);
        DateTime today = DateTime.now();
        DateTime next7days = today.plusDays(7);
        vigipayCreateInvoice.setInvoiceDate(today.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setDueDate(next7days.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setEnforceDueDate(false);
        vigipayCreateInvoice.setInvoiceType(1);
        vigipayCreateInvoice.setInvoiceAction(2);
        vigipayCreateInvoice.setCreateContacts(true);
        return vigipayCreateInvoice;
    }

    private VigipayCreateInvoice createMultipleItemInvoiceFromInstitution(Institution institution, List<AuthInfo> authInfos,
                                                                          List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = new VigipayCreateInvoice();
        vigipayCreateInvoice.setCustomerCode(institution.getVgPayCustomerCode());
        vigipayCreateInvoice.setRecipients(vigipayRecipientListFromAdmins(authInfos));
        vigipayCreateInvoice.setLocationCode(locationCode);
        vigipayCreateInvoice.setCurrencyCode(currencyCode);
        vigipayCreateInvoice.setNote("From Lagos State Lotteries Board");

        vigipayCreateInvoice.setInvoiceItems(new ArrayList<>(vigipayInvoiceItems));
        vigipayCreateInvoice.setCorporateRevenueCode(corporateRevenueCode);
        DateTime today = DateTime.now();
        DateTime next7days = today.plusDays(7);
        vigipayCreateInvoice.setInvoiceDate(today.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setDueDate(next7days.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setEnforceDueDate(false);
        vigipayCreateInvoice.setInvoiceType(1);
        vigipayCreateInvoice.setInvoiceAction(2);
        vigipayCreateInvoice.setCreateContacts(true);
        return vigipayCreateInvoice;
    }

    private VigipayCreateInvoice createInvoiceForAgent(Agent agent, List<VigipayInvoiceItem> vigipayInvoiceItems) {
        VigipayCreateInvoice vigipayCreateInvoice = new VigipayCreateInvoice();
        vigipayCreateInvoice.setCustomerCode(agent.getVgPayCustomerCode());
        vigipayCreateInvoice.setRecipients(vigipayRecipientListFromAgent(agent));
        vigipayCreateInvoice.setLocationCode(locationCode);
        vigipayCreateInvoice.setCurrencyCode(currencyCode);
        vigipayCreateInvoice.setNote("From Lagos State Lotteries Board");
        vigipayCreateInvoice.setInvoiceItems(vigipayInvoiceItems);
        vigipayCreateInvoice.setCorporateRevenueCode(corporateRevenueCode);
        DateTime today = DateTime.now();
        DateTime next7days = today.plusDays(7);
        vigipayCreateInvoice.setInvoiceDate(today.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setDueDate(next7days.toString("yyyy-MM-dd"));
        vigipayCreateInvoice.setEnforceDueDate(false);
        vigipayCreateInvoice.setInvoiceType(1);
        vigipayCreateInvoice.setInvoiceAction(2);
        vigipayCreateInvoice.setCreateContacts(true);
        return vigipayCreateInvoice;
    }


    private List<VigipayRecipient> vigipayRecipientListFromAgent(Agent agent) {
        List<VigipayRecipient> vigipayRecipientList = new ArrayList<>();
        VigipayRecipient vigipayRecipient = new VigipayRecipient();
        vigipayRecipient.setEmail(agent.getEmailAddress());
        vigipayRecipient.setPhone(agent.getPhoneNumber());
        vigipayRecipient.setTitle( "Mr");
        vigipayRecipient.setLastName(agent.getLastName());
        vigipayRecipient.setFirstName(agent.getFirstName());
        vigipayRecipientList.add(vigipayRecipient);
        return vigipayRecipientList;
    }

    private List<VigipayRecipient> vigipayRecipientListFromAdmins(List<AuthInfo> admins) {
        List<VigipayRecipient> vigipayRecipientList = new ArrayList<>();
        for (AuthInfo admin : admins) {
            VigipayRecipient vigipayRecipient = new VigipayRecipient();
            vigipayRecipient.setFirstName(admin.getFirstName());
            vigipayRecipient.setLastName(admin.getLastName());
            vigipayRecipient.setTitle("Mr");
            vigipayRecipient.setPhone(admin.getPhoneNumber());
            vigipayRecipient.setEmail(admin.getEmailAddress());
            vigipayRecipientList.add(vigipayRecipient);
        }
        return vigipayRecipientList;
    }
}