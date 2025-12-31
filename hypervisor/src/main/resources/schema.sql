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
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    url                character varying(255),                                            -- 接口
    http_method        character varying(255),                                            -- http方法
    params             character varying(255),                                            -- 参数
    body               character varying(255),                                            -- 请求体
    ip                 inet,                                                              -- IP地址
    status_code        integer,                                                           -- HTTP状态码
    duration           bigint,                                                            -- 响应时长
    response           character varying(255),                                            -- 响应
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
);
comment on table access_logs is '访问日志表';
comment on column access_logs.id is '主键';
comment on column access_logs.url is '接口';
comment on column access_logs.http_method is 'http方法';
comment on column access_logs.params is '参数';
comment on column access_logs.body is '请求体';
comment on column access_logs.ip is 'IP地址';
comment on column access_logs.status_code is 'HTTP状态码';
comment on column access_logs.duration is '响应时长';
comment on column access_logs.response is '响应';
comment on column access_logs.enabled is '是否启用';
comment on column access_logs.created_by is '创建者';
comment on column access_logs.created_date is '创建时间';
comment on column access_logs.last_modified_by is '最后修改者';
comment on column access_logs.last_modified_date is '最后修改时间';

create table audit_logs
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自增
    action             character varying(255)         not null,                           -- 操作
    resource           character varying(255)         not null,                           -- 资源
    old_value          character varying(255),                                            -- 旧值（JSON 格式）
    new_value          character varying(255),                                            -- 新值（JSON 格式）
    ip                 inet                           not null,                           -- IP 地址
    status_code        integer                        not null,                           -- 状态码
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    duration           bigint                         not null,                           -- 操作时长
    target_id          bigint                                                             -- 对象主键
);
comment on table audit_logs is '审计日志表';
comment on column audit_logs.id is '主键，自增';
comment on column audit_logs.action is '操作';
comment on column audit_logs.resource is '资源';
comment on column audit_logs.old_value is '旧值（JSON 格式）';
comment on column audit_logs.new_value is '新值（JSON 格式）';
comment on column audit_logs.ip is 'IP 地址';
comment on column audit_logs.status_code is '状态码';
comment on column audit_logs.enabled is '是否启用';
comment on column audit_logs.created_by is '创建者';
comment on column audit_logs.created_date is '创建时间';
comment on column audit_logs.last_modified_by is '最后修改者';
comment on column audit_logs.last_modified_date is '最后修改时间';
comment on column audit_logs.duration is '操作时长';
comment on column audit_logs.target_id is '对象主键';

create table authorities
(
    tableoid  oid                   not null,
    cmax      cid                   not null,
    xmax      xid                   not null,
    cmin      cid                   not null,
    xmin      xid                   not null,
    ctid      tid                   not null,
    id        bigint primary key    not null, -- 主键
    username  character varying(64) not null, -- 用户名
    authority character varying(64) not null, -- 权限
    foreign key (username) references users (username)
        match simple on update no action on delete no action
);
create unique index ix_auth_username on authorities using btree (username, authority);
comment on table authorities is '用户权限表';
comment on column authorities.id is '主键';
comment on column authorities.username is '用户名';
comment on column authorities.authority is '权限';

create table comments
(
    tableoid           oid                not null,
    cmax               cid                not null,
    xmax               xid                not null,
    cmin               cid                not null,
    xmin               xid                not null,
    ctid               tid                not null,
    id                 bigint primary key not null,
    created_by         character varying(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   character varying(255),
    last_modified_date timestamp(6) with time zone,
    body               character varying(255),
    post_id            bigint             not null,
    superior_id        bigint,
    foreign key (post_id) references posts (id)
        match simple on update no action on delete no action
);

create table connections
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    host               character varying(255),                                            -- 主机
    port               integer,                                                           -- 端口
    database           character varying(255),                                            -- 库名称
    username           character varying(255),                                            -- 账号
    password           character varying(255),                                            -- 密码
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    params             character varying(255),                                            -- 参数
    type               character varying(255)                                             -- 类型：POSTGRESQL-postgresql, MYSQL-mysql
);
comment on table connections is '数据库连接表';
comment on column connections.id is '主键';
comment on column connections.host is '主机';
comment on column connections.port is '端口';
comment on column connections.database is '库名称';
comment on column connections.username is '账号';
comment on column connections.password is '密码';
comment on column connections.enabled is '是否启用';
comment on column connections.created_by is '创建者';
comment on column connections.created_date is '创建时间';
comment on column connections.last_modified_by is '最后修改者';
comment on column connections.last_modified_date is '最后修改时间';
comment on column connections.params is '参数';
comment on column connections.type is '类型：POSTGRESQL-postgresql, MYSQL-mysql';

