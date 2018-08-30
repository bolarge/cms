package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "GamingOperatorMachines")
public class GamingOperatorMachine extends AbstractFact {
   protected String institutionId;
   protected String agentId;
   protected boolean managedByInstitution;

    @Override
    public String getFactName() {
        return "GamingOperatorAdmins";
    }
}
