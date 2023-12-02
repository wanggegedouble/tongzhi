package com.yupi.usercenter.contant;

import lombok.Getter;

@Getter
public enum TeamStatusEnums {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");

    public static TeamStatusEnums getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnums[] enums = TeamStatusEnums.values();
        for (TeamStatusEnums anEnum : enums) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    private final Integer value;
    private final String text;
    TeamStatusEnums(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
}
