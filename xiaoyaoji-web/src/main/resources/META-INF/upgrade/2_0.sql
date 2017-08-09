drop table if exists doc;
create table doc (
	id char(12) PRIMARY KEY,
	name varchar(200),
    sort int default 100,
    type varchar(100),
    content longtext,
    createTime datetime,
    lastUpdateTime datetime,
    parentId char(12),
    projectId char(12),
    KEY parentId (parentId) USING BTREE,
    KEY projectId (projectId) USING BTREE
) ENGINE=InnoDB;

ALTER TABLE `project_user` ADD INDEX `project_user` (`projectId`, `userId`) USING BTREE ;


drop table if exists doc_history;

CREATE TABLE `doc_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT '100',
  `type` varchar(100) DEFAULT NULL,
  `content` longtext,
  `createTime` datetime DEFAULT NULL,
  `parentId` char(12) DEFAULT NULL,
  `projectId` char(12) DEFAULT NULL,
  `comment` text,
  `userId` char(12) DEFAULT NULL,
  `docId` char(12) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists attach;
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

drop table if exists sys;
CREATE TABLE `sys` (
  `version` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists project_global;
CREATE TABLE `project_global` (
  `id` char(12) NOT NULL DEFAULT '',
  `environment` mediumtext,
  `http` mediumtext,
  `projectId` char(12) NOT NULL DEFAULT '',
  `status` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `project`
ADD COLUMN `lastUpdateTime` DATETIME NULL AFTER `details`;
