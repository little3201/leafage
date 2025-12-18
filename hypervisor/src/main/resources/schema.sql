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
create table access_logs
(
    id                 bigint generated always as identity
        primary key,
    url                varchar(255),
    http_method        varchar(255),
    params             varchar(255),
    body               varchar(255),
    ip                 inet,
    status_code        integer,
    response_times     bigint,
    response_message   varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table access_logs is '访问日志表';

comment on column access_logs.id is '主键';

comment on column access_logs.url is '接口';

comment on column access_logs.http_method is 'http方法';

comment on column access_logs.params is '参数';

comment on column access_logs.body is '请求体';

comment on column access_logs.ip is 'IP地址';

comment on column access_logs.status_code is 'HTTP状态码';

comment on column access_logs.response_times is '响应时长';

comment on column access_logs.response_message is '响应消息';

comment on column access_logs.enabled is '是否启用';

comment on column access_logs.created_by is '创建者';

comment on column access_logs.created_date is '创建时间';

comment on column access_logs.last_modified_by is '最后修改者';

comment on column access_logs.last_modified_date is '最后修改时间';

alter table access_logs
    owner to postgres;

create table audit_logs
(
    id                 bigint generated always as identity
        primary key,
    operation          varchar(255)                           not null,
    resource           varchar(255)                           not null,
    old_value          varchar(255),
    new_value          varchar(255),
    ip                 inet                                   not null,
    location           varchar(100),
    status_code        integer                                not null,
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    operated_times     bigint                                 not null
);

comment on table audit_logs is '审计日志表';

comment on column audit_logs.id is '主键，自增';

comment on column audit_logs.operation is '操作类型';

comment on column audit_logs.resource is '操作资源';

comment on column audit_logs.old_value is '旧值（JSON 格式）';

comment on column audit_logs.new_value is '新值（JSON 格式）';

comment on column audit_logs.ip is 'IP 地址';

comment on column audit_logs.location is '位置';

comment on column audit_logs.status_code is '状态码';

comment on column audit_logs.enabled is '是否启用';

comment on column audit_logs.created_by is '创建者';

comment on column audit_logs.created_date is '创建时间';

comment on column audit_logs.last_modified_by is '最后修改者';

comment on column audit_logs.last_modified_date is '最后修改时间';

comment on column audit_logs.operated_times is '操作时长';

alter table audit_logs
    owner to postgres;

create table authorities
(
    id        bigint generated always as identity
        primary key,
    username  varchar(64) not null,
    authority varchar(64) not null
);

comment on table authorities is '用户权限表';

comment on column authorities.id is '主键';

comment on column authorities.username is '用户名';

comment on column authorities.authority is '权限';

alter table authorities
    owner to postgres;

create unique index ix_auth_username
    on authorities (username, authority);

create table connections
(
    id                 bigint generated always as identity
        primary key,
    host               varchar(255),
    port               integer,
    name               varchar(255),
    username           varchar(255),
    password           varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table connections is '数据库连接表';

comment on column connections.id is '主键';

comment on column connections.host is '主机';

comment on column connections.port is '端口';

comment on column connections.name is '库名称';

comment on column connections.username is '账号';

comment on column connections.password is '密码';

comment on column connections.enabled is '是否启用';

comment on column connections.created_by is '创建者';

comment on column connections.created_date is '创建时间';

comment on column connections.last_modified_by is '最后修改者';

comment on column connections.last_modified_date is '最后修改时间';

alter table connections
    owner to postgres;

create table dictionaries
(
    id                 bigint generated by default as identity
        primary key,
    name               varchar(50)                            not null,
    superior_id        bigint,
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table dictionaries is '字典表';

comment on column dictionaries.id is '主键';

comment on column dictionaries.name is '名称';

comment on column dictionaries.superior_id is '上级ID';

comment on column dictionaries.description is '描述';

comment on column dictionaries.enabled is '是否启用';

comment on column dictionaries.created_by is '创建者';

comment on column dictionaries.created_date is '创建时间';

comment on column dictionaries.last_modified_by is '最后修改者';

comment on column dictionaries.last_modified_date is '最后修改时间';

alter table dictionaries
    owner to postgres;

create table fields
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(255),
    data_type          varchar(255),
    length             integer,
    field_type         varchar(255),
    form_type          varchar(255),
    ts_type            varchar(255),
    schema_id          bigint                                 not null,
    nullable           boolean,
    is_unique          boolean,
    queryable          boolean,
    editable           boolean,
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    query_type         varchar(255),
    sortable           boolean
);

comment on table fields is '属性配置表';

comment on column fields.id is '主键，自动生成';

comment on column fields.name is '属性命';

comment on column fields.data_type is '字段类型';

comment on column fields.length is '字段长度';

comment on column fields.field_type is '属性类型';

comment on column fields.form_type is '表单类型';

comment on column fields.ts_type is 'ts类型';

comment on column fields.schema_id is 'schema主键';

comment on column fields.nullable is '是否可为空';

comment on column fields.is_unique is '是否唯一';

comment on column fields.queryable is '是否可查询';

comment on column fields.editable is '是否可编辑';

comment on column fields.enabled is '是否启用';

comment on column fields.created_by is '创建者';

comment on column fields.created_date is '创建时间';

comment on column fields.last_modified_by is '最后修改者';

comment on column fields.last_modified_date is '最后修改时间';

comment on column fields.query_type is '查询类型';

comment on column fields.sortable is '是否可排序';

alter table fields
    owner to postgres;

create table file_records
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(50)                            not null,
    content_type       varchar(255),
    path               varchar(255),
    size               bigint,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    directory          boolean,
    regular_file       boolean,
    symbolic_link      boolean,
    extension          varchar(255),
    superior_id        bigint
);

comment on table file_records is '文件记录表';

comment on column file_records.id is '主键';

comment on column file_records.name is '名称';

comment on column file_records.content_type is '类型';

comment on column file_records.path is '路径';

comment on column file_records.size is '大小';

comment on column file_records.created_by is '创建者';

comment on column file_records.created_date is '创建时间';

comment on column file_records.last_modified_by is '最后修改者';

comment on column file_records.last_modified_date is '最后修改时间';

comment on column file_records.directory is '是否目录';

comment on column file_records.regular_file is '是否文件';

comment on column file_records.symbolic_link is '是否软链';

comment on column file_records.extension is '扩展类型';

comment on column file_records.superior_id is '上级ID';

alter table file_records
    owner to postgres;

create table group_authorities
(
    id        bigint generated always as identity
        primary key,
    group_id  bigint      not null,
    authority varchar(50) not null
);

comment on table group_authorities is '用户组权限关系表';

comment on column group_authorities.id is '主键';

comment on column group_authorities.group_id is '用户组ID';

comment on column group_authorities.authority is '权限';

alter table group_authorities
    owner to postgres;

create table groups
(
    id                 bigint generated always as identity
        primary key,
    group_name         varchar(255)                           not null
        constraint uk_groups_group_name
            unique,
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    superior_id        bigint
);

comment on table groups is '用户组表';

comment on column groups.id is '主键';

comment on column groups.group_name is '名称';

comment on column groups.description is '描述';

comment on column groups.enabled is '是否启用';

comment on column groups.created_by is '创建者';

comment on column groups.created_date is '创建时间';

comment on column groups.last_modified_by is '最后修改者';

comment on column groups.last_modified_date is '最后修改时间';

comment on column groups.superior_id is '上级ID';

alter table groups
    owner to postgres;

create table messages
(
    id                 bigint generated always as identity
        primary key,
    title              varchar(255)                           not null,
    content            text,
    unread             boolean      default false             not null,
    receiver           varchar(255)                           not null,
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255)                           not null,
    last_modified_date timestamp(6),
    body               varchar(255)
);

