SmsMould = function () {

    var smsMouldStore = createJsonStore('sms/smsMould/list', [
        {
            name:'id',
            type:'int'
        },
        {
            name:'content',
            type:'string'
        },
        {
            name:'description',
            type:'string'
        }
    ]);

    smsMouldStore.load();

    var grid = new Ext.grid.GridPanel({
        store:smsMouldStore,
        height:document.body.clientHeight * 0.30,
        loadMask:true,
        viewConfig:{
            forceFit:true
        },
        columns:[
            {
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'模板描述',
                dataIndex:'description'
            }
        ]
    });

    grid.on('rowdblclick', function (grid, rowIndex, event) {
        var record = grid.getStore().getAt(rowIndex);
        var form = buildForm('sms/smsMould/update', [
            {
                text:'提交',
                handler:function () {
                    commitForm(form, function () {
                        form.ownerCt.close();
                        grid.getStore().reload();
                        Ext.getCmp('smsMouldComboBox').getStore().reload();
                    })
                }
            }
        ], [
            {
                xtype:'hidden',
                name:'id',
                value:record.get('id')
            },
            {
                fieldLabel:'模板描述',
                name:'description',
                value:record.get('description'),
                allowBlank:false
            },
            {
                fieldLabel:'模板内容',
                name:'content',
                xtype:'textarea',
                height:200,
                value:record.get('content')
            }
        ]);

        buildWin('修改模板', 500, form).show(this.id)

    });


    SmsMould.superclass.constructor.call(this, {
        title:'短信模板',
        items: grid,
        tbar:[
            {
                text:'添加',
                iconCls:'add',
                handler:function () {

                    var form = buildForm('sms/smsMould/add', [
                        {
                            text:'提交',
                            handler:function () {
                                commitForm(form, function () {
                                    form.ownerCt.close();
                                    grid.getStore().reload();
                                    Ext.getCmp('smsMouldComboBox').getStore().reload();
                                })
                            }
                        }
                    ], [
                        {
                            fieldLabel:'模板描述',
                            name:'description',
                            allowBlank:false
                        },
                        {
                            fieldLabel:'模板内容',
                            name:'content',
                            height:200,
                            xtype:'textarea'
                        }
                    ]);

                    buildWin('新建模板', 500, form).show(this.id)

                }
            },
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
                    doGridRowDelete(grid, 'sms/smsMould/delete', function () {
                        grid.getStore().reload();
                        Ext.getCmp('smsMouldComboBox').getStore().reload();
                    })
                }
            }
        ]
    });
}

Ext.extend(SmsMould, Ext.Panel);