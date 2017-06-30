define(function(){
    var o={
        run:function(){
            this.removeListener();
            this.addListener();
        },
        addListener:function(){
            document.addEventListener('result.success', this.success);
            document.addEventListener('result.complete',this._complete);
            document.addEventListener('result.error',this.error);
        },
        removeListener:function(){
            document.addEventListener('result.success', this.success);
            document.addEventListener('result.complete',this._complete);
            document.addEventListener('result.error',this.error);
        },
        _complete:function(e){
            var details =e.detail;
            
            var xhr={
                beginTime:Date.now() - details.useTime,
                responseText:details.responseText,
                status:details.status,
                statusText:details.statusText,
                getAllResponseHeaders:function(){
                    return details.headers;
                },
                readyState:details.readyState
            };
            o.complete(xhr,details.statusText);
        },
        success:null,
        error:  null,
        complete: null
    };
    return o;
});