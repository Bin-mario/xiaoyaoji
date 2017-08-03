<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/15
  Time: 19:31
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .md-doc-name{
        width: 100%;
        padding: 8px 20px;
        font-size: 18px;
        border: none;
    }
    .md-doc-lastupdatetime{
        font-size: 12px;
        color: #8c8c8c;
    }
</style>
<div id="mdDocApp">
    <div class="md-doc-name cb">${doc.name} <span class="fr md-doc-lastupdatetime">最后更新时间:${doc.lastUpdateTime}</span></div>
    <div id="editormdvalue" class="hide">${doc.content}</div>
    <div id="editormd"></div>
</div>
<link rel="stylesheet" href="${ctx}/proxy/${pluginInfo.id}/assets/editor.md/lib/codemirror/codemirror.min.css?v=${pluginInfo.version}"/>
<link rel="stylesheet" href="${ctx}/proxy/${pluginInfo.id}/assets/editor.md/lib/codemirror/addon/fold/foldgutter.css?v=${pluginInfo.version}"/>
<link rel="stylesheet" href="${ctx}/proxy/${pluginInfo.id}/assets/editor.md/css/editormd.min.css?v=${pluginInfo.version}"/>
<script>
    requirejs.config({
        baseUrl:'${ctx}/proxy/${pluginInfo.id}/assets/',
        urlArgs:'v=${pluginInfo.version}',
        paths: {
            'vue':'/assets/vue/vue.2.3.3',
            'utils':'/assets/js/utils',
            marked: "editor.md/lib/marked.min",
            prettify: "editor.md/lib/prettify.min",
            raphael: "editor.md/lib/raphael.min",
            underscore: "editor.md/lib/underscore.min",
            flowchart: "editor.md/lib/flowchart.min",
            jqueryflowchart: "editor.md/lib/jquery.flowchart.min",
            sequenceDiagram: "editor.md/lib/sequence-diagram.min",
            katex: "editor.md/lib/katex.min",
            editormd: "editor.md/editormd.amd" // Using Editor.md amd version for Require.js
        }
    });

    var deps = [
        "editormd"
    ];
    require(deps, function (editormd) {

        editormd.markdownToHTML('editormd',{
            height: 500,
            flowChart: false,
            sequenceDiagram: false,
            markdown:$('#editormdvalue').html(),
            autoLoadModules: true,
            emoji: false,
            tocm:true,
            searchReplace: false,
            taskList: false,
            tex: false
        });
    })

</script>