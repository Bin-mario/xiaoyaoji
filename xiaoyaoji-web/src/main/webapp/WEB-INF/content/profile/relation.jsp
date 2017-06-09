<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="content">
        <div>
            <div class="db-relation cb">
                <h1>关联账户</h1><br/>
                <p class="db-rel-desc">通过关联账户的绑定，您可以使用相关的授权账号登录小幺鸡服务。您也可以通过这些社会化服务，<br/>及时获取小幺鸡服务的信息。</p>
                <ul style="padding: 0;margin: 0">
                    <li class="ta-c">
                        <i class="iconfont icon-github"></i>
                        <p>Github</p>
                        <input type="button" class="btn btn-default" v-cloak v-on:click="github" value="关联Github"
                               v-if="!user.bindGithub">
                        <input type="button" class="btn btn-info" v-cloak v-on:click="unbind('GITHUB')" value="取消关联" v-else>
                    </li>
                    <li class="ta-c">
                        <i class="iconfont icon-weibo"></i>
                        <p>微博</p>
                        <input type="button" class="btn btn-default" v-cloak v-on:click="weibo" value="关联微博"
                               v-if="!user.bindWeibo">
                        <input type="button" class="btn btn-info" v-cloak v-on:click="unbind('WEIBO')" value="取消关联" v-else>
                    </li>
                    <li class="ta-c">
                        <i class="iconfont icon-qq"></i>
                        <p>QQ</p>
                        <input type="button" class="btn btn-default" v-cloak v-on:click="qq" value="关联QQ" v-if="!user.bindQQ">
                        <input type="button" class="btn btn-info" v-cloak v-on:click="unbind('QQ')" value="取消关联" v-else>
                    </li>
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
                init: function () {
                    var self = this;
                    if (window.relationInitialized) {
                        return true;
                    }
                    window.relationInitialized = true;
                    window.addEventListener('message', function (e) {
                        if (e.origin == 'http://www.xiaoyaoji.com.cn' || e.origin == 'https://www.xiaoyaoji.com.cn') {
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
                github: function () {
                    this.init();
                    utils.window.github();
                },
                weibo: function () {
                    this.init();
                    utils.window.weibo();
                },
                qq: function () {
                    this.init();
                    utils.window.qq();
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
