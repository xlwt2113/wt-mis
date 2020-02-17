
var config = {
    //查询页面中table的基本属性
    searchListTable : {
        method:'post', //设置提交方式为post
        elem: '#searchListTable',  //默认的查询列表页面中table的ID
        toolbar: '#TopTableBar',  //顶部工具栏的默认ID
        height: 'full-140',       //表格高度微调，适应页面
        //将返回的列表数据格式化成layui的格式
        parseData: function (res) { //res 即为原始返回的数据
            return {
                "code": res.code, //解析接口状态
                "msg": res.message, //解析提示文本
                "count": res.data.totalElements, //解析数据长度
                "data": res.data.content //解析数据列表
            };
        },
        response: {
            statusCode: 200 //规定成功的状态码，原框架默认：0
        },
        limits: [10, 15, 20, 25, 50, 100],
        limit: 15,
        page: true
    },
    //添加页面的基本属性
    add_layer : {
        type: 2 //iframe载入
        , anim: 2 //载入方式
        , area: ['600px', '500px'] //设置窗口宽高
        , title: '添加' //窗口标题
        , maxmin: true //允许最大化
        , btn: ['保存']
        , btnAlign: 'r' //按钮靠右
        , yes: function (index, layerObj) {
            //点击确认触发 iframe 内容中的按钮提交
            var submit = layerObj.find('iframe').contents().find("#saveBtn");
            submit.click();
        }
        ,success:function () {
            $(":focus").blur(); //防止按回车键后重复加载层
        }
    },
    //查看页面的基本属性
    view_layer : {
        type: 2 //iframe载入
        , anim: 2 //载入方式
        , area: ['600px', '500px'] //设置窗口宽高
        , title: '查看' //窗口标题
        , maxmin: true //允许最大化
        , btn: ['关闭']
        , btnAlign: 'r' //按钮靠右
        , yes: function (index, layerObj) {
            layer.close(index);
        },success:function () {
            $(":focus").blur();//防止按回车键后重复加载层
        }
    },
    //编辑页面的基本属性
    edit_layer :{
        type: 2 //iframe载入
        , anim: 2 //载入方式
        , area: ['600px', '500px'] //设置窗口宽高
        , title: '修改' //窗口标题
        , maxmin: true //允许最大化
        , btn: ['保存']
        , btnAlign: 'r' //按钮靠右
        , yes: function (index, layerObj) {
            //点击确认触发 iframe 内容中的按钮提交
            var submit = layerObj.find('iframe').contents().find("#saveBtn");
            submit.click();
        },success:function () {
            $(":focus").blur();//防止按回车键后重复加载层
        }
    },
    //列表页绵中通用的删除数据操作
    delete : function (obj,delete_url) {
        layer.confirm('真的删除本行吗？', {icon:3},function (index) {
            layer.close(index); //先执行关闭，避免重复点击按钮
            $.get(delete_url + "?id=" + obj.data.id, function (result) {
                let r = JSON.parse(result);
                if(r.code == 200){
                    obj.del();
                }
                layer.msg(r.message);
            })
        });
    },
    //列表页绵中通用的打开页面操作（表格中选中的行对象，查看页面的路径，打开层的自定义参数）
    view : function (obj,view_page_url,layer_params) {
        layer_params = layer_params||{};
        layer_params = $.extend(layer_params,{content: view_page_url + "?id=" + obj.data.id });
        layer.open($.extend(config.view_layer,layer_params));
    },
    //列表页绵中通用的打开添加页面操作
    add: function (add_page_url,layer_params) {
        layer_params = layer_params||{};
        layer_params = $.extend(layer_params,{content: add_page_url});
        layer.open($.extend(config.add_layer,layer_params));
    },
    //列表页绵中通用的打开编辑页面操作
    edit: function (obj,edit_page_url,layer_params) {
        layer_params = layer_params||{};
        layer_params = $.extend(layer_params,{content: edit_page_url + "?id=" + obj.data.id });
        layer.open($.extend(config.edit_layer,layer_params));
    },
    //列表页绵中通用的删除操作，删除多条（layui的table对象，删除路径）
    deleteAll:function (table,delete_url) {
        var checkStatus = table.checkStatus('searchListTable');
        if (checkStatus.data.length > 0) {
            layer.confirm('真的删除所选数据吗？',{icon:3}, function (index) {
                var ids = [];
                $.each(checkStatus.data, function (i, n) {
                    ids[i] = n.id;
                });
                layer.close(index); //先执行关闭，避免重复点击按钮
                $.post(delete_url,{ids: ids.toString()},function (result) {
                    let r = JSON.parse(result);
                    if(r.code == 200){
                        layui.table.reload('searchListTable');
                    }
                    layer.msg(r.message);
                });
            });
        } else {
            layer.msg('请选择要删除的数据！');
        }
    },
    //获取树形组件选中的Id
    // treeNode  ： tree.getChecked('menuTree')返回的数据，
    // ids  ： 存储选中id的数组
    getTreeSelectIds:function(treeNode,ids){
        $.each(treeNode, function(i, node){
            ids.push(node.id);
            config.getTreeSelectIds(node.children,ids);
        });
    },



};


