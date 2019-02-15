/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.56.101
 Source Server Type    : MySQL
 Source Server Version : 50714
 Source Host           : 192.168.56.101:3306
 Source Schema         : testjson

 Target Server Type    : MySQL
 Target Server Version : 50714
 File Encoding         : 65001

 Date: 15/02/2019 17:44:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_test
-- ----------------------------
DROP TABLE IF EXISTS `t_test`;
CREATE TABLE `t_test`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `properties` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_test
-- ----------------------------
INSERT INTO `t_test` VALUES (1, 'aaa', '{\"p1\": \"v1\", \"p2\": \"v2\"}');
INSERT INTO `t_test` VALUES (2, 'bbb', '{\"p1\": \"1234\", \"p2\": \"5678\"}');

SET FOREIGN_KEY_CHECKS = 1;
