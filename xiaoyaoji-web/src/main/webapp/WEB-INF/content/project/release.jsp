<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>解散项目-${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body>
<jsp:include page="../dashboard/header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="release">
        <div class="db-view-release">
            <button class="btn btn-danger" v-on:click="status.deleteModal=true" style="padding: 10px 100px;">删除项目
            </button>
            <div class="modal" v-cloak v-if="status.deleteModal">
                <div class="modal-header">
                    <i class="iconfont icon-close modal-close" v-on:click="status.deleteModal=false"></i>
                </div>
                <div class="modal-content">
                    <div class="modal-layout1 form">
                        <p class="title">删除项目</p>
                        <input type="text" class="k1 text" id="projectName" v-bind:class="{'invalid':isOk}"
                               maxlength="20"
                               initial="off"
                               v-model="projectName"
                               autofocus="autofocus"
                               tabindex="1" placeholder="请输入项目名称">
                        <div class="tip">项目名称错误</div>
                        <div class="ta-c actions">
                            <button class="btn btn-default-box middle" tabindex="3"
                                    v-on:click="status.deleteModal=false">取消
                            </button>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <button class="btn btn-danger middle" v-on:click="ok" tabindex="2">确定</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>
    require(['vue', 'utils'], function (Vue, utils) {
        new Vue({
            el: '#release',
            data: {
                isOk: false,
                project: null,
                projectName: '',
                status: {
                    deleteModal: false
                },
                oriName: '${project.name}',
                id: '${project.id}'
            },
            watch: {
                "status.deleteModal": function (value) {
                    if (value) {
                        setTimeout(function () {
                            $("#projectName").focus();
                        }, 100);
                    }
                }
            },
            methods: {
                ok: function () {
                    var id = this.id;
                    if (this.oriName == this.projectName) {
                        utils.delete('/project/' + id + '.json', function (rs) {
                            toastr.success('删除成功');
                            location.href = ctx + '/dashboard';
                        })
                    } else {
                        this.isOk = true;
                    }
                }
            }
        })
    });
</script>

