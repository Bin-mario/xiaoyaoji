<%--
  User: zhoujingjie
  Date: 2017/8/8
  Time: 17:28
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="left db-left" id="sidebar">
    <div class="db-left-content dlc1 bg hide">
        <div class="ta-c logo"><a href="#!/" class="v-link-active"><img
                src="${assets}/img/logo/full.png"></a></div>
        <div class="dbl-projects">
            <div class="db-left-search">
                <div class="cb">
                    <div class="fl"><i class="iconfont icon-sousuo"></i></div>
                    <div class="fl"><input type="text" placeholder="快速查找项目" value=""></div>
                </div>
            </div>
            <div class="line"></div>
            <br>
            <ul>
                <li class="db-item"><a class="bd-add" href="#!/add"> <i class="iconfont icon-add-circle"></i>创建项目</a>
                </li>
                <li class="line"></li>
                <li class="bd-project-title">常用项目</li> <!--v-for-start-->
                <li class="db-item"><a href="#!/project/2r4QfConx"><i class="iconfont icon-projects"></i>JLL智慧物业</a> <a
                        class="shoucang"><i class="iconfont icon-biaoxingfill"></i></a></li>
                <li class="db-item"><a href="#!/project/2r7J3PAv6"><i class="iconfont icon-projects"></i>帕丁停车</a> <a
                        class="shoucang"><i class="iconfont icon-biaoxingfill"></i></a></li>
                <li class="db-item"><a href="#!/project/18AKhgLb4H"><i class="iconfont icon-projects"></i>jll海外版</a> <a
                        class="shoucang"><i class="iconfont icon-biaoxingfill"></i></a></li><!--v-for-end-->
                <li class="bd-project-title">我的项目</li> <!--v-for-start-->
                <li v-show="loading.project">
                    <div id="loading">
                        <div class="spinner">
                            <div class="double-bounce1"></div>
                            <div class="double-bounce2"></div>
                        </div>
                    </div>
                </li>
                <li class="db-item" v-for="item in projects">
                    <a v-bind:href="${ctx}/doc/"+item.id><i class="iconfont icon-projects"></i>{{item.name}}</a>
                    <a class="shoucang"><i class="iconfont icon-biaoxing"></i></a>
                </li>

            </ul>
        </div>

    </div>
    <div class="sidebar bg">
        <div class="db-left-bar">
            <div class="logo ta-c"><a href="#!/" class="v-link-active"><img
                    src="${assets}/img/logo/full-white.png"></a></div>
            <br> <br> <br>
            <ul class="ta-c">
                <li class="db-item"><a href="#!/add"><i class="iconfont icon-add-circle"></i></a></li>

                <li class="db-item "><a href="${ctx}/project/${project.id}/info" title="项目设置"><i class="iconfont icon-setting2"></i></a></li>
                <li class="db-item"><a title="预览文档" v-on:click="viewpage"><i class="iconfont icon-eye"></i></a></li>
                <li class="db-item "><a title="编辑文档" v-on:click="editpage"><i class="iconfont icon-yulan"></i></a></li>
                <li class="db-item "><a title="历史版本" v-on:click="editpage"><i class="iconfont icon-yulan"></i></a></li>
                <li class="db-item "><a title="全局设置" v-on:click="editpage"><i class="iconfont icon-yulan"></i></a></li>
            </ul>
            <ul class="sidebar-o-op ta-c">
                <li class="db-item "><a href="${ctx}/profile" title="个人中心"><i class="iconfont icon-user"></i></a></li>
                <li class="db-item"><a href="${ctx}/dashboard" title="控制台"><i class="iconfont icon-dashboard"></i></a></li>
                <li class="db-item" v-on:click="showProject"><a title="项目列表"><i class="iconfont icon-projects active"></i></a></li>
            </ul>
        </div>
        <div class="db-left-layer hide" onclick="$('#sidebar').removeClass('layer')"></div>
    </div>
</div>
<script src="${assets}/js/project/sidebar.js"></script>