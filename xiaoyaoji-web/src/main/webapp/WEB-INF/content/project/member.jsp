<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>成员管理-${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body>
<jsp:include page="../dashboard/header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="projectMember">
        <div class="db-main db-members-box" style="padding: 20px 0 0 20px">
            <div class="db-members cb">
                <div class="fl">

                    <div>
                        <ul uk-tab>
                            <li class="uk-active" v-on:click="tab='list'"><a>成员列表</a></li>
                            <li v-on:click="tab='add'"><a>添加</a></li>
                        </ul>
                    </div>
                    <div class="db-m-list" v-cloak v-show="tab=='list'">
                        <ul>
                            <c:forEach items="${users}" var="item">
                                <li class="cb">
                                    <div class="col-sm-2"><img class="user-logo"
                                                               src="${fileAccess}${item.avatar}">
                                    </div>
                                    <div class="col-sm-2"> ${item.nickname}</div>
                                    <div class="col-sm-3"> ${item.email}</div>
                                    <div class="col-sm-2"><label>
                                        <input v-on:change="changeEditStatus('${item.id}',$event)"
                                            type="checkbox" ${item.editable=='YES'?'checked':''}> 可编辑</label></div>
                                    <div class="col-sm-1"><input type="button" v-on:click="remove('${item.id}')"
                                                                 class="btn btn-danger" value="移除"></div>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="form dvn-import-members" v-cloak v-show="tab=='add'">
                        <div class="item">
                            <div class="col-sm-2 label">邀请同事</div>
                            <div class="col-sm-10">
                                <ul class="cb dbv-chose-users"> <!--v-for-start-->
                                    <li v-for="item in users" v-bind:class="{'active':exists[item.id]}">
                                        <div class="dbv-user-icon" v-on:click="inviteByUserId(item,$event)">
                                            <img class="img" v-bind:src="fileAccess+item.avatar" v-if="item.avatar">
                                            <div class="img ta-c word" v-else>{{item.nickname.substring(0,3)}}</div>
                                            <p class="flag"></p>
                                        </div>
                                        <p>{{item.nickname}}</p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="spinner" v-if="loading">
                            <div class="rect1"></div>
                            <div class="rect2"></div>
                            <div class="rect3"></div>
                            <div class="rect4"></div>
                            <div class="rect5"></div>
                        </div>
                        <div class="item">
                            <div class="col-sm-2 label">邮箱邀请</div>
                            <div class="col-sm-9"><input type="text" v-model="email" class="text"
                                                         placeholder="请输入成员的邮箱">

                                <div class="tip">错误消息</div>
                            </div>

                            <div class="col-sm-1">
                                <input type="button" v-on:click="inviteByEmail" class="btn btn-danger" value="邀请">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>
    var exists={};
    <c:forEach items="${users}" var="item">exists['${item.id}']=1;</c:forEach>
    $(function () {
        require(['vue', 'utils', 'veeValidate'], function (Vue, utils, VeeValidate) {
            function loadUser(self){
                utils.get('/user/project_users', {}, function (rs) {
                    self.users = rs.data.users;
                    self.fileAccess = rs.data.fileAccess;
                })
            }
            Vue.use(VeeValidate);
            new Vue({
                el: '#projectMember',
                data: {
                    exists:exists,
                    loading:false,
                    id: '${project.id}',
                    tab: 'list',
                    fileAccess: '',
                    users: [],
                    email:''
                },
                created: function () {
                    var self = this;
                    loadUser(self);
                },
                methods: {
                    changeEditStatus: function (userId,e) {
                        var editable = $(e.target).prop('checked') ? 'YES' : 'NO';
                        utils.post('/project/' + this.id + '/pu/' + userId + '/' + editable, {editable: editable}, function () {
                            toastr.success('操作成功');
                        });
                    },

                    remove: function (userId) {
                        utils.delete('/project/' + this.id + '/pu/' + userId + '.json', function () {
                            location.reload();
                        });
                    },
                    inviteByUserId: function (user, e) {
                        var self = this;
                        utils.post('/project/' + this.id + "/invite", {userId: user.id}, function (rs) {
                            toastr.success('添加成功');
                            self.users.push(user);
                            $(e.target).parents("li").addClass("active");
                        });
                    },
                    inviteByEmail: function () {
                        if (this.email) {
                            this.loading=true;
                            var self = this;
                            utils.post('/project/' + this.id + "/invite/email", {email: this.email}, function () {
                                self.loading=false;
                                self.email = '';
                                toastr.success('邀请成功');
                                self.showList = true;
                                loadUser(self);
                            });
                        }
                    }
                }
            });
        });
    });
</script>
</body>
</html>