comment on table messages is '消息表';

comment on column messages.id is '主键';

comment on column messages.title is '标题';

comment on column messages.content is '内容';

comment on column messages.unread is '是否未读';

comment on column messages.receiver is '接收者';

comment on column messages.description is '描述';

comment on column messages.enabled is '是否启用';

comment on column messages.created_by is '创建者';

comment on column messages.created_date is '创建时间';

comment on column messages.last_modified_by is '最后修改者';

comment on column messages.last_modified_date is '最后修改时间';

alter table messages
    owner to postgres;

create table oauth2_authorization
(
    id                            varchar(100) not null
        primary key,
    registered_client_id          varchar(100) not null,
    principal_name                varchar(200) not null,
    authorization_grant_type      varchar(100) not null,
    authorized_scopes             varchar(1000) default NULL::character varying,
    attributes                    text,
    state                         varchar(500)  default NULL::character varying,
    authorization_code_value      text,
    authorization_code_issued_at  timestamp(6),
    authorization_code_expires_at timestamp(6),
    authorization_code_metadata   text,
    access_token_value            text,
    access_token_issued_at        timestamp(6),
    access_token_expires_at       timestamp(6),
    access_token_metadata         text,
    access_token_type             varchar(100)  default NULL::character varying,
    access_token_scopes           varchar(1000) default NULL::character varying,
    oidc_id_token_value           text,
    oidc_id_token_issued_at       timestamp(6),
    oidc_id_token_expires_at      timestamp(6),
    oidc_id_token_metadata        text,
    refresh_token_value           text,
    refresh_token_issued_at       timestamp(6),
    refresh_token_expires_at      timestamp(6),
    refresh_token_metadata        text,
    user_code_value               text,
    user_code_issued_at           timestamp(6),
    user_code_expires_at          timestamp(6),
    user_code_metadata            text,
    device_code_value             text,
    device_code_issued_at         timestamp(6),
    device_code_expires_at        timestamp(6),
    device_code_metadata          text
);

