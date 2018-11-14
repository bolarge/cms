package com.software.finatech.lslb.cms.service.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryUtils {

    public static void addDateToQuery(Query query, String startDate, String endDate, String dateProperty){
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            if (StringUtils.isEmpty(dateProperty)) {
                dateProperty = "creationDate";
            }
            LocalDateTime startDateTime = new LocalDateTime(startDate);
            LocalDateTime endDateTime = new LocalDateTime(endDate);
            query.addCriteria(Criteria.where(dateProperty).gte(startDateTime).lte(endDateTime));
        }
    }
}