create table dictionaries
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    name               character varying(50)          not null,                           -- 名称
    superior_id        bigint,                                                            -- 上级ID
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
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

create table fields
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自动生成
    name               character varying(255),                                            -- 属性命
    data_type          character varying(255),                                            -- 字段类型
    length             integer,                                                           -- 字段长度
    field_type         character varying(255),                                            -- 属性类型
    form_type          character varying(255),                                            -- 表单类型
    ts_type            character varying(255),                                            -- ts类型
    nullable           boolean,                                                           -- 是否可为空
    is_unique          boolean,                                                           -- 是否唯一
    queryable          boolean,                                                           -- 是否可查询
    editable           boolean,                                                           -- 是否可编辑
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    query_type         character varying(255),                                            -- 查询类型
    sortable           boolean,                                                           -- 是否可排序
    table_name         character varying(255)         not null,                           -- 表名称
    schema_id          bigint                         not null,                           -- schema主键
    foreign key (schema_id) references schemes (id)
        match simple on update no action on delete no action
);
comment on table fields is '属性配置表';
comment on column fields.id is '主键，自动生成';
comment on column fields.name is '属性命';
comment on column fields.data_type is '字段类型';
comment on column fields.length is '字段长度';
comment on column fields.field_type is '属性类型';
comment on column fields.form_type is '表单类型';
comment on column fields.ts_type is 'ts类型';
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
comment on column fields.table_name is '表名称';
comment on column fields.schema_id is 'schema主键';

create table file_records
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    name               character varying(50)          not null,                           -- 名称
    content_type       character varying(255),                                            -- 类型
    path               character varying(255),                                            -- 路径
    size               bigint,                                                            -- 大小
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    directory          boolean,                                                           -- 是否目录
    regular_file       boolean,                                                           -- 是否文件
    symbolic_link      boolean,                                                           -- 是否软链
    extension          character varying(255),                                            -- 扩展类型
    superior_id        bigint                                                             -- 上级ID
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

