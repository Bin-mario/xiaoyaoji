ALTER TABLE `doc_history`
MODIFY COLUMN `comment`  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `projectId`;

ALTER TABLE `user_third`
MODIFY COLUMN `type`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `userid`;

update user_third set type = 'cn.xiaoyaoji.login.qq' where type='QQ';
update user_third set type = 'cn.xiaoyaoji.login.weibo' where type='WEIBO';
update user_third set type = 'cn.xiaoyaoji.login.github' where type='GITHUB';

ALTER TABLE `user_third`
ADD INDEX `userId` (`userid`) ;

