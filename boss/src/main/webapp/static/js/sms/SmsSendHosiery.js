SmsSendHosiery = function () {

    var smsMouldStore = createJsonStore('sms/messageTask/list', [
        {
            name:'id',
            type:'int'
        },
        {
            name:'contact',
            type:'string'
        },
        {
            name:'content',
            type:'string'
        },
        {
            name:'sendSuccess',
            type:'boolean'
        },
        {
            name:'sendCount',
            type:'int'
        },
        {
            name:'date',
            type:'string'
        }
    ]);
    smsMouldStore.load({
        params:{
            start:0,
            limit:20
        }
    });

    var isSuccess=function(value){
       if(value){
          return '是';
       }else{
           return '否';
       }
    };

    var grid = new Ext.grid.GridPanel({
        store:smsMouldStore,
        height:document.body.clientHeight * 0.30,
        loadMask:true,
        region:'center',
        viewConfig:{
            forceFit:true
        },
        bbar: new Ext.PagingToolbar({
            pageSize: 20,
            store: smsMouldStore,
            displayInfo: true
        }),
        columns:[
            {
                header:'发送号码',
                dataIndex:'contact'
            },
            {
                header:'发送内容',
                dataIndex:'content'
            },
            {
                header:'是否成功',
                dataIndex:'sendSuccess',
                renderer:isSuccess
            },
            {
                header:'发送次数',
                dataIndex:'sendCount'
            },
            {
                header:'短信生成时间(发送会延迟)',
                dataIndex:'date'
            }
        ],
        tbar:[
            {
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    grid.getStore().reload();
                }
            },
            {
                text:'删除',
                iconCls:'delete',
                handler:function () {
                    doGridRowDelete(grid, 'sms/messageTask/delete', function () {
                        grid.getStore().reload();
                    })
                }
            }
        ]
    });

    SmsSendHosiery.superclass.constructor.call(this, {
        title:'发送记录',
        layout:'border',
        items: grid
    });
}

Ext.extend(SmsSendHosiery, Ext.Panel);