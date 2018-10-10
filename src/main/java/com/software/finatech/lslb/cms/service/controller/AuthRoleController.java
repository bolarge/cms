package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthRoleService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "AuthRole", description = "", tags = "AuthRole Controller")
@RestController
@RequestMapping("/api/v1/authrole")
public class AuthRoleController extends BaseController {


   /* @Autowired
    private MailContentBuilderService mailContentBuilderService;*/

    private static Logger logger = LoggerFactory.getLogger(AuthRoleController.class);
    private static final String roleAuditActionId = AuditActionReferenceData.ROLE_ID;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuditLogHelper auditLogHelper;
    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    /**
     * @param id AuthInfo id
     * @return AuthInfo full information
     */
    @ApiOperation(value = "Get AuthRole By Id", response = AuthRoleDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity> getById(@PathVariable String id) {
        AuthRole authRole = (AuthRole) mongoRepositoryReactive.findById(id, AuthRole.class).block();
        if (authRole == null) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(authRole.convertToDto(), HttpStatus.OK));
    }

    /**
     *
     * @param token
     * @param userId
     * @return
     */


    /**
     * @param authRoleCreateDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/role/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Creates a new role", response = AuthRoleDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> createAuthRole(@Valid @RequestBody AuthRoleCreateDto authRoleCreateDto, HttpServletRequest request) {
        try {
            // Lookup AuthIRole in database by name
            AuthRole authRoleExists = (AuthRole) mongoRepositoryReactive.find(new Query(Criteria.where("name").is(authRoleCreateDto.getName())), AuthRole.class).block();
            if (authRoleExists != null) {
                return Mono.just(new ResponseEntity("Role Name Already Exist", HttpStatus.BAD_REQUEST));
            }
            AuthRole authRole = new AuthRole();

            authRole.setId(UUID.randomUUID().toString());
            authRole.setDescription(authRoleCreateDto.getDescription());
            authRole.setName(authRoleCreateDto.getName());
            authRole.getAuthPermissionIds().addAll(authRoleCreateDto.getAuthPermissionIds());


            mongoRepositoryReactive.saveOrUpdate(authRole);


            Mapstore.STORE.get("AuthRole").put(authRole.getId(), authRole);

            String verbiage = String.format("Created Role -> Role Name : %s ", authRole.getName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(roleAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity(authRole.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param
     * @return
     */
    @RequestMapping(value = "/role/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Update an authRole", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> updateAuthRole(@Valid @RequestBody AuthRoleUpdateDto authRoleUpdateDto, HttpServletRequest request) {
        try {
            AuthRole authRole = (AuthRole) Mapstore.STORE.get("AuthRole").get(authRoleUpdateDto.getId());
            if (authRole == null) {
                return Mono.just(new ResponseEntity("Role does not exist", HttpStatus.BAD_REQUEST));
            }
            authRole.getAuthPermissionIds().clear();
            authRole.getAuthPermissions().clear();

            authRole.setDescription(authRoleUpdateDto.getDescription());
            authRole.setName(authRoleUpdateDto.getName());
            authRole.getAuthPermissionIds().addAll(authRoleUpdateDto.getAuthPermissionIds());
            authRole.setSsoRoleMapping(authRoleUpdateDto.getSsoRoleMapping());

            mongoRepositoryReactive.saveOrUpdate(authRole);
            Mapstore.STORE.get("AuthRole").put(authRole.getId(), authRole);

            authRole.setAssociatedProperties();


            String verbiage = String.format("Updated Role, Role Name: %s ", authRole.getName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(roleAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));


            return Mono.just(new ResponseEntity(authRole.convertToDto(), HttpStatus.OK));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @return All role full information
     */
    @RequestMapping(method = RequestMethod.GET, value = "/role/allroles")
    @ApiOperation(value = "Get AuthRoles", response = AuthRoleDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> authRoles() {
        try {
            //@TODO validate request params
            List<FactObject> authRoles = Mapstore.STORE.get("AuthRole").values().stream().collect(Collectors.toList());
            //ArrayList<FactObject> authRoles = (ArrayList<FactObject>) mongoRepositoryReactive.findAll(AuthRole.class).toStream().collect(Collectors.toList());
            ArrayList<AuthRoleDto> authRoleDtos = authRoleDtoListFromAuthRoleList(authRoles);
            if (authRoleDtos.size() == 0) {
                return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }

            return Mono.just(new ResponseEntity(authRoleDtos, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return All permission full information
     */
    @RequestMapping(method = RequestMethod.GET, value = "/permission/all")
    @ApiOperation(value = "Get AuthPermisions", response = AuthPermissionDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> authPermissions() {
        try {
            //@TODO validate request params
            List<FactObject> authPermissions = Mapstore.STORE.get("AuthPermission").values().stream().collect(Collectors.toList());
            //ArrayList<FactObject> authRoles = (ArrayList<FactObject>) mongoRepositoryReactive.findAll(AuthRole.class).toStream().collect(Collectors.toList());
            ArrayList<AuthPermissionDto> authPermissionDtos = new ArrayList<>();
            authPermissions.forEach(entry -> {
                authPermissionDtos.add(((AuthPermission) entry).convertToDto());
            });

            if (authPermissionDtos.size() == 0) {
                return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }

            return Mono.just(new ResponseEntity(authPermissionDtos, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param authPermissionCreateDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/permission/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Creates a new permission", response = AuthPermissionDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> createAuthPermission(@Valid @RequestBody AuthPermissionCreateDto authPermissionCreateDto, HttpServletRequest request) {
        try {
            // Lookup AuthIRole in database by name
            AuthPermission authPermissionExists = (AuthPermission) mongoRepositoryReactive.find(new Query(Criteria.where("name").is(authPermissionCreateDto.getName())), AuthPermission.class).block();
            if (authPermissionExists != null) {
                return Mono.just(new ResponseEntity("Role Name Already Exist", HttpStatus.BAD_REQUEST));
            }
            AuthPermission authPermission = new AuthPermission();

            //authRole.setTenantId();
            authPermission.setDescription(authPermissionCreateDto.getDescription());
            authPermission.setName(authPermissionCreateDto.getName());


            mongoRepositoryReactive.saveOrUpdate(authPermission);


            Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);


            return Mono.just(new ResponseEntity(authPermission.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param
     * @return
     */
    @RequestMapping(value = "/permission/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Update an authRole", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> updateAuthRole(@Valid @RequestBody AuthPermissionUpdateDto authPermissionUpdateDto, HttpServletRequest request) {
        try {

            AuthPermission authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionUpdateDto.getId(), AuthRole.class).block();
            if (authPermission == null) {
                return Mono.just(new ResponseEntity("Bad Request", HttpStatus.BAD_REQUEST));
            }
            String permissionName = authPermission.getName();
            if (authPermissionUpdateDto.getId() != null && !authPermissionUpdateDto.getId().isEmpty()) {
                authPermission.setId(authPermissionUpdateDto.getId());
            } else if (authPermissionUpdateDto.getName() != null && !authPermissionUpdateDto.getName().isEmpty()) {
                authPermission.setDescription(authPermissionUpdateDto.getDescription());
            }

            mongoRepositoryReactive.saveOrUpdate(authPermission);
            Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);

            String verbiage = String.format("Updated Permission, Permission name: %s ", permissionName);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(roleAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity(authPermission.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param userRoleId
     * @return
     */
    @RequestMapping(value = "/eligible-roles/{userRoleId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Get all roles a user can create ", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> getEligibleRolesForCreation(@PathVariable(name = "userRoleId") long userRoleId) {
        Collection<FactObject> authRoles = Mapstore.STORE.get("AuthRole").values();
        List<AuthRole> eligibleRoles = new ArrayList<>();
        for (FactObject factObject : authRoles) {
            AuthRole authRole = (AuthRole) factObject;
            try {
                long id = Long.valueOf(authRole.getId());
                if (id > userRoleId) {
                    eligibleRoles.add(authRole);
                }
            } catch (Exception e) {
                logger.error(String.format("Error parsing Id %s", authRole.getId()));
            }
        }

        ArrayList<AuthRoleDto> authRoleDtos = new ArrayList<>();
        eligibleRoles.forEach(authRole -> {
            authRole.setAssociatedProperties();
            authRoleDtos.add(authRole.convertToDto());
        });
        if (authRoleDtos.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(authRoleDtos, HttpStatus.OK));
    }

    private ArrayList<AuthRoleDto> authRoleDtoListFromAuthRoleList(List<FactObject> authRoles) {
        ArrayList<AuthRoleDto> authRoleDtos = new ArrayList<>();
        authRoles.forEach(entry -> {
            ((AuthRole) entry).setAssociatedProperties();
            authRoleDtos.add(((AuthRole) entry).convertToDto());
        });
        return authRoleDtos;
    }
}
