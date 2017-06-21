<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/15
  Time: 19:31
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="mdDocApp">
    <div id="editormd"></div>
</div>
<link rel="stylesheet" href="${assets}/editor.md/lib/codemirror/codemirror.min.css"/>
<link rel="stylesheet" href="${assets}/editor.md/lib/codemirror/addon/fold/foldgutter.css"/>
<link rel="stylesheet" href="${assets}/editor.md/css/editormd.min.css"/>
<script>
    $(function(){
        requirejs.config({
            baseUrl:'${assets}',
            paths: {
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
            "vue", "utils",
            "editormd",
            "editor.md/plugins/link-dialog/link-dialog",
            "editor.md/plugins/reference-link-dialog/reference-link-dialog",
            "editor.md/plugins/image-dialog/image-dialog",
            "editor.md/plugins/code-block-dialog/code-block-dialog",
            "editor.md/plugins/table-dialog/table-dialog",
            "editor.md/plugins/help-dialog/help-dialog",
            "editor.md/plugins/html-entities-dialog/html-entities-dialog",
            "editor.md/plugins/preformatted-text-dialog/preformatted-text-dialog"
        ];
        var doc = ${doc};

        require(deps, function (Vue, utils, editormd) {
            var app = new Vue({
                el: '#mdDocApp',
                data:{
                    doc:doc
                }
            });

            var editor = editormd('editormd', {
                path: '${assets}/editor.md/lib/',
                width: '100%',
                height: $(window).height()-200,
                flowChart: false,
                sequenceDiagram: false,
                markdown:(doc.content || ''),
                autoLoadModules: true,
                emoji: false,
                tocm:true,
                searchReplace: false,
                taskList: false,
                tex: false,
                toolbarIcons: ["undo", "redo", "|",
                    "bold", "del", "italic", "quote", "|",
                    "h1", "h2", "h3", "|",
                    "list-ul", "list-ol", "hr", "|",
                    "link", "reference-link", "image", "code", "preformatted-text", "code-block", "table", "datetime", "pagebreak", "|",
                    "watch", "preview", "fullscreen", "clear", "info"],
                imageUpload : false,
                imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                imageUploadURL : "./php/upload.php",
                onload : function() {
                    //this.fullscreen();
                    //this.unwatch();
                    //this.watch().fullscreen();

                    //this.setMarkdown("#PHP");
                    //this.width("100%");
                    //this.height(480);
                    //this.resize("100%", 640);
                }

            });
            window.getDoc = function(){
                return {
                    name:app.doc.name,
                    content:editor.getMarkdown()
                }
            }

        })
    });


</script>