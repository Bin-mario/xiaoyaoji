12/26:
1.修改boolean 变number bug
2.修改api404bug
3.修改导出pdf未分层级bug
4.修改导出pdf未导出公共参数bug


12/16:
1.修改部分bug
2.增加项目导出json和导入。

11/30:
1.修改页面文字错误
2.修改导出被隐藏bug
3.增加接口与文件夹排序功能
4.增加参数排序功能
5.增加头像上传功能
6.修改接口description类型
7.解决第三方登录bug

SQL修改:
1. interface_folder 增加sort int 字段 alter table interface_folder add column sort int;
2. interface增加sort int字段 alter table interface add column sort int;
3. module 增加sort int 字段  alter table module add column sort int;
4. alter table interface MODIFY description text;


10/23:
1.增强json、xml请求类型操作
2.接口新增已废弃状态
3.增加成员权限管理
4.增加密码分享
5.增加常用项目
6.模块全局接口
SQL修改：
1.interface 增加status char(10) 字段。默认ENABLE
2.project_user 增加editable char(3) 默认YES，增加commonlyUsed char(3) 默认NO
3.module 增加requestHeaders text ，增加requestArgs text
3.新增share表
	CREATE TABLE share (
	  id char(12) NOT NULL DEFAULT '' PRIMARY key,
	  name varchar(50) DEFAULT NULL,
	  createTime datetime DEFAULT NULL,
	  userId char(12) DEFAULT NULL,
	  shareAll char(3) DEFAULT NULL,
	  password varchar(20) DEFAULT NULL,
	  moduleIds varchar(500) DEFAULT NULL,
	  projectId char(12) DEFAULT NULL
	) DEFAULT CHARSET=utf8mb4;

09/29:
1.增加团队协作开发时，消息提示操作记录
2.默认增加index.html，解决某些环境下无斜线“/” 404的情况

09/26:
1.优化操作流程，使用更简单；
2.解决响应类型为html时演示bug；
3.增强数据类型为html时的操作；
4.集成html与api到一个项目，不用再分开部署；

09/24:
1.修改样式；
2.模块描述改为项目文档说明且整个项目只有一个文档说明；
SQL修改：
2.删除module表host,devHost,description 字段

0920
1.修改样式
SQL修改：
1.project表增加environments 字段；