create table fragments
(
    tableoid           oid                not null,
    cmax               cid                not null,
    xmax               xid                not null,
    cmin               cid                not null,
    xmin               xid                not null,
    ctid               tid                not null,
    id                 bigint primary key not null, -- 主键
    name               character varying(255),      -- 名称
    code               character varying(255),      -- 编码
    body               text,                        -- 内容
    language           character varying(255),      -- 语言
    sequence           character varying(255),      -- 序号
    version            integer,                     -- 版本号
    enabled            boolean,                     -- 是否启用
    created_by         character varying(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   character varying(255),
    last_modified_date timestamp(6) with time zone,
    imports            text
);
comment on column fragments.id is '主键';
comment on column fragments.name is '名称';
comment on column fragments.code is '编码';
comment on column fragments.body is '内容';
comment on column fragments.language is '语言';
comment on column fragments.sequence is '序号';
comment on column fragments.version is '版本号';
comment on column fragments.enabled is '是否启用';

create table group_authorities
(
    tableoid  oid                   not null,
    cmax      cid                   not null,
    xmax      xid                   not null,
    cmin      cid                   not null,
    xmin      xid                   not null,
    ctid      tid                   not null,
    id        bigint primary key    not null, -- 主键
    group_id  bigint                not null, -- 用户组ID
    authority character varying(50) not null, -- 权限
    foreign key (group_id) references groups (id)
        match simple on update no action on delete no action
);
comment on table group_authorities is '用户组权限关系表';
comment on column group_authorities.id is '主键';
comment on column group_authorities.group_id is '用户组ID';
comment on column group_authorities.authority is '权限';

create table group_members
(
    tableoid oid                   not null,
    cmax     cid                   not null,
    xmax     xid                   not null,
    cmin     cid                   not null,
    xmin     xid                   not null,
    ctid     tid                   not null,
    id       bigint primary key    not null, -- 主键
    group_id bigint                not null, -- 用户组ID
    username character varying(50) not null, -- 用户名
    foreign key (group_id) references groups (id)
        match simple on update no action on delete no action,
    foreign key (username) references users (username)
        match simple on update no action on delete no action
);
comment on table group_members is '用户组成员关系表';
comment on column group_members.id is '主键';
comment on column group_members.group_id is '用户组ID';
comment on column group_members.username is '用户名';

create table group_privilege_actions
(
    tableoid           oid                    not null,
    cmax               cid                    not null,
    xmax               xid                    not null,
    cmin               cid                    not null,
    xmin               xid                    not null,
    ctid               tid                    not null,
    id                 bigint primary key     not null, -- 主键
    group_privilege_id bigint                 not null, -- 分组权限ID
    actions            character varying(255) not null, -- 操作
    foreign key (group_privilege_id) references group_privileges (id)
        match simple on update no action on delete no action
);
comment on table group_privilege_actions is '分组权限操作表';
comment on column group_privilege_actions.id is '主键';
comment on column group_privilege_actions.group_privilege_id is '分组权限ID';
comment on column group_privilege_actions.actions is '操作';

create table group_privileges
(
    tableoid     oid                not null,
    cmax         cid                not null,
    xmax         xid                not null,
    cmin         cid                not null,
    xmin         xid                not null,
    ctid         tid                not null,
    id           bigint primary key not null, -- 主键
    group_id     bigint             not null, -- 分组ID
    privilege_id bigint             not null, -- 权限ID
    foreign key (group_id) references groups (id)
        match simple on update no action on delete no action,
    foreign key (privilege_id) references privileges (id)
        match simple on update no action on delete no action
);
comment on table group_privileges is '分组权限关系表';
comment on column group_privileges.id is '主键';
comment on column group_privileges.group_id is '分组ID';
comment on column group_privileges.privilege_id is '权限ID';

create table group_roles
(
    tableoid oid                not null,
    cmax     cid                not null,
    xmax     xid                not null,
    cmin     cid                not null,
    xmin     xid                not null,
    ctid     tid                not null,
    id       bigint primary key not null, -- 主键
    group_id bigint             not null, -- 用户组ID
    role_id  bigint             not null, -- 角色ID
    foreign key (group_id) references groups (id)
        match simple on update no action on delete no action,
    foreign key (role_id) references roles (id)
        match simple on update no action on delete no action
);
comment on table group_roles is '用户组角色关系表';
comment on column group_roles.id is '主键';
comment on column group_roles.group_id is '用户组ID';
comment on column group_roles.role_id is '角色ID';

create table groups
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    group_name         character varying(255)         not null,                           -- 名称
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    superior_id        bigint                                                             -- 上级ID
);
create unique index uk_groups_group_name on groups using btree (group_name);
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

create table messages
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    title              character varying(255)         not null,                           -- 标题
    content            text,                                                              -- 内容
    unread             boolean                        not null default false,             -- 是否未读
    receiver           character varying(255)         not null,                           -- 接收者
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255)         not null,                           -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    body               character varying(255)
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

create table module_fragments
(
    tableoid    oid                not null,
    cmax        cid                not null,
    xmax        xid                not null,
    cmin        cid                not null,
    xmin        xid                not null,
    ctid        tid                not null,
    id          bigint primary key not null, -- 主键
    module_id   bigint,                      -- module ID
    fragment_id bigint,                      -- fragment ID
    foreign key (fragment_id) references fragments (id)
        match simple on update no action on delete no action,
    foreign key (module_id) references modules (id)
        match simple on update no action on delete no action
);
comment on column module_fragments.id is '主键';
comment on column module_fragments.module_id is 'module ID';
comment on column module_fragments.fragment_id is 'fragment ID';