comment on table oauth2_authorization is 'authorization 表';

comment on column oauth2_authorization.id is '主键';

comment on column oauth2_authorization.registered_client_id is '客户端ID';

comment on column oauth2_authorization.principal_name is '认证账号';

comment on column oauth2_authorization.authorization_grant_type is '授权类型';

comment on column oauth2_authorization.attributes is '参数';

comment on column oauth2_authorization.state is '状态';

comment on column oauth2_authorization.authorization_code_value is 'authorization code';

comment on column oauth2_authorization.authorization_code_issued_at is 'authorization code生效时间';

comment on column oauth2_authorization.authorization_code_expires_at is 'authorization code失效时间';

comment on column oauth2_authorization.authorization_code_metadata is 'authorization code 元数据';

comment on column oauth2_authorization.access_token_value is 'access token';

comment on column oauth2_authorization.access_token_issued_at is 'access token 生效时间';

comment on column oauth2_authorization.access_token_expires_at is 'access_token 失效时间';

comment on column oauth2_authorization.access_token_metadata is 'access token元数据';

comment on column oauth2_authorization.access_token_type is 'access token 类型';

comment on column oauth2_authorization.access_token_scopes is 'access token 域';

comment on column oauth2_authorization.oidc_id_token_value is 'oidc token';

comment on column oauth2_authorization.oidc_id_token_issued_at is 'oidc token 生效时间';

comment on column oauth2_authorization.oidc_id_token_expires_at is 'oidc token 失效时间';

comment on column oauth2_authorization.oidc_id_token_metadata is 'oidc token 元数据';

comment on column oauth2_authorization.refresh_token_value is 'refresh token';

comment on column oauth2_authorization.refresh_token_issued_at is 'refresh token 生效时间';

comment on column oauth2_authorization.refresh_token_expires_at is 'refresh token 失效时间';

comment on column oauth2_authorization.refresh_token_metadata is 'refresh token 元数据';

alter table oauth2_authorization
    owner to postgres;

create table oauth2_authorization_consent
(
    registered_client_id varchar(100)  not null,
    principal_name       varchar(200)  not null,
    authorities          varchar(1000) not null,
    primary key (registered_client_id, principal_name)
);

comment on table oauth2_authorization_consent is 'consent 表';

comment on column oauth2_authorization_consent.registered_client_id is '客户端ID';

comment on column oauth2_authorization_consent.principal_name is '认证账号';

comment on column oauth2_authorization_consent.authorities is '权限';

alter table oauth2_authorization_consent
    owner to postgres;

