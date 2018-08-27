package com.software.finatech.lslb.cms.service.domain;

import org.bson.types.Binary;

@org.springframework.data.mongodb.core.mapping.Document(collection = "Attachment")
public class Attachment extends AbstractFact{
    private String filename;
    private String mimeType;
    private String healthInstitutionId;
    private Binary file;


    public String getHealthInstitutionId() {
        return healthInstitutionId;
    }

    public void setHealthInstitutionId(String healthInstitutionId) {
        this.healthInstitutionId = healthInstitutionId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Binary getFile() {
        return file;
    }

    public void setFile(Binary file) {
        this.file = file;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String getFactName() {
        return "Attachment";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AuthInfo == false) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        AuthInfo that = (AuthInfo) obj;

        Object thisObject = this.getId();
        Object thatObject = that.getId();

        if ((thisObject != null) && (thatObject != null)) {
            return thisObject.equals(thatObject);
        } else {
            return false;
        }
    }

}