create table modules
(
    tableoid           oid                not null,
    cmax               cid                not null,
    xmax               xid                not null,
    cmin               cid                not null,
    xmin               xid                not null,
    ctid               tid                not null,
    id                 bigint primary key not null, -- 主键
    name               character varying(255),      -- 名称
    sequence           smallint,                    -- 序号
    body               text,
    enabled            boolean,
    created_by         character varying(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   character varying(255),
    last_modified_date timestamp(6) with time zone,
    module_config      character varying(255),
    version            integer            not null,
    description        character varying(255)
);
comment on column modules.id is '主键';
comment on column modules.name is '名称';
comment on column modules.sequence is '序号';

create table oauth2_authorization
(
    tableoid                      oid                                not null,
    cmax                          cid                                not null,
    xmax                          xid                                not null,
    cmin                          cid                                not null,
    xmin                          xid                                not null,
    ctid                          tid                                not null,
    id                            character varying(100) primary key not null, -- 主键
    registered_client_id          character varying(100)             not null, -- 客户端ID
    principal_name                character varying(200)             not null, -- 认证账号
    authorization_grant_type      character varying(100)             not null, -- 授权类型
    authorized_scopes             character varying(1000) default NULL,
    attributes                    text,                                        -- 参数
    state                         character varying(500)  default NULL,        -- 状态
    authorization_code_value      text,                                        -- authorization code
    authorization_code_issued_at  timestamp(6) without time zone,              -- authorization code生效时间
    authorization_code_expires_at timestamp(6) without time zone,              -- authorization code失效时间
    authorization_code_metadata   text,                                        -- authorization code 元数据
    access_token_value            text,                                        -- access token
    access_token_issued_at        timestamp(6) without time zone,              -- access token 生效时间
    access_token_expires_at       timestamp(6) without time zone,              -- access_token 失效时间
    access_token_metadata         text,                                        -- access token元数据
    access_token_type             character varying(100)  default NULL,        -- access token 类型
    access_token_scopes           character varying(1000) default NULL,        -- access token 域
    oidc_id_token_value           text,                                        -- oidc token
    oidc_id_token_issued_at       timestamp(6) without time zone,              -- oidc token 生效时间
    oidc_id_token_expires_at      timestamp(6) without time zone,              -- oidc token 失效时间
    oidc_id_token_metadata        text,                                        -- oidc token 元数据
    refresh_token_value           text,                                        -- refresh token
    refresh_token_issued_at       timestamp(6) without time zone,              -- refresh token 生效时间
    refresh_token_expires_at      timestamp(6) without time zone,              -- refresh token 失效时间
    refresh_token_metadata        text,                                        -- refresh token 元数据
    user_code_value               text,
    user_code_issued_at           timestamp(6) without time zone,
    user_code_expires_at          timestamp(6) without time zone,
    user_code_metadata            text,
    device_code_value             text,
    device_code_issued_at         timestamp(6) without time zone,
    device_code_expires_at        timestamp(6) without time zone,
    device_code_metadata          text,
    foreign key (registered_client_id) references oauth2_registered_client (id)
        match simple on update no action on delete no action
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

create table oauth2_authorization_consent
(
    tableoid             oid                     not null,
    cmax                 cid                     not null,
    xmax                 xid                     not null,
    cmin                 cid                     not null,
    xmin                 xid                     not null,
    ctid                 tid                     not null,
    registered_client_id character varying(100)  not null, -- 客户端ID
    principal_name       character varying(200)  not null, -- 认证账号
    authorities          character varying(1000) not null, -- 权限
    primary key (registered_client_id, principal_name),
    foreign key (principal_name) references users (username)
        match simple on update no action on delete no action,
    foreign key (registered_client_id) references oauth2_registered_client (id)
        match simple on update no action on delete no action
);
comment on table oauth2_authorization_consent is 'consent 表';
comment on column oauth2_authorization_consent.registered_client_id is '客户端ID';
comment on column oauth2_authorization_consent.principal_name is '认证账号';
comment on column oauth2_authorization_consent.authorities is '权限';

create table oauth2_registered_client
(
    tableoid                      oid                                not null,
    cmax                          cid                                not null,
    xmax                          xid                                not null,
    cmin                          cid                                not null,
    xmin                          xid                                not null,
    ctid                          tid                                not null,
    id                            character varying(100) primary key not null,                           -- 主键
    client_id                     character varying(100)             not null,                           -- 客户端ID
    client_id_issued_at           timestamp(6) without time zone     not null default CURRENT_TIMESTAMP, -- 生效时间
    client_secret                 character varying(200)                      default NULL,              -- 密钥
    client_secret_expires_at      timestamp(6) without time zone,                                        -- 密钥失效时间
    client_name                   character varying(200)             not null,                           -- 名称
    client_authentication_methods character varying(1000)            not null,                           -- 认证方法
    authorization_grant_types     character varying(1000)            not null,                           -- 授权方式
    redirect_uris                 character varying(1000)                     default NULL,              -- 跳转连接
    post_logout_redirect_uris     character varying(1000)                     default NULL,              -- 后置退出跳转连接
    scopes                        character varying(1000)            not null,                           -- 作用域
    client_settings               character varying(2000)            not null,                           -- 客户端设置
    token_settings                character varying(2000)            not null                            -- token 设置
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

create table operation_logs
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    module             character varying(255),                                            -- 模块
    params             character varying(255),                                            -- 参数
    ip                 inet,                                                              -- IP地址
    action             character varying(255),                                            -- 操作
    body               character varying(255),                                            -- 内容
    user_agent         character varying(255),                                            -- 用户代理信息
    session_id         character varying(255),                                            -- 会话标识符
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    status_code        integer                                                            -- 状态码
);
comment on table operation_logs is '访问日志表';
comment on column operation_logs.id is '主键';
comment on column operation_logs.module is '模块';
comment on column operation_logs.params is '参数';
comment on column operation_logs.ip is 'IP地址';
comment on column operation_logs.action is '操作';
comment on column operation_logs.body is '内容';
comment on column operation_logs.user_agent is '用户代理信息';
comment on column operation_logs.session_id is '会话标识符';
comment on column operation_logs.enabled is '是否启用';
comment on column operation_logs.created_by is '创建者';
comment on column operation_logs.created_date is '创建时间';
comment on column operation_logs.last_modified_by is '最后修改者';
comment on column operation_logs.last_modified_date is '最后修改时间';
comment on column operation_logs.status_code is '状态码';

create table persistent_logins
(
    tableoid  oid                               not null,
    cmax      cid                               not null,
    xmax      xid                               not null,
    cmin      cid                               not null,
    xmin      xid                               not null,
    ctid      tid                               not null,
    username  character varying(64)             not null, -- 用户名
    series    character varying(64) primary key not null, -- 系列
    token     character varying(64)             not null, -- 令牌
    last_used timestamp(6) without time zone    not null, -- 最后使用时间
    foreign key (username) references users (username)
        match simple on update no action on delete no action
);
comment on table persistent_logins is '持久化登录表';
comment on column persistent_logins.username is '用户名';
comment on column persistent_logins.series is '系列';
comment on column persistent_logins.token is '令牌';
comment on column persistent_logins.last_used is '最后使用时间';

create table post_tags
(
    tableoid oid    not null,
    cmax     cid    not null,
    xmax     xid    not null,
    cmin     cid    not null,
    xmin     xid    not null,
    ctid     tid    not null,
    post_id  bigint not null,
    tags     character varying(255),
    foreign key (post_id) references posts (id)
        match simple on update no action on delete no action
);

create table posts
(
    tableoid           oid                    not null,
    cmax               cid                    not null,
    xmax               xid                    not null,
    cmin               cid                    not null,
    xmin               xid                    not null,
    ctid               tid                    not null,
    id                 bigint primary key     not null,
    created_by         character varying(255),
    created_date       timestamp(6) with time zone,
    last_modified_by   character varying(255),
    last_modified_date timestamp(6) with time zone,
    body               character varying(255),
    published_at       timestamp(6) with time zone,
    summary            character varying(255),
    title              character varying(255) not null
);
create unique index ukmchce1gm7f6otpphxd6ixsdps on posts using btree (title);

create table privilege_actions
(
    tableoid     oid                    not null,
    cmax         cid                    not null,
    xmax         xid                    not null,
    cmin         cid                    not null,
    xmin         xid                    not null,
    ctid         tid                    not null,
    id           bigint primary key     not null, -- 主键
    privilege_id bigint                 not null, -- 权限ID
    actions      character varying(255) not null, -- 操作
    foreign key (privilege_id) references privileges (id)
        match simple on update no action on delete no action
);
comment on table privilege_actions is '权限操作表';
comment on column privilege_actions.id is '主键';
comment on column privilege_actions.privilege_id is '权限ID';
comment on column privilege_actions.actions is '操作';

create table privileges
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    superior_id        bigint,                                                            -- 上级ID
    name               character varying(255)         not null,                           -- 名称
    path               character varying(255),                                            -- 路径
    redirect           character varying(255),                                            -- 跳转路径
    component          character varying(255),                                            -- 组件路径
    icon               character varying(255),                                            -- 图标
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
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

create table regions
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    name               character varying(50)          not null,                           -- 名称
    superior_id        bigint,                                                            -- 上级ID
    area_code          character varying(255),                                            -- 区号
    postal_code        character varying(255),                                            -- 邮政编码
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
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

create table role_members
(
    tableoid oid                    not null,
    cmax     cid                    not null,
    xmax     xid                    not null,
    cmin     cid                    not null,
    xmin     xid                    not null,
    ctid     tid                    not null,
    id       bigint primary key     not null, -- 主键
    role_id  bigint                 not null, -- 角色ID
    username character varying(255) not null, -- 用户名
    foreign key (role_id) references roles (id)
        match simple on update no action on delete no action,
    foreign key (username) references users (username)
        match simple on update no action on delete no action
);
comment on table role_members is '角色成员关系表';
comment on column role_members.id is '主键';
comment on column role_members.role_id is '角色ID';
comment on column role_members.username is '用户名';

create table role_privilege_actions
(
    tableoid          oid                    not null,
    cmax              cid                    not null,
    xmax              xid                    not null,
    cmin              cid                    not null,
    xmin              xid                    not null,
    ctid              tid                    not null,
    id                bigint primary key     not null, -- 主键
    role_privilege_id bigint                 not null, -- 角色权限ID
    actions           character varying(255) not null, -- 操作
    foreign key (role_privilege_id) references role_privileges (id)
        match simple on update no action on delete no action
);
create unique index ux_role_privilege_actions on role_privilege_actions using btree (role_privilege_id, actions);
comment on table role_privilege_actions is '角色权限操作表';
comment on column role_privilege_actions.id is '主键';
comment on column role_privilege_actions.role_privilege_id is '角色权限ID';
comment on column role_privilege_actions.actions is '操作';

create table role_privileges
(
    tableoid     oid                not null,
    cmax         cid                not null,
    xmax         xid                not null,
    cmin         cid                not null,
    xmin         xid                not null,
    ctid         tid                not null,
    id           bigint primary key not null, -- 主键
    role_id      bigint             not null, -- 角色ID
    privilege_id bigint             not null, -- 权限ID
    foreign key (privilege_id) references privileges (id)
        match simple on update no action on delete no action,
    foreign key (role_id) references roles (id)
        match simple on update no action on delete no action
);
create unique index ux_role_privileges on role_privileges using btree (role_id, privilege_id);
comment on table role_privileges is '角色权限关系表';
comment on column role_privileges.id is '主键';
comment on column role_privileges.role_id is '角色ID';
comment on column role_privileges.privilege_id is '权限ID';

create table roles
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键
    name               character varying(255)         not null,                           -- 名称
    description        character varying(255),                                            -- 描述
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
);
create unique index uk_roles_name on roles using btree (name);
comment on table roles is '角色表';
comment on column roles.id is '主键';
comment on column roles.name is '名称';
comment on column roles.description is '描述';
comment on column roles.enabled is '是否启用';
comment on column roles.created_by is '创建者';
comment on column roles.created_date is '创建时间';
comment on column roles.last_modified_by is '最后修改者';
comment on column roles.last_modified_date is '最后修改时间';

create table sample_modules
(
    tableoid  oid                not null,
    cmax      cid                not null,
    xmax      xid                not null,
    cmin      cid                not null,
    xmin      xid                not null,
    ctid      tid                not null,
    id        bigint primary key not null, -- 主键
    sample_id bigint,                      -- sample ID
    module_id bigint,                      -- module ID
    foreign key (module_id) references modules (id)
        match simple on update no action on delete no action,
    foreign key (sample_id) references samples (id)
        match simple on update no action on delete no action
);
comment on column sample_modules.id is '主键';
comment on column sample_modules.sample_id is 'sample ID';
comment on column sample_modules.module_id is 'module ID';

create table samples
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自动生成
    name               character varying(255)         not null,                           -- 名称
    type               character varying(255),                                            -- 类型：SINGLE（单文件） / TREE（多文件结构树）
    body               text,                                                              -- 内容
    module             character varying(255),                                            -- 模块
    file_path          character varying(255),                                            -- 【单文件模式】生成相对路径（支持占位符）
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    stack              character varying(255),                                            -- 技术栈
    version            integer                                                            -- 版本号
);
comment on table samples is '样板表';
comment on column samples.id is '主键，自动生成';
comment on column samples.name is '名称';
comment on column samples.type is '类型：SINGLE（单文件） / TREE（多文件结构树）';
comment on column samples.body is '内容';
comment on column samples.module is '模块';
comment on column samples.file_path is '【单文件模式】生成相对路径（支持占位符）';
comment on column samples.enabled is '是否启用';
comment on column samples.created_by is '创建者';
comment on column samples.created_date is '创建时间';
comment on column samples.last_modified_by is '最后修改者';
comment on column samples.last_modified_date is '最后修改时间';
comment on column samples.stack is '技术栈';
comment on column samples.version is '版本号';

