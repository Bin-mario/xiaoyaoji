# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Generation Time: 2016-11-28 12:59:02 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table email_token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `email_token`;

CREATE TABLE `email_token` (
  `id` char(12) NOT NULL,
  `email` varchar(45) NOT NULL,
  `isUsed` tinyint(1) NOT NULL DEFAULT '0',
  `createtime` datetime NOT NULL,
  `token` char(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table find_password
# ------------------------------------------------------------

DROP TABLE IF EXISTS `find_password`;

CREATE TABLE `find_password` (
  `id` char(12) NOT NULL DEFAULT '',
  `email` varchar(45) DEFAULT NULL,
  `isUsed` tinyint(1) DEFAULT '0',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table interface
# ------------------------------------------------------------

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table interface_folder
# ------------------------------------------------------------

DROP TABLE IF EXISTS `interface_folder`;

CREATE TABLE `interface_folder` (
  `id` char(14) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `moduleId` char(14) DEFAULT NULL,
  `projectId` char(14) DEFAULT NULL,
  `sort` int(11) DEFAULT '100',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table module
# ------------------------------------------------------------

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table project
# ------------------------------------------------------------

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table project_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `project_log`;

CREATE TABLE `project_log` (
  `id` char(14) NOT NULL DEFAULT '',
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `log` text,
  `projectId` char(14) DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table project_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `project_user`;

CREATE TABLE `project_user` (
  `id` char(14) NOT NULL,
  `projectId` char(14) DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `status` char(255) DEFAULT 'PENDING',
  `editable` char(3) DEFAULT 'YES',
  `commonlyUsed` char(3) DEFAULT 'NO',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table share
# ------------------------------------------------------------

DROP TABLE IF EXISTS `share`;

CREATE TABLE `share` (
  `id` char(12) NOT NULL DEFAULT '',
  `name` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `userId` char(12) DEFAULT NULL,
  `shareAll` char(3) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `moduleIds` varchar(500) DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table team
# ------------------------------------------------------------

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



# Dump of table team_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `team_user`;

CREATE TABLE `team_user` (
  `id` char(14) NOT NULL,
  `teamId` char(14) DEFAULT NULL,
  `userId` char(14) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table user
# ------------------------------------------------------------

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



# Dump of table user_third
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_third`;

CREATE TABLE `user_third` (
  `id` varchar(60) NOT NULL,
  `userid` char(12) NOT NULL,
  `type` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