create table oauth2_registered_client
(
    id                            varchar(100)                            not null
        primary key,
    client_id                     varchar(100)                            not null,
    client_id_issued_at           timestamp(6)  default CURRENT_TIMESTAMP not null,
    client_secret                 varchar(200)  default NULL::character varying,
    client_secret_expires_at      timestamp(6),
    client_name                   varchar(200)                            not null,
    client_authentication_methods varchar(1000)                           not null,
    authorization_grant_types     varchar(1000)                           not null,
    redirect_uris                 varchar(1000) default NULL::character varying,
    post_logout_redirect_uris     varchar(1000) default NULL::character varying,
    scopes                        varchar(1000)                           not null,
    client_settings               varchar(2000)                           not null,
    token_settings                varchar(2000)                           not null
);

comment on table oauth2_registered_client is 'client 表';

comment on column oauth2_registered_client.id is '主键';

comment on column oauth2_registered_client.client_id is '客户端ID';

comment on column oauth2_registered_client.client_id_issued_at is '生效时间';

comment on column oauth2_registered_client.client_secret is '密钥';

comment on column oauth2_registered_client.client_secret_expires_at is '密钥失效时间';

comment on column oauth2_registered_client.client_name is '名称';

comment on column oauth2_registered_client.client_authentication_methods is '认证方法';

comment on column oauth2_registered_client.authorization_grant_types is '授权方式';

comment on column oauth2_registered_client.redirect_uris is '跳转连接';

comment on column oauth2_registered_client.post_logout_redirect_uris is '后置退出跳转连接';

comment on column oauth2_registered_client.scopes is '作用域';

comment on column oauth2_registered_client.client_settings is '客户端设置';

comment on column oauth2_registered_client.token_settings is 'token 设置';

alter table oauth2_registered_client
    owner to postgres;

create table operation_logs
(
    id                 bigint generated always as identity
        primary key,
    module             varchar(255),
    params             varchar(255),
    browser            varchar(50),
    ip                 inet,
    action             varchar(255),
    body               varchar(255),
    user_agent         varchar(255),
    referer            varchar(255),
    session_id         varchar(255),
    device_type        varchar(20),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    status_code        integer
);

comment on table operation_logs is '访问日志表';

comment on column operation_logs.id is '主键';

comment on column operation_logs.module is '模块';

comment on column operation_logs.params is '参数';

comment on column operation_logs.browser is '浏览器';

comment on column operation_logs.ip is 'IP地址';

comment on column operation_logs.action is '操作';

comment on column operation_logs.body is '内容';

comment on column operation_logs.user_agent is '用户代理信息';

comment on column operation_logs.referer is '来源页面';

comment on column operation_logs.session_id is '会话标识符';

comment on column operation_logs.device_type is '设备类型';

comment on column operation_logs.enabled is '是否启用';

comment on column operation_logs.created_by is '创建者';

comment on column operation_logs.created_date is '创建时间';

comment on column operation_logs.last_modified_by is '最后修改者';

comment on column operation_logs.last_modified_date is '最后修改时间';

comment on column operation_logs.status_code is '状态码';

alter table operation_logs
    owner to postgres;

create table persistent_logins
(
    username  varchar(64)  not null,
    series    varchar(64)  not null
        primary key,
    token     varchar(64)  not null,
    last_used timestamp(6) not null
);

comment on table persistent_logins is '持久化登录表';

comment on column persistent_logins.username is '用户名';

comment on column persistent_logins.series is '系列';

comment on column persistent_logins.token is '令牌';

comment on column persistent_logins.last_used is '最后使用时间';

alter table persistent_logins
    owner to postgres;

