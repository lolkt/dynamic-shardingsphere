# 正式数据库

CREATE DATABASE `tt_master` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_slave` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_shard_01` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_shard_02` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';


# 主库
CREATE TABLE `tt_master`.`t_order_0`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_master`.`t_order_1`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_master`.`t_user`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
    `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

# 从库
CREATE TABLE `tt_slave`.`t_order_0`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_slave`.`t_order_1`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_slave`.`t_user`  (
    `id` bigint(19) NOT NULL AUTO_INCREMENT,
    `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
    `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

# 分库 (id 使用 bigint 存储百度 uid)
CREATE TABLE `tt_shard_01`.`t_customer`  (
    `id` bigint(19) NOT NULL,
    `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `tt_shard_02`.`t_customer`  (
    `id` bigint(19) NOT NULL,
    `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


# UID 数据库
CREATE DATABASE `uid_center` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

CREATE TABLE `uid_center`.`WORKER_NODE` (
    ID          bigint auto_increment comment '自增ID（WorkerID）' primary key,
    HOST_NAME   varchar(64)                         not null comment '主机名',
    PORT        varchar(64)                         not null comment '端口',
    TYPE        int                                 not null comment '节点类型（1=物理机，2=容器）',
    LAUNCH_DATE date                                not null comment '节点启动日期（避免同一天重启导致WorkerID重复）',
    MODIFIED    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '最后修改时间（自动更新）',
    CREATED     timestamp default CURRENT_TIMESTAMP not null comment '创建时间（首次插入时自动设置）',
    constraint UK_HOST_PORT_DATE unique (HOST_NAME, PORT, LAUNCH_DATE) comment '确保同一节点同一天启动只分配一个WorkerID'
) comment '用于uid-generator分配WorkerID的节点信息表' charset = utf8mb4;


# 修改已存在的表 (如果表已存在，执行以下 ALTER 语句)
# ALTER TABLE `tt_shard_01`.`t_customer` MODIFY COLUMN `id` bigint(19) NOT NULL;
# ALTER TABLE `tt_shard_02`.`t_customer` MODIFY COLUMN `id` bigint(19) NOT NULL;
