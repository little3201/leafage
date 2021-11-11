/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.assets.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * DTO class for Category
 *
 * @author liwenqiang 2020-10-06 22:09
 */
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = -6972802402513706361L;

    /**
     * 别名
     */
    @NotBlank
    private String alias;
    /**
     * 描述
     */
    private String description;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