create table privileges
(
    id                 bigint generated by default as identity
        primary key,
    superior_id        bigint,
    name               varchar(255)                           not null,
    path               varchar(255),
    redirect           varchar(255),
    component          varchar(255),
    icon               varchar(255),
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table privileges is '权限表';

comment on column privileges.id is '主键';

comment on column privileges.superior_id is '上级ID';

comment on column privileges.name is '名称';

comment on column privileges.path is '路径';

comment on column privileges.redirect is '跳转路径';

comment on column privileges.component is '组件路径';

comment on column privileges.icon is '图标';

comment on column privileges.description is '描述';

comment on column privileges.enabled is '是否启用';

comment on column privileges.created_by is '创建者';

comment on column privileges.created_date is '创建时间';

comment on column privileges.last_modified_by is '最后修改者';

comment on column privileges.last_modified_date is '最后修改时间';

alter table privileges
    owner to postgres;

create table group_privileges
(
    id           bigint generated by default as identity
        primary key,
    group_id     bigint not null
        constraint fk_group_privileges_group_id
            references groups,
    privilege_id bigint not null
        constraint fk_group_privileges_privilege_id
            references privileges
);

comment on table group_privileges is '分组权限关系表';

comment on column group_privileges.id is '主键';

comment on column group_privileges.group_id is '分组ID';

comment on column group_privileges.privilege_id is '权限ID';

alter table group_privileges
    owner to postgres;

create table group_privilege_actions
(
    id                 bigint generated by default as identity
        primary key,
    group_privilege_id bigint       not null
        constraint fk_group_privilege_actions_id
            references group_privileges,
    actions            varchar(255) not null
);

comment on table group_privilege_actions is '分组权限操作表';

comment on column group_privilege_actions.id is '主键';

comment on column group_privilege_actions.group_privilege_id is '分组权限ID';

comment on column group_privilege_actions.actions is '操作';

alter table group_privilege_actions
    owner to postgres;

create table privilege_actions
(
    id           bigint generated always as identity
        primary key,
    privilege_id bigint       not null
        constraint fk_privileges_action
            references privileges,
    actions      varchar(255) not null
);

comment on table privilege_actions is '权限操作表';

comment on column privilege_actions.id is '主键';

comment on column privilege_actions.privilege_id is '权限ID';

comment on column privilege_actions.actions is '操作';

alter table privilege_actions
    owner to postgres;

create table regions
(
    id                 bigint generated by default as identity
        primary key,
    name               varchar(50)                            not null,
    superior_id        bigint,
    area_code          varchar(255),
    postal_code        varchar(255),
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table regions is '地区表';

comment on column regions.id is '主键';

comment on column regions.name is '名称';

comment on column regions.superior_id is '上级ID';

comment on column regions.area_code is '区号';

comment on column regions.postal_code is '邮政编码';

comment on column regions.description is '描述';

comment on column regions.enabled is '是否启用';

comment on column regions.created_by is '创建者';

comment on column regions.created_date is '创建时间';

comment on column regions.last_modified_by is '最后修改者';

comment on column regions.last_modified_date is '最后修改时间';

alter table regions
    owner to postgres;

create table roles
(
    id                 bigint generated by default as identity
        primary key,
    name               varchar(255)                           not null
        constraint uk_roles_name
            unique,
    description        varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table roles is '角色表';

comment on column roles.id is '主键';

comment on column roles.name is '名称';

comment on column roles.description is '描述';

comment on column roles.enabled is '是否启用';

comment on column roles.created_by is '创建者';

comment on column roles.created_date is '创建时间';

comment on column roles.last_modified_by is '最后修改者';

comment on column roles.last_modified_date is '最后修改时间';

alter table roles
    owner to postgres;

create table group_roles
(
    id       bigint generated always as identity
        primary key,
    group_id bigint not null
        constraint fk_group_roles_groups
            references groups,
    role_id  bigint not null
        constraint fk_group_roles_roles
            references roles
);

comment on table group_roles is '用户组角色关系表';

comment on column group_roles.id is '主键';

comment on column group_roles.group_id is '用户组ID';

comment on column group_roles.role_id is '角色ID';

alter table group_roles
    owner to postgres;

create table role_privileges
(
    id           bigint generated by default as identity
        constraint role_privileges_pkey1
            primary key,
    role_id      bigint not null
        constraint fk_role_privileges_role_id
            references roles,
    privilege_id bigint not null
        constraint fk_role_privileges_privilege_id
            references privileges,
    constraint ux_role_privileges
        unique (role_id, privilege_id)
);

comment on table role_privileges is '角色权限关系表';

comment on column role_privileges.id is '主键';

comment on column role_privileges.role_id is '角色ID';

comment on column role_privileges.privilege_id is '权限ID';

alter table role_privileges
    owner to postgres;

create table role_privilege_actions
(
    id                bigint generated always as identity
        primary key,
    role_privilege_id bigint       not null
        constraint fk_role_privilege_actions_id
            references role_privileges,
    actions           varchar(255) not null,
    constraint ux_role_privilege_actions
        unique (role_privilege_id, actions)
);

comment on table role_privilege_actions is '角色权限操作表';

comment on column role_privilege_actions.id is '主键';

comment on column role_privilege_actions.role_privilege_id is '角色权限ID';

comment on column role_privilege_actions.actions is '操作';

alter table role_privilege_actions
    owner to postgres;

create table samples
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(255)                           not null,
    suffix             varchar(255)                           not null,
    body               text,
    type               varchar(255),
    category           varchar(255)                           not null,
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table samples is '样板表';

comment on column samples.id is '主键，自动生成';

comment on column samples.name is '表名称';

comment on column samples.suffix is '文件后缀';

comment on column samples.type is '类型';

comment on column samples.category is '类别';

comment on column samples.enabled is '是否启用';

comment on column samples.created_by is '创建者';

comment on column samples.created_date is '创建时间';

comment on column samples.last_modified_by is '最后修改者';

comment on column samples.last_modified_date is '最后修改时间';

alter table samples
    owner to postgres;

create table scheduler_logs
(
    id                 bigint generated always as identity
        constraint scheduler_log_pkey
            primary key,
    name               varchar(255)                           not null,
    start_time         timestamp(6) with time zone,
    executed_times     integer      default 0,
    next_execute_time  timestamp(6) with time zone,
    status             varchar(255)
        constraint scheduler_log_status_check
            check ((status)::text = ANY
                   (ARRAY [('PENDING'::character varying)::text, ('RUNNING'::character varying)::text, ('SUCCESS'::character varying)::text, ('FAILED'::character varying)::text, ('CANCELED'::character varying)::text])),
    record             varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6)
);

comment on table scheduler_logs is '定时任务日志表';

comment on column scheduler_logs.id is '主键，自增';

comment on column scheduler_logs.name is '名称';

comment on column scheduler_logs.start_time is '开始时间';

comment on column scheduler_logs.executed_times is '执行时长';

comment on column scheduler_logs.next_execute_time is '下次执行时间';

comment on column scheduler_logs.status is '状态';

comment on column scheduler_logs.record is '记录';

comment on column scheduler_logs.enabled is '是否启用';

comment on column scheduler_logs.created_by is '创建者';

comment on column scheduler_logs.created_date is '创建时间';

comment on column scheduler_logs.last_modified_by is '最后修改者';

comment on column scheduler_logs.last_modified_date is '最后修改时间';

alter table scheduler_logs
    owner to postgres;

create table schemas
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(255),
    package_name       varchar(255),
    prefix             varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    connection_id      bigint                                 not null
        constraint fk_schemas_connection_id
            references connections
);

comment on table schemas is 'schema配置表';

comment on column schemas.id is '主键，自动生成';

comment on column schemas.name is '表名称';

comment on column schemas.package_name is '包名';

comment on column schemas.prefix is '前缀';

comment on column schemas.enabled is '是否启用';

comment on column schemas.created_by is '创建者';

comment on column schemas.created_date is '创建时间';

comment on column schemas.last_modified_by is '最后修改者';

comment on column schemas.last_modified_date is '最后修改时间';

comment on column schemas.connection_id is 'connection主键';

alter table schemas
    owner to postgres;

create table schema_samples
(
    id        bigint generated always as identity
        constraint schema_templates_pkey
            primary key,
    schema_id bigint not null
        constraint fk_shcemas_samples_schema_id
            references schemas,
    sample_id bigint not null
        constraint fk_shcemas_samples_sample_id
            references samples
);

comment on table schema_samples is 'schema 样板关联表';

comment on column schema_samples.id is '主键';

comment on column schema_samples.schema_id is 'schema主键';

comment on column schema_samples.sample_id is 'sample主键';

alter table schema_samples
    owner to postgres;

create table scripts
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(255),
    icon               varchar(255),
    version            integer,
    body               varchar(255),
    enabled            boolean      default true              not null,
    created_by         varchar(255),
    created_date       timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    type               varchar(255)
);

