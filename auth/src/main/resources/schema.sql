-- Drop table if exists oauth2_registered_client;
DROP TABLE IF EXISTS oauth2_registered_client;

-- Create table oauth2_registered_client
CREATE TABLE oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

-- Add comment to the table and columns
COMMENT
ON TABLE oauth2_registered_client IS 'client 表';
COMMENT
ON COLUMN oauth2_registered_client.id IS '主键';
COMMENT
ON COLUMN oauth2_registered_client.client_id IS '客户端ID';
COMMENT
ON COLUMN oauth2_registered_client.client_id_issued_at IS '生效时间';
COMMENT
ON COLUMN oauth2_registered_client.client_secret IS '密钥';
COMMENT
ON COLUMN oauth2_registered_client.client_secret_expires_at IS '密钥失效时间';
COMMENT
ON COLUMN oauth2_registered_client.client_name IS '名称';
COMMENT
ON COLUMN oauth2_registered_client.client_authentication_methods IS '认证方法';
COMMENT
ON COLUMN oauth2_registered_client.authorization_grant_types IS '授权方式';
COMMENT
ON COLUMN oauth2_registered_client.redirect_uris IS '跳转连接';
COMMENT
ON COLUMN oauth2_registered_client.post_logout_redirect_uris IS '后置退出跳转连接';
COMMENT
ON COLUMN oauth2_registered_client.scopes IS '作用域';
COMMENT
ON COLUMN oauth2_registered_client.client_settings IS '客户端设置';
COMMENT
ON COLUMN oauth2_registered_client.token_settings IS 'token 设置';

-- Drop table if exists oauth2_authorization
DROP TABLE IF EXISTS oauth2_authorization;

-- Create table oauth2_authorization
CREATE TABLE oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    text          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      text          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   text          DEFAULT NULL,
    access_token_value            text          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         text          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           text          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        text          DEFAULT NULL,
    refresh_token_value           text          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        text          DEFAULT NULL,
    user_code_value               text          DEFAULT NULL,
    user_code_issued_at           timestamp     DEFAULT NULL,
    user_code_expires_at          timestamp     DEFAULT NULL,
    user_code_metadata            text          DEFAULT NULL,
    device_code_value             text          DEFAULT NULL,
    device_code_issued_at         timestamp     DEFAULT NULL,
    device_code_expires_at        timestamp     DEFAULT NULL,
    device_code_metadata          text          DEFAULT NULL,
    PRIMARY KEY (id)
);

-- Add comment to the table and columns
COMMENT
ON TABLE oauth2_authorization IS 'authorization 表';
COMMENT
ON COLUMN oauth2_authorization.id IS '主键';
COMMENT
ON COLUMN oauth2_authorization.registered_client_id IS '客户端ID';
COMMENT
ON COLUMN oauth2_authorization.principal_name IS '认证账号';
COMMENT
ON COLUMN oauth2_authorization.authorization_grant_type IS '授权类型';
COMMENT
ON COLUMN oauth2_authorization.attributes IS '参数';
COMMENT
ON COLUMN oauth2_authorization.state IS '状态';
COMMENT
ON COLUMN oauth2_authorization.authorization_code_value IS 'authorization code';
COMMENT
ON COLUMN oauth2_authorization.authorization_code_issued_at IS 'authorization code生效时间';
COMMENT
ON COLUMN oauth2_authorization.authorization_code_expires_at IS 'authorization code失效时间';
COMMENT
ON COLUMN oauth2_authorization.authorization_code_metadata IS 'authorization code 元数据';
COMMENT
ON COLUMN oauth2_authorization.access_token_value IS 'access token';
COMMENT
ON COLUMN oauth2_authorization.access_token_issued_at IS 'access token 生效时间';
COMMENT
ON COLUMN oauth2_authorization.access_token_expires_at IS 'access_token 失效时间';
COMMENT
ON COLUMN oauth2_authorization.access_token_metadata IS 'access token元数据';
COMMENT
ON COLUMN oauth2_authorization.access_token_type IS 'access token 类型';
COMMENT
ON COLUMN oauth2_authorization.access_token_scopes IS 'access token 域';
COMMENT
ON COLUMN oauth2_authorization.oidc_id_token_value IS 'oidc token';
COMMENT
ON COLUMN oauth2_authorization.oidc_id_token_issued_at IS 'oidc token 生效时间';
COMMENT
ON COLUMN oauth2_authorization.oidc_id_token_expires_at IS 'oidc token 失效时间';
COMMENT
ON COLUMN oauth2_authorization.oidc_id_token_metadata IS 'oidc token 元数据';
COMMENT
ON COLUMN oauth2_authorization.refresh_token_value IS 'refresh token';
COMMENT
ON COLUMN oauth2_authorization.refresh_token_issued_at IS 'refresh token 生效时间';
COMMENT
ON COLUMN oauth2_authorization.refresh_token_expires_at IS 'refresh token 失效时间';
COMMENT
ON COLUMN oauth2_authorization.refresh_token_metadata IS 'refresh token 元数据';

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

-- Drop table if exists oauth2_authorization_consent
DROP TABLE IF EXISTS oauth2_authorization_consent;

-- Create table oauth2_authorization_consent
CREATE TABLE oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL,
    principal_name       VARCHAR(200)  NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

-- Add comment to the table and columns
COMMENT
ON TABLE oauth2_authorization_consent IS 'consent 表';
COMMENT
ON COLUMN oauth2_authorization_consent.registered_client_id IS '客户端ID';
COMMENT
ON COLUMN oauth2_authorization_consent.principal_name IS '认证账号';
COMMENT
ON COLUMN oauth2_authorization_consent.authorities IS '权限';