/**
 * 如果ajax失败，显示失败信息
 * @param response
 */
function statusTips(response) {
    if (response.status == 400) {
        Ext.Msg.alert('错误', '参数不对');
        return;
    }
    if (response.status == 403) {
        Ext.Msg.alert('错误', '没有设置访问权限');
        return;
    }
    if (response.status == 404) {
        Ext.Msg.alert('错误', '访问路径不存在');
        return;
    }

    Ext.Msg.alert("错误", "服务器出错，错误码:" + response.status);
}

/**
 * 提交表单
 * @param form
 * @param callBack
 */
function commitForm(form, callBack, url, extra) {
    if (!form.getForm().isValid()) {
        return;
    }

    var submitObject = {
        success:function (form, action) {
            callBack && callBack(action, Ext.util.JSON.decode(action.response.responseText));
        },
        failure:function (form, action) {
            var result = Ext.util.JSON.decode(action.response.responseText);
            if (action.response.status == 200 || result.msg!="") {
                Ext.Msg.alert('错误', result.msg);
                return;
            }
            Ext.Msg.alert('错误', '服务器出错，错误码:' + action.response.status);
        }
    }

    if (extra) {
        Ext.apply(submitObject, extra);
    }

    if (url) {
        submitObject.url = url;
    }

    form.getForm().submit(submitObject);
}


//提交ajax请求
function doAjax(url, callBack, params, confirmMsg) {
    function request() {
        Ext.Ajax.request({
            url:url,
            success:function (response) {
                var obj = Ext.decode(response.responseText);
                if (!obj.success) {
                    Ext.Msg.alert('错误', obj.msg);
                } else {
                    callBack && callBack(obj, response.responseText);
                }
            },
            failure:function (response) {
                statusTips(response);
            },
            params:params
        });
    }

    if (confirmMsg) {
        Ext.Msg.confirm("确认", confirmMsg, function (btn) {
            if (btn == 'yes') {
                request();
            }
        });
    } else {
        request();
    }
}


function createJsonStore(url, fields) {
    var jsonStore = new Ext.data.JsonStore({
        proxy:new Ext.data.HttpProxy({
            url:url,
            failure:statusTips
        }),
        totalProperty:'data.totalCount',
        root:'data.result',
        fields:fields
    });
    return jsonStore;
}


function getStore(url, model) {
    var jsonStore = new Ext.data.JsonStore({
        autoLoad:true,
        proxy:new Ext.data.HttpProxy({
            url:url,
            failure:statusTips
        }),
        baseParams:model.baseParams,
        totalProperty:'data.totalCount',
        root:'data.result',
        fields:model.recordDef
    });
    return jsonStore;
}


/**
 * 设置优先级的公用代码
 * @param getUrl
 * @param updateUrl
 * @param callBack
 */
function changePriority(getUrl, updateUrl, callBack) {

    var store = createJsonStore(getUrl, [
        {
            name:'name',
            type:'string'
        },
        {
            name:'id',
            type:'int'
        },
        {
            name:'priority',
            type:'int'
        }
    ]);

    store.load();


    var grid = new Ext.grid.EditorGridPanel({
        border:false,
        store:store,
        columns:[
            {
                header:'名字',
                dataIndex:'name',
                width:300
            },

            {
                header:'优先级(小优先)',
                dataIndex:'priority',
                id:'priority',
                editor:new Ext.form.NumberField({
                    allowBlank:false
                })
            }
        ],
        viewConfig:{forceFit:true},
        height:400,
        clicksToEdit:1});


    grid.on("afteredit", function (e) {
        var record = e.record;
        doAjax(updateUrl, function () {
            grid.getStore().reload();
            callBack();
        }, {
            priority:record.get('priority'),
            id:record.get('id')
        });

    });


    var win = new Ext.Window({
        title:'设置优先级',
        width:500,
        height:450,
        layout:'fit',
        items:grid
    });


    win.show(this.id);
}


/**
 * 表格组件的删除行公用代码
 * @param grid
 * @param url
 *
 * interrupt是一个中断函数
 */
function doGridRowDelete(grid, url, callBack, interrupt) {
    var sm = grid.getSelectionModel();
    if (!sm.hasSelection()) {
        Ext.Msg.alert('错误', '请选择要删除的数据');
    } else {
        var records = sm.getSelections();
        var ids = [];

        for (var i = 0; i < records.length; i++) {
            var record = records[i];
            if (interrupt && interrupt(record)) {
                return;
            }
            ids.push(record.get('id'));
        }

        doAjax(url, callBack, { ids:ids }, "你确定删除这些数据吗？");
    }
}


/**
 * 创建一个自适应高度和fit布局的窗口
 * @param title
 * @param width
 * @param items
 * @return {Ext.Window}
 */
function buildWin(title, width, items) {

    var config = {
        title:title,
        width:width,
        autoHeight:true,
        layout:'fit',
        bodyStyle:'padding:5px;',
        items:items
    };

    return new Ext.Window(config);
}


/**
 * 创建一个表单
 */
function buildForm(url, buttons, items) {
    return new Ext.FormPanel({
        baseCls:'x-plain',
        labelWidth:80,
        url:url,
        buttonAlign:'center',
        autoHeight:true,
        defaults:{
            anchor:'100%',
            xtype:'textfield'
        },
        buttons:buttons,
        items:items});
}