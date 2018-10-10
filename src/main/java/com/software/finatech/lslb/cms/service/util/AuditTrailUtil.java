package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.AuditTrail;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.UUID;

public class AuditTrailUtil {

    public static AuditTrail createAuditTrail(String auditActionId,
                                              String performedBy,
                                              String owner,
                                              LocalDateTime auditDateTime,
                                              LocalDate auditDate,
                                              Boolean viaAPI,
                                              String remoteAddress,
                                              String actionPerformed){
        AuditTrail auditTrail =  new AuditTrail();
        auditTrail.setActionPerformed(actionPerformed);
        auditTrail.setAuditActionId(auditActionId);
        auditTrail.setAuditDate(auditDate);
        auditTrail.setAuditDateTime(auditDateTime);
        auditTrail.setOwner(owner);
        auditTrail.setPerformedBy(performedBy);
        auditTrail.setRemoteAddress(remoteAddress);
        auditTrail.setViaAPI(viaAPI);
        auditTrail.setId(UUID.randomUUID().toString());
        return auditTrail;
    }
}
