/*
 * Copyright (c) 2025.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-- Drop table if exists tags
DROP TABLE IF EXISTS tags;

-- Table structure tags
CREATE TABLE tags
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name               varchar(127) NOT NULL UNIQUE,
    enabled            boolean      NOT NULL DEFAULT true,
    created_by         varchar(50),
    created_date       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add comment to the table and columns
COMMENT
ON TABLE tags IS '标签表';
COMMENT
ON COLUMN tags.id IS 'the pk.';
COMMENT
ON COLUMN tags.name IS '名称';
COMMENT
ON COLUMN tags.enabled IS '是否启用';
COMMENT
ON COLUMN tags.created_by IS '创建者';
COMMENT
ON COLUMN tags.created_date IS '创建时间';
COMMENT
ON COLUMN tags.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN tags.last_modified_date IS '最后修改时间';


-- Drop table if exists posts
DROP TABLE IF EXISTS posts;

-- Table structure posts
CREATE TABLE posts
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title              varchar(127) NOT NULL,
    excerpt            varchar(127),
    enabled            boolean      NOT NULL DEFAULT true,
    created_by         varchar(50),
    created_date       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp
);

-- Add comment to the table and columns
COMMENT
ON TABLE posts IS '帖子表';
COMMENT
ON COLUMN posts.id IS 'the pk.';
COMMENT
ON COLUMN posts.title IS '标题';
COMMENT
ON COLUMN posts.excerpt IS '概述';
COMMENT
ON COLUMN posts.enabled IS '是否启用';
COMMENT
ON COLUMN posts.created_by IS '创建者';
COMMENT
ON COLUMN posts.created_date IS '创建时间';
COMMENT
ON COLUMN posts.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN posts.last_modified_date IS '最后修改时间';


-- Drop table if exists tags
DROP TABLE IF EXISTS tag_posts;

-- Table structure tags
CREATE TABLE tag_posts
(
    id      bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tag_id  bigint NOT NULL,
    post_id bigint NOT NULL,
    CONSTRAINT fk_tag_posts_tags FOREIGN KEY (tag_id) REFERENCES tags (id),
    CONSTRAINT fk_tag_posts_posts FOREIGN KEY (post_id) REFERENCES posts (id)
);

-- Add comment to the table and columns
COMMENT
ON TABLE tag_posts IS '标签帖子关联表';
COMMENT
ON COLUMN tag_posts.id IS 'the pk.';
COMMENT
ON COLUMN tag_posts.tag_id IS 'tag主键';
COMMENT
ON COLUMN tag_posts.post_id IS 'post主键';


-- Drop table if exists post_content
DROP TABLE IF EXISTS post_content;

-- Table structure post_content
CREATE TABLE post_content
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id            bigint    NOT NULL UNIQUE,
    body            text,
    enabled            boolean   NOT NULL DEFAULT true,
    created_by         varchar(50),
    created_date       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_post_content_posts FOREIGN KEY (post_id) REFERENCES posts (id)
);

-- Add comment to the table and columns
COMMENT
ON TABLE post_content IS '帖子内容表';
COMMENT
ON COLUMN post_content.id IS 'the pk.';
COMMENT
ON COLUMN post_content.post_id IS '帖子ID';
COMMENT
ON COLUMN post_content.body IS '内容';
COMMENT
ON COLUMN posts.enabled IS '是否启用';
COMMENT
ON COLUMN post_content.created_by IS '创建者';
COMMENT
ON COLUMN post_content.created_date IS '创建时间';
COMMENT
ON COLUMN post_content.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN post_content.last_modified_date IS '最后修改时间';


-- Drop table if exists comments
DROP TABLE IF EXISTS comments;

-- Table structure comments
CREATE TABLE comments
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id            bigint    NOT NULL,
    location           varchar(255),
    body            varchar(512),
    created_by         varchar(50),
    created_date       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_comments_posts FOREIGN KEY (post_id) REFERENCES posts (id)
);

-- Add comment to the table and columns
COMMENT
ON TABLE comments IS '评论表';
COMMENT
ON COLUMN comments.id IS 'the pk.';
COMMENT
ON COLUMN comments.post_id IS '帖子ID';
COMMENT
ON COLUMN comments.location IS '位置';
COMMENT
ON COLUMN comments.body IS '内容';
COMMENT
ON COLUMN comments.created_by IS '创建者';
COMMENT
ON COLUMN comments.created_date IS '创建时间';
COMMENT
ON COLUMN comments.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN comments.last_modified_date IS '最后修改时间';


-- Drop table if exists regions
DROP TABLE IF EXISTS regions;

-- Table structure regions
CREATE TABLE regions
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name               varchar(50) NOT NULL,
    superior_id        bigint,
    area_code          varchar(4),
    postal_code        int4,
    description        varchar(255),
    enabled            boolean     NOT NULL DEFAULT true,
    created_by         varchar(50),
    created_date       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp
);

-- Add comment to the table and columns
COMMENT
ON TABLE regions IS '地区表';
COMMENT
ON COLUMN regions.id IS 'the pk.';
COMMENT
ON COLUMN regions.name IS '名称';
COMMENT
ON COLUMN regions.superior_id IS '上级ID';
COMMENT
ON COLUMN regions.area_code IS '区号';
COMMENT
ON COLUMN regions.postal_code IS '邮政编码';
COMMENT
ON COLUMN regions.description IS '描述';
COMMENT
ON COLUMN regions.enabled IS '是否启用';
COMMENT
ON COLUMN regions.created_by IS '创建者';
COMMENT
ON COLUMN regions.created_date IS '创建时间';
COMMENT
ON COLUMN regions.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN regions.last_modified_date IS '最后修改时间';


-- ----------------------------
-- Table structure for file_records
-- ----------------------------
DROP TABLE IF EXISTS file_records;
CREATE TABLE file_records
(
    id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name               varchar(50)  NOT NULL,
    type               varchar(255),
    path               varchar(255),
    size               float4,
    description        varchar(255),
    enabled            bool         NOT NULL DEFAULT true,
    created_by         varchar(50),
    created_date       timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6)
);
COMMENT
ON COLUMN file_records.id IS 'the pk.';
COMMENT
ON COLUMN file_records.name IS '名称';
COMMENT
ON COLUMN file_records.mime_type IS '类型';
COMMENT
ON COLUMN file_records.path IS '路径';
COMMENT
ON COLUMN file_records.size IS '大小';
COMMENT
ON COLUMN file_records.description IS '描述';
COMMENT
ON COLUMN file_records.enabled IS '是否启用';
COMMENT
ON COLUMN file_records.created_by IS '创建者';
COMMENT
ON COLUMN file_records.created_date IS '创建时间';
COMMENT
ON COLUMN file_records.last_modified_by IS '最后修改者';
COMMENT
ON COLUMN file_records.last_modified_date IS '最后修改时间';
COMMENT
ON TABLE file_records IS '文件记录表';
