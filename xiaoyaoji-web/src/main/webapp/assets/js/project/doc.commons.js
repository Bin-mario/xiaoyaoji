(function(){
    define(['utils',ctx+'/assets/html5sortable/html.sortable.min.js'],function(utils,sortable){
        function _initsort_(docApp){
            setTimeout(function () {
                sortable('.div-editing-table', 'destroy');
                var doms = sortable('.div-editing-table', {
                    handle: '.icon-drag-copy',
                    items: '.div-editing-line'
                });
                $(doms).off('sortupdate').on('sortupdate', function (e) {
                    var moduleName = $(e.target).attr("data-module-name");
                    var component;
                    docApp.$children.forEach(function(item){
                        if(moduleName && item.name == moduleName){
                            component = item;
                            return false;
                        }
                    });
                    if(!component){
                        return false;
                    }
                    var oldIndex=e.originalEvent.detail.oldElementIndex,index=e.originalEvent.detail.elementIndex;
                    component.$emit('sortUpdate',{
                        id:$(e.originalEvent.detail.item).data('id'),
                        index:index,
                        oldIndex:oldIndex
                    });
                });
            }, 500);
        }
        window._initsort_=_initsort_;

        function getArrayValueType(value) {
            var type = 'array';
            if (value.length > 0) {
                var name = value[0].constructor.name;
                if (name == 'Array') {
                    type = 'array[array]';
                } else if (name == 'Object') {
                    type = 'array[object]';
                } else if (name == 'String') {
                    type = 'array[string]'
                } else if (name == 'Number') {
                    type = 'array[number]'
                } else if (name == 'Boolean') {
                    type = 'array[boolean]'
                }
            }
            return type;
        }

        function parseImportData(data, temp) {
            if (data.constructor.name == 'Array') {
                var fullObj = {};
                data.forEach(function (d) {
                    if (d.constructor.name == 'Object') {
                        for (var key in d) {
                            fullObj[key] = d[key];
                        }
                    } else if (d.constructor.name == 'Array') {
                        parseImportData(d, temp);
                    }
                });
                parseImportData(fullObj, temp);
            } else if (data.constructor.name == 'Object') {
                for (var key in data) {
                    var v = data[key];
                    var t = {children: []};
                    t.name = key;
                    if (v != undefined) {
                        if (v.constructor.name == 'Object') {
                            t.type = 'object';
                            parseImportData(v, t.children);
                        } else if (v.constructor.name == 'Array') {
                            t.type = getArrayValueType(v);
                            if (t.type == 'array[object]') {
                                parseImportData(v, t.children);
                            } else if (t.type == 'array[array]') {
                                parseImportData(v[0], t.children);
                            }
                        } else if (v.constructor.name == 'String') {
                            t.type = 'string'
                        } else if (v.constructor.name == 'Number') {
                            t.type = 'number'
                        } else if (v.constructor.name == 'Boolean') {
                            t.type = 'boolean'
                        }
                    } else {
                        t.type = '';
                    }
                    t.require = 'true';
                    temp.push(t);
                }
            }
        }

        function checkId(arr){
            if(arr && arr.length>0){
                arr.forEach(function(item){
                    if(!item.id){
                        item.id = utils.generateUID();
                    }
                    if(item.children && item.children.length>0){
                        checkId(item.children);
                    }
                });
            }
        }

        return {
            _initsort_:_initsort_,
            parseImportData:parseImportData,
            checkId:checkId,
            headers:["User-Agent", "Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language", "Accept-Datetime", "Authorization", "Cache-Control", "Connection", "Cookie", "Content-Length", "Content-MD5", "Content-Type"],
            requests: ["name", "id", "password", "email", "createtime", "datetime", "createTime", "dateTime", "user", "code", "status", "type", "msg", "message", "time", "image", "file", "token", "accesstoken", "access_token", "province", "city", "area", "description", "remark", "logo"],
            responses: ["name", "id", "password", "email", "createtime", "datetime", "createTime", "dateTime", "user", "code", "status", "type", "msg", "message", "error", "errorMsg", "test", "fileAccess", "image", "require", "token", "accesstoken", "accessToken", "access_token", "province", "city", "area", "remark", "description", "logo"] 
        };



    });
})();