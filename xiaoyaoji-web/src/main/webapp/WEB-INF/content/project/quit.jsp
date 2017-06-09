<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>退出项目-${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body>
<jsp:include page="../dashboard/header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="quit">
        <div class="db-view-quit">
            <button class="btn btn-danger" v-on:click="ok" style="padding: 10px 100px;">退出项目</button>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>
    require(['vue', 'utils'], function (Vue, utils) {
        new Vue({
            el: '#quit',
            data:{
                id:'${project.id}'
            },
            methods: {
                ok:function(){
                    var self= this;
                    UIkit.modal.confirm('是否确认退出?').then(function(){
                        utils.delete('/project/' +self.id + '/quit.json', function () {
                            toastr.success('操作成功');

                            setTimeout(function(){
                                location.href=ctx+'/dashboard';
                            },1000);
                        })
                    });
                }
            }
        })
    })
</script>