create table scheduler_logs
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自增
    name               character varying(255)         not null,                           -- 名称
    start_time         timestamp(6) with time zone,                                       -- 开始时间
    duration           bigint                                  default 0,                 -- 执行时长
    next_execute_time  timestamp(6) with time zone,                                       -- 下次执行时间
    status             character varying(255),                                            -- 状态
    record             character varying(255),                                            -- 记录
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone                                     -- 最后修改时间
);
comment on table scheduler_logs is '定时任务日志表';
comment on column scheduler_logs.id is '主键，自增';
comment on column scheduler_logs.name is '名称';
comment on column scheduler_logs.start_time is '开始时间';
comment on column scheduler_logs.duration is '执行时长';
comment on column scheduler_logs.next_execute_time is '下次执行时间';
comment on column scheduler_logs.status is '状态';
comment on column scheduler_logs.record is '记录';
comment on column scheduler_logs.enabled is '是否启用';
comment on column scheduler_logs.created_by is '创建者';
comment on column scheduler_logs.created_date is '创建时间';
comment on column scheduler_logs.last_modified_by is '最后修改者';
comment on column scheduler_logs.last_modified_date is '最后修改时间';

create table scheme_samples
(
    tableoid  oid                not null,
    cmax      cid                not null,
    xmax      xid                not null,
    cmin      cid                not null,
    xmin      xid                not null,
    ctid      tid                not null,
    id        bigint primary key not null, -- 主键
    scheme_id bigint             not null, -- scheme主键
    sample_id bigint             not null, -- sample主键
    foreign key (sample_id) references samples (id)
        match simple on update no action on delete no action,
    foreign key (scheme_id) references schemes (id)
        match simple on update no action on delete no action
);
comment on table scheme_samples is 'scheme 样板关联表';
comment on column scheme_samples.id is '主键';
comment on column scheme_samples.scheme_id is 'scheme主键';
comment on column scheme_samples.sample_id is 'sample主键';

