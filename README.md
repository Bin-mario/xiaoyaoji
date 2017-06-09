![LOGO](http://www.xiaoyaoji.com.cn/assets/img/logo/full.png)
# 小幺鸡文档管理工具，支持富文本、markdown、http、websocket 及其在线测试
## [在线演示地址demo](http://www.xiaoyaoji.com.cn/project/demo/view)


### [下载最新版本](http://git.oschina.net/zhoujingjie/apiManager/releases)

## 兼容性
* 静态页系统暂时只兼容chrome，其他浏览器未测试。
* 后端：jdk1.7 tomcat7  mariadb5.5


### module说明
* xiaoyaoji-docformatter : 文档格式化
* xiaoyaoji-web : 小幺鸡web工程



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
    * sendcloud.apikey : sendcloud apikey
* 缓存
    * cache.provider.factory: 缓存工厂
        * cn.com.xiaoyaoji.extension.cache.factory.DefaultCacheFactory ：基于jvm的缓存
        * cn.com.xiaoyaoji.extension.cache.factory.RedisCacheFactory   ：redis 缓存
            * redis.host： redis host
            * redis.port：redis端口
            * redis.password ：redis密码
            * redis.connection.timeout :连接超时时间
            
            

### 离线部署说明
* 新建数据库-utf8mb4格式，INNODB引擎。
* 导入sql
* 修改/WEB-INF/classes/config.properties 的数据库与其他信息
* 启动tomcat