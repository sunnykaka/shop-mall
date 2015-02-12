AttentionInfo = function (productId) {

    var useStore = new Ext.data.JsonStore({
        url: 'product/attentionInfo/useList/' + productId,
        root: 'data.UseList',
        fields: ['id', 'pictureUrl', 'whether', 'info']
    });
    useStore.load();

    var maintenanceStore = new Ext.data.JsonStore({
        url: 'product/attentionInfo/maintenanceList/' + productId,
        root: 'data.maintenanceList',
        fields: ['id', 'pictureUrl', 'whether', 'info']
    });
    maintenanceStore.load();
    var textRow = [];
    var attentionInfoForm = new Ext.FormPanel({
        items: [
            {
                xtype: 'hidden',
                name: 'productId',
                value: productId
            },
            {
                fieldLabel: '注意信息',
                name: 'info',
                allowBlank: false,
                maxLength: 100,
                regex: /^\S+$/,
                xtype: 'textfield'
            },
            {
                xtype: 'combo',
                hiddenName: 'type',
                fieldLabel: '信息类别',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        ['使用注意', 'Use' ],
                        ['保养注意', 'Maintenance' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                allowBlank: false
            }
        ],
        border: false,
        height: 400,
        url: 'product/attentionInfo/create',
        bodyStyle: 'background:transparent;padding:10px;',
        defaults: {
            anchor: '100%'
        }
    });
    var fm = Ext.form;
    var sm = new Ext.grid.CheckboxSelectionModel();
    var sm1 = new Ext.grid.CheckboxSelectionModel();
    this.cm = new Ext.grid.ColumnModel({
        columns: [  sm,
            {dataIndex: 'id', hidden: true, name: 'id'},
            {width: 100, sortable: true, dataIndex: 'info', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 100
            })}
        ]
    });
    var cm1 = new Ext.grid.ColumnModel({
        columns: [sm1,
            {dataIndex: 'id', hidden: true, name: 'id'},
            {width: 100, sortable: true, dataIndex: 'info', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 100
            })}
        ]
    });


    useGrid = new Ext.grid.EditorGridPanel({
        title: '使用注意事项',
        id: 'useInfo',
        border: false,
        store: useStore,
        loadMask: true,
        sm: sm1,
        cm: cm1,
        viewConfig: {
            forceFit: true
        },
        region: 'north',
        height: 320,
        clicksToEdit: 1
    });

    maintenanceGrid = new Ext.grid.EditorGridPanel({
        title: '保养注意事项',
        id: 'maintenanceInfo',
        border: false,
        store: maintenanceStore,
        cm: this.cm,
        loadMask: true,
        sm: sm,
        viewConfig: {
            forceFit: true
        },
        region: 'center',
        height: 320,
        clicksToEdit: 1

    });

    var addInfo = function () {

        commitForm(attentionInfoForm, function () {
            attentionInfoForm.getForm().reset();
            win.hide();
            maintenanceStore.load();
            useStore.load();
        });

    }

    var win = new Ext.Window({
        autoHeight: true,
        buttonAlign: 'center',
        width: 400,
        resizable: false,
        plain: true,
        modal: true,
        closeAction: 'hide',
        title: '增加信息',
        keys: [
            {
                key: Ext.EventObject.ENTER,
                fn: addInfo //执行的方法
            }
        ],

        buttons: [
            {
                text: '提交',
                handler: addInfo
            },
            {
                text: '多行数据添加',
                handler: function () {
                    var text = new Ext.form.TextField({
                        fieldLabel: '注意信息',
                        regex: /^\S+$/,
                        name: 'info'
                    });
                    attentionInfoForm.add(text);
                    textRow.push(text);
                    attentionInfoForm.doLayout();
                }
            }
        ],

        items: attentionInfoForm
    });

    AttentionInfo.superclass.constructor.call(this, {
        title: '注意事项',
        layout: 'border',
        tbar: [
            {
                text: '增加信息',
                iconCls: 'add',
                handler: function () {
                    if (textRow != null || textRow.length > 0) {
                        Ext.each(textRow, function (row) {
                            attentionInfoForm.remove(row);
                        })
                    }
                    textRow = [];
                    win.show(this.id);
                }
            },
            '-',
            {
                text: '删除信息',
                iconCls: 'remove',
                handler: function () {
                    var ids = [];
                    var msm = maintenanceGrid.getSelectionModel();
                    var usm = useGrid.getSelectionModel();
                    var rows1 = msm.getSelections();
                    var rows2 = usm.getSelections();
                    if (rows1.length > 0 || rows2.length > 0) {
                        for (var i = 0; i < rows1.length; i++) {
                            var id = rows1[i].get('id');
                            ids.push(id);
                        }
                        for (var i = 0; i < rows2.length; i++) {
                            var id = rows2[i].get('id');
                            ids.push(id);
                        }
                    } else {
                        Ext.Msg.alert('提示', '请选择要删除的记录');
                        return;
                    }

                    doAjax('product/attentionInfo/delete', function () {
                        maintenanceStore.load();
                        useStore.load();
                    }, {
                        ids: ids
                    }, "你确定删除这些信息吗？");

                }
            },
            '-',
            {

                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    maintenanceStore.load();
                    useStore.load();
                }
            } ,
            '-'
        ],
        items: [useGrid, maintenanceGrid]
    });
    maintenanceGrid.on("afteredit", function (e) {
        doAjax('product/attentionInfo/update', function () {
            maintenanceStore.load();
            Ext.Msg.alert('成功', "更新成功");
        }, {
            id: e.record.get('id'),
            info: e.record.get('info')
        });

    }, this);

    useGrid.on("afteredit", function (e) {
        doAjax('product/attentionInfo/update', function () {
            useStore.load();
            Ext.Msg.alert('成功', "更新成功");
        }, {
            id: e.record.get('id'),
            info: e.record.get('info')
        });

    }, this);
};

Ext.extend(AttentionInfo, Ext.Panel);