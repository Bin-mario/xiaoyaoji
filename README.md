![LOGO](http://www.xiaoyaoji.cn/assets/img/logo/full.png)
# 小幺鸡文档管理工具，支持富文本、markdown、http、websocket 及其在线测试
## [在线演示地址demo](http://www.xiaoyaoji.cn/project/demo/view)


### [下载最新版本](http://git.oschina.net/zhoujingjie/apiManager/releases)

## 兼容性
* 静态页系统暂时只兼容chrome，其他浏览器未测试。
* 后端：jdk1.7 tomcat7  mariadb5.5


### module说明
* xiaoyaoji-docformatter : 文档格式化
* xiaoyaoji-web : 小幺鸡web工程
* xiaoyaoji-config: 配置模块



### config.properties 说明
* jdbc.xxx ：数据库的相关配置
* file.access.url : 文件服务的访问地址
* file.upload.provider ：文件上传提供者
    * cn.com.xiaoyaoji.extension.file.QiniuFileProvider 七牛云文件上传
        * file.qiniu.bucket :七牛云bucket
        * file.qiniu.accessKey :七牛云ak
        * file.qiniu.secretKey :七牛云sk
    * cn.com.xiaoyaoji.extension.fileupload.DefaultFileUploadProvider 默认文件上传
        * file.upload.dir :默认上传的文件路径 (全路径)
         
* 第三方登录配置
    * 微博
        * weibo.appkey :微博登录需要的appkey
        * weibo.appsecret : appsecret   
        * weibo.redirect_uri :   微博登录成功后重定向地址
    * QQ
        * qq.appid : qq appId
        * qq.appkey :qq appkey 
        * qq.redirect_uri:qq登录成功后的重定向地址
    * GITHUB
        * github.clientid :github clientid
        * github.secret: github 的secret
        * github.redirect_uri : github登录成功后的重定向地址
* salt : 密码混淆盐
* token.expires: 登录后会话有效期 单位秒
* 邮件发送
    * email.provider: 邮箱发送提供者
        * cn.com.xiaoyaoji.extension.email.SendCloudEMailProvider : 使用sendcloud 发送
            * sendcloud.apikey  : sendcloud apikey
            * sendcloud.system.apiuser: apiuser
            * sendcloud.system.from :发送人
        * cn.com.xiaoyaoji.extension.email.DefaultEMailProvider
            * email.from: 邮件发送人
            * email.smtp.server: smtp服务地址
            * email.smtp.port: stmp ssl端口
            * email.username: 用户名
            * email.password: 密码
    * sendcloud.apikey : sendcloud apikey
* 缓存
    * cache.provider.factory: 缓存工厂
        * cn.com.xiaoyaoji.extension.cache.factory.DefaultCacheFactory ：基于jvm的缓存
        * cn.com.xiaoyaoji.extension.cache.factory.RedisCacheFactory   ：redis 缓存
            * redis.host： redis host
            * redis.port：redis端口
            * redis.password ：redis密码
            * redis.connection.timeout :连接超时时间
            
            

### 离线部署说明 (标准的JavaWeb项目)
* 下载jdk1.7+ 并配置环境变量 [JDK8下载地址](http://www.oracle.com/technetwork/cn/java/javase/downloads/jdk8-downloads-2133151-zhs.html) [JAVA环境变量设置](https://www.java.com/zh_CN/download/help/path.xml)
* 下载Tomcat7.0+  [TOMCAT8下载地址](http://tomcat.apache.org/download-80.cgi)
* [下载最新版本](http://git.oschina.net/zhoujingjie/apiManager/releases) 解压到tomcat_home/webapps/ROOT目录下
* 新建MYSQL数据库-编码格式utf8mb4格式，INNODB引擎。
* 导入sql - SQL文件在doc目录下
* 修改tomcat_home/webapps/ROOT/WEB-INF/classes/config.properties 的数据库与其他信息
* 启动tomcat，使用chrome浏览器访问http://localhost:8080


## 如果是由1.x版本升级的，不用导入SQL，替换文件后启动tomcat后，用http post方式运行http://localhost:port/sys/update 即可

### bug
如果大家发现了bug，可以在[Issues](http://git.oschina.net/zhoujingjie/apiManager/issues)里面提交，然后也可以直接修改bug然后提交代码


## 如果觉得还不错，请作者喝杯咖啡吧 ☺

## 分支说明
* 默认分支是dev，开发版本的，开发版本的可能会出现编译错误等各种异常情况
* 如果想自己下载编译，请下载其他分支


