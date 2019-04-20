package com.bigmantra.kbus.quickbooks;

import lombok.Getter;

public enum CustomerNameEnum {

    DHARMAPURI_PAAVAKKAL("Dharmapuri - Pavakkal Route","TN29BD2324"),
    DHARMAPURI_HOSUR("Dharmapuri - Hosur Route","TN29BD3777"),
    DHARMAPURI_SALEM("Dharmapuri - Salem Route","TN29BD3444");

    @Getter
    private String routeName;
    @Getter
    private String plateName;

    CustomerNameEnum(String routeName,String plateName) {
        this.routeName = routeName;
        this.plateName=plateName;
    }

    public String getText() {
        return this.routeName;
    }

    public static CustomerNameEnum fromRouteName(String routeName) {
        for (CustomerNameEnum b : CustomerNameEnum.values()) {
            if (b.routeName.equalsIgnoreCase(routeName)) {
                return b;
            }
        }
        return null;
    }


    public static CustomerNameEnum fromPlateName(String plateName) {
        for (CustomerNameEnum b : CustomerNameEnum.values()) {
            if (b.plateName.equalsIgnoreCase(plateName)) {
                return b;
            }
        }
        return null;
    }
}
