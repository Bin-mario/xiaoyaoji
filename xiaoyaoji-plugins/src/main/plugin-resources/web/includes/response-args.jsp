<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script  type="text/template" id="response-args-template"class="hide">
    <div  v-bind:class="{'div-editing-table':editing}" class="placeholder-response-args">
    <div class="div-table-line " v-bind:class="{'div-editing-line':editing}"  v-for="(item,index) in responseArgs" :key="item.id" :data-id="item.id">
        <div v-if="editing">
        <ul class="cb">
            <li class="col-sm-1">
                <i class="iconfont icon-close" v-on:click="removeRow(item,responseArgs)"></i>
                <i class="iconfont icon-tianjia" v-on:click="insertRow(item)"></i>
                <i class="iconfont icon-drag-copy" v-on:dragstart="dragstart(responseArgs,item)"></i>
            </li>
            <li class="col-sm-3 input"><input type="text" list="responselist" class="text name" v-model="item.name" :value="item.name"></li>
            <li class="col-sm-2"><select v-model="item.require">
                <option value="true">true</option>
                <option value="false">false</option>
            </select></li>
            <li class="col-sm-2">
                <select v-model="item.type">
                    <option value="string">string</option>
                    <option value="number">number</option>
                    <option value="boolean">boolean</option>
                    <option value="object">object</option>
                    <option value="array">array</option>
                    <option value="array[number]">array[number]</option>
                    <option value="array[boolean]">array[boolean]</option>
                    <option value="array[string]">array[string]</option>
                    <option value="array[object]">array[object]</option>
                    <option value="array[array]">array[array]</option>
                </select>
            </li>
            <li class="col-sm-4 input full-height">
                <input type="text" class="text" v-model="item.description" :value="item.description">
            </li>
        </ul>
        </div>
        <div v-else>
        <ul class="cb">
            <li class="col-sm-3 name">
                <template v-if="item.type &&(item.type.indexOf('object')!=-1)">
                    <i class="iconfont icon-my open" v-on:click="apiArgsColumnFold($event)"></i>
                </template>
                <div class="value">{{item.name}}</div>
                <div class="hover">{{item.name}}</div>
            </li>
            <li class="col-sm-1">&nbsp;{{item.require || 'false'}} </li>
            <li class="col-sm-2" :title="item.type"> {{item.type}} </li>
            <li class="col-sm-6 full-height" :title="item.description">
                <div class="value">{{item.description}}</div>
                <div class="hover">{{item.description}}</div>
            </li>
        </ul>
        </div>

        <div class="sub">
            <response-args-vue v-bind:response-args="item.children" v-bind:editing="editing"></response-args-vue>
        </div>
    </div>
    </div>
</script>
<script>
    requirejs(['vue','${assets}/js/project/doc/component/table.js'],function(Vue,table){
        table = $.extend(true,{},table);
        table.template=document.getElementById('response-args-template').innerHTML;
        table.props=['responseArgs','editing','name'];
        Vue.component('ResponseArgsVue',table);
    });
</script>
