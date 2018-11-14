package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.InstitutionOnboardingWorkFlow;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface InstitutionOnboardingWorkflowService {

    void createInstitutionOnBoardingWorkflow(String institutionId);

    InstitutionOnboardingWorkFlow findWorkflowByInstitutionId(String institutionId);

    Mono<ResponseEntity> getWorkflowByInstitutionId(String institutionId);

    void updateWorkflowForApplicationFeePayment(PaymentRecord paymentRecord);

    void updateWorkflowForCreateApplicationForm(ApplicationForm applicationForm);

    void updateWorkflowForCompleteApplicationForm(ApplicationForm applicationForm);

    void updateWorkflowForApprovedApplicationForm(ApplicationForm applicationForm);

    void updateWorkflowForNewApplicantMeeting(ScheduledMeeting scheduledMeeting);

    void updateWorkflowForCanceledMeeting(ScheduledMeeting scheduledMeeting);

    void updateWorkflowForCompletedMeeting(ScheduledMeeting scheduledMeeting);
}
