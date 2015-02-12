/**
 * 创建模块Tab对象的类
 * @param templateVersionId
 */

CommonModuleTab = function () {

    //初始化gird要显示的数据
    var store = createJsonStore('page/module/common/list', [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'caption',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'moduleConfig',
            type: 'string'
        },
        {
            name: 'moduleContent',
            type: 'string'
        },
        {
            name: 'editModuleContent',
            type: 'string'
        },
        {
            name: 'formContent',
            type: 'string'
        },
        {
            name: 'editFormContent',
            type: 'string'
        },
        {
            name: 'moduleGranularity',
            type: 'string'
        },
        {
            name: 'logicCode',
            type: 'string'
        },
        {
            name: 'editLogicCode',
            type: 'string'
        },
        {
            name: 'moduleJs',
            type: 'string'
        },
        {
            name: 'editModuleJs',
            type: 'string'
        },
        {
            name: 'moduleCssContent',
            type: 'string'
        }
    ]);


    var addCommonModule = function () {
        commitForm(Ext.getCmp('commonForm'), function () {
            Ext.getCmp("commonModuleGrid").commonModuleWin.hide();
            Ext.getCmp('commonForm').getForm().setValues({
                id: 0,
                name: '',
                editModuleContent: '',
                editLogicCode: '',
                editFormContent: '',
                moduleCssContent: '// 一行一个 css 文件, 相对路径即可.\n\n' +
                    '// 如想加载: http://assets.yijushang.com/css/common.css, 写 css/common.css 即可.\n' +
                    '// 双斜杠开头是注释, 会和空行一样被忽略, 就像你现在看到的这四行.\n\n',
                moduleConfig: '<?xml version="1.0" encoding="UTF-8"?>\n<module name="">\n<config>\n' +
                    '<isCacheable>false</isCacheable>\n</config>\n<params>\n' +
                    '<param name="你的参数名"  label="你的参数Label" dataType="" formType="text">你的参数值</param>\n' +
                    '</params>\n</module>'
            });
            Ext.getCmp("commonModuleGrid").getStore().reload();
        }, Ext.getCmp("commonModuleGrid").commitUrl);
    };

    function releaseModule(ids) {
        doAjax('page/module/common/release', function () {
            Ext.Msg.alert("成功", "成功发布到产品模式");
            Ext.getCmp("commonModuleGrid").getStore().reload();
        }, {ids: ids});
    }

    //调用超类构造函数
    CommonModuleTab.superclass.constructor.call(this, {
        title: '系统模块',
        id: 'commonModuleGrid',
        loadMask: true,
        store: store,
        columns: [
            {
                header: 'ID',
                dataIndex: 'id',
                width: 20
            },
            {
                header: '模块名',
                dataIndex: 'name'
            },
            {
                header: '模块说明',
                dataIndex: 'caption'
            }
        ],
        viewConfig: {
            forceFit: true
        },
        tbar: [
            {
                text: '系统模块',
                iconCls: 'add',
                handler: function () {
                    Ext.getCmp("commonModuleGrid").commonModuleWin.show('commonModuleGrid');
                    Ext.getCmp("commonModuleGrid").commitUrl = 'page/module/common/new';
                }
            },
            '-',
            {
                text: '预览',
                iconCls: 'preview',
                handler: function () {
                    var sm = Ext.getCmp("commonModuleGrid").getSelectionModel();
                    if (!sm.hasSelection()) {
                        Ext.Msg.alert('错误', '请选择要浏览的行');
                    } else if (sm.getSelections().length > 1) {
                        Ext.Msg.alert('错误', '请仅仅选择一行');
                    } else {
                        var record = sm.getSelected();
                        window.open('page/module/common/preview/' + record.get('name'));
                    }
                }
            },
            '-',
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    Ext.getCmp("commonModuleGrid").getStore().reload();
                }
            },
            '-',
            {
                text: '同步',
                iconCls: 'sync',
                handler: function () {
                    Ext.MessageBox.show({
                        title:'同步中',
                        msg : '正在同步，请稍后...',
                        width : 300,
                        wait : true,
                        progress : true,
                        closable : true,
                        waitConfig : {
                            interval : 400
                        },
                        icon : Ext.Msg.INFO
                    });
                    Ext.Ajax.request({
                        method:'POST',
                        url:'page/module/common/-1/push?type=all',
                        success:function (response, opts) {
                            Ext.MessageBox.hide();
                            var obj = Ext.decode(response.responseText);
                            Ext.Msg.alert("成功", obj.msg);
                        },
                        failure:function (response, opts) {
                            Ext.MessageBox.hide();
                            Ext.Msg.alert("失败", "同步失败");
                        }
                    });
                }

            },
            {
                text: '批量发布',
                iconCls: 'run',
                handler: function () {
                    var commonModuleGrid = Ext.getCmp("commonModuleGrid");
                    var sm = commonModuleGrid.getSelectionModel();
                    if (!sm.hasSelection()) {
                        Ext.Msg.alert('错误', '请选择要发布的模块');
                        return;
                    }
                    if (sm.getSelections().length > 0) {
                        var records = sm.getSelections();
                        var ids = [];
                        for (var i = 0; i < records.length; i++) {
                            ids.push(records[i].get('id'));
                        }
                        Ext.Msg.confirm("确认", "你确定批量发布这些模块吗？请确保调试完全没问题", function (btn) {
                            if (btn == 'yes') {
                                releaseModule(ids);
                            }
                        });


                    }
                }
            }
        ]
    });

    //ajax提交数据的URL
    this.commitUrl = '';

    this.currentEditId = -1;

    //系统模块表单
    this.commonForm = new Ext.FormPanel({
        id: 'commonForm',
        baseCls: 'x-plain',
        labelAlign: 'top',
        buttonAlign: 'center',
        frame: true,
        width: 500,

        items: [
            {
                xtype: 'hidden',
                name: 'id',
                value: 0
            },
            {
                xtype: 'textfield',
                fieldLabel: '模块名称',
                anchor: '100%',
                name: 'name',
                regex: /^\S+$/,
                allowBlank: false,
                maxLength: 45
            },
            {
                xtype: 'textfield',
                fieldLabel: '模块说明',
                anchor: '100%',
                name: 'caption',
                allowBlank: true,
                maxLength: 64
            },
            {
                xtype: 'combo',
                hiddenName: 'moduleGranularity',
                fieldLabel: '模块类型',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                anchor: '100%',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        [ '全局', 'GLOBAL' ],
                        [ '缺省', 'DEFAULT' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                allowBlank: false
            },
            {
                xtype: 'tabpanel',
                plain: true,
                activeTab: 0,
                height: 360,
                deferredRender: false,
                items: [
                    {
                        title: 'groovy 代码',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editLogicCode',
                            allowBlank: false,
                            value: "\ndef execute(context, params) {\n\treturn new HashMap()\n}\n\n" +
                                "def executeForm(context, params) {\n\treturn new HashMap()\n}\n"
                        }
                    },
                    {
                        title: 'js 脚本',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editModuleJs',
                            allowBlank: false,
                            value: '<script type="text/javascript"></script>'
                        }
                    },
                    {
                        title: 'vm 页面',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editModuleContent',
                            allowBlank: false,
                            value: '<div></div>'
                        }
                    },
                    {
                        title: '默认配置(xml)',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'moduleConfig',
                            allowBlank: false,
                            value: '<?xml version="1.0" encoding="UTF-8"?>\n<module name="">\n\t<config>\n\t\t' +
                                '<isCacheable>false</isCacheable>\n\t</config>\n\t<params>\n\t</params>\n</module>'
                        }
                    },
                    {
                        title: 'css 样式',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'moduleCssContent',
                            allowBlank: true,
                            value: "// 一行一个 css 文件, 相对路径即可.\n\n" +
                                "// 如想加载: http://assets.yijushang.com/css/common.css, 写 css/common.css 即可.\n" +
                                "// 双斜杠开头是注释, 会和空行一样被忽略, 就像你现在看到的这四行.\n\n"
                        }
                    },
                    {
                        title: '参数编辑(vm)',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editFormContent',
                            allowBlank: false,
                            value: '<div class="com_modify_wrapper com_edit">\n\t这个模块不需要编辑\n</div>'
                        }
                    }
                ]
            }
        ],
        buttons: [
            {
                text: '保存',
                handler: addCommonModule
            },
            {
                text: '取消',
                handler: function () {
                    Ext.getCmp('commonForm').getForm().setValues({
                        id: 0,
                        name: '',
                        editLogicCode: "",
                        editModuleJs: '',
                        editModuleContent: '',
                        moduleCssContent: '// 一行一个 css 文件, 相对路径即可.\n\n' +
                            '// 如想加载: http://assets.yijushang.com/css/common.css, 写 css/common.css 即可.\n' +
                            '// 双斜杠开头是注释, 会和空行一样被忽略, 就像你现在看到的这四行.\n\n',
                        moduleConfig: '<?xml version="1.0" encoding="UTF-8"?>\n<module name="">\n\t<config>\n\t\t' +
                            '<isCacheable>false</isCacheable>\n\t</config>\n\t<params>\n\t</params>\n</module>',
                        editFormContent: ''
                    });
                    Ext.getCmp("commonModuleGrid").commonModuleWin.hide();
                }
            }
        ]
    });
    var editCommonModule = function () {
        commitForm(Ext.getCmp('commonEditForm'), function () {
            Ext.Msg.alert('信息', '编辑成功，请尽快发布');
            Ext.getCmp("commonModuleGrid").getStore().reload();
        }, Ext.getCmp("commonModuleGrid").commitUrl);
    };

    this.commonEditForm = new Ext.FormPanel({
        id: 'commonEditForm',
        baseCls: 'x-plain',
        buttonAlign: 'center',
        labelAlign: 'top',
        frame: true,
        width: 500,

        items: [
            {
                xtype: 'hidden',
                name: 'id',
                value: 0
            },
            {
                xtype: 'textfield',
                fieldLabel: '模块名称',
                anchor: '100%',
                name: 'name',
                readOnly: true,
                regex: /^\S+$/,
                allowBlank: false,
                maxLength: 45
            },
            {
                xtype: 'textfield',
                fieldLabel: '模块说明',
                anchor: '100%',
                name: 'caption',
                readOnly: false,
                allowBlank: true,
                maxLength: 64
            },
            {
                xtype: 'combo',
                hiddenName: 'moduleGranularity',
                fieldLabel: '模块类型',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                anchor: '100%',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        [ '全局', 'GLOBAL' ],
                        [ '缺省', 'DEFAULT' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                allowBlank: false
            },
            {
                xtype: 'tabpanel',
                plain: true,
                activeTab: 0,
                height: 320,
                deferredRender: false,
                items: [
                    {
                        title: 'groovy 代码',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editLogicCode',
                            allowBlank: false
                        }
                    },
                    {
                        title: 'js 脚本',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editModuleJs',
                            allowBlank: false
                        }
                    },
                    {
                        title: 'vm 页面',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editModuleContent',
                            allowBlank: false
                        }
                    },
                    {
                        title: '默认参数配置(xml)',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'moduleConfig',
                            allowBlank: false
                        }
                    },
                    {
                        title: 'css 样式',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'moduleCssContent',
                            allowBlank: true
                        }
                    },
                    {
                        title: '参数编辑(vm)',
                        layout: 'fit',
                        items: {
                            xtype: 'textarea',
                            name: 'editFormContent',
                            allowBlank: false
                        }
                    }
                ]
            }
        ],
        buttons: [
            {
                text: '推送',
                handler: function () {
                    Ext.MessageBox.show({
                        title:'推送中',
                        msg : '正在推送，请稍后...',
                        width : 300,
                        wait : true,
                        progress : true,
                        closable : true,
                        waitConfig : {
                            interval : 400
                        },
                        icon : Ext.Msg.INFO
                    });
                    Ext.Ajax.request({
                        method:'POST',
                        url:'page/module/common/' + Ext.getCmp("commonModuleGrid").currentEditId + '/push?type=single',
                        success:function (response, opts) {
                            Ext.MessageBox.hide();
                            var obj = Ext.decode(response.responseText);
                            Ext.Msg.alert("成功", obj.msg);
                        },
                        failure:function (response, opts) {
                            Ext.MessageBox.hide();
                            Ext.Msg.alert("失败", "同步失败");
                        }
                    });
                }
            },
            {
                text: '发布',
                handler: function () {
                    var ids = [];
                    var id = Ext.getCmp('commonEditForm').getForm().findField('id').getValue();
                    ids.push(id);
                    Ext.Msg.confirm("确认", "你确定发布吗？请确保调试完全没问题", function (btn) {
                        if (btn == 'yes') {
                            releaseModule(ids);
                        }
                    });
                }
            },
            {
                text: '保存',
                handler: editCommonModule
            },
            {
                text: '取消',
                handler: function () {
                    Ext.getCmp('commonEditForm').getForm().setValues({
                        id: 0,
                        name: '',
                        editLogicCode: "",
                        editModuleJs: '',
                        editModuleContent: '',
                        moduleCssContent: '// 一行一个 css 文件, 相对路径即可.\n\n' +
                            '// 如想加载: http://assets.yijushang.com/css/common.css, 写 css/common.css 即可.\n' +
                            '// 双斜杠开头是注释, 会和空行一样被忽略, 就像你现在看到的这四行.\n\n',
                        moduleConfig: '<?xml version="1.0" encoding="UTF-8"?>\n<module name="">\n\t<config>\n\t\t' +
                            '<isCacheable>false</isCacheable>\n\t</config>\n\t<params>\n\t</params>\n</module>',
                        editFormContent: ''
                    });
                    Ext.getCmp("commonModuleGrid").commonEditModuleWin.hide();
                }
            }
        ]
    });


    this.commonModuleWin = new Ext.Window({
        title: '增加系统模块',
        layout: 'fit',
        width: 600,
        height: 540,
        plain: true,
        closeAction: 'hide',
        items: this.commonForm});

    this.commonEditModuleWin = new Ext.Window({
        title: '编辑系统模块',
        layout: 'fit',
        width: 600,
        height: 540,
        plain: true,
        closeAction: 'hide',
        items: this.commonEditForm});

    this.getStore().load();

    this.on('rowdblclick', function (grid, rowIndex, event) {
        var record = grid.getStore().getAt(rowIndex);
        Ext.getCmp('commonEditForm').getForm().loadRecord(record);
        grid.currentEditId = record.get('id');
        grid.commitUrl = 'page/module/common/update';
        grid.commonEditModuleWin.show('commonModuleGrid');
    });

};

Ext.extend(CommonModuleTab, Ext.grid.GridPanel);