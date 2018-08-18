package com.software.finatech.lslb.cms.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service("mailContentBuilderService")
public class MailContentBuilderService {
    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilderService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(Map<String, Object> model, String templateName) {
        Context context = new Context();
        if (model != null) {
            model.forEach((key,value) -> context.setVariable(key, value));
            return templateEngine.process(templateName, context);
        }
        return "";
    }
}
