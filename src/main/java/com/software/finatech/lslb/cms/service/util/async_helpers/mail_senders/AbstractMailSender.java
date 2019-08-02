package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMailSender {
    @Autowired
    protected MailContentBuilderService mailContentBuilderService;
    @Autowired
    protected EmailService emailService;
    @Autowired
    protected FrontEndPropertyHelper frontEndPropertyHelper;
    @Autowired
    protected AuthInfoService authInfoService;
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
}
