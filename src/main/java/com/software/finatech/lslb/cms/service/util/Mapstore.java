package com.software.finatech.lslb.cms.userservice.util;

import com.software.finatech.lslb.cms.userservice.domain.FactObject;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by djaiyeola on 9/20/17.
 */
public class Mapstore {
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, FactObject>> STORE = new ConcurrentHashMap<>();

}
