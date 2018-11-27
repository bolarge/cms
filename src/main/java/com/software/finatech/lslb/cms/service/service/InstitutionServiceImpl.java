package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ManagementDetail;
import com.software.finatech.lslb.cms.service.model.applicantMembers.Shareholder;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionOnboardingWorkflowService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.CustomerCodeCreatorAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    private static final String institutionAuditActionId = AuditActionReferenceData.INSTITUTION;
    private static final Logger logger = LoggerFactory.getLogger(InstitutionServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private CustomerCodeCreatorAsync customerCodeCreatorAsync;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private GameTypeService gameTypeService;
    private InstitutionOnboardingWorkflowService institutionOnboardingWorkflowService;

    @Autowired
    public InstitutionServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                  CustomerCodeCreatorAsync customerCodeCreatorAsync,
                                  AuditLogHelper auditLogHelper,
                                  SpringSecurityAuditorAware springSecurityAuditorAware,
                                  GameTypeService gameTypeService,
                                  InstitutionOnboardingWorkflowService institutionOnboardingWorkflowService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.customerCodeCreatorAsync = customerCodeCreatorAsync;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.gameTypeService = gameTypeService;
        this.institutionOnboardingWorkflowService = institutionOnboardingWorkflowService;
    }

    @Override
    public Mono<ResponseEntity> createInstitution(InstitutionCreateDto institutionCreateDto) {
        Mono<ResponseEntity> validateInstitutionResponse = validateInstitutionCreateInstitution(institutionCreateDto);
        if (validateInstitutionResponse != null) {
            return validateInstitutionResponse;
        }
        Institution newInstitution = fromCreateInstitutionDto(institutionCreateDto);
        try {
            mongoRepositoryReactive.saveOrUpdate(newInstitution);
            return Mono.just(new ResponseEntity<>(newInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to save institution";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateInstitution(InstitutionUpdateDto institutionUpdateDto, HttpServletRequest request) {
        Query queryForId = new Query();
        String institutionId = institutionUpdateDto.getId();
        String institutionName = institutionUpdateDto.getInstitutionName();
        String institutionEmail = institutionUpdateDto.getEmailAddress();
        queryForId.addCriteria(Criteria.where("id").is(institutionId));
        Institution existingInstitution = (Institution) mongoRepositoryReactive.find(queryForId, Institution.class).block();
        if (existingInstitution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        String existingInstitutionName = existingInstitution.getInstitutionName();
        if (!StringUtils.equals(existingInstitution.getInstitutionName(), institutionUpdateDto.getInstitutionName())) {
            Query queryForName = new Query();
            queryForName.addCriteria(Criteria.where("institutionName").is(institutionName));
            Institution existingInstitutionWithName = (Institution) mongoRepositoryReactive.find(queryForName, Institution.class).block();
            if (existingInstitutionWithName != null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with name %s already exist", institutionName), HttpStatus.BAD_REQUEST));
            }
        }

        if (!StringUtils.equals(existingInstitution.getEmailAddress(), institutionUpdateDto.getEmailAddress())) {
            Query queryForEmail = new Query();
            queryForEmail.addCriteria(Criteria.where("emailAddress").is(institutionEmail));
            Institution existingInstitutionWithEmail = (Institution) mongoRepositoryReactive.find(queryForEmail, Institution.class).block();
            if (existingInstitutionWithEmail != null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with email %s already exist", institutionName), HttpStatus.BAD_REQUEST));
            }
        }
        existingInstitution.setGameTypeIds(institutionUpdateDto.getGameTypeIds());
        existingInstitution.setEmailAddress(institutionUpdateDto.getEmailAddress());
        existingInstitution.setPhoneNumber(institutionUpdateDto.getPhoneNumber());
        existingInstitution.setDescription(institutionUpdateDto.getDescription());
        existingInstitution.setInstitutionName(institutionUpdateDto.getInstitutionName());
        try {
            mongoRepositoryReactive.saveOrUpdate(existingInstitution);

            String verbiage = String.format("Updated Institution , Name -> %s ", institutionName);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(institutionAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), existingInstitutionName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(existingInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while updating the institution";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> disableInstitution(InstitutionDto institutionDto, HttpServletRequest request) {
        try {
            String institutionId = institutionDto.getId();
            if (StringUtils.isEmpty(institutionId)) {
                return Mono.just(new ResponseEntity<>("Institution Id should not be empty", HttpStatus.BAD_REQUEST));
            }
            Institution existingInstitution = (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
            if (existingInstitution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
            }

            if (!existingInstitution.getActive()) {
                return Mono.just(new ResponseEntity<>("Operator is already disabled", HttpStatus.BAD_REQUEST));
            }
            existingInstitution.setActive(false);
            mongoRepositoryReactive.saveOrUpdate(existingInstitution);

            String verbiage = String.format("Disabled institution, Name -> %s ", existingInstitution.getInstitutionName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(institutionAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), existingInstitution.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(existingInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while disabling institution", e);
        }
    }

    @Override
    public Mono<ResponseEntity> enableInstitution(InstitutionDto institutionDto, HttpServletRequest request) {
        try {
            String institutionId = institutionDto.getId();
            if (StringUtils.isEmpty(institutionId)) {
                return Mono.just(new ResponseEntity<>("Institution Id should not be empty", HttpStatus.BAD_REQUEST));
            }
            Institution existingInstitution = (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
            if (existingInstitution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
            }

            if (existingInstitution.getActive()) {
                return Mono.just(new ResponseEntity<>("Operator is already active", HttpStatus.BAD_REQUEST));
            }
            existingInstitution.setActive(true);
            mongoRepositoryReactive.saveOrUpdate(existingInstitution);

            String verbiage = String.format("Enabled institution, Name -> %s ", existingInstitution.getInstitutionName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(institutionAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), existingInstitution.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(existingInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while disabling institution", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findAllInstitutions(int page,
                                                    int pageSize,
                                                    String sortType,
                                                    String sortProperty,
                                                    String gameTypeIds,
                                                    String institutionId,
                                                    HttpServletResponse response) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("\\s*,\\s*"));
                query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeIdList));
            }
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("id").is(institutionId));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, Institution.class).block();
                response.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive
                    .findAll(query, Institution.class).toStream().collect(Collectors.toList());
            if (institutions.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<InstitutionDto> institutionDtos = new ArrayList<>();
            institutions.forEach(entry -> {
                institutionDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(institutionDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Institution findByInstitutionId(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private Mono<ResponseEntity> validateInstitutionCreateInstitution(InstitutionCreateDto institutionCreateDto) {
        String institutionName = institutionCreateDto.getInstitutionName();
        Query queryForName = Query.query(Criteria.where("institutionName").is(institutionName));
        Institution existingInstitutionWithName = (Institution) mongoRepositoryReactive.find(queryForName, Institution.class).block();
        if (existingInstitutionWithName != null) {
            return Mono.just(new ResponseEntity<>(String.format("An institution already exist with name %s", institutionName), HttpStatus.BAD_REQUEST));
        }

        String emailAddress = institutionCreateDto.getEmailAddress();
        Query queryForEmail = Query.query(Criteria.where("emailAddress").is(emailAddress));
        Institution existingInstitutionWithEmail = (Institution) mongoRepositoryReactive.find(queryForEmail, Institution.class).block();
        if (existingInstitutionWithEmail != null) {
            return Mono.just(new ResponseEntity<>(String.format("An institution already exist with email %s", emailAddress), HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    @Override
    public Mono<ResponseEntity> createApplicantInstitution(InstitutionCreateDto institutionCreateDto, AuthInfo applicantUser) {
        Mono<ResponseEntity> validateInstitutionResponse = validateInstitutionCreateInstitution(institutionCreateDto);
        if (validateInstitutionResponse != null) {
            return validateInstitutionResponse;
        }
        Institution newInstitution = fromCreateInstitutionDto(institutionCreateDto);
        try {
            mongoRepositoryReactive.saveOrUpdate(newInstitution);
            applicantUser.setInstitutionId(newInstitution.getId());
            mongoRepositoryReactive.saveOrUpdate(applicantUser);
            customerCodeCreatorAsync.createVigipayCustomerCodeForInstitution(newInstitution);
            institutionOnboardingWorkflowService.createInstitutionOnBoardingWorkflow(newInstitution.getId());
            return Mono.just(new ResponseEntity<>(newInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to save institution";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public void saveInstitution(Institution institution) {
        mongoRepositoryReactive.saveOrUpdate(institution);
    }

    @Override
    public Mono<ResponseEntity> uploadMultipleExistingLicensedInstitutions(MultipartFile multipartFile, HttpServletRequest request) {
        try {
            if (multipartFile.isEmpty()) {
                return Mono.just(new ResponseEntity<>("File supplied is empty", HttpStatus.BAD_REQUEST));
            }
            List<FailedLine> failedLines = new ArrayList<>();
            UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
            List<InstitutionUpload> institutionUploadList = new ArrayList<>();
            try {
                byte[] bytes = multipartFile.getBytes();
                String completeData = new String(bytes);
                String[] rows = completeData.split("\\r?\\n");
                //TODO:: remove comma in string
                for (int i = 1; i < rows.length; i++) {
                    // String[] columns = rows[i].split(",");
                    String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (columns.length < 8) {
                        failedLines.add(FailedLine.fromLineAndReason(rows[i], "Line  is less than 8 fields"));
                    } else {
                        try {
                            InstitutionUpload institutionUpload = new InstitutionUpload();
                            institutionUpload.setLine(rows[i]);
                            institutionUpload.setInstitutionName(columns[0]);
                            institutionUpload.setEmailAddress(columns[1]);
                            institutionUpload.setPhoneNumber(columns[2]);
                            institutionUpload.setDescription(columns[3]);
                            institutionUpload.setAddress(columns[4].replace("\"", ""));
                            String gameTypeId = columns[5];
                            GameType gameType = gameTypeService.findById(gameTypeId);
                            if (gameType == null) {
                                failedLines.add(FailedLine.fromLineAndReason(rows[i], "The Category specified does not exist"));
                                continue;
                            }
//                            institutionUpload.setGameTypeId(gameTypeId);
//                            institutionUpload.setLicenseStartDate(new LocalDate(columns[6]));
//                            institutionUpload.setLicenseEndDate(new LocalDate(columns[7]));
                            institutionUploadList.add(institutionUpload);
                        } catch (IllegalArgumentException e) {
                            failedLines.add(FailedLine.fromLineAndReason(rows[i], "Invalid date format in one of the date fields please use YYYY-MM-dd"));
                        }
                    }
                }
            } catch (IOException e) {
                return logAndReturnError(logger, "An error occurred while parsing the file", e);
            }

            if (!failedLines.isEmpty()) {
                uploadTransactionResponse.setFailedLines(failedLines);
                uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                uploadTransactionResponse.setMessage("Please review with sample file and re upload");
                return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.BAD_REQUEST));
            }

            uploadTransactionResponse = saveInstitutionUploads(institutionUploadList);
            String verbiage = "Uploaded multiple licenced operators";
            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(institutionAuditActionId,
                    currentAuditorName, currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while uploading multiple institutions", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findInstitutionsBySearchKey(String searchKey) {
        try {
            Query query = new Query();
            Criteria criteria = new Criteria();
            if (!StringUtils.isEmpty(searchKey)) {
                criteria.orOperator(Criteria.where("institutionName").regex(searchKey, "i"));
            }
            query.addCriteria(criteria);
            query.with(PageRequest.of(0, 20));
            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
            if (institutions == null || institutions.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<InstitutionDto> institutionDtos = new ArrayList<>();
            institutions.forEach(institution -> {
                InstitutionDto institutionDto = new InstitutionDto();
                institutionDto.setId(institution.getId());
                institutionDto.setInstitutionName(institution.getInstitutionName());
                institutionDtos.add(institutionDto);
            });
            return Mono.just(new ResponseEntity<>(institutionDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while searching institutions by key", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getInstitutionFullDetailById(String id) {
        try {
            Institution institution = findByInstitutionId(id);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Operator with id %s not found", id), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(institution.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting institution full detail by id", e);
        }
    }


    /**
     * @param applicationForm saves the
     *                        director and shareholder details on the
     *                        application form to the operator and his category details
     */
    @Override
    @Async
    public void saveOperatorMembersDetailsToOperator(ApplicationForm applicationForm) {
        Set<String> directorNames = new HashSet<>();
        Set<String> shareHolderNames = new HashSet<>();
        Institution institution = applicationForm.getInstitution();
        InstitutionCategoryDetails institutionCategoryDetails = findInstitutionCategoryDetailsByInstitutionIdAndGameTypeId(applicationForm.getInstitutionId(), applicationForm.getGameTypeId());
        if (institutionCategoryDetails == null) {
            institutionCategoryDetails = new InstitutionCategoryDetails();
            institutionCategoryDetails.setId(UUID.randomUUID().toString());
            institutionCategoryDetails.setInstitutionId(applicationForm.getInstitutionId());
            institutionCategoryDetails.setGameTypeId(applicationForm.getGameTypeId());
        }
        for (ManagementDetail managementDetail : applicationForm.getApplicantMemberDetails().getManagementDetailList()) {
            directorNames.add(String.format("%s %s", managementDetail.getFirstName(), managementDetail.getSurname()));
        }
        for (Shareholder shareholder : applicationForm.getApplicantMemberDetails().getShareholderList()) {
            shareHolderNames.add(String.format("%s %s", shareholder.getFirstName(), shareholder.getSurname()));
        }
        institution.getShareHolderNames().addAll(shareHolderNames);
        institutionCategoryDetails.getShareHolderNames().addAll(shareHolderNames);
        institution.getDirectorsNames().addAll(directorNames);
        institutionCategoryDetails.getDirectorsNames().addAll(directorNames);
        institution.getInstitutionCategoryDetailIds().add(institutionCategoryDetails.getId());
        mongoRepositoryReactive.saveOrUpdate(institution);
        mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
    }

    private UploadTransactionResponse saveInstitutionUploads(List<InstitutionUpload> institutionUploadList) {
        List<FailedLine> failedLineList = new ArrayList<>();
        UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
        for (InstitutionUpload institutionUpload : institutionUploadList) {
            Institution existingInstitutionWithName = findInstitutionByName(institutionUpload.getInstitutionName());
            if (existingInstitutionWithName != null) {
                failedLineList.add(FailedLine.fromLineAndReason(institutionUpload.getLine(), "An institution already exist with the same name"));
                continue;
            }
            Institution institution = fromInstitutionUpload(institutionUpload);
            License license = fromInstitutionUploadAndInstitution(institutionUpload, institution);
            mongoRepositoryReactive.saveOrUpdate(institution);
            mongoRepositoryReactive.saveOrUpdate(license);
        }
        if (!failedLineList.isEmpty()) {
            uploadTransactionResponse.setMessage(String.format("%s operators were not uploaded", failedLineList.size()));
            uploadTransactionResponse.setFailedTransactionCount(failedLineList.size());
            uploadTransactionResponse.setFailedLines(failedLineList);
            return uploadTransactionResponse;
        }
        uploadTransactionResponse.setMessage("Operators uploaded successfully");
        return uploadTransactionResponse;
    }


    private Institution findInstitutionByName(String institutionName) {
        if (StringUtils.isEmpty(institutionName)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.find(Query.query(Criteria.where("institutionName").is(institutionName)), Institution.class).block();
    }

    private Institution fromInstitutionUpload(InstitutionUpload institutionUpload) {
        Institution institution = new Institution();
        institution.setActive(true);
        institution.setInstitutionName(institutionUpload.getInstitutionName());
        institution.setAddress(institutionUpload.getAddress());
        institution.setEmailAddress(institutionUpload.getEmailAddress());
        //    institution.getGameTypeIds().add(institutionUpload.getGameTypeId());
        institution.setDescription(institutionUpload.getDescription());
        institution.setPhoneNumber(institutionUpload.getPhoneNumber());
        institution.setId(UUID.randomUUID().toString());
        return institution;
    }

    private License fromInstitutionUploadAndInstitution(InstitutionUpload institutionUpload, Institution institution) {
        License license = new License();
        license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
        license.setInstitutionId(institution.getId());
//        license.setEffectiveDate(institutionUpload.getLicenseStartDate());
//        license.setExpiryDate(institutionUpload.getLicenseEndDate());
        license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        license.setLicenseNumber(generateLicenseNumberForInstitutionUpload(institutionUpload));
        // license.setGameTypeId(institutionUpload.getGameTypeId());
        license.setId(UUID.randomUUID().toString());
        return license;
    }

    private String generateLicenseNumberForInstitutionUpload(InstitutionUpload institutionUpload) {
        String prefix = "LSLB-";
        prefix = prefix + "OP-";
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
//        GameType gameType = gameTypeService.findById(institutionUpload.getGameTypeId());
//        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
//            prefix = prefix + gameType.getShortCode() + "-";
//        }
        return String.format("%s%s%s", prefix, randomDigit, LocalDateTime.now().getSecondOfMinute());
    }


    private void loadInstitution(MultipartFile multipartFile) {
    }

    private Institution fromCreateInstitutionDto(InstitutionCreateDto institutionCreateDto) {
        Institution institution = new Institution();
        institution.setId(UUID.randomUUID().toString());
        institution.setInstitutionName(institutionCreateDto.getInstitutionName());
        institution.setDescription(institutionCreateDto.getDescription());
        institution.setPhoneNumber(institutionCreateDto.getPhoneNumber());
        institution.setEmailAddress(institutionCreateDto.getEmailAddress());
        institution.setGameTypeIds(institutionCreateDto.getGameTypeIds());
        institution.setAddress(institutionCreateDto.getAddress());
        institution.setActive(true);
        return institution;
    }

    @Override
    public InstitutionCategoryDetails findInstitutionCategoryDetailsByInstitutionIdAndGameTypeId(String institutionId, String gameTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        return (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
    }
}
