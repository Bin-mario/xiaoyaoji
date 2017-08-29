<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="cn.com.xiaoyaoji.core.plugin.PluginManager" %><%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setAttribute("loginPlugins", PluginManager.getInstance().getLoginPlugins());
%>
<jsp:include page="header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="content">
        <div>
            <div class="db-relation cb">
                <h1>关联账户</h1><br/>
                <p class="db-rel-desc">通过关联账户的绑定，您可以使用相关的授权账号登录小幺鸡服务。您也可以通过这些社会化服务，<br/>及时获取小幺鸡服务的信息。</p>
                <ul style="padding: 0;margin: 0">
                    <c:forEach items="${loginPlugins}" var="item">
                    <li class="ta-c">
                        <img src="${ctx}/proxy/${item.id}/${item.icon.icon128x128}?v=${item.version}" title="${item.description}"/>
                        <p>${item.name}</p>
                        <input type="button" class="btn btn-default" v-cloak v-on:click="relate('${item.id}','${item.plugin.openURL}')" value="关联${item.name}"
                               v-if="!user.bindingMap['${item.id}']">
                        <input type="button" class="btn btn-info" v-cloak v-on:click="unbind('${item.id}')" value="取消关联" v-else>
                    </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>
    require(['vue', 'utils'], function (Vue, utils) {
        new Vue({
            el: '#content',
            data:{
                user: utils.toJSON('${user}')
            },
            methods: {
                relate:function(pluginId,openURL){
                    window.open(openURL, pluginId, 'height=550, width=900, top=0, left=0, toolbar=no, menubar=no, scrollbars=no,resizable=no,location=no, status=no');
                    if (window.relationInitialized) {
                        return true;
                    }
                    window.relationInitialized = true;
                    window.addEventListener('message', function (e) {
                        if (e.origin == 'http://www.xiaoyaoji.cn' || e.origin == 'https://www.xiaoyaoji.cn') {
                            var data = e.data;
                            data = JSON.parse(data);
                            utils.post('/user/bind.json', data, function (rs) {
                                toastr.success('绑定成功');
                                setTimeout(function(){
                                    location.reload();
                                },1000);
                            });
                        }
                    });
                },
                unbind: function (type) {
                    var self = this;
                    utils.post('/user/unbind/' + type + '.json', {}, function (rs) {
                        toastr.success('解绑成功');
                        setTimeout(function(){
                            location.reload();
                        },1000);
                    })
                }

            }
        })
    })
</script>
