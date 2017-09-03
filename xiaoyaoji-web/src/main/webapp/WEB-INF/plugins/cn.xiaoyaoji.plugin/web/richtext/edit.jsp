<%--
  User: zhoujingjie
  Date: 17/4/15
  Time: 22:43
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="docWeApp" id="docWeApp">
    <div id="we">${doc.content}</div>
</div>
<script>

    var editJs = '${cdn}/assets/wangeditor3/wangEditor.js';

    (function () {
        function resizeHeight(f) {
            var height = $(window).height() - $('.doc-content').offset().top - 60;
            $('.w-e-text-container').height(height);
        }

        $(window).resize(function () {
            resizeHeight()
        });

        require(['vue', editJs], function (Vue,wangEditor) {
            var editor;
            new Vue({
                el: '#docWeApp',
                data: {
                    editorContent: ''
                },
                mounted: function () {
                    var self = this;
                    editor = new wangEditor('#we');
                    editor.onchange = function () {
                        // onchange 事件中更新数据
                        self.editorContent = editor.txt.html();
                    };
                    editor.customConfig.zIndex=66;
                    editor.create();

                    resizeHeight(true);
                }
            });
            window.getDoc = function () {
                return {
                    content: editor.txt.html()
                }
            }
        });
    })();
</script>
