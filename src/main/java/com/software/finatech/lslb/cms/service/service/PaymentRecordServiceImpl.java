package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;


@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Autowired
    MapValues mapValues;


    @Override
    public Mono<ResponseEntity> findAllPaymentRecords(int page,
                                                      int pageSize,
                                                      String sortDirection,
                                                      String sortProperty,
                                                      String institutionId,
                                                      String approverId,
                                                      String feeId,
                                                      HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(feeId)) {
                query.addCriteria(Criteria.where("feeId").is(feeId));
            }
            if (!StringUtils.isEmpty(approverId)) {
                query.addCriteria(Criteria.where("approverId").is(approverId));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, PaymentRecord.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<PaymentRecord> paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
            if (paymentRecords.size() == 0 || paymentRecords.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<PaymentRecordDto> paymentRecordDtos = new ArrayList<>();

            paymentRecords.forEach(paymentRecord -> {
                paymentRecordDtos.add(paymentRecord.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(paymentRecordDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all payment records";
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }
    }

    public List<EnumeratedFactDto> getPaymentStatus() {
        Map paymentStatusMap = Mapstore.STORE.get("PaymentStatus");
        ArrayList<PaymentStatus> paymentStatus = new ArrayList<PaymentStatus> (paymentStatusMap.values());
        List<EnumeratedFactDto> paymentStatusDtoList = new ArrayList<>();
        paymentStatus.forEach(factObject -> {
            PaymentStatus paymentStat =  factObject;
            paymentStatusDtoList.add(paymentStat.convertToDto());
        });
        return paymentStatusDtoList;
    }

    @Override
    public Mono<ResponseEntity> getAllPaymentStatus() {
        return Mono.just(new ResponseEntity<>(getPaymentStatus(), HttpStatus.OK));
    }

    @Override
    public List<PaymentRecord> findPayments(String institutionId,String agentId, String gamingMachineId, String feeId,String startYear) {
        List<PaymentRecord> findPaymentRecords=findPaymentRecords(institutionId, agentId, gamingMachineId,feeId, startYear);
        return findPaymentRecords;

    }

    public List<PaymentRecord> findPaymentRecords(String institutionId,String agentId, String gamingMachineId, String feeId, String startYear) {
        try {

            Query query = new Query();

            if(StringUtils.isEmpty(agentId)&&StringUtils.isEmpty(gamingMachineId)&&!StringUtils.isEmpty(institutionId)){
                query.addCriteria(Criteria.where("institutionId").is(institutionId));

                if(!StringUtils.isEmpty(agentId)){
                    query.addCriteria(Criteria.where("agentId").is(""));
                }
                if(!StringUtils.isEmpty(gamingMachineId)){
                    query.addCriteria(Criteria.where("gamingMachineId").is(""));
                }
            }else{
                if(!StringUtils.isEmpty(agentId)){
                    query.addCriteria(Criteria.where("agentId").is(agentId));
                }
                if(!StringUtils.isEmpty(gamingMachineId)){
                    query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
                }

            }
            if(!StringUtils.isEmpty(feeId)){
                query.addCriteria(Criteria.where("feeId").is(feeId));
            }
            if(!StringUtils.isEmpty(startYear)){
                query.addCriteria(Criteria.where("startYear").is(startYear));
            }
            List<PaymentRecord> paymentRecords=(List<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());


                return paymentRecords;

        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching license with id";
            return null;
        }
    }
    @Override
    public Mono<ResponseEntity> createPaymentRecord(PaymentRecordCreateDto paymentRecordCreateDto) {
       if(StringUtils.isEmpty(paymentRecordCreateDto.getInstitutionId())
               &&StringUtils.isEmpty(paymentRecordCreateDto.getAgentId())
       &&StringUtils.isEmpty(paymentRecordCreateDto.getGamingMachineId())){
           return Mono.just(new ResponseEntity<>("Invalid Payment Record", HttpStatus.OK));

       }
       Query queryPaymentRecord= new Query();
       queryPaymentRecord.addCriteria(Criteria.where("feeId").is(paymentRecordCreateDto.getFeeId()));
       queryPaymentRecord.addCriteria(Criteria.where("startYear").is(paymentRecordCreateDto.getStartYear()));
       if(!StringUtils.isEmpty(paymentRecordCreateDto.getInstitutionId())){
           queryPaymentRecord.addCriteria(Criteria.where("institutionId").is(paymentRecordCreateDto.getInstitutionId()));
       }
       if(!StringUtils.isEmpty(paymentRecordCreateDto.getGamingMachineId())){
           queryPaymentRecord.addCriteria(Criteria.where("gamingMachineId").is(paymentRecordCreateDto.getGamingMachineId()));
       }
       if(!StringUtils.isEmpty(paymentRecordCreateDto.getAgentId())){
            queryPaymentRecord.addCriteria(Criteria.where("agentId").is(paymentRecordCreateDto.getAgentId()));
        }
       PaymentRecord paymentRecordCheck = (PaymentRecord)mongoRepositoryReactive.find(queryPaymentRecord, PaymentRecord.class).block();
        if(paymentRecordCheck!=null){
            return Mono.just(new ResponseEntity<>("Duplicate Payment, Check and Try Again", HttpStatus.OK));

        }

       PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(UUID.randomUUID().toString());
        paymentRecord.setApproverId(paymentRecordCreateDto.getApproverId());
        paymentRecord.setFeeId(paymentRecordCreateDto.getFeeId());
        paymentRecord.setInstitutionId(paymentRecordCreateDto.getInstitutionId());
        paymentRecord.setPaymentStatusId(paymentRecordCreateDto.getPaymentStatusId());
        paymentRecord.setAgentId(paymentRecordCreateDto.getAgentId());
        paymentRecord.setGamingMachineId(paymentRecordCreateDto.getGamingMachineId());
        paymentRecord.setStartYear(paymentRecordCreateDto.getStartYear());
        int startYear= Integer.parseInt(paymentRecordCreateDto.getStartYear());

        String startYearValue= String.valueOf(startYear-1);

        Fee fee = (Fee) mongoRepositoryReactive.findById(paymentRecordCreateDto.getFeeId(),Fee.class).block();
        if(paymentRecordCreateDto.getRenewalCheck()=="true"){
            List<PaymentRecord> previousLicenses=
                    findPaymentRecords(paymentRecordCreateDto.getInstitutionId(), paymentRecordCreateDto.getAgentId(),paymentRecordCreateDto.getGamingMachineId(), fee.getGameTypeId(),startYearValue);

            PaymentRecord lastLicense = previousLicenses.get(previousLicenses.size() - 1);
            paymentRecord.setParentLicenseId(lastLicense.getId());
        }
        if (!paymentRecord.getPaymentStatusId().equalsIgnoreCase(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID)
                || fee.getFeePaymentTypeId().equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID)) {
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            return Mono.just(new ResponseEntity<>(paymentRecord.convertToDto(), HttpStatus.OK));
        }

        GameType gameType = (GameType) mongoRepositoryReactive.findById(fee.getGameTypeId(),GameType.class).block();

       License license;
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("gameTypeId").is(fee.getGameTypeId()));
        queryLicence.addCriteria(Criteria.where("institutionId").is(paymentRecordCreateDto.getInstitutionId()));
        License licenseCheck = (License) mongoRepositoryReactive.find(queryLicence,License.class).block();

        if(licenseCheck==null){
                license=new License();
                license.setId(UUID.randomUUID().toString());
                license.setFirstPayment(true);

            }else{
                license=licenseCheck;
            license.setFirstPayment(false);

        }
        if(fee.getFeePaymentTypeId().equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID)) {
            if (!StringUtils.isEmpty(paymentRecord.getAgentId())
                    &&!StringUtils.isEmpty(paymentRecord.getInstitutionId())
            &&StringUtils.isEmpty(paymentRecord.getGamingMachineId())) {
                license.setLicenseType("Agent");
               int duration= Integer.parseInt(gameType.getAgentLicenseDuration());
               int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));
                license.setAgentId(paymentRecord.getAgentId());
                license.setInstitutionId(paymentRecord.getInstitutionId());

            } else if (!StringUtils.isEmpty(paymentRecord.getInstitutionId())
                    &&StringUtils.isEmpty(paymentRecord.getAgentId())&&
                    StringUtils.isEmpty(paymentRecord.getGamingMachineId()) ) {
                license.setLicenseType("Institution");
                license.setInstitutionId(paymentRecord.getInstitutionId());
                int duration= Integer.parseInt(gameType.getLicenseDuration());
                int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));

            } else if (!StringUtils.isEmpty(paymentRecord.getGamingMachineId())
                    &&!StringUtils.isEmpty(paymentRecord.getInstitutionId())&&
                    StringUtils.isEmpty(paymentRecord.getAgentId())) {
                int duration= Integer.parseInt(gameType.getGamingMachineLicenseDuration());
                int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));
                license.setLicenseType("GamingMachine");
                license.setGamingMachineId(paymentRecord.getGamingMachineId());
                license.setInstitutionId(paymentRecord.getInstitutionId());
            }else{
                return Mono.just(new ResponseEntity<>("Invalid Payment Record", HttpStatus.OK));
            }
        }
        license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID);
        license.setInstitutionId(paymentRecord.getInstitutionId());
        license.setGameTypeId(fee.getGameTypeId());
        license.setPaymentRecordId(paymentRecord.getId());
        mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        mongoRepositoryReactive.saveOrUpdate(license);
        return Mono.just(new ResponseEntity<>(paymentRecord.convertToDto(), HttpStatus.OK));

    }




    @Override
    public Mono<ResponseEntity> updatePaymentRecord(PaymentRecordUpdateDto paymentRecordUpdateDto) {


        PaymentRecord paymentRecord= (PaymentRecord)mongoRepositoryReactive.findById(paymentRecordUpdateDto.getPaymentRecordId(), PaymentRecord.class).block();
        if(paymentRecord==null){
            return Mono.just(new ResponseEntity<>("Invalid Payment Record", HttpStatus.BAD_REQUEST));
        }
        paymentRecord.setApproverId(paymentRecordUpdateDto.getApproverId());
        paymentRecord.setPaymentStatusId(paymentRecordUpdateDto.getPaymentStatusId());
        if (!paymentRecord.getPaymentStatusId().equalsIgnoreCase(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID)
                || paymentRecord.convertToDto().getFee().getFeePaymentType().getId().equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID)) {
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            return Mono.just(new ResponseEntity<>(paymentRecord.convertToDto(), HttpStatus.OK));
        }
        License license;
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.convertToDto().getFee().getGameType().getId()));
        queryLicence.addCriteria(Criteria.where("paymentRecordId").is(paymentRecord.getId()));
        License licenseCheck = (License) mongoRepositoryReactive.find(queryLicence,License.class).block();

        if(licenseCheck==null){
            license=new License();
            license.setId(UUID.randomUUID().toString());
        }else{
            license=licenseCheck;
        }
        Fee fee = (Fee) mongoRepositoryReactive.findById(paymentRecordUpdateDto.getFeeId(),Fee.class).block();
        GameType gameType = (GameType) mongoRepositoryReactive.findById(fee.getGameTypeId(),GameType.class).block();
        int startYear= Integer.parseInt(paymentRecordUpdateDto.getStartYear());
        if(paymentRecord.convertToDto().getFee().getFeePaymentType().getId().equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID)) {
            if (!StringUtils.isEmpty(paymentRecord.getAgentId())
                    &&!StringUtils.isEmpty(paymentRecord.getInstitutionId())
                    &&StringUtils.isEmpty(paymentRecord.getGamingMachineId())) {
                license.setLicenseType("Agent");
                int duration= Integer.parseInt(gameType.getAgentLicenseDuration());
                int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));
                license.setAgentId(paymentRecord.getAgentId());
                license.setInstitutionId(paymentRecord.getInstitutionId());
            } else if (!StringUtils.isEmpty(paymentRecord.getInstitutionId())
                    &&StringUtils.isEmpty(paymentRecord.getAgentId())&&
                    StringUtils.isEmpty(paymentRecord.getGamingMachineId()) ) {
                license.setLicenseType("Institution");
                license.setInstitutionId(paymentRecord.getInstitutionId());
                int duration= Integer.parseInt(gameType.getLicenseDuration());
                int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));

            } else if (!StringUtils.isEmpty(paymentRecord.getGamingMachineId())
                    &&!StringUtils.isEmpty(paymentRecord.getInstitutionId())&&
                    StringUtils.isEmpty(paymentRecord.getAgentId())) {
                int duration= Integer.parseInt(gameType.getGamingMachineLicenseDuration());
                int endYear= startYear+ (duration/12);
                paymentRecord.setEndYear(String.valueOf(endYear));
                license.setLicenseType("GamingMachine");
                license.setGamingMachineId(paymentRecord.getGamingMachineId());
                license.setInstitutionId(paymentRecord.getInstitutionId());
            }else{
                return Mono.just(new ResponseEntity<>("Invalid Payment Record", HttpStatus.OK));
            }
        }

        license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID);
        license.setInstitutionId(paymentRecord.getInstitutionId());
        license.setGamingMachineId(paymentRecord.getGamingMachineId());
        license.setAgentId(paymentRecord.getAgentId());

        license.setGameTypeId(paymentRecord.convertToDto().getFee().getGameType().getId());
        license.setPaymentRecordId(paymentRecord.getId());
        mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        mongoRepositoryReactive.saveOrUpdate(license);
        return Mono.just(new ResponseEntity<>(paymentRecord.convertToDto(), HttpStatus.OK));

    }

    @Override
    public PaymentRecord findById(String paymentRecordId) {
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }


    @Override
    public void savePaymentRecord(PaymentRecord paymentRecord) {
        mongoRepositoryReactive.saveOrUpdate(paymentRecord);
    }
}

