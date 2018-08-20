package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.FactObject;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by djaiyeola on 9/20/17.
 */
public class Mapstore {
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, FactObject>> STORE = new ConcurrentHashMap<>();

}
