(function () {
    define(function(){
        return {
            saveDoc:function(){
                if (_isGlobal_) {
                    window.submitProjectGlobal();
                } else {
                    var doc = window.getDoc();
                    var url = '/doc/' + _docId_;
                    utils.post(url, {
                        name: doc.name,
                        comment: this.submitComment,
                        content: doc.content
                    }, function () {
                        toastr.success('操作成功');
                    });
                }
            }
        }
    });
})();