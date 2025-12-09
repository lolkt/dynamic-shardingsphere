#正式数据库

CREATE DATABASE `tt_master` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_slave` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_shard_01` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `tt_shard_02` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';


#主库
CREATE TABLE `tt_master`.`t_order_0`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                              `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_master`.`t_order_1`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                              `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_master`.`t_user`  (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
                           `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

#从库
CREATE TABLE `tt_slave`.`t_order_0`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                           `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_slave`.`t_order_1`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                           `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `tt_slave`.`t_user`  (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
                                        `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

# 分库
CREATE TABLE `tt_shard_01`.`t_customer`  (
                               `id` int(11) NOT NULL,
                               `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                               `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `tt_shard_02`.`t_customer`  (
                                              `id` int(11) NOT NULL,
                                              `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                                              `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;



#测试数据库

CREATE DATABASE `test_master` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `test_slave` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `test_shard_01` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `test_shard_02` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';


#主库
CREATE TABLE `test_master`.`t_order_0`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                           `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `test_master`.`t_order_1`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                           `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `test_master`.`t_user`  (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
                                        `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

#从库
CREATE TABLE `test_slave`.`t_order_0`  (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                          `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `test_slave`.`t_order_1`  (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `customer_id` bigint(11) NULL DEFAULT NULL COMMENT '客户id',
                                          `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


CREATE TABLE `test_slave`.`t_user`  (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
                                       `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户角色',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

# 分库
CREATE TABLE `test_shard_01`.`t_customer`  (
                                              `id` int(11) NOT NULL,
                                              `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                                              `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `test_shard_02`.`t_customer`  (
                                              `id` int(11) NOT NULL,
                                              `mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                                              `age` tinyint(3) NULL DEFAULT NULL COMMENT '年龄',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
