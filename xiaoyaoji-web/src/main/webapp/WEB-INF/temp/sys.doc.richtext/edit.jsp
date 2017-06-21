<%--
  User: zhoujingjie
  Date: 17/4/15
  Time: 22:43
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="${assets}/wangeditor/dist/css/wangEditor.min.css">
<div class="docWeApp" id="docWeApp" v-cloak>
    <div id="we" style="height: 600px;">${doc.content}</div>
</div>
<script>
    $(function () {
        function resizeHeight(f) {
            var height = $(window).height() - $('.doc-content').offset().top - 40;
            if(!f){
                //it's a bug for wangeditor
                height-=45;
            }
            $('#we').height(height);
        }
        $(window).resize(function(){resizeHeight()});
        resizeHeight(true);
        require(['vue', 'wangeditor/dist/js/wangEditor'], function (Vue) {
            var editor;
            var app = new Vue({
                el: '#docWeApp',
                data: {
                    editorContent: ''
                },
                mounted: function () {
                    var self = this;
                    editor = new wangEditor('we');
                    editor.onchange = function () {
                        // onchange 事件中更新数据
                        self.editorContent = editor.$txt.html();
                    };
                    editor.create();
                }
            });
            window.getDoc = function () {
                return {
                    content: editor.$txt.html()
                }
            }
        });
    });
</script>