package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import io.advantageous.boon.HTTP;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDateTime;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;


@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentRecords(int page,
                                                      int pageSize,
                                                      String sortDirection,
                                                      String sortProperty,
                                                      String institutionId,
                                                      String approverId,
                                                      String feePaymentTypeId,
                                                      HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(feePaymentTypeId)) {
                query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
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
            if (paymentRecords == null || paymentRecords.isEmpty()) {
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

    @Override
    public Mono<ResponseEntity> getAllPaymentStatus() {
        try {
            List<FactObject> factObjectList = (List<FactObject>) mongoRepositoryReactive.findAll(PaymentStatus.class).collect(Collectors.toList());
            if (factObjectList == null || factObjectList.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record found", HttpStatus.NOT_FOUND));
            }
            List<EnumeratedFactDto> paymentStatusDtoList = new ArrayList<>();
            factObjectList.forEach(factObject -> {
                PaymentStatus paymentStatus = (PaymentStatus) factObject;
                paymentStatusDtoList.add(paymentStatus.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(paymentStatusDtoList, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all payment types";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    public Mono<ResponseEntity> findLicenses(String institutionId, String gameTypeId,String objectType, String startDate, String endDate) {
        try {
            LocalDateTime fromDate=null;
            LocalDateTime toDate=null;
            if ((startDate != "" && !startDate.isEmpty()) ||(endDate != "" && !endDate.isEmpty())) {
                if (!startDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")||!endDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") ) {
                    return Mono.just(new ResponseEntity("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                }
                fromDate = new LocalDateTime(startDate);
                toDate = new LocalDateTime(endDate);

            }
            Query query = new Query();
            if(!StringUtils.isEmpty(startDate)&&!StringUtils.isEmpty(endDate) ){
                query.addCriteria(Criteria.where("endDate").lte(toDate));
                query.addCriteria(Criteria.where("startDate").gte(fromDate));
            }else if(!StringUtils.isEmpty(endDate)||StringUtils.isEmpty(startDate)){
                query.addCriteria(Criteria.where("endDate").lte(fromDate));
            }
            if(!StringUtils.isEmpty(institutionId)){
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if(!StringUtils.isEmpty(gameTypeId)){
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            List<PaymentRecord> licenseRecords;
            List<PaymentRecordDto> licenseDtos =new ArrayList<>();
            licenseRecords= (List<PaymentRecord>)mongoRepositoryReactive.findAll(query, PaymentRecord.class);
            if (licenseRecords == null) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            } else {
                for(PaymentRecord licenseRecord: licenseRecords){
                    licenseDtos.add(licenseRecord.convertToDto());
                }
                if(objectType=="LicenseRecord"){
                    return Mono.just(new ResponseEntity<>(licenseRecords, HttpStatus.OK));
                }
                return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
            }
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching license with id";
            return logAndReturnError(logger, errorMsg, e);
        }
    }
    public Mono<ResponseEntity> createPaymentRecord(PaymentRecordCreateDto paymentRecordCreateDto) {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(UUID.randomUUID().toString());
        paymentRecord.setApproverId(paymentRecordCreateDto.getApproverId());
        paymentRecord.setFeeId(paymentRecordCreateDto.getFeeId());
        paymentRecord.setInstitutionId(paymentRecordCreateDto.getInstitutionId());
        paymentRecord.setPaymentStatusId(paymentRecordCreateDto.getPaymentStatusId());
        paymentRecord.setGameTypeId(paymentRecordCreateDto.getGameTypeId());
        LocalDateTime fromDate;
        String startDate=paymentRecordCreateDto.getStartDate();
        if ((startDate != "" && !startDate.isEmpty())) {
            if (!startDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") ) {
                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
            }
            fromDate = new LocalDateTime(startDate);

        } else {
            return Mono.just(new ResponseEntity("Invalid Date format. " +
                    "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));

        }
          paymentRecord.setStartDate(fromDate);
          if(paymentRecordCreateDto.getRenewalCheck()=="true"){
            List<PaymentRecord> previousLicenses=
                    (List<PaymentRecord>)findLicenses(paymentRecordCreateDto.getInstitutionId(), paymentRecordCreateDto.getGameTypeId(),"LicenseRecord",startDate,"");
            if(previousLicenses.size()==0){
            }
              PaymentRecord lastLicense= previousLicenses.get(previousLicenses.size()-1);

              paymentRecord.setParentLicenseId(lastLicense.getId());
        }
        Query queryFee= new Query();
        queryFee.addCriteria(Criteria.where("gameTypeId").is(paymentRecordCreateDto.getGameTypeId()));
        Fee fee = (Fee) mongoRepositoryReactive.find(queryFee,Fee.class).block();
        int duration = Integer.parseInt(fee.getDuration());
        paymentRecord.setEndDate(fromDate.plusDays(duration));
        License license;
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("gameTypeId").is(paymentRecordCreateDto.getGameTypeId()));
        queryLicence.addCriteria(Criteria.where("institutionId").is(paymentRecordCreateDto.getInstitutionId()));
        License licenseCheck = (License) mongoRepositoryReactive.find(queryLicence,License.class).block();
            if(licenseCheck==null){
                license=new License();
            license.setId(UUID.randomUUID().toString());

            }else{
                license=licenseCheck;
            }
        license.setGameTypeId(paymentRecordCreateDto.getGameTypeId());
            license.setLicenseStatusId("04");
        license.setInstitutionId(paymentRecordCreateDto.getInstitutionId());
        /*int result = DateTimeComparator.getInstance().compare(paymentRecord.getEndDate().toLocalDate(), new LocalDateTime().toLocalDate());
        if( result!=1){
            return Mono.just(new ResponseEntity<>("No Valid Payment Record", HttpStatus.BAD_REQUEST));
        }*/
        license.setPaymentRecordId(paymentRecord.getId());
        mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        mongoRepositoryReactive.saveOrUpdate(license);
        return Mono.just(new ResponseEntity<>(paymentRecord.convertToDto(), HttpStatus.OK));

    }
}

