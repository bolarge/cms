package com.software.finatech.lslb.cms.service.model.vigipay;

public class VigipayInvoiceItem {
    private String Detail;
    private double Amount;
    private int Quantity;
    private String ProductCode;

    public String getdetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public double getamount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public int getquantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getproductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }
}
