-- ----------------------------
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS "public"."oauth2_authorization";
CREATE TABLE "public"."oauth2_authorization"
(
    "id"                            varchar(100) PRIMARY KEY,
    "registered_client_id"          varchar(100) NOT NULL,
    "principal_name"                varchar(200) NOT NULL,
    "authorization_grant_type"      varchar(100) NOT NULL,
    "authorized_scopes"             varchar(1000) DEFAULT NULL::character varying,
    "attributes"                    text COLLATE "pg_catalog"."default",
    "state"                         varchar(500)  DEFAULT NULL::character varying,
    "authorization_code_value"      text COLLATE "pg_catalog"."default",
    "authorization_code_issued_at"  timestamp(6),
    "authorization_code_expires_at" timestamp(6),
    "authorization_code_metadata"   text COLLATE "pg_catalog"."default",
    "access_token_value"            text COLLATE "pg_catalog"."default",
    "access_token_issued_at"        timestamp(6),
    "access_token_expires_at"       timestamp(6),
    "access_token_metadata"         text COLLATE "pg_catalog"."default",
    "access_token_type"             varchar(100)  DEFAULT NULL::character varying,
    "access_token_scopes"           varchar(1000) DEFAULT NULL::character varying,
    "oidc_id_token_value"           text COLLATE "pg_catalog"."default",
    "oidc_id_token_issued_at"       timestamp(6),
    "oidc_id_token_expires_at"      timestamp(6),
    "oidc_id_token_metadata"        text COLLATE "pg_catalog"."default",
    "refresh_token_value"           text COLLATE "pg_catalog"."default",
    "refresh_token_issued_at"       timestamp(6),
    "refresh_token_expires_at"      timestamp(6),
    "refresh_token_metadata"        text COLLATE "pg_catalog"."default",
    "user_code_value"               text COLLATE "pg_catalog"."default",
    "user_code_issued_at"           timestamp(6),
    "user_code_expires_at"          timestamp(6),
    "user_code_metadata"            text COLLATE "pg_catalog"."default",
    "device_code_value"             text COLLATE "pg_catalog"."default",
    "device_code_issued_at"         timestamp(6),
    "device_code_expires_at"        timestamp(6),
    "device_code_metadata"          text
)
;
COMMENT
ON COLUMN "public"."oauth2_authorization"."id" IS '主键';
COMMENT
ON COLUMN "public"."oauth2_authorization"."registered_client_id" IS 'client id';
COMMENT
ON COLUMN "public"."oauth2_authorization"."principal_name" IS '认证账号';
COMMENT
ON COLUMN "public"."oauth2_authorization"."authorization_grant_type" IS '授权类型';
COMMENT
ON COLUMN "public"."oauth2_authorization"."attributes" IS '参数';
COMMENT
ON COLUMN "public"."oauth2_authorization"."state" IS '状态';
COMMENT
ON COLUMN "public"."oauth2_authorization"."authorization_code_value" IS 'authorization code';
COMMENT
ON COLUMN "public"."oauth2_authorization"."authorization_code_issued_at" IS 'authorization code生效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."authorization_code_expires_at" IS 'authorization code失效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."authorization_code_metadata" IS 'authorization code 元数据';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_value" IS 'access token';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_issued_at" IS 'access token 生效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_expires_at" IS 'access_token 失效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_metadata" IS 'access token元数据';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_type" IS 'access token 类型';
COMMENT
ON COLUMN "public"."oauth2_authorization"."access_token_scopes" IS 'access token 域';
COMMENT
ON COLUMN "public"."oauth2_authorization"."oidc_id_token_value" IS 'oidc token';
COMMENT
ON COLUMN "public"."oauth2_authorization"."oidc_id_token_issued_at" IS 'oidc token 生效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."oidc_id_token_expires_at" IS 'oidc token 失效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."oidc_id_token_metadata" IS 'oidc token 元数据';
COMMENT
ON COLUMN "public"."oauth2_authorization"."refresh_token_value" IS 'refresh token';
COMMENT
ON COLUMN "public"."oauth2_authorization"."refresh_token_issued_at" IS 'refresh token 生效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."refresh_token_expires_at" IS 'refresh token 失效时间';
COMMENT
ON COLUMN "public"."oauth2_authorization"."refresh_token_metadata" IS 'refresh token 元数据';
COMMENT
ON TABLE "public"."oauth2_authorization" IS 'authorization 表';