comment on table scripts is '脚本配置表';

comment on column scripts.id is '主键，自动生成';

comment on column scripts.name is '名称';

comment on column scripts.icon is '图标';

comment on column scripts.version is '版本';

comment on column scripts.enabled is '是否启用';

comment on column scripts.created_by is '创建者';

comment on column scripts.created_date is '创建时间';

comment on column scripts.last_modified_by is '最后修改者';

comment on column scripts.last_modified_date is '最后修改时间';

comment on column scripts.type is '类型';

alter table scripts
    owner to postgres;

create table users
(
    id                      bigint generated always as identity
        primary key,
    username                varchar(255)                           not null
        unique,
    password                varchar(255)                           not null,
    email                   varchar(255),
    avatar                  varchar(255),
    enabled                 boolean      default true              not null,
    account_non_locked      boolean,
    created_by              varchar(255),
    created_date            timestamp(6) default CURRENT_TIMESTAMP not null,
    last_modified_by        varchar(255),
    last_modified_date      timestamp(6),
    full_name               varchar(255),
    account_non_expired     boolean,
    credentials_non_expired boolean
);

comment on table users is '用户表';

comment on column users.id is '主键';

comment on column users.username is '用户名';

comment on column users.password is '密码';

comment on column users.email is '邮箱';

