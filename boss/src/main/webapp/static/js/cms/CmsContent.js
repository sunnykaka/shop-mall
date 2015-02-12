CmsContent = function (id) {
    //初始化gird要显示的数据

    var store = getStore('cms/content/contentList/' + id, Ext.Model.Content);

    var addCommonModule = function () {
        KE.app.getEditor(Ext.getCmp("contentGrid").editorElId) && KE.app.getEditor(Ext.getCmp("contentGrid").editorElId).sync();

        var htmlForm = Ext.getCmp("contentGrid").currentForm;
        commitForm(htmlForm, function () {
            Ext.getCmp("contentGrid").currentWin.close();
            Ext.getCmp("contentGrid").getStore().reload();
        }, Ext.getCmp("contentGrid").commitUrl);
    };


    var templateStore = new Ext.data.Store({
        reader: new Ext.data.JsonReader({
            fields: [ 'id', 'name' ],
            root: 'data.templateList'
        }),
        proxy: new Ext.data.HttpProxy({
            url: 'cms/template/templateNotIndexList',
            method: 'GET'
        })
    });
    templateStore.load();

    //调用超类构造函数
    CmsContent.superclass.constructor.call(this, {
        title: '内容列表',
        id: 'contentGrid',
        loadMask: true,
        store: store,
        columns: [
            {
                header: '编号',
                dataIndex: 'id',
                width: 25
            },
            {
                header: '标题',
                dataIndex: 'title'
            } ,
            {
                header: '所属模板',
                dataIndex: 'templateName'
            }
            ,
            {
                header: '所属分类',
                dataIndex: 'categoryName'
            },
            {
                header: '优先级（小优先）',
                dataIndex: 'priority'
            }
        ],
        viewConfig: {
            forceFit: true
        },
        tbar: [
            new Ext.ux.CopyButton({
                text: '复制链接',
                iconCls: 'config',
                getValue: function () {
                    var contentGrid = Ext.getCmp("contentGrid");
                    var sm = contentGrid.getSelectionModel();
                    if (!sm.hasSelection()) {
                        Ext.Msg.alert('错误', '请选择要复制的内容链接');
                    } else if (sm.getSelections().length > 1) {
                        Ext.Msg.alert('错误', '不允许选择多行');
                    } else {
                        var records = sm.getSelections();
                        var id = records[0].get('id');
                        return window.BuyHome + "/help/" + id + ".html";
                    }
                }
            }), '-',
            {
                text: '添加内容',
                iconCls: 'add',
                handler: function () {
                    var tempCombo = new Ext.form.ComboBox({
                        xtype: 'comboBox',
                        anchor: '99%',
                        hiddenName: 'templateId',
                        fieldLabel: '选择模板',
                        mode: 'local',
                        editable: false,
                        store: templateStore,
                        triggerAction: 'all',
                        emptyText: '请选择',
                        displayField: 'name',
                        valueField: 'id',
                        allowBlank: false
                    });

                    var addEditor = new Ext.form.TextArea({
                        name: 'content',
                        fieldLabel: '内容描述',
                        anchor: '100%',
                        width: 700,
                        height: 500
                    });
                    Ext.getCmp("contentGrid").editorElId = addEditor.getId();
                    //内容模块表单
                    var contentForm = new Ext.FormPanel({
                        baseCls: 'x-plain',
                        buttonAlign: 'center',
                        frame: true,
                        width: 500,
                        listeners: {
                            'render': function () {
                                KE.app.init({
                                    renderTo: Ext.getCmp("contentGrid").editorElId,
                                    uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                                    filePostName: 'uploadFile',
                                    filterMode: false
                                });
                            }
                        },
                        items: [
                            {
                                xtype: 'hidden',
                                name: 'id',
                                value: 0
                            },
                            {
                                xtype: 'hidden',
                                name: 'categoryId',
                                value: id
                            },
                            {
                                xtype: 'textfield',
                                name: 'title',
                                anchor: '99%',
                                allowBlank: false,
                                fieldLabel: '标题'
                            },
                            tempCombo,
                            addEditor
                        ],
                        buttons: [
                            {
                                text: '保存',
                                handler: addCommonModule
                            },
                            {
                                text: '取消',
                                handler: function () {
                                    contentWin.close();
                                }
                            }
                        ]
                    });
                    Ext.getCmp("contentGrid").currentForm = contentForm;
                    var contentWin = new Ext.Window({
                        title: '增加内容',
                        layout: 'fit',
                        width: 800,
                        height: 650,
                        plain: true,
                        closeAction: 'close',
                        items: contentForm});

                    Ext.getCmp("contentGrid").currentWin = contentWin;
                    Ext.getCmp("contentGrid").commitUrl = 'cms/content/add';
                    contentWin.show(this.id);
                }
            },
            '-',
            {
                text: '删除',
                iconCls: 'remove',
                handler: function () {
                    doGridRowDelete(Ext.getCmp("contentGrid"), 'cms/content/delete', function () {
                        Ext.getCmp("contentGrid").getStore().reload();
                    });
                }
            },
            '-',
            {
                text: '设置内容优先级',
                iconCls: 'run',
                handler: function () {

                    changePriority('cms/content/contentList/' + id, 'cms/content/priority/update', function () {
                        Ext.getCmp("contentGrid").getStore().reload();
                    });

                }
            }, '-',
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    Ext.getCmp("contentGrid").getStore().reload();
                }
            }
        ]
    });

    //ajax提交数据的URL
    this.commitUrl = '';

    this.currentEditId = -1;

    //双击一行数据打开编辑窗口
    this.on('rowdblclick', function (grid, rowIndex, event) {
        var addEditor = new Ext.form.TextArea({
            name: 'content',
            fieldLabel: '内容描述',
            anchor: '100%',
            width: 700,
            height: 500
        });
        Ext.getCmp("contentGrid").editorElId = addEditor.getId();
        var tempCombo = new Ext.form.ComboBox({
            xtype: 'comboBox',
            anchor: '99%',
            hiddenName: 'templateId',
            fieldLabel: '选择模板',
            mode: 'local',
            editable: false,
            store: templateStore,
            triggerAction: 'all',
            emptyText: '请选择',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false
        });
        //内容模块表单
        var editContentForm = new Ext.FormPanel({
            baseCls: 'x-plain',
            buttonAlign: 'center',
            frame: true,
            width: 500,
            listeners: {
                'render': function () {
                    KE.app.init({
                        renderTo: Ext.getCmp("contentGrid").editorElId,
                        uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                        filePostName: 'uploadFile',
                        filterMode: false
                    });
                }
            },
            items: [
                {
                    xtype: 'hidden',
                    name: 'id',
                    value: 0
                },
                {
                    xtype: 'hidden',
                    name: 'categoryId',
                    value: id
                },
                {
                    xtype: 'textfield',
                    name: 'title',
                    anchor: '99%',
                    allowBlank: false,
                    fieldLabel: '标题'
                },
                tempCombo,
                addEditor
            ],
            buttons: [
                {
                    text: '保存',
                    handler: addCommonModule
                },
                {
                    text: '取消',
                    handler: function () {
                        contentEditWin.close();
                    }
                }
            ]
        });

        var contentEditWin = new Ext.Window({
            title: '编辑内容',
            layout: 'fit',
            width: 850,
            height: 650,
            plain: true,
            closeAction: 'close',
            items: editContentForm});
        var record = grid.getStore().getAt(rowIndex);

        editContentForm.getForm().loadRecord(record);
        if (record.get('templateId') <= 0) {
            tempCombo.setValue("");
        }
        grid.currentEditId = record.get('id');
        grid.currentForm = editContentForm;
        grid.currentWin = contentEditWin;

        grid.commitUrl = 'cms/content/update';
        contentEditWin.show('contentGrid');
    });
};

Ext.extend(CmsContent, Ext.grid.GridPanel);