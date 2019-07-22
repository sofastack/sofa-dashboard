-- H2 测试SQL，移除了 sql/SofaDashboardDB.sql 里面的建库语句和
-- 表的 COMMENT 在中文情况下会报错，故移除
-- ----------------------------
-- Table structure for ark_module_app
-- ----------------------------
DROP TABLE IF EXISTS `ark_module_app`;
CREATE TABLE `ark_module_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模块应用主键',
  `module_id` int(11) NOT NULL COMMENT '模块id',
  `app_name` varchar(128) NOT NULL COMMENT '应用名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '模块创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for ark_module_info
-- ----------------------------
DROP TABLE IF EXISTS `ark_module_info`;
CREATE TABLE `ark_module_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模块主键',
  `plugin_name` varchar(128) NOT NULL COMMENT '模块名',
  `description` varchar(128) DEFAULT '' COMMENT '模块描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '模块创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for ark_module_user
-- ----------------------------
DROP TABLE IF EXISTS `ark_module_user`;
CREATE TABLE `ark_module_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模块负责人表主键',
  `module_id` int(11) NOT NULL COMMENT '插件模块id',
  `user_id` int(11) DEFAULT NULL COMMENT '插件ownerId',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for ark_module_version
-- ----------------------------
DROP TABLE IF EXISTS `ark_module_version`;
CREATE TABLE `ark_module_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模块版本表主键',
  `module_id` int(11) NOT NULL COMMENT '模块id',
  `module_version` varchar(64) NOT NULL COMMENT '模块版本',
  `source_path` varchar(64) DEFAULT '' COMMENT '资源路径',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_release` tinyint(4) DEFAULT 0 COMMENT '是否发布',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sofa_dashboard_user
-- ----------------------------
DROP TABLE IF EXISTS `sofa_dashboard_user`;
CREATE TABLE `sofa_dashboard_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表id',
  `nick_name` varchar(64) DEFAULT '' COMMENT '昵称',
  `email` varchar(64) DEFAULT '' COMMENT '用户邮箱',
  `phone` varchar(12) DEFAULT '' COMMENT '用户电话',
  `password` varchar(32) DEFAULT '' COMMENT '用户密码',
  `header_img_url` varchar(64) DEFAULT '' COMMENT '头像地址链接',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 ;