/*
Navicat MySQL Data Transfer

Source Server         : 47.94.211.245-xyj
Source Server Version : 50716
Source Host           : 47.94.211.245:3306
Source Database       : xiaoyaoji

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2017-09-07 11:16:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for attach
-- ----------------------------
DROP TABLE IF EXISTS `attach`;
CREATE TABLE `attach` (
  `id` char(12) NOT NULL,
  `url` varchar(1000) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `relatedId` char(12) DEFAULT NULL,
  `fileName` varchar(1000) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `normal` (`relatedId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for doc
-- ----------------------------
DROP TABLE IF EXISTS `doc`;
CREATE TABLE `doc` (
  `id` char(12) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT '100',
  `type` varchar(100) DEFAULT NULL,
  `content` longtext,
  `createTime` datetime DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `parentId` char(12) DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`) USING BTREE,
  KEY `projectId` (`projectId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for doc_history
-- ----------------------------
DROP TABLE IF EXISTS `doc_history`;
CREATE TABLE `doc_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT '100',
  `type` varchar(100) DEFAULT NULL,
  `content` longtext,
  `createTime` datetime DEFAULT NULL,
  `parentId` char(12) DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `userId` char(12) DEFAULT NULL,
  `docId` char(12) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51473 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for email_token
-- ----------------------------
DROP TABLE IF EXISTS `email_token`;
CREATE TABLE `email_token` (
  `id` char(12) NOT NULL,
  `email` varchar(45) NOT NULL,
  `isUsed` tinyint(1) NOT NULL DEFAULT '0',
  `createtime` datetime NOT NULL,
  `token` char(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for find_password
-- ----------------------------
DROP TABLE IF EXISTS `find_password`;
CREATE TABLE `find_password` (
  `id` char(12) NOT NULL DEFAULT '',
  `email` varchar(45) DEFAULT NULL,
  `isUsed` tinyint(1) DEFAULT '0',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for interface
-- ----------------------------
DROP TABLE IF EXISTS `interface`;
CREATE TABLE `interface` (
  `id` char(14) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `description` text,
  `folderId` char(14) DEFAULT NULL,
  `url` varchar(300) DEFAULT NULL,
  `requestMethod` varchar(50) DEFAULT NULL,
  `contentType` varchar(50) DEFAULT NULL,
  `requestHeaders` text,
  `requestArgs` text,
  `responseArgs` text,
  `example` mediumtext,
  `moduleId` varchar(50) DEFAULT NULL,
  `projectId` char(14) DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `dataType` varchar(30) DEFAULT NULL,
  `protocol` varchar(30) DEFAULT NULL,
  `status` char(10) DEFAULT 'ENABLE',
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `projectid` (`projectId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for interface_folder
-- ----------------------------
DROP TABLE IF EXISTS `interface_folder`;
CREATE TABLE `interface_folder` (
  `id` char(14) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `moduleId` char(14) DEFAULT NULL,
  `projectId` char(14) DEFAULT NULL,
  `sort` int(11) DEFAULT '100',
  PRIMARY KEY (`id`),
  KEY `projectid` (`projectId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` char(14) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `description` mediumtext,
  `lastUpdateTime` datetime DEFAULT NULL,
  `projectId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `requestHeaders` text,
  `requestArgs` text,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `projectid` (`projectId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` char(14) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(300) DEFAULT NULL COMMENT 'test',
  `teamId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'VALID',
  `permission` varchar(20) DEFAULT 'PRIVATE',
  `environments` text,
  `details` text,
  `lastUpdateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for project_global
-- ----------------------------
DROP TABLE IF EXISTS `project_global`;
CREATE TABLE `project_global` (
  `id` char(12) NOT NULL DEFAULT '',
  `environment` mediumtext,
  `http` mediumtext,
  `projectId` char(12) NOT NULL DEFAULT '',
  `status` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for project_log
-- ----------------------------
DROP TABLE IF EXISTS `project_log`;
CREATE TABLE `project_log` (
  `id` char(14) NOT NULL DEFAULT '',
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `log` text,
  `projectId` char(14) DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `projectid` (`projectId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user` (
  `id` char(14) NOT NULL,
  `projectId` char(14) DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `status` char(255) DEFAULT 'PENDING',
  `editable` char(3) DEFAULT 'YES',
  `commonlyUsed` char(3) DEFAULT 'NO',
  PRIMARY KEY (`id`),
  KEY `project_user` (`projectId`,`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
  `id` char(12) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `userId` char(12) DEFAULT NULL,
  `shareAll` char(3) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `moduleIds` varchar(2000) DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  `docIds` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sys
-- ----------------------------
DROP TABLE IF EXISTS `sys`;
CREATE TABLE `sys` (
  `version` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` char(14) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(300) DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'VALID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for team_user
-- ----------------------------
DROP TABLE IF EXISTS `team_user`;
CREATE TABLE `team_user` (
  `id` char(14) NOT NULL,
  `teamId` char(14) DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` char(12) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `password` char(32) DEFAULT NULL,
  `type` varchar(5) DEFAULT 'USER',
  `nickname` varchar(30) DEFAULT NULL,
  `avatar` varchar(200) DEFAULT NULL,
  `status` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_third
-- ----------------------------
DROP TABLE IF EXISTS `user_third`;
CREATE TABLE `user_third` (
  `id` varchar(60) NOT NULL,
  `userid` char(12) NOT NULL,
  `type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Procedure structure for trans_module
-- ----------------------------
DROP PROCEDURE IF EXISTS `trans_module`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `trans_module`()
begin
DECLARE done INT DEFAULT FALSE;
declare id varchar(100);
declare name,host,projectId varchar(100);
declare lastUpdateTime,createTime datetime;
declare description MEDIUMTEXT;

declare cur1 cursor for select id from module limit 100;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

open cur1;
read_loop: LOOP

FETCH  cur1 into id;

if done
	then  leave read_loop;
end if;

select id;

end LOOP;
close cur1;

end
;;
DELIMITER ;