create table scheme_tables
(
    tableoid  oid                not null,
    cmax      cid                not null,
    xmax      xid                not null,
    cmin      cid                not null,
    xmin      xid                not null,
    ctid      tid                not null,
    scheme_id bigint             not null, -- scheme ID
    tables    character varying(255),      -- 表名称
    id        bigint primary key not null, -- 主键
    foreign key (scheme_id) references schemes (id)
        match simple on update no action on delete no action
);
comment on column scheme_tables.scheme_id is 'scheme ID';
comment on column scheme_tables.tables is '表名称';
comment on column scheme_tables.id is '主键';

create table schemes
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自动生成
    module             character varying(255),                                            -- 模块名
    package_name       character varying(255),                                            -- 包名
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    connection_id      bigint                         not null,                           -- connection主键
    scope              character varying(255),                                            -- 范围：ALL-全部，PARTIAL-部分
    foreign key (connection_id) references connections (id)
        match simple on update no action on delete no action
);
comment on table schemes is 'schema配置表';
comment on column schemes.id is '主键，自动生成';
comment on column schemes.module is '模块名';
comment on column schemes.package_name is '包名';
comment on column schemes.enabled is '是否启用';
comment on column schemes.created_by is '创建者';
comment on column schemes.created_date is '创建时间';
comment on column schemes.last_modified_by is '最后修改者';
comment on column schemes.last_modified_date is '最后修改时间';
comment on column schemes.connection_id is 'connection主键';
comment on column schemes.scope is '范围：ALL-全部，PARTIAL-部分';

