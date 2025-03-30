drop table if exists categories;

/*==============================================================*/
/* Table: categories                                              */
/*==============================================================*/
create table categories
(
    id                 bigserial    not null primary key,
    name               varchar(127) not null UNIQUE,
    description        varchar(255),
    enabled boolean not null default true,
    created_by         varchar(32)  not null,
    created_date       timestamp    not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32)  not null,
    last_modified_date timestamp    not null default CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX idx_unique_name ON categories (name);


drop table if exists posts;

/*==============================================================*/
/* Table: posts                                                 */
/*==============================================================*/
CREATE TABLE posts
(
    id                 bigserial    not null primary key,
    title              varchar(127) not null,
    tags               varchar[],
    cover              varchar(127),
    category_id        bigint       not null,
    enabled boolean not null default true,
    created_by         varchar(32)  not null,
    created_date       timestamp    not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32)  not null,
    last_modified_date timestamp    not null default CURRENT_TIMESTAMP,
    CONSTRAINT fk_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_category_id ON posts (category_id);
CREATE INDEX idx_created_by ON posts (created_by);


drop table if exists post_content;

/*==============================================================*/
/* Table: post_content                                         */
/*==============================================================*/
CREATE TABLE post_content
(
    id                 bigserial   not null primary key,
    post_id            bigint      not null UNIQUE,
    content            text,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_content_post
        FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE UNIQUE INDEX idx_unique_post_id ON post_content (post_id);


drop table if exists comments;

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
/* Table: comments                                              */
/*==============================================================*/
CREATE TABLE comments
(
    id                 bigserial   not null primary key,
    post_id            bigint      not null,
    country            varchar(255),
    location           varchar(255),
    context            varchar(512),
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id) REFERENCES posts (id)
);


drop table if exists post_statistics;

/*==============================================================*/
/* Table: post_statistics                                       */
/*==============================================================*/
CREATE TABLE post_statistics
(
    id                 bigserial   not null primary key,
    post_id            bigint      not null,
    viewed             bigint,
    likes              bigint,
    comments           bigint,
    created_by         varchar(32) not null,
    created_date       timestamp   not null default CURRENT_TIMESTAMP,
    last_modified_by   varchar(32) not null,
    last_modified_date timestamp   not null default CURRENT_TIMESTAMP
);