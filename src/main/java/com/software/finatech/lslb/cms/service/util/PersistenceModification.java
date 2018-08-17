package com.software.finatech.lslb.cms.userservice.util;


import com.software.finatech.lslb.cms.userservice.domain.FactObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by djaiyeola on 5/15/17.
 */
public class PersistenceModification {
    ConcurrentHashMap<String, FactObject> newFacts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, FactObject> changedFacts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, FactObject> deletedFacts = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, FactObject> getNewFacts() {
        return newFacts;
    }

    public void setNewFacts(ConcurrentHashMap<String, FactObject> newFacts) {
        this.newFacts = newFacts;
    }

    public ConcurrentHashMap<String, FactObject> getChangedFacts() {
        return changedFacts;
    }

    public void setChangedFacts(ConcurrentHashMap<String, FactObject> changedFacts) {
        this.changedFacts = changedFacts;
    }

    public ConcurrentHashMap<String, FactObject> getDeletedFacts() {
        return deletedFacts;
    }

    public void setDeletedFacts(ConcurrentHashMap<String, FactObject> deletedFacts) {
        this.deletedFacts = deletedFacts;
    }
}
