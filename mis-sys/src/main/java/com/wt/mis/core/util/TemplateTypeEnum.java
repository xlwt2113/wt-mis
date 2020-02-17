package com.wt.mis.core.util;

/**
 * 代码模板类型 枚举
 * Create by Kalvin on 2019/6/19.
 */
public enum TemplateTypeEnum {

    CONTROLLER("CONTROLLER", "controller.vm"),
    ENTITY("ENTITY", "entity.vm"),
    REPOSITORY("REPOSITORY", "repository.vm"),
    LIST("LIST", "list.vm"),
    EDIT("EDIT", "edit.vm"),
    VIEW("VIEW", "view.vm");

    private String type;
    private String name;

    TemplateTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    TemplateTypeEnum get(String type) {
        for (TemplateTypeEnum typeEnum : TemplateTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
