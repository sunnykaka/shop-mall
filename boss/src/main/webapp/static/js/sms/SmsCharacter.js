SmsCharacter = function () {

    var smsCharacterStore = createJsonStore('sms/smsCharacter/list', [
        {
            name:'id',
            type:'int'
        },
        {
            name:'value',
            type:'string'
        },
        {
            name:'name',
            type:'string'
        }
    ]);
    var characterGrid = new Ext.grid.GridPanel({
        store:smsCharacterStore,
        height:document.body.clientHeight * 0.30,
        loadMask:true,
        columns:[
            {
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'字符值',
                dataIndex:'value'
            }
            ,
            {
                header:'字符内容',
                dataIndex:'name'
            }
        ],
        region:'center',
        tbar:[
            {
                text:'添加',
                iconCls:'add',
                handler:function () {

                    var form = buildForm('sms/smsCharacter/add', [
                        {
                            text:'提交',
                            handler:function () {
                                commitForm(form, function () {
                                    form.ownerCt.close();
                                    characterGrid.getStore().reload()
                                    Ext.getCmp('smsCharacterCombo').getStore().reload();
                                })
                            }
                        }
                    ], [
                        {
                            fieldLabel:'字符值',
                            name:'value',
                            allowBlank:false
                        },
                        {
                            fieldLabel:'字符内容',
                            name:'name'
                        }
                    ]);

                    buildWin('新建字符', 500, form).show(this.id)

                }
            },
            {
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    characterGrid.getStore().reload();
                }
            },
            {
                text:'删除',
                iconCls:'delete',
                handler:function () {
                    doGridRowDelete(characterGrid, 'sms/smsCharacter/delete', function () {
                        characterGrid.getStore().reload()
                        Ext.getCmp('smsCharacterCombo').getStore().reload();
                    })
                }
            }
        ],
        viewConfig:{
            forceFit:true
        }

    });

    characterGrid.on('rowdblclick', function (grid, rowIndex, event) {
        var record = grid.getStore().getAt(rowIndex);
        var form = buildForm('sms/smsCharacter/update', [
            {
                text:'提交',
                handler:function () {
                    commitForm(form, function () {
                        form.ownerCt.close();
                        characterGrid.getStore().reload();
                        Ext.getCmp('smsCharacterCombo').getStore().reload();
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
                fieldLabel:'字符值',
                name:'value',
                value:record.get('value'),
                allowBlank:false
            },
            {
                fieldLabel:'字符内容',
                value:record.get('name'),
                name:'name'
            }
        ]);

        buildWin('修改模板', 500, form).show(this.id)

    });


    SmsCharacter.superclass.constructor.call(this, {
        title:'字符管理',
        layout:'border',
        items:characterGrid,
        listeners:{'render':function () {
            smsCharacterStore.reload();
        }
        }
    });
}

Ext.extend(SmsCharacter, Ext.Panel);