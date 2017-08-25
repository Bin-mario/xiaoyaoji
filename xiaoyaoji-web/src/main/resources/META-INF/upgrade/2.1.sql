ALTER TABLE share
MODIFY COLUMN moduleIds  varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER password,
ADD COLUMN docIds  varchar(2000) NULL AFTER projectId;

update share set docIds = moduleIds;
