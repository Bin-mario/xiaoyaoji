<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/template" id="response-headers-template" class="hide">
    <div v-bind:class="{'div-editing-table':editing}" class="placeholder-response-headers">
    <div class="div-table-line " v-bind:class="{'div-editing-line':editing}" v-for="item in responseHeaders" :key="item.id" :data-id="item.id">
        <div v-if="editing">
        <ul class="cb">
            <li class="col-sm-1">
                <i class="iconfont icon-close" v-on:click="removeRow(item,responseHeaders)"></i>
                <i class="iconfont icon-drag-copy" v-on:dragstart="dragstart(responseHeaders,item)"></i>
            </li>
            <li class="col-sm-3 input"><input type="text" list="headerlist" class="text name" v-model="item.name" :value="item.name"></li>
            <li class="col-sm-2"><select v-model="item.require">
                <option value="true">true</option>
                <option value="false">false</option>
            </select></li>
            <li class="col-sm-6 input"><input type="text" class="text" v-model="item.description" :value="item.description"></li>
        </ul>
        </div>
        <div v-else>
        <ul class="cb">
            <li class="col-sm-2 name">
                <template v-if="item.type &&( item.type=='object' || item.type.indexOf('array')!=-1)">
                    <i class="iconfont icon-my open" v-on:click="apiArgsColumnFold($event)"></i>
                </template>
                <div class="value">{{item.name}}</div>
                <div class="hover">{{item.name}}</div>
            </li>
            <li class="col-sm-1"> {{item.require || 'false' }} </li>
            <li class="col-sm-9 full-height" :title="item.description">
                <div class="value">{{item.description}}</div>
                <div class="hover">{{item.description}}</div>
            </li>
        </ul>
        </div>

        <div class="sub" >
            <response-headers-vue v-bind:request-headers.sync="item.children" v-bind:editing="editing"></response-headers-vue>
        </div>
    </div>
    </div>
</script>
<script>
    requirejs(['vue','${assets}/js/project/doc/component/table.js'],function(Vue,table){
        table = $.extend(true,{},table);
        table.template=document.getElementById('response-headers-template').innerHTML;
        table.props=['responseHeaders','editing','name'];
        Vue.component('ResponseHeadersVue',table);
    });
</script>
