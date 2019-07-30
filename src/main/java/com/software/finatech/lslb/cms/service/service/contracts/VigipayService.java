package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.exception.VigiPayServiceException;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;

import java.util.List;

public interface VigipayService {

    String createCustomerCodeForAgent(Agent agent) throws VigiPayServiceException;

    String createCustomerCodeForInstitution(Institution institution) throws VigiPayServiceException;

    String createInBranchInvoiceForAgent(Agent agent, List<VigipayInvoiceItem> vigipayInvoiceItems) throws VigiPayServiceException;

    String createInBranchInvoiceForInstitution(Institution institution, List<AuthInfo> adminsForInstitution, List<VigipayInvoiceItem> vigipayInvoiceItems) throws VigiPayServiceException;

    String createInBranchMultipleItemInvoiceForInstitution(Institution institution,
                                                           List<AuthInfo> adminsForInstitution,
                                                           List<VigipayInvoiceItem> vigipayInvoiceItems) throws VigiPayServiceException;

    String getVigipayPaymentStatusForInvoice(String invoiceNumber) throws VigiPayServiceException;
}
