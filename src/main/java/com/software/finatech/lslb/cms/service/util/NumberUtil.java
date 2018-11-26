package com.software.finatech.lslb.cms.service.util;

import org.joda.time.LocalDateTime;

import java.util.Random;

public class NumberUtil {
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static String generateTransactionReferenceForPaymentRecord() {
        LocalDateTime presentDateTime = LocalDateTime.now();
        return String.format("%s%s%s%s%s%s%s", getRandomNumberInRange(20, 5000),
                presentDateTime.getDayOfMonth(),
                presentDateTime.getMonthOfYear(),
                presentDateTime.getYear(),
                presentDateTime.getHourOfDay(),
                presentDateTime.getMinuteOfHour(),
                presentDateTime.getSecondOfMinute());
    }

    public static String generateAgentId() {
        return String.format("LAGOS-AG-%s%s%s", NumberUtil.getRandomNumberInRange(20, 100), LocalDateTime.now().getMinuteOfHour(), LocalDateTime.now().getSecondOfMinute());
    }
}
