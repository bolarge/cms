package com.software.finatech.lslb.cms.service.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryUtils {

    public static void addDateToQuery(Query query, String startDate, String endDate, String dateProperty) {
        if (StringUtils.isEmpty(dateProperty)) {
            dateProperty = "creationDate";
        }
        LocalDate today = LocalDate.now();
        LocalDate startDateTime;
        if (StringUtils.isEmpty(startDate)) {
            startDateTime = today;
        } else {
            startDateTime = new LocalDate(startDate);
        }
        LocalDate endDateTime;
        if (StringUtils.isEmpty(endDate)) {
            endDateTime = today;
        } else {
            endDateTime = new LocalDate(endDate);
        }
        query.addCriteria(Criteria.where(dateProperty).gte(startDateTime).lte(endDateTime));
    }
}
