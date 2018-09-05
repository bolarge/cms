package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.ExpirationList;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
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
import java.util.Map;
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

    @Autowired
    ExpirationList expirationList;

    @Override
    public Mono<ResponseEntity> findAllLicense(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               String agentId,
                                               String gamingMachineId,
                                               String licenseStatusId,
                                               String gameTypeId,
                                               String paymentRecordId, String licenseType, HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(gamingMachineId)) {
                query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            }
            if (!StringUtils.isEmpty(licenseStatusId)) {
                query.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
            }
            if (!StringUtils.isEmpty(paymentRecordId)) {
                query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            }if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(licenseType)) {
                query.addCriteria(Criteria.where("licenseType").is(licenseType));
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
            if (licenses.size() == 0 || licenses.isEmpty()) {
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
    public Mono<ResponseEntity> findLicense(String licenseId, String institutionId, String agentId, String gamingMachineId,String gameTypeId) {
        LocalDateTime dateTime = new LocalDateTime();
        dateTime = dateTime.plusDays(90);
        Query queryLicence = new Query();

            if(!StringUtils.isEmpty(institutionId)&&StringUtils.isEmpty(agentId)&&
                    StringUtils.isEmpty(gamingMachineId)){
                queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
                queryLicence.addCriteria(Criteria.where("gamingMachineId").is(""));
                queryLicence.addCriteria(Criteria.where("agentId").is(""));

            }
            if(!StringUtils.isEmpty(agentId)&&StringUtils.isEmpty(institutionId)&&StringUtils.isEmpty(gamingMachineId)){
                queryLicence.addCriteria(Criteria.where("agentId").is(agentId));
                queryLicence.addCriteria(Criteria.where("institutionId").is(""));
                queryLicence.addCriteria(Criteria.where("gamingMachineId").is(""));
            }
            if(!StringUtils.isEmpty(gamingMachineId)&&StringUtils.isEmpty(institutionId)&&StringUtils.isEmpty(agentId)){
                queryLicence.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
                queryLicence.addCriteria(Criteria.where("institutionId").is(""));
                queryLicence.addCriteria(Criteria.where("agentId").is(""));
            }
        if(!StringUtils.isEmpty(gamingMachineId)&&!StringUtils.isEmpty(institutionId)
                &&StringUtils.isEmpty(institutionId)){
            queryLicence.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryLicence.addCriteria(Criteria.where("institutionId").is(""));
        }
        if(!StringUtils.isEmpty(agentId)&&!StringUtils.isEmpty(institutionId)
                &&StringUtils.isEmpty(institutionId)){
            queryLicence.addCriteria(Criteria.where("agentId").is(agentId));
            queryLicence.addCriteria(Criteria.where("institutionId").is(""));
            queryLicence.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));

        }if(!StringUtils.isEmpty(agentId)&&!StringUtils.isEmpty(gamingMachineId)
                &&!StringUtils.isEmpty(institutionId)){
            queryLicence.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            queryLicence.addCriteria(Criteria.where("agentId").is(agentId));
            queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));

        }


            if(!StringUtils.isEmpty(licenseId)){
                queryLicence.addCriteria(Criteria.where("id").is(licenseId));
            }

        if(!StringUtils.isEmpty(gameTypeId)){
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        }

        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryLicence, License.class).toStream().collect(Collectors.toList());
        List<LicenseDto> licenseDtos= new ArrayList<>();
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }
        for(License license: licenses){

            int days = Days.daysBetween(dateTime,license.getEndDate()).getDays();
            if (days > 0) {
                license.setRenewalStatus("true");
            } else {
                license.setRenewalStatus("false");
            }
            licenseDtos.add(license.convertToDto());
        }

        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }
    @Override
    public List<EnumeratedFactDto> getLicenseStatus() {
        Map licenseMap = Mapstore.STORE.get("LicenseStatus");
        ArrayList<LicenseStatus> licenseStatuses = new ArrayList<LicenseStatus>(licenseMap.values());
        List<EnumeratedFactDto> licenseStatusDtoLists = new ArrayList<>();
        licenseStatuses.forEach(factObject -> {
            LicenseStatus licenseStatus = factObject;
            licenseStatusDtoLists.add(licenseStatus.convertToDto());
        });
        return licenseStatusDtoLists;
    }

    @Override
    public Mono<ResponseEntity> getAllLicenseStatus() {
        return Mono.just(new ResponseEntity<>(getLicenseStatus(), HttpStatus.OK));

    }

    @Override
    public Mono<ResponseEntity> getExpiringLicenses() {

        List<License> licenses=expirationList.getExpiringLicences( 90, LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        if(licenses.size()==0){
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });

     return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));

    }

    @Override
    public Mono<ResponseEntity> getExpiringAIPs() {
        List<License> licenses=expirationList.getExpiringLicences( 14, LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));
       // return ;
    }

    @Override
    public Mono<ResponseEntity> getExpiredLicenses() {
        List<License> licenses=expirationList.getExpiredLicences(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));


    }

    @Override
    public Mono<ResponseEntity> getExpiredAIPs() {
        List<License> licenses=expirationList.getExpiredLicences(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        if(licenses.size()==0){
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));

       // return
    }

    @Override
    public Mono<ResponseEntity> updateLicense(LicenseUpdateDto licenseUpdateDto) {
        try {
            License license;
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("paymentRecordId").is(licenseUpdateDto.getPaymentRecordId()));
            License licenseCheck = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();

            if (licenseCheck == null) {
                return Mono.just(new ResponseEntity<>("No Valid Payment Record", HttpStatus.BAD_REQUEST));
            }
            Query queryPaymentRecord = new Query();

            queryPaymentRecord.addCriteria(Criteria.where("id").is(licenseUpdateDto.getPaymentRecordId()));
            PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(queryPaymentRecord, PaymentRecord.class).block();
            int paymentRecordEndYear= Integer.parseInt(paymentRecord.getEndYear());

            license = licenseCheck;
            LocalDateTime fromDate;
            if(licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_REVOKED_LICENSE_STATUS_ID)
                    || licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID)){
                fromDate=null;
            }

            else {
                String startDate = licenseUpdateDto.getStartDate();
                if ((startDate != "" && !startDate.isEmpty())) {
                    if (!startDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                        return Mono.just(new ResponseEntity("Invalid Date format. " +
                                "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                    }
                    fromDate = new LocalDateTime(startDate);
                    license.setStartDate(fromDate);

                } else {

                    return Mono.just(new ResponseEntity("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));

                }
            }

            Query queryGameType = new Query();
            queryGameType.addCriteria(Criteria.where("id").is(license.getPaymentRecord().convertToDto().getFee().getGameType().getId()));
            GameType gameType = (GameType) mongoRepositoryReactive.find(queryGameType, GameType.class).block();
            int duration=0;
            if(licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID)){
                duration = Integer.parseInt(gameType.convertToDto().getAipDuration());
            }else if(licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID)){
               if(licenseUpdateDto.getLicenseType().equalsIgnoreCase("agent")){
                   duration = Integer.parseInt(gameType.convertToDto().getAgentLicenseDuration());
               }else if(licenseUpdateDto.getLicenseType().equalsIgnoreCase("gamingMachine")){
                   duration = Integer.parseInt(gameType.convertToDto().getGamingMachineLicenseDuration());
               }else if(licenseUpdateDto.getLicenseType().equalsIgnoreCase("institution")){
                    duration = Integer.parseInt(gameType.convertToDto().getLicenseDuration());
               }
            }

            if (!licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_REVOKED_LICENSE_STATUS_ID) &&
                    !licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID)
                    &&!licenseUpdateDto.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID)
                    ) {
                        license.setEndDate(fromDate.plusMonths(duration));
                        license.setRenewalStatus("false");

            }
            int licenseRecordEndYear=Integer.parseInt(licenseCheck.getEndDate().toString("yyyy"));
            license.setLicenseStatusId(licenseUpdateDto.getLicenseStatusId());
           if(!licenseUpdateDto.getLicenseStatusId().equalsIgnoreCase(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID)){
               if(paymentRecordEndYear != licenseRecordEndYear){
                   return Mono.just(new ResponseEntity<>("No Valid Payment Record", HttpStatus.BAD_REQUEST));

               }
           }
            LocalDateTime dateTime = new LocalDateTime();

            if(license.getEndDate().toDate().compareTo(dateTime.toDate())<0){
               license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);
               license.setRenewalStatus("true");
            }


            mongoRepositoryReactive.saveOrUpdate(license);
            return Mono.just(new ResponseEntity<>(license.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating a License ";
            logger.error(e.getMessage());
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId) {
        Query queryForLicensedInstitutionInGameType = new Query();
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        License licenseForInstitutionAndGameType = (License)mongoRepositoryReactive.find(queryForLicensedInstitutionInGameType, License.class).block();
        if (licenseForInstitutionAndGameType == null){
            return  false;
        }else {
            return StringUtils.equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID, licenseForInstitutionAndGameType.getLicenseStatusId());
        }
    }
}
