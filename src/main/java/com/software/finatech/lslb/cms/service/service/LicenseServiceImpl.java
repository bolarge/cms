package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseCreateDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
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
public class LicenseServiceImpl implements LicenseService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllLicense(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               String licenseStatusId,
                                               String paymentRecordId, HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(licenseStatusId)) {
                query.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
            }
            if (!StringUtils.isEmpty(paymentRecordId)) {
                query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, License.class).block();
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

            ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
            if (licenses == null || licenses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LicenseDto> licenseDtos = new ArrayList<>();

            licenses.forEach(license -> {
                licenseDtos.add(license.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all licenses";
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }

    }

    @Override
    public Mono<ResponseEntity> findLicenseById(String licenseId) {
        return null;
    }



    public Mono<ResponseEntity> getAllLicenseStatus() {
        try {
            List<FactObject> factObjectList = (List<FactObject>) mongoRepositoryReactive.findAll(LicenseStatus.class).collect(Collectors.toList());
            if (factObjectList == null || factObjectList.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record found", HttpStatus.NOT_FOUND));
            }
            List<EnumeratedFactDto> licenseStatusDtoList = new ArrayList<>();
            factObjectList.forEach(factObject -> {
                LicenseStatus licenseStatus = (LicenseStatus) factObject;
                licenseStatusDtoList.add(licenseStatus.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(licenseStatusDtoList, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all license status";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    public Mono<ResponseEntity> createLicense(LicenseCreateDto licenseCreateDto) {
        try {
            String gameTypeId = licenseCreateDto.getGameTypeId();
           // String paymentRecordId = licenseCreateDto.getPaymentRecordId();
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(licenseCreateDto.getInstitutionId()));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
           /* License existingFeeWithGameTypeAndFeePaymentType = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("A fee setting already exist with the Fee Type and Game Type please update it", HttpStatus.BAD_REQUEST));
            }*/

            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setGameTypeId(gameTypeId);
            license.setInstitutionId(licenseCreateDto.getInstitutionId());
            license.setLicenseStatusId(licenseCreateDto.getLicenseStatusId());

            PaymentRecordServiceImpl paymentRecordService= new PaymentRecordServiceImpl();
            List<PaymentRecord> previousLicenses=
                    (List<PaymentRecord>)paymentRecordService.findLicenses(license.getInstitutionId(), license.getGameTypeId(),"LicenseRecord","","");
            if(previousLicenses.size()==0){
            }
            PaymentRecord lastLicense= previousLicenses.get(previousLicenses.size()-1);

            int result = DateTimeComparator.getInstance().compare(lastLicense.getEndDate().toLocalDate(), new LocalDateTime().toLocalDate());
            if(lastLicense==null || result!=1){
                return Mono.just(new ResponseEntity<>("No Valid Payment Record", HttpStatus.BAD_REQUEST));
            }
            license.setPaymentRecordId(lastLicense.getId());
            mongoRepositoryReactive.saveOrUpdate(license);
            return Mono.just(new ResponseEntity<>(license.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating a License ";
            logger.error(e.getMessage());
            return logAndReturnError(logger, errorMsg, e);
        }
    }
}
