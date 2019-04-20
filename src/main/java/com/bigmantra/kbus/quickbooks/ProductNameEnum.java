package com.bigmantra.kbus.quickbooks;

import lombok.Getter;

public enum ProductNameEnum {

    FUEL_DIESEL("Fuel - Diesel","Fuel - Diesel");

    @Getter
    private String productName;
    @Getter
    private String categoryName;

    ProductNameEnum(String productName, String categoryName) {
        this.productName = productName;
        this.categoryName = categoryName;
    }


    public static ProductNameEnum fromCategoryName(String categoryName) {
        for (ProductNameEnum b : ProductNameEnum.values()) {
            if (b.categoryName.equalsIgnoreCase(categoryName)) {
                return b;
            }
        }
        return null;
    }

    public static ProductNameEnum fromProductName(String productName) {
        for (ProductNameEnum b : ProductNameEnum.values()) {
            if (b.productName.equalsIgnoreCase(productName)) {
                return b;
            }
        }
        return null;
    }


    }