comment on column users.avatar is '头像';

comment on column users.enabled is '是否启用';

comment on column users.account_non_locked is '是否未锁定';

comment on column users.created_by is '创建者';

comment on column users.created_date is '创建时间';

comment on column users.last_modified_by is '最后修改者';

comment on column users.last_modified_date is '最后修改时间';

comment on column users.full_name is '姓名';

comment on column users.account_non_expired is '账号是否有效';

comment on column users.credentials_non_expired is '密码是否有效';

alter table users
    owner to postgres;

create table group_members
(
    id       bigint generated always as identity
        primary key,
    group_id bigint      not null
        constraint fk_group_members_group_id
            references groups,
    username varchar(50) not null
        constraint fk_group_members_username
            references users (username)
);

comment on table group_members is '用户组成员关系表';

comment on column group_members.id is '主键';

comment on column group_members.group_id is '用户组ID';

comment on column group_members.username is '用户名';

alter table group_members
    owner to postgres;

create table role_members
(
    id       bigint generated always as identity
        primary key,
    role_id  bigint       not null
        constraint fk_role_members_roles
            references roles,
    username varchar(255) not null
        constraint fk_role_members_users
            references users (username)
);

comment on table role_members is '角色成员关系表';

comment on column role_members.id is '主键';

comment on column role_members.role_id is '角色ID';

comment on column role_members.username is '用户名';

alter table role_members
    owner to postgres;

create table user_privileges
(
    id           bigint generated by default as identity
        primary key,
    username     varchar(255) not null
        constraint fk_user_privileges_username
            references users (username),
    privilege_id bigint       not null
        constraint fk_user_privileges_privilege_id
            references privileges
);

comment on table user_privileges is '用户权限关系表';

comment on column user_privileges.id is '主键';

comment on column user_privileges.username is '账号';

comment on column user_privileges.privilege_id is '权限ID';

alter table user_privileges
    owner to postgres;

create table user_privilege_actions
(
    id                bigint generated by default as identity
        primary key,
    user_privilege_id bigint       not null
        constraint fk_user_privilege_actions_id
            references user_privileges,
    actions           varchar(255) not null
);

comment on table user_privilege_actions is '用户权限操作表';

comment on column user_privilege_actions.id is '主键';

comment on column user_privilege_actions.user_privilege_id is '用户权限ID';

comment on column user_privilege_actions.actions is '操作';

alter table user_privilege_actions
    owner to postgres;

create table comments
(
    id                 bigint not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6) with time zone,
    body               varchar(255),
    post_id            bigint not null,
    replier            bigint
);

alter table comments
    owner to postgres;

create table posts
(
    id                 bigint       not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6) with time zone,
    body               varchar(255),
    published_at       timestamp(6) with time zone,
    summary            varchar(255),
    title              varchar(255) not null
        constraint ukmchce1gm7f6otpphxd6ixsdps
            unique
);

alter table posts
    owner to postgres;

create table post_tags
(
    post_id bigint not null
        constraint fkkifam22p4s1nm3bkmp1igcn5w
            references posts,
    tags    varchar(255)
);

alter table post_tags
    owner to postgres;

