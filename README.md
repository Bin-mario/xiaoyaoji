![LOGO](http://www.xiaoyaoji.cn/assets/img/logo/full.png)
# 小幺鸡文档管理工具，支持富文本、markdown、http、websocket 及其在线测试
## [在线演示地址demo](http://www.xiaoyaoji.cn/project/demo)
#### 由于在线版本之前未做缓存控制，导致很多用户打开页面后出现各种问题。（临时解决办法是CTRL+F5）。 在后面会修复该问题


### [下载最新版本](https://github.com/zhoujingjie/xiaoyaoji/releases)

## 兼容性
* 静态页系统暂时只兼容chrome，其他浏览器未测试。
* 后端：jdk1.7 tomcat7  mariadb5.5

## [离线部署配置说明](http://www.xiaoyaoji.cn/doc/TxybXPTdx)


## module说明
* xiaoyaoji-web : 小幺鸡web工程
* xiaoyaoji-biz : 小幺鸡业务代码模块
* 其他： 插件目录



## bug
如果大家发现了bug，可以在[Issues](https://github.com/zhoujingjie/xiaoyaoji/issues)里面提交，然后也可以直接修改bug然后提交代码
提交bug时请尽量多提供一些信息，如截图



## 分支说明
* 默认分支是dev，开发版本的，开发版本的可能会出现编译错误等各种异常情况
* 如果想自己下载编译，请下载其他分支



## 插件开发
* 目前插件类型有导入，导出，文档三种类型
* 可以参照xiaoyaoji-pdf 或者 xiaoyaoji-plugins 模块
### 文档插件
1. 实现cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin接口
2. 插件目录（采用标准的maven目录结构）
 --- xiaoyaoji-plugins  
 ------ src  
 --------- main  
 -------------- java  
 ------------------ com.xxxxx  
 -------------- plugins-resources  
 ------------------ web （静态页面）  
 ------------------ plugin.json  
3. 打包插件： 采用apache的 maven-assembly-plugin 插件。 具体配置参考已有插件配置
4. 打包方法。 mvn package；




## 如果觉得还不错，请作者喝杯咖啡吧 ☺
![微信](http://0d077ef9e74d8.cdn.sohucs.com/pV4oizD_png)
![支付宝](http://0d077ef9e74d8.cdn.sohucs.com/pV4ojyC_png)

