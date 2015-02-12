CmsTemplate = function () {

    var store = getStore('cms/template/list', Ext.Model.CmsTemplate);

    var addTemplate = function () {
        commitForm(Ext.getCmp('templateForm'), function () {
            Ext.getCmp("templateWin").close();
            Ext.getCmp("templateGrid").getStore().reload();
        }, Ext.getCmp("templateGrid").commitUrl);
    };

    //调用超类构造函数
    CmsTemplate.superclass.constructor.call(this, {
        title: '模板列表',
        id: 'templateGrid',
        loadMask: true,
        store: store,
        columns: [
            {
                header: '编号',
                dataIndex: 'id',
                width: 20
            },
            {
                header: '模板名称',
                dataIndex: 'name'
            }
        ],
        viewConfig: {
            forceFit: true
        },
        tbar: [
            {
                text: '添加模板',
                iconCls: 'add',
                handler: function () {
                    loadContentForm();
                    var templateWin = new Ext.Window({
                        title: '添加模板',
                        id: 'templateWin',
                        layout: 'fit',
                        width: 800,
                        height: 500,
                        plain: true,
                        closeAction: 'close',
                        items: Ext.getCmp('templateForm')});
                    Ext.getCmp("templateGrid").commitUrl = 'cms/template/add';
                    templateWin.show(this.id);
                }
            },
            '-',
            {
                text: '删除',
                iconCls: 'remove',
                handler: function () {
                    doGridRowDelete(Ext.getCmp("templateGrid"), 'cms/template/delete', function () {
                        Ext.getCmp("templateGrid").getStore().reload();
                    });
                }
            },
            '-',
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    Ext.getCmp("templateGrid").getStore().reload();
                }
            }
        ]
    });

    //ajax提交数据的URL
    this.commitUrl = '';

    this.currentEditId = -1;

    //双击一行数据打开编辑窗口
    this.on('rowdblclick', function (grid, rowIndex, event) {
        loadContentForm();
        var templateWin = new Ext.Window({
            title: '编辑内容',
            id: 'templateWin',
            layout: 'fit',
            width: 800,
            height: 500,
            plain: true,
            closeAction: 'close',
            items: Ext.getCmp('templateForm')});
        var record = grid.getStore().getAt(rowIndex);
        Ext.getCmp('templateForm').getForm().loadRecord(record);
        grid.currentEditId = record.get('id');
        grid.commitUrl = 'cms/template/update';
        templateWin.show('templateGrid');
    });

    function loadContentForm() {
        //内容模块表单
        var templateForm = new Ext.FormPanel({
            id: 'templateForm',
            baseCls: 'x-plain',
            buttonAlign: 'center',
            frame: true,
            items: [
                {
                    xtype: 'hidden',
                    name: 'id',
                    value: 0
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '90%',
                    allowBlank: false,
                    fieldLabel: '模板名称'
                },
                {
                    name: 'templateType',
                    anchor: '90%',
                    xtype: 'combo',
                    hiddenName: 'templateType',
                    fieldLabel: '模板类型',
                    mode: 'local',
                    editable: false,
                    triggerAction: 'all',
                    emptyText: '请选择',
                    store: new Ext.data.ArrayStore({
                        fields: ['id', 'type' ],
                        data: [
                            [ 'Single', '单页' ],
                            [ 'NavOne', '一级导航' ],
                            [ 'NavTwo', '二级导航' ],
                            [ 'Index', '首页' ]
                        ]
                    }),
                    displayField: 'type',
                    valueField: 'id'
                },
                {
                    name: 'templateContent',
                    fieldLabel: '模块代码',
                    xtype: 'textarea',
                    anchor: '90%',
                    height: 400
                }
            ],
            buttons: [
                {
                    text: '保存',
                    handler: addTemplate
                },
                {
                    text: '取消',
                    handler: function () {
                        Ext.getCmp("templateWin").close();
                    }
                }
            ]
        });
    }
};

Ext.extend(CmsTemplate, Ext.grid.GridPanel);