-- ----------------------------
-- Table structure for oauth2_authorization_consent
-- ----------------------------
DROP TABLE IF EXISTS "public"."oauth2_authorization_consent";
CREATE TABLE "public"."oauth2_authorization_consent"
(
    "registered_client_id" varchar(100)  NOT NULL,
    "principal_name"       varchar(200)  NOT NULL,
    "authorities"          varchar(1000) NOT NULL
)
;
COMMENT
ON COLUMN "public"."oauth2_authorization_consent"."registered_client_id" IS '客户端id';
COMMENT
ON COLUMN "public"."oauth2_authorization_consent"."principal_name" IS '认证账号';
COMMENT
ON COLUMN "public"."oauth2_authorization_consent"."authorities" IS '权限';
COMMENT
ON TABLE "public"."oauth2_authorization_consent" IS 'consent 表';

-- ----------------------------
-- Table structure for oauth2_registered_client
-- ----------------------------
DROP TABLE IF EXISTS "public"."oauth2_registered_client";
CREATE TABLE "public"."oauth2_registered_client"
(
    "id"                            varchar(100) PRIMARY KEY,
    "client_id"                     varchar(100)  NOT NULL,
    "client_id_issued_at"           timestamp(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "client_secret"                 varchar(200)           DEFAULT NULL::character varying,
    "client_secret_expires_at"      timestamp(6),
    "client_name"                   varchar(200)  NOT NULL,
    "client_authentication_methods" varchar(1000) NOT NULL,
    "authorization_grant_types"     varchar(1000) NOT NULL,
    "redirect_uris"                 varchar(1000)          DEFAULT NULL::character varying,
    "post_logout_redirect_uris"     varchar(1000)          DEFAULT NULL::character varying,
    "scopes"                        varchar(1000) NOT NULL,
    "client_settings"               varchar(2000) NOT NULL,
    "token_settings"                varchar(2000) NOT NULL
)
;
COMMENT
ON COLUMN "public"."oauth2_registered_client"."id" IS '主键';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_id" IS 'ID';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_id_issued_at" IS '生效时间';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_secret" IS '密钥';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_secret_expires_at" IS '密钥失效时间';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_name" IS '名称';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_authentication_methods" IS '认证方法';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."authorization_grant_types" IS '授权方式';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."redirect_uris" IS '跳转连接';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."post_logout_redirect_uris" IS '后置退出跳转连接';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."scopes" IS '作用域';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."client_settings" IS '客户端设置';
COMMENT
ON COLUMN "public"."oauth2_registered_client"."token_settings" IS 'token 设置';
COMMENT
ON TABLE "public"."oauth2_registered_client" IS 'client 表';

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS "public"."persistent_logins";
CREATE TABLE "public"."persistent_logins"
(
    "series"    varchar(64) PRIMARY KEY,
    "username"  varchar(64)  NOT NULL,
    "token"     varchar(64)  NOT NULL,
    "last_used" timestamp(6) NOT NULL
)
;
COMMENT
ON COLUMN "public"."persistent_logins"."username" IS '用户名';
COMMENT
ON COLUMN "public"."persistent_logins"."series" IS '系列';
COMMENT
ON COLUMN "public"."persistent_logins"."token" IS '令牌';
COMMENT
ON COLUMN "public"."persistent_logins"."last_used" IS '最后使用时间';
COMMENT
ON TABLE "public"."persistent_logins" IS '持久化登录表';

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

-- ----------------------------
-- Primary Key structure for table oauth2_authorization_consent
-- ----------------------------
ALTER TABLE "public"."oauth2_authorization_consent"
    ADD CONSTRAINT "oauth2_authorization_consent_pkey" PRIMARY KEY ("registered_client_id", "principal_name");

