package com.bigmantra.kbus.quickbooks;

import lombok.Getter;

public enum CustomerNameEnum {

    DHARMAPURI_PAAVAKKAL("Dharmapuri - Pavakkal Route","TN29BD2324"),
    DHARMAPURI_HOSUR("Dharmapuri - Hosur Route","TN29BD3777"),
    DHARMAPURI_SALEM("Dharmapuri - Salem Route","TN29BD3444");

    @Getter
    private String customerName;
    @Getter
    private String groupName;

    CustomerNameEnum(String customerName,String groupName) {
        this.customerName = customerName;
        this.groupName=groupName;
    }

    public String getText() {
        return this.customerName;
    }

    public static CustomerNameEnum fromRouteName(String customerName) {
        for (CustomerNameEnum b : CustomerNameEnum.values()) {
            if (b.customerName.equalsIgnoreCase(customerName)) {
                return b;
            }
        }
        return null;
    }


    public static CustomerNameEnum fromPlateName(String groupName) {
        for (CustomerNameEnum b : CustomerNameEnum.values()) {
            if (b.groupName.equalsIgnoreCase(groupName)) {
                return b;
            }
        }
        return null;
    }
}