create table scripts
(
    tableoid           oid                            not null,
    cmax               cid                            not null,
    xmax               xid                            not null,
    cmin               cid                            not null,
    xmin               xid                            not null,
    ctid               tid                            not null,
    id                 bigint primary key             not null,                           -- 主键，自动生成
    name               character varying(255),                                            -- 名称
    icon               character varying(255),                                            -- 图标
    version            integer,                                                           -- 版本
    body               character varying(255),                                            -- 内容
    enabled            boolean                        not null default true,              -- 是否启用
    created_by         character varying(255),                                            -- 创建者
    created_date       timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by   character varying(255),                                            -- 最后修改者
    last_modified_date timestamp(6) without time zone,                                    -- 最后修改时间
    type               character varying(255)                                             -- 类型
);
comment on table scripts is '脚本配置表';
comment on column scripts.id is '主键，自动生成';
comment on column scripts.name is '名称';
comment on column scripts.icon is '图标';
comment on column scripts.version is '版本';
comment on column scripts.body is '内容';
comment on column scripts.enabled is '是否启用';
comment on column scripts.created_by is '创建者';
comment on column scripts.created_date is '创建时间';
comment on column scripts.last_modified_by is '最后修改者';
comment on column scripts.last_modified_date is '最后修改时间';
comment on column scripts.type is '类型';

