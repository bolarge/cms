package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.InstitutionOnboardingWorkFlow;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionOnboardingWorkflowService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class InstitutionOnboardingWorkflowServiceImpl implements InstitutionOnboardingWorkflowService {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionOnboardingWorkflowServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public void createInstitutionOnBoardingWorkflow(String institutionId) {
        InstitutionOnboardingWorkFlow workflow = findWorkflowByInstitutionId(institutionId);
        if (workflow != null) {
            return;
        }
        workflow = new InstitutionOnboardingWorkFlow();
        workflow.setId(UUID.randomUUID().toString());
        workflow.setInstitutionId(institutionId);
        workflow.setCreatedInstitution(true);
        mongoRepositoryReactive.saveOrUpdate(workflow);
    }

    @Override
    public InstitutionOnboardingWorkFlow findWorkflowByInstitutionId(String institutionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        return (InstitutionOnboardingWorkFlow) mongoRepositoryReactive.find(query, InstitutionOnboardingWorkFlow.class).block();
    }

    @Override
    public void updateWorkflowForApplicationFeePayment(PaymentRecord paymentRecord) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(paymentRecord.getInstitutionId());
        if (workFlow != null) {
            workFlow.setPaidApplicationFees(true);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }

    @Override
    public void updateWorkflowForCreateApplicationForm(ApplicationForm applicationForm) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(applicationForm.getInstitutionId());
        if (workFlow != null) {
            workFlow.setCreatedApplicationForm(true);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }

    @Override
    public void updateWorkflowForCompleteApplicationForm(ApplicationForm applicationForm) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(applicationForm.getInstitutionId());
        if (workFlow != null) {
            workFlow.setSubmittedApplicationForm(true);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }

    @Override
    public void updateWorkflowForApprovedApplicationForm(ApplicationForm applicationForm) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(applicationForm.getInstitutionId());
        if (workFlow != null) {
            workFlow.setHasApprovedApplicationForm(true);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }

    @Override
    public void updateWorkflowForNewApplicantMeeting(ScheduledMeeting scheduledMeeting) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(scheduledMeeting.getInstitutionId());
        if (workFlow != null) {
            workFlow.setHasScheduledPresentation(true);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }

    @Override
    public void updateWorkflowForCanceledMeeting(ScheduledMeeting scheduledMeeting) {
        setWorkflowToFalseForMeeting(scheduledMeeting);
    }

    @Override
    public void updateWorkflowForCompletedMeeting(ScheduledMeeting scheduledMeeting) {
        setWorkflowToFalseForMeeting(scheduledMeeting);
    }

    @Override
    public Mono<ResponseEntity> getWorkflowByInstitutionId(String institutionId) {
        try {
            InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(institutionId);
            if (workFlow == null) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(workFlow.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "A error occurred while getting workflow by institution id ", e);
        }
    }

    private void setWorkflowToFalseForMeeting(ScheduledMeeting scheduledMeeting) {
        InstitutionOnboardingWorkFlow workFlow = findWorkflowByInstitutionId(scheduledMeeting.getInstitutionId());
        if (workFlow != null) {
            workFlow.setHasScheduledPresentation(false);
            mongoRepositoryReactive.saveOrUpdate(workFlow);
        }
    }
}
