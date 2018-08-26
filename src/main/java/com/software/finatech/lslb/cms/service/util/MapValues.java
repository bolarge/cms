package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.domain.PaymentStatus;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class MapValues {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public GameType getGameType(String gameTypeId) {
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }
    public FeePaymentType getFeePaymentType(String feePaymentTypeId) {
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        FeePaymentType feePaymentType = null;
        if (feePaymentTypeMap != null) {
            feePaymentType = (FeePaymentType) feePaymentTypeMap.get(feePaymentTypeId);
        }
        if (feePaymentType == null) {
            feePaymentType = (FeePaymentType) mongoRepositoryReactive.findById(feePaymentTypeId, FeePaymentType.class).block();
            if (feePaymentType != null && feePaymentTypeMap != null) {
                feePaymentTypeMap.put(feePaymentTypeId, feePaymentType);
            }
        }
        return feePaymentType;
    }
    public LicenseStatus getLicenseStatus(String licenseStatusId) {
        Map licenseStatusMap = Mapstore.STORE.get("LicenseStatus");
        LicenseStatus licenseStatus = null;
        if (licenseStatusMap != null) {
            licenseStatus = (LicenseStatus) licenseStatusMap.get(licenseStatusId);
        }
        if (licenseStatus == null) {
            licenseStatus = (LicenseStatus) mongoRepositoryReactive.findById(licenseStatusId, LicenseStatus.class).block();
            if (licenseStatus != null && licenseStatusMap != null) {
                licenseStatusMap.put(licenseStatusId, licenseStatus);
            }
        }
        return licenseStatus;
    }

    public PaymentStatus getPaymentStatus(String paymentStatusId) {
        Map paymentStatusMap = Mapstore.STORE.get("PaymentStatus");
        PaymentStatus paymentStatus = null;
        if (paymentStatusMap != null) {
            paymentStatus = (PaymentStatus) paymentStatusMap.get(paymentStatusId);
        }
        if (paymentStatus == null) {
            paymentStatus = (PaymentStatus) mongoRepositoryReactive.findById(paymentStatusId, PaymentStatus.class).block();
            if (paymentStatus != null && paymentStatusMap != null) {
                paymentStatusMap.put(paymentStatusId, paymentStatus);
            }
        }
        return paymentStatus;
    }

    public List<EnumeratedFactDto> getPaymentStatus() {
        List<PaymentStatus> paymentStatus = (List<PaymentStatus>) Mapstore.STORE.get("PaymentStatus");
        List<EnumeratedFactDto> paymentStatusDtoList = new ArrayList<>();
        paymentStatus.forEach(factObject -> {
            PaymentStatus paymentStat =  factObject;
            paymentStatusDtoList.add(paymentStat.convertToDto());
        });
        return paymentStatusDtoList;
    }

    public List<EnumeratedFactDto> getFeePaymentType() {
        List<FeePaymentType> feePaymentTypes = (List<FeePaymentType>) Mapstore.STORE.get("FeePaymentType");
        List<EnumeratedFactDto> feePaymentTypeDtoList = new ArrayList<>();
        feePaymentTypes.forEach(factObject -> {
            FeePaymentType feePaymentType = factObject;
            feePaymentTypeDtoList.add(feePaymentType.convertToDto());
        });
        return feePaymentTypeDtoList;
    }

    public List<EnumeratedFactDto> getLicenseStatus() {
        List<LicenseStatus> licenseStatuses = (List<LicenseStatus>) Mapstore.STORE.get("LicenseStatus");
        List<EnumeratedFactDto> licenseStatusDtoLists = new ArrayList<>();
        licenseStatuses.forEach(factObject -> {
            LicenseStatus licenseStatus = factObject;
            licenseStatusDtoLists.add(licenseStatus.convertToDto());
        });
        return licenseStatusDtoLists;
    }

}
