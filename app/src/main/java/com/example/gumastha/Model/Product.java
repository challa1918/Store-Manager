package com.example.gumastha.Model;

import java.io.Serializable;

public class Product implements Serializable {
    String PRODUCT_NAME;
    String SELLING_PRICE;
    String COST_PRICE;
    Integer STOCK_AVAILABLE;
    String BARCODE;
    String URI;

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }

    String CATEGORY;





    public String getMANUFACTURING_DATE() {
        return MANUFACTURING_DATE;
    }

    public void setMANUFACTURING_DATE(String MANUFACTURING_DATE) {
        this.MANUFACTURING_DATE = MANUFACTURING_DATE;
    }

    public String getEXPIRY_DATE() {
        return EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(String EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    String MANUFACTURING_DATE;
    String EXPIRY_DATE;

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    String UNIT;





    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public Product(){

    }
    public Product(String PRODUCT_NAME, String COST_PRICE, String SELLING_PRICE, Integer STOCK_AVAILABLE, String BARCODE, String URI, String UNIT,
                   String MANUFACTURING_DATE, String EXPIRY_DATE, String CATEGORY){
        this.PRODUCT_NAME=PRODUCT_NAME;
        this.COST_PRICE=COST_PRICE;
        this.SELLING_PRICE=SELLING_PRICE;
        this.STOCK_AVAILABLE=STOCK_AVAILABLE;
        this.BARCODE=BARCODE;
        this.URI=URI;
        this.UNIT=UNIT;
        this.MANUFACTURING_DATE=MANUFACTURING_DATE;
        this.EXPIRY_DATE=EXPIRY_DATE;
        this.CATEGORY=CATEGORY;
    }

    public String getPRODUCT_NAME() {
        return PRODUCT_NAME;
    }

    public void setPRODUCT_NAME(String PRODUCT_NAME) {
        this.PRODUCT_NAME = PRODUCT_NAME;
    }

    public String getSELLING_PRICE() {
        return SELLING_PRICE;
    }

    public void setSELLING_PRICE(String SELLING_PRICE) {
        this.SELLING_PRICE = SELLING_PRICE;
    }

    public String getCOST_PRICE() {
        return COST_PRICE;
    }

    public void setCOST_PRICE(String COST_PRICE) {
        this.COST_PRICE = COST_PRICE;
    }

    public Integer getSTOCK_AVAILABLE() {
        return STOCK_AVAILABLE;
    }

    public void setSTOCK_AVAILABLE(Integer STOCK_AVAILABLE) {
        this.STOCK_AVAILABLE= STOCK_AVAILABLE;
    }

    public String getBARCODE() {
        return BARCODE;
    }

    public void setBARCODE(String BARCODE) {
        this.BARCODE = BARCODE;
    }

    @Override
    public String toString() {
        return "Product : {" +
                "PRODUCT_NAME='" + PRODUCT_NAME + '\'' +
                ", SELLING_PRICE='" + SELLING_PRICE + '\'' +
                ", COST_PRICE='" + COST_PRICE + '\'' +
                ", STOCK_AVAILABLE=" + STOCK_AVAILABLE +
                ", BARCODE='" + BARCODE + '\'' +
                ", URI='" + URI + '\'' +
                ", MANUFACTURING_DATE='" + MANUFACTURING_DATE + '\'' +
                ", EXPIRY_DATE='" + EXPIRY_DATE + '\'' +
                ", UNIT='" + UNIT + '\'' +
                '}';
    }
}