create table user_privilege_actions
(
    tableoid          oid                    not null,
    cmax              cid                    not null,
    xmax              xid                    not null,
    cmin              cid                    not null,
    xmin              xid                    not null,
    ctid              tid                    not null,
    id                bigint primary key     not null, -- 主键
    user_privilege_id bigint                 not null, -- 用户权限ID
    actions           character varying(255) not null, -- 操作
    foreign key (user_privilege_id) references user_privileges (id)
        match simple on update no action on delete no action
);
comment on table user_privilege_actions is '用户权限操作表';
comment on column user_privilege_actions.id is '主键';
comment on column user_privilege_actions.user_privilege_id is '用户权限ID';
comment on column user_privilege_actions.actions is '操作';

create table user_privileges
(
    tableoid     oid                    not null,
    cmax         cid                    not null,
    xmax         xid                    not null,
    cmin         cid                    not null,
    xmin         xid                    not null,
    ctid         tid                    not null,
    id           bigint primary key     not null, -- 主键
    username     character varying(255) not null, -- 账号
    privilege_id bigint                 not null, -- 权限ID
    foreign key (privilege_id) references privileges (id)
        match simple on update no action on delete no action,
    foreign key (username) references users (username)
        match simple on update no action on delete no action
);
comment on table user_privileges is '用户权限关系表';
comment on column user_privileges.id is '主键';
comment on column user_privileges.username is '账号';
comment on column user_privileges.privilege_id is '权限ID';

create table users
(
    tableoid                oid                            not null,
    cmax                    cid                            not null,
    xmax                    xid                            not null,
    cmin                    cid                            not null,
    xmin                    xid                            not null,
    ctid                    tid                            not null,
    id                      bigint primary key             not null,                           -- 主键
    username                character varying(255)         not null,                           -- 用户名
    password                character varying(255)         not null,                           -- 密码
    email                   character varying(255),                                            -- 邮箱
    avatar                  character varying(255),                                            -- 头像
    enabled                 boolean                        not null default true,              -- 是否启用
    account_non_locked      boolean,                                                           -- 是否未锁定
    created_by              character varying(255),                                            -- 创建者
    created_date            timestamp(6) without time zone not null default CURRENT_TIMESTAMP, -- 创建时间
    last_modified_by        character varying(255),                                            -- 最后修改者
    last_modified_date      timestamp(6) without time zone,                                    -- 最后修改时间
    full_name               character varying(255),                                            -- 姓名
    account_non_expired     boolean,                                                           -- 账号是否有效
    credentials_non_expired boolean                                                            -- 密码是否有效
);
create unique index users_username_key on users using btree (username);
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

