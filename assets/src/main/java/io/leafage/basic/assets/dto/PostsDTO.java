/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.assets.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户信息入参
 *
 * @author liwenqiang  2019-03-03 22:59
 **/
public class PostsDTO implements Serializable {

    private static final long serialVersionUID = -4116939329295119085L;

    /**
     * 标题
     */
    @NotBlank
    private String title;
    /**
     * 副标题
     */
    @NotBlank
    private String subtitle;
    /**
     * 分类
     */
    @NotNull
    private String category;
    /**
     * 标签
     */
    @NotEmpty
    private Set<String> tags;
    /**
     * 封面
     */
    @NotBlank
    private String cover;
    /**
     * 内容
     */
    @NotBlank
    private String content;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
