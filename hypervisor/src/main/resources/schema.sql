drop table if exists groups;

/*==============================================================*/
/* Table: groups                                                */
/*==============================================================*/
create table groups
(
    id                 bigserial   not null primary key,
    name               varchar(64) not null UNIQUE,
    description        varchar(512),
    enabled boolean not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);


drop table if exists users;

/*==============================================================*/
/* Table: users                                                 */
/*==============================================================*/
create table users
(
    id                     bigserial    not null primary key,
    username               varchar(16)  not null UNIQUE,
    password               varchar(255) not null,
    given_name         varchar(64),
    middle_name        varchar(64),
    family_name        varchar(64),
    avatar                 varchar(127),
    account_expires_at     timestamp,
    credentials_expires_at timestamp,
    account_non_locked     boolean      not null default true,
    enabled            boolean     not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);


drop table if exists group_members;

/*==============================================================*/
/* Table: group_members                                         */
/*==============================================================*/
create table group_members
(
    id       bigserial   not null primary key,
    group_id bigint      not null,
    username varchar(32) not null,
    CONSTRAINT fk_group_members_group
        FOREIGN KEY (group_id) REFERENCES groups (id),
    CONSTRAINT fk_group_members_username
        FOREIGN KEY (username) REFERENCES users (username)
);


drop table if exists group_privileges;

/*==============================================================*/
/* Table: group_privileges                                         */
/*==============================================================*/
create table group_privileges
(
    id           bigserial not null primary key,
    group_id     bigint    not null,
    privilege_id bigint    not null,
    CONSTRAINT fk_group_privileges_group
        FOREIGN KEY (group_id) REFERENCES groups (id),
    CONSTRAINT fk_group_privileges_privileges
        FOREIGN KEY (privilege_id) REFERENCES privileges (id)
);


drop table if exists privileges;

/*==============================================================*/
/* Table: privileges                                             */
/*==============================================================*/
create table privileges
(
    id                 bigserial   not null primary key,
    superior_id        bigint,
    name               varchar(64) not null,
    path               varchar(127),
    component          varchar(127),
    redirect           varchar(127),
    icon               varchar(127),
    action             varchar[],
    description        varchar(512),
    enabled            boolean     not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);


drop table if exists dictionaries;

/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/*==============================================================*/
/* Table: dictionaries                                          */
/*==============================================================*/
create table dictionaries
(
    id                 bigserial   not null primary key,
    name               varchar(64) not null,
    superior_id        bigint,
    description        varchar(512),
    enabled boolean not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);


drop table if exists messages;

/*==============================================================*/
/* Table: messages                                              */
/*==============================================================*/
create table messages
(
    id                 bigserial    not null primary key,
    title              varchar(255) not null,
    context            text,
    is_read            boolean      not null default false,
    receiver           varchar(32)  not null,
    description        varchar(512),
    enabled boolean not null default true,
    created_by         varchar(32)  not null,
    created_date       timestamp    not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32)  not null,
    last_modified_date timestamp    not null default CURRENT_TIMESTAMP
);


drop table if exists regions;

/*==============================================================*/
/* Table: regions                                               */
/*==============================================================*/
create table regions
(
    id                 bigserial   not null primary key,
    name               varchar(64) not null,
    superior_id        bigint,
    postal_code        bigint,
    area_code          bigint,
    description        varchar(512),
    enabled boolean not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);


drop table if exists access_logs;

/*==============================================================*/
/* Table: access_logs                                           */
/*==============================================================*/
create table access_logs
(
    id                 bigserial   not null primary key,
    ip                 inet,
    location           varchar(127),
    context            text,
    user_agent         varchar(255), -- 用户代理信息
    http_method        varchar(10),  -- HTTP方法
    url                text,         -- 请求URL
    status_code        integer,      -- HTTP状态码
    response_time      bigint,       -- 响应时间
    referer            varchar(255), -- 来源页面
    session_id         varchar(50),  -- 会话标识符
    device_type        varchar(20),  -- 设备类型
    os                 varchar(50),  -- 操作系统
    browser            varchar(50)   -- 浏览器
        enabled           boolean not null default true,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);