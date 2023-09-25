# 数据库初始化

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    accessKey    varchar(512)                           null comment 'accessKey',
    secretKey    varchar(512)                           null comment 'secretKey',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

use my_db;

create table if not exists interface_info
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '名称',
    `description` varchar(256) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestParams` text not null comment '请求参数',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态(0-关闭,1-开启)',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删,1-已删)'
) comment '接口信息';


INSERT INTO interface_info (name, description, url, requestHeader, responseHeader, status, method, userId, createTime, updateTime, isDelete)
VALUES
    ('用户登录', '用户登录接口', 'https://api.example.com/login', '{}', '{}', 1, 'POST', 1, NOW(), NOW(), 0),
    ('获取用户信息', '获取用户详细信息接口', 'https://api.example.com/user/info', '{}', '{}', 1, 'GET', 1, NOW(), NOW(), 0),
    ('创建订单', '创建新订单接口', 'https://api.example.com/order/create', '{}', '{}', 1, 'POST', 2, NOW(), NOW(), 0),
    ('获取订单列表', '获取用户订单列表接口', 'https://api.example.com/order/list', '{}', '{}', 1, 'GET', 2, NOW(), NOW(), 0),
    ('更新用户信息', '更新用户详细信息接口', 'https://api.example.com/user/update', '{}', '{}', 1, 'PUT', 3, NOW(), NOW(), 0),
    ('删除订单', '删除订单接口', 'https://api.example.com/order/delete', '{}', '{}', 1, 'DELETE', 3, NOW(), NOW(), 0),
    ('搜索商品', '搜索商品接口', 'https://api.example.com/product/search', '{}', '{}', 1, 'GET', 4, NOW(), NOW(), 0),
    ('添加商品到购物车', '添加商品到购物车接口', 'https://api.example.com/cart/add', '{}', '{}', 1, 'POST', 4, NOW(), NOW(), 0),
    ('创建新商品', '创建新商品接口', 'https://api.example.com/product/create', '{}', '{}', 1, 'POST', 5, NOW(), NOW(), 0),
    ('更新商品信息', '更新商品详细信息接口', 'https://api.example.com/product/update', '{}', '{}', 1, 'PUT', 5, NOW(), NOW(), 0),
    ('删除用户', '删除用户接口', 'https://api.example.com/user/delete', '{}', '{}', 1, 'DELETE', 6, NOW(), NOW(), 0),
    ('获取购物车列表', '获取用户购物车列表接口', 'https://api.example.com/cart/list', '{}', '{}', 1, 'GET', 6, NOW(), NOW(), 0),
    ('创建新分类', '创建新分类接口', 'https://api.example.com/category/create', '{}', '{}', 1, 'POST', 7, NOW(), NOW(), 0),
    ('更新分类信息', '更新分类详细信息接口', 'https://api.example.com/category/update', '{}', '{}', 1, 'PUT', 7, NOW(), NOW(), 0),
    ('删除分类', '删除分类接口', 'https://api.example.com/category/delete', '{}', '{}', 1, 'DELETE', 8, NOW(), NOW(), 0),
    ('获取商品详情', '获取商品详细信息接口', 'https://api.example.com/product/detail', '{}', '{}', 1, 'GET', 8, NOW(), NOW(), 0),
    ('创建新文章', '创建新文章接口', 'https://api.example.com/article/create', '{}', '{}', 1, 'POST', 9, NOW(), NOW(), 0),
    ('更新文章内容', '更新文章详细内容接口', 'https://api.example.com/article/update', '{}', '{}', 1, 'PUT', 9, NOW(), NOW(), 0),
    ('删除文章', '删除文章接口', 'https://api.example.com/article/delete', '{}', '{}', 1, 'DELETE', 10, NOW(), NOW(), 0),
    ('获取文章列表', '获取文章列表接口', 'https://api.example.com/article/list', '{}', '{}', 1, 'GET', 10, NOW(), NOW(), 0);



-- 用户接口关系表
create table if not exists user_interface_info_linkage
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户Id',
    `interfaceInfoId` bigint not null comment '调用接口Id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常 1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删,1-已删)'
) comment '用户接口关系表';
