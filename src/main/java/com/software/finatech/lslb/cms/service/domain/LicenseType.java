package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "LicenseTypes")
public class LicenseType extends EnumeratedFact {

    @Override
    public String getFactName() {
        return "LicenseType";
    }

    public boolean appliesToInstitution() {
        return StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, this.id)
                || StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, this.id);
    }

    public boolean appliesToAgent() {
        return StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, this.id)
                || StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, this.id);
    }

    public boolean isAgent() {
        return StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, this.id);
    }

    public boolean isInstitution() {
        return StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, this.id);
    }

    public boolean isGamingTerminal() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, this.id);
    }

    public boolean isGamingMachine() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, this.id);
    }
}
