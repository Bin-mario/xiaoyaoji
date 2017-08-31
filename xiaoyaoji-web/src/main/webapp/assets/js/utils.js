(function () {
    define(function (require, exports) {
        var utils = {
            config: {
                root: '',
                ctx: x.ctx,
                vue: false,
                websocket: ('ws://' + location.host)
            },
            push: function (data, item) {
                if (data === undefined) {
                    data = [];
                }
                data.push(item);
            },
            token: function (token) {
                if (token) {
                    localStorage.setItem("token", token);
                } else {
                    return localStorage.getItem("token") || '';
                }
            },
            toJSON: function (data) {
                if (data === undefined || data === null)
                    return data;
                if (data.constructor.name === 'String') {
                    return JSON.parse(data);
                }
                return data;
            },
            ajax: function (params) {
                var url = this.config.root + params.url;
                params.url = x.ctx + url;
                params.xhrFields={withCredentials:true};
                $._ajax_(params);
            },
            get: function (url, params, success, complete, expired) {
                this.ajax({
                    url: url,
                    cache:false,
                    data: params,
                    type: 'get',
                    dataType: 'json',
                    success: success,
                    complete: complete,
                    expired: expired
                });
            },
            post: function (url, data, success, error) {
                this.ajax({
                    url: url,
                    data: data,
                    type: 'post',
                    dataType: 'json',
                    success: success,
                    error: error
                });
            },
            put:function(url, data, success, error){
                this.ajax({
                    url: url,
                    data: data,
                    type: 'put',
                    dataType: 'json',
                    success: success,
                    error: error
                });
            },
            fileloader: function (url, data, success, error) {
                this.ajax({
                    url: url,
                    data: data,
                    type: 'post',
                    cache: false,
                    processData: false,
                    contentType: false,
                    dataType: 'json',
                    success: success,
                    error: error
                });
            },
            "delete": function (url, success, error) {
                this.ajax({
                    url: url,
                    type: 'delete',
                    success: success,
                    dataType: 'json',
                    error: error
                });
            },
            escape: function (str) {
                if (!str)
                    return '';
                return str.replace(/\</g, '&lt;')
                    .replace(/\>/g, '&gt;')
                    .replace(/\"/g, "&quot;")
                    .replace(/\'/g, "&apos;")

            },
            unescape: function (str) {
                if (!str)
                    return '';
                return str.replace(/&lt;/g, '<')
                    .replace(/&gt;/g, '>')
                    .replace(/&quot;/g, "\"")
                    .replace(/&apos;/g, "'")

            },
            getQueryParams: function (qs) {
                qs = qs.split('+').join(' ');
                var params = {},
                    tokens,
                    re = /[?&]?([^=]+)=([^&]*)/g;
                while (tokens = re.exec(qs)) {
                    params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
                }
                return params;
            },
            login: {
                submit: function (url, data) {
                    utils.post(url, data, function (rs) {
                        localStorage.clear();
                        location.href = x.ctx + '/dashboard?v='+x.v;
                    });
                },
                success: function (href) {
                    if (href) {
                        location.href = href;
                    } else {
                        location.href = x.ctx + '/dashboard?v='+x.v;
                    }
                }
            },
            copy: function (source) {
                return $.extend(true, {}, source);
            },
            copyArray: function (source) {
                return $.extend(true, [], source);
            },
            args2Params: function (args) {
                var params = '';
                for (var p in args) {
                    params += (p + '=' + args[p] + '&');
                }
                return params;
            }
        };
        $._ajax_ = function (params) {
            if (params.data) {
                for (var key in params.data) {
                    if (params.data[key] === undefined || params.data[key] === null) {
                        delete params.data[key];
                    }
                }
            }
            var complete = params.complete;
            var success = params.success;
            params.complete = function (xhr, result) {
                if (result === 'error') {
                    if (xhr.readyState === 0) {
                        toastr.error('网络错误');
                    } else {
                        console.log(arguments)
                    }
                }
                if (complete) {
                    complete.apply(this, arguments);
                }
            };
            var expired = params.expired;
            params.success = function (rs) {
                if (rs.code === 0) {
                    if (success) {
                        success.apply(this, arguments);
                    }
                } else if (rs.code === -2) {
                    if (expired && expired(rs)) {
                        return true;
                    }
                    if (location.href.indexOf('/project/demo') !== -1) {
                        toastr.error('请登陆后尝试');
                        return true;
                    }
                    localStorage.setItem("token", "");
                    localStorage.setItem("user", "");
                    location.href = x.ctx + '/login?status=expired&refer=' + encodeURIComponent(location.href);
                } else {
                    toastr.error(rs.errorMsg);
                }
            };
            $.ajax(params);
        };

//fix id bug start
        $.support.cors = true;
        if (Function.prototype.name === undefined && Object.defineProperty !== undefined) {
            Object.defineProperty(Function.prototype, 'name', {
                get: function () {
                    var funcNameRegex = /function\s([^(]{1,})\(/;
                    var results = (funcNameRegex).exec((this).toString());
                    return (results && results.length > 1) ? results[1].trim() : "";
                },
                set: function (value) {
                }
            });
        }
        (function () {
            function CustomEvent(event, params) {
                params = params || {bubbles: false, cancelable: false, detail: undefined};
                var evt = document.createEvent('CustomEvent');
                evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
                return evt;
            }

            CustomEvent.prototype = window.Event.prototype;
            window.CustomEvent = CustomEvent;
        })();
//fix id bug end


//扩展
        if (!Array.prototype.findIndex) {
            Array.prototype.findIndex = function (predicate) {
                if (this === null) {
                    throw new TypeError('Array.prototype.findIndex called on null or undefined');
                }
                if (typeof predicate !== 'function') {
                    throw new TypeError('predicate must be a function');
                }
                var list = Object(this);
                var length = list.length >>> 0;
                var thisArg = arguments[1];
                var value;

                for (var i = 0; i < length; i++) {
                    value = list[i];
                    if (predicate.call(thisArg, value, i, list)) {
                        return i;
                    }
                }
                return -1;
            };
        }
        Array.prototype.move = function (old_index, new_index) {
            /*var temp = this[old_index];
             this[old_index]=this[new_index];
             this[new_index]=temp;*/
            if (new_index >= this.length) {
                var k = new_index - this.length;
                while ((k--) + 1) {
                    this.push(undefined);
                }
            }
            this.splice(new_index, 0, this.splice(old_index, 1)[0]);
            return this; // for testing purposes
        };

        Array.prototype.mergeArray = function(source){
            //var target = this.slice();
            var target = $.extend(true,[],this);
            if(source && source.length>0){
                var targetKey={};
                target.forEach(function(item){
                    targetKey[item.name]=item;
                });
                source.forEach(function(item){
                    var temp = targetKey[item.name];
                    if(!temp){
                        temp = item;
                        targetKey[item.name]=temp;
                        target.push(temp);
                    }
                    if(!temp.children){
                        temp.children=[];
                    }
                    if(item.children && item.children.length>0){
                        item.children = temp.children.mergeArray(item.children);
                    }

                });
            }
            return target;
        };

        utils.generateUID = function() {
            // I generate the UID from two parts here 
            // to ensure the random number provide enough bits.
            var firstPart = (Math.random() * 46656) | 0;
            var secondPart = (Math.random() * 46656) | 0;
            firstPart = ("000" + firstPart.toString(36)).slice(-3);
            secondPart = ("000" + secondPart.toString(36)).slice(-3);
            return firstPart + secondPart;
        };
        return utils;

    });
})();
