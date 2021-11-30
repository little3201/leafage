/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.hypervisor.document;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Model class for Group User
 *
 * @author liwenqiang 2019/9/16 10:09
 **/
@Document(collection = "user_group")
public class UserGroup extends AbstractDocument {

    /**
     * 用户主键
     */
    @Indexed
    @Field(value = "user_id")
    private ObjectId userId;
    /**
     * 分组主键
     */
    @Indexed
    @Field(value = "group_id")
    private ObjectId groupId;


    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getGroupId() {
        return groupId;
    }

    public void setGroupId(ObjectId groupId) {
        this.groupId = groupId;
    }

}
