package com.software.finatech.lslb.cms.userservice.controller;

import com.software.finatech.jjwt.JwtHeaderTokenExtractor;
import com.software.finatech.lslb.cms.userservice.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.userservice.service.EmailService;
import io.advantageous.boon.json.JsonFactory;
import io.advantageous.boon.json.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class BaseController {
    @Autowired
    protected JwtHeaderTokenExtractor tokenExtractor;
    @Value("${racs.ui.host}")
    protected String frontendHost;
    @Value("${racs.ui.port}")
    protected String frontendPort;
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    protected ObjectMapper mapper;
    //protected ObjectMapper mapperAnnotation;
    protected String appHostPort ;
    @Autowired
    protected EmailService emailService;

    /**
     * Initialize class
     */
    @PostConstruct
    public void initialize(){
        mapper =  JsonFactory.createUseAnnotations(true);
        appHostPort = "http://" + frontendHost +((frontendPort!=null && !frontendPort.isEmpty())? ":"+frontendPort:"");
        //mapperAnnotation =  JsonFactory.createUseAnnotations(true);
    }


}
