KindEditorHtmlDesc = function (product) {

    //初始化gird要显示的数据
    var store = createJsonStore('product/html/list/' + product.get('id'), [
        {
            name: 'productId',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'content',
            type: 'string'
        }
    ]);


    store.load();


    KindEditorHtmlDesc.superclass.constructor.call(this, {
        id: 'kindHtmlDescGrid',
        title: '描述管理',
        loadMask: true,
        store: store,
        columns: [
            {
                header: '名字',
                dataIndex: 'name'
            }
        ],
        viewConfig: {
            forceFit: true
        },
        tbar: [
            {
                text: '增加',
                iconCls: 'add',
                handler: function () {

                    var addEditor = new Ext.form.TextArea({
                        name: 'content',
                        fieldLabel: '内容描述',
                        width: 700,
                        height: 500
                    });

                    Ext.getCmp("kindHtmlDescGrid").editorElId = addEditor.getId();

                    var htmlDescForm = new Ext.FormPanel({
                        baseCls: 'x-plain',
                        buttonAlign: 'center',
                        labelWidth: 60,
                        frame: true,
                        width: 500,
                        defaults: {
                            anchor: '100%',
                            width: 350
                        },
                        defaultType: 'textfield',
                        listeners: {
                            'render': function () {
                                KE.app.init({
                                    renderTo: Ext.getCmp("kindHtmlDescGrid").editorElId,
                                    uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                                    filePostName: 'uploadFile'
                                });
                            }
                        },
                        items: [
                            {
                                xtype: 'hidden',
                                name: 'productId',
                                value: product.get('id')
                            },

                            {
                                fieldLabel: '名称',
                                name: 'name',
                                maxLength: 100,
                                allowBlank: false
                            },
                            addEditor
                        ],
                        keys: [
                            {
                                key: Ext.EventObject.ENTER,
                                fn: commitFunction //执行的方法
                            }
                        ],
                        buttons: [
                            {
                                text: '保存',
                                handler: commitFunction
                            },
                            {
                                text: '取消',
                                handler: function () {
                                    Ext.getCmp("kindHtmlDescGrid").currentWin.close();
                                }
                            }
                        ]
                    });
                    Ext.getCmp("kindHtmlDescGrid").currentForm = htmlDescForm;
                    var addWin = new Ext.Window({
                        layout: 'fit',
                        width: 800,
                        height: 650,
                        plain: true,
                        closeAction: 'close',
                        bodyStyle: 'padding:5px;',
                        items: htmlDescForm});
                    Ext.getCmp("kindHtmlDescGrid").currentWin = addWin;

                    addWin.show('kindHtmlDescGrid');
                    Ext.getCmp("kindHtmlDescGrid").commitUrl = 'product/html/new';
                }
            },
            '-',
            {
                text: '删除',
                iconCls: 'delete',
                handler: function () {
                    var kindHtmlDescGrid = Ext.getCmp("kindHtmlDescGrid");
                    var sm = kindHtmlDescGrid.getSelectionModel();
                    if (!sm.hasSelection()) {
                        Ext.Msg.alert('错误', '请选择要删除的行');
                    } else {
                        var records = sm.getSelections();
                        var names = [];
                        for (var i = 0; i < records.length; i++) {
                            names.push(records[i].get('name'));
                        }

                        doAjax('product/html/delete', function () {
                            Ext.getCmp("kindHtmlDescGrid").getStore().reload();
                        }, {
                            names: names,
                            productId: product.get('id')
                        }, "你确定删除这些描述吗？");

                    }
                }
            },
            '-',
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    Ext.getCmp("kindHtmlDescGrid").getStore().reload();
                }
            },
            '-'
        ]
    });


    var commitFunction = function () {
        KE.app.getEditor(Ext.getCmp("kindHtmlDescGrid").editorElId) && KE.app.getEditor(Ext.getCmp("kindHtmlDescGrid").editorElId).sync();

        var htmlForm = Ext.getCmp("kindHtmlDescGrid").currentForm;

        commitForm(htmlForm, function () {
            Ext.getCmp("kindHtmlDescGrid").currentWin.close();
            Ext.getCmp("kindHtmlDescGrid").getStore().reload();
            KE.app.getEditor(Ext.getCmp("kindHtmlDescGrid").editorElId) && KindEditor.remove(Ext.getCmp("kindHtmlDescGrid").editorElId);
        }, Ext.getCmp("kindHtmlDescGrid").commitUrl);

    };


    //双击一行数据打开编辑窗口
    this.on('rowdblclick', function (grid, rowIndex, event) {
        var record = grid.getStore().getAt(rowIndex);
        var editEditor = new Ext.form.TextArea({
            name: 'content',
            fieldLabel: '内容描述',
            width: 700,
            height: 500
        });

        Ext.getCmp("kindHtmlDescGrid").editorElId = editEditor.getId();

        var editForm = new Ext.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 80,
            buttonAlign: 'center',
            frame: true,
            width: 500,
            defaults: {
                anchor: '100%',
                width: 350
            },
            defaultType: 'textfield',
            listeners: {
                'render': function () {
                    KE.app.init({
                        renderTo: Ext.getCmp("kindHtmlDescGrid").editorElId,
                        uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                        filePostName: 'uploadFile'
                    });
                }
            },
            items: [
                {
                    xtype: 'hidden',
                    name: 'productId',
                    value: 0
                },
                {
                    fieldLabel: '页面名称',
                    name: 'name',
                    readOnly: true,
                    allowBlank: false,
                    regex: /^\S+$/,
                    maxLength: 100
                },
                editEditor
            ],
            keys: [
                {
                    key: Ext.EventObject.ENTER,
                    fn: commitFunction //执行的方法
                }
            ],
            buttons: [
                {
                    text: '保存',
                    handler: commitFunction
                },
                {
                    text: '取消',
                    handler: function () {
                        Ext.getCmp("kindHtmlDescGrid").currentWin.close();
                    }
                }
            ]
        });
        var editWin = new Ext.Window({
            layout: 'fit',
            width: 800,
            height: 650,
            plain: true,
            closeAction: 'close',
            bodyStyle: 'padding:5px;',
            items: editForm});
        editForm.getForm().loadRecord(record);
        editWin.show('kindHtmlDescGrid');
        grid.commitUrl = 'product/html/update';
        grid.currentForm = editForm;
        grid.currentWin = editWin;
    });

};

Ext.extend(KindEditorHtmlDesc, Ext.grid.GridPanel);