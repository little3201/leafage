/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.hypervisor.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO class for authority.
 *
 * @author liwenqiang 2019/8/31 15:50
 */
public class AuthorityDTO implements Serializable {

    private static final long serialVersionUID = 8659525799803097800L;
    /**
     * 上级
     */
    private String superior;
    /**
     * 名称
     */
    @NotBlank
    @Size(max = 16)
    private String name;
    /**
     * 类型
     */
    @NotNull
    private Character type;
    /**
     * 图标
     */
    private String icon;
    /**
     * 路径
     */
    @NotBlank
    private String path;
    /**
     * 描述
     */
    private String description;


    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
