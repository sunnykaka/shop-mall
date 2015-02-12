/**
 * 创建原型页Tab对象的类
 *
 */

PagePrototype = function (dev) {

    //初始化gird要显示的数据
    var store = createJsonStore('page/prototype/list', [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'prototypeState',
            type: 'string'
        },
        {
            name: 'pageCode',
            type: 'string'
        },
        {
            name: 'areaType',
            type: 'string'
        }
    ]);


    var toolBar = [];

    if (dev) {
        toolBar.push({
            text: '增加',
            iconCls: 'add',
            handler: function () {
                Ext.getCmp("pagePrototypeGrid").pageWin.show('pagePrototypeGrid');
                Ext.getCmp("pagePrototypeGrid").commitUrl = 'page/prototype/new';
            }
        });
        toolBar.push({
            text: '删除',
            iconCls: 'remove',
            handler: function () {
                doGridRowDelete(Ext.getCmp("pagePrototypeGrid"), 'page/prototype/delete/batch', function () {
                    Ext.getCmp("pagePrototypeGrid").getStore().reload();
                });
            }
        });
        toolBar.push({
            text: '预览',
            iconCls: 'run',
            handler: function () {
                var sm = Ext.getCmp("pagePrototypeGrid").getSelectionModel();
                if (!sm.hasSelection()) {
                    Ext.Msg.alert('错误', '请选择要浏览的行');
                } else if (sm.getSelections().length > 1) {
                    Ext.Msg.alert('错误', '请仅仅选择一行');
                } else {
                    var record = sm.getSelected();
                    window.open('page/prototype/debug/' + record.get('id'));
                }
            }
        });
        toolBar.push({
            text: '刷新',
            iconCls: 'refresh',
            handler: function () {
                Ext.getCmp("pagePrototypeGrid").getStore().reload();
            }
        });
        toolBar.push({
            text: '初始化',
            iconCls: 'run',
            handler: function () {
                var sm = Ext.getCmp("pagePrototypeGrid").getSelectionModel();
                if (!sm.hasSelection()) {
                    Ext.Msg.alert('错误', '请选择页面原形');
                } else if (sm.getSelections().length > 1) {
                    Ext.Msg.alert('错误', '请仅仅选择一行');
                } else {
                    var record = sm.getSelected();
                    if (record.get('prototypeState') == "DEBUG") {
                        Ext.Msg.alert("错误", "页面原型还没发布");
                        return;
                    }

                    var type = record.get('areaType');
                    if (type == "HEAD") {

                        doAjax('page/prototype/initShopHead', function () {
                            Ext.Msg.alert("成功", "初始化店铺头成功");
                        }, {prototypeId: record.get('id')}, "你确定要初始化店铺头吗？");

                    }
                    if (type == "FOOT") {

                        doAjax('page/prototype/initShopFoot', function () {
                            Ext.Msg.alert("成功", "初始化店铺尾成功");
                        }, {prototypeId: record.get('id')}, "你确定要初始化店铺尾吗？");

                    }
                    if (type == "BODY") {

                        var addPageType = function () {
                            commitForm(pageTypeForum, function () {
                                Ext.Msg.alert("成功", "初始化页面成功");
                                win.close();
                            });
                        };

                        var pageTypeForum = new Ext.FormPanel({
                            baseCls: 'x-plain',
                            labelWidth: 80,
                            url: 'page/prototype/initShopPage',
                            frame: true,
                            autoHeight: true,
                            labelAlign: 'top',
                            items: [
                                {
                                    xtype: 'hidden',
                                    name: 'prototypeId',
                                    value: record.get('id')
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'name',
                                    fieldLabel: '页面名称',
                                    regex: /^\S+$/,
                                    allowBlank: false,
                                    anchor: '100%',
                                    maxLength: 45
                                },
                                {
                                    xtype: 'combo',
                                    hiddenName: 'pageType',
                                    fieldLabel: '页面类型',
                                    mode: 'local',
                                    editable: false,
                                    triggerAction: 'all',
                                    emptyText: '请选择',
                                    store: new Ext.data.ArrayStore({
                                        fields: [ 'type', 'desc' ],
                                        data: [
                                            [ 'index', '首页' ],
                                            [ 'searchList', '搜索列表' ],
                                            [ 'detail', '详情页' ],
                                            [ 'detailIntegral', '积分兑换详情页' ],
                                            [ 'channel', '频道页' ],
                                            [ 'meal_detail', '套餐详情页' ]
                                        ]
                                    }),
                                    displayField: 'desc',
                                    valueField: 'type',
                                    allowBlank: false,
                                    anchor: '100%'
                                }
                            ]
                        });

                        var win = new Ext.Window({
                            title: '选择页面类型',
                            width: 400,
                            autoHeight: true,
                            layout: 'fit',
                            plain: true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px;',
                            items: pageTypeForum,
                            keys: [
                                {
                                    key: Ext.EventObject.ENTER,
                                    fn: addPageType //执行的方法
                                }
                            ],
                            buttons: [
                                {
                                    text: '保存',
                                    handler: addPageType
                                },
                                {
                                    text: '取消',
                                    handler: function () {
                                        win.close();
                                    }
                                }
                            ]
                        });
                        win.show(this.id);
                    }
                }
            }
        });
        toolBar.push({
            text: '升级',
            iconCls: 'upgrade',
            handler: function () {
                var sm = Ext.getCmp("pagePrototypeGrid").getSelectionModel();
                if (!sm.hasSelection()) {
                    Ext.Msg.alert('错误', '请选择页面原形');
                } else if (sm.getSelections().length > 1) {
                    Ext.Msg.alert('错误', '请仅仅选择一行');
                } else {
                    var record = sm.getSelected();
                    if (record.get('prototypeState') == "DEBUG") {
                        Ext.Msg.alert("错误", "页面原型还没发布");
                        return;
                    }
                    Ext.Msg.confirm("确认", "你确定要升级吗？", function (btn) {
                        if (btn == 'yes') {
                            var type = record.get('areaType');
                            if (type == "HEAD") {

                                doAjax('page/prototype/applyShopHead', function () {
                                    Ext.Msg.alert("成功", "成功升级店铺头");
                                }, {
                                    prototypeId: record.get('id')
                                });

                            }
                            if (type == "FOOT") {

                                doAjax('page/prototype/applyShopFoot', function () {
                                    Ext.Msg.alert("成功", "成功升级店铺尾");
                                }, {
                                    prototypeId: record.get('id')
                                });

                            }
                            if (type == "BODY") {

                                var pageUpgrade = function () {
                                    commitForm(pageTypeForum, function () {
                                        Ext.Msg.alert("成功", "成功升级页面");
                                        win.close();
                                    });
                                };

                                var pageTypeForum = new Ext.FormPanel({
                                    baseCls: 'x-plain',
                                    labelWidth: 80,
                                    url: 'page/prototype/applyShopPage',
                                    frame: true,
                                    autoHeight: true,
                                    labelAlign: 'top',
                                    items: [
                                        {
                                            xtype: 'hidden',
                                            name: 'prototypeId',
                                            value: record.get('id')
                                        },
                                        {
                                            xtype: 'combo',
                                            hiddenName: 'pageType',
                                            fieldLabel: '页面类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'desc' ],
                                                data: [
                                                    [ 'index', '首页' ],
                                                    [ 'searchList', '搜索列表' ],
                                                    [ 'detail', '详情页' ],
                                                    [ 'detailIntegral', '积分兑换详情页' ],
                                                    [ 'channel', '频道页' ],
                                                    [ 'meal_detail', '套餐详情页' ]
                                                ]
                                            }),
                                            displayField: 'desc',
                                            valueField: 'type',
                                            allowBlank: false,
                                            anchor: '100%'
                                        }
                                    ]
                                });
                                var win = new Ext.Window({
                                    title: '选择要升级的页面类型',
                                    width: 400,
                                    autoHeight: true,
                                    layout: 'fit',
                                    plain: true,
                                    buttonAlign: 'center',
                                    bodyStyle: 'padding:5px;',
                                    items: pageTypeForum,
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: pageUpgrade //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: pageUpgrade
                                        },
                                        {
                                            text: '取消',
                                            handler: function () {
                                                win.close();
                                            }
                                        }
                                    ]
                                });
                                win.show(this.id);
                            }
                        }
                    });
                }
            }
        });
        toolBar.push({
            text: '批量发布',
            iconCls: 'run',
            handler: function () {
                var pagePrototypeGrid = Ext.getCmp("pagePrototypeGrid");
                var sm = pagePrototypeGrid.getSelectionModel();
                if (!sm.hasSelection()) {
                    Ext.Msg.alert('错误', '请选择要发布的页面模型');
                    return;
                }
                if (sm.getSelections().length > 0) {
                    var records = sm.getSelections();
                    var ids = [];
                    for (var i = 0; i < records.length; i++) {
                        ids.push(records[i].get('id'));
                    }

                    doAjax('page/prototype/batch/release', function () {
                        Ext.getCmp("pagePrototypeGrid").getStore().reload();
                        Ext.Msg.alert("成功", "原型发布成功");
                    }, {
                        ids: ids
                    }, "你确定批量发布这些页面模型吗？");

                }
            }
        });
    }

    toolBar.push({
        text: '制作页面',
        iconCls: 'run',
        handler: function () {
            var sm = Ext.getCmp("pagePrototypeGrid").getSelectionModel();
            if (!sm.hasSelection()) {
                Ext.Msg.alert('错误', '请选择页面原形');
            } else if (sm.getSelections().length > 1) {
                Ext.Msg.alert('错误', '请仅仅选择一行');
            } else {
                var record = sm.getSelected();
                if (record.get('prototypeState') == "DEBUG") {
                    Ext.Msg.alert("错误", "页面原型还没发布");
                    return;
                }
                var pageTypeForum = new Ext.FormPanel({
                    labelWidth: 80,
                    baseCls: 'x-plain',
                    frame: true,
                    buttonAlign: 'center',
                    url: 'design/shop/page/other',
                    defaults: {
                        width: 350
                    },
                    items: [
                        {
                            xtype: 'hidden',
                            name: 'prototypeId',
                            value: record.get('id')
                        },
                        {
                            xtype: 'textfield',
                            name: 'name',
                            fieldLabel: '页面名称',
                            allowBlank: false,
                            maxLength: 30,
                            regex: /^\S+$/,
                            anchor: '100%'
                        },
                        {
                            xtype: 'textfield',
                            name: 'title',
                            fieldLabel: '主题',
                            allowBlank: false,
                            maxLength: 100,
                            regex: /^\S+$/,
                            anchor: '100%'
                        },
                        {
                            name: 'keywords',
                            fieldLabel: '关键字',
                            maxLength: 150,
                            xtype: 'textarea',
                            anchor: '100%',
                            height: 100
                        },
                        {
                            name: 'description',
                            xtype: 'textarea',
                            fieldLabel: '描述信息',
                            maxLength: 200,
                            anchor: '100%',
                            height: 100
                        }
                    ]
                });

                var initShopPage = function () {
                    commitForm(pageTypeForum, function () {
                        Ext.Msg.alert("成功", "初始化页面成功，请查看页面列表");
                        Ext.getCmp("shopPageGrid").getStore().reload();
                        win.close();
                    });
                };

                var win = new Ext.Window({
                    title: '填写页面名称',
                    width: 500,
                    autoHeight: true,
                    layout: 'fit',
                    plain: true,
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px;',
                    items: pageTypeForum,
                    keys: [
                        {
                            key: Ext.EventObject.ENTER,
                            fn: initShopPage //执行的方法
                        }
                    ],
                    buttons: [
                        {
                            text: '保存',
                            handler: initShopPage
                        },
                        {
                            text: '取消',
                            handler: function () {
                                win.close();
                            }
                        }
                    ]
                });
                win.show(this.id);
            }
        }
    });


    //调用超类构造函数
    PagePrototype.superclass.constructor.call(this, {
        title: '页面原型',
        id: 'pagePrototypeGrid',
        loadMask: true,
        store: store,
        columns: [
            {
                header: '页名',
                dataIndex: 'name'
            },
            {
                header: '类型',
                dataIndex: 'areaType',
                renderer: function (value) {
                    if (value == 'HEAD') {
                        return '头部';
                    } else if (value == 'FOOT') {
                        return '底部';
                    }
                    return value;
                }
            },
            {
                header: '状态',
                dataIndex: 'prototypeState'
            }
        ],
        viewConfig: {
            forceFit: true
        },
        tbar: toolBar
    });

    //ajax提交数据的URL
    this.commitUrl = '';

    var addPagePrototype = function () {
        commitForm(Ext.getCmp('pagePrototypeAdd'), function () {
            Ext.getCmp("pagePrototypeGrid").pageWin.hide();
            Ext.getCmp('pagePrototypeAdd').getForm().setValues({
                id: 0,
                name: '',
                description: '',
                areaType: '',
                pageCode: ''
            });
            Ext.getCmp("pagePrototypeGrid").getStore().reload();
        }, Ext.getCmp("pagePrototypeGrid").commitUrl);

    };

    //模板页的表单
    this.pagePrototypeAdd = new Ext.FormPanel({
        id: 'pagePrototypeAdd',
        buttonAlign: 'center',
        baseCls: 'x-plain',
        labelWidth: 60,
        frame: true,
        width: 500,
        defaults: {
            anchor: '100%',
            width: 350
        },
        defaultType: 'textfield',

        items: [
            {
                xtype: 'hidden',
                name: 'id',
                value: 0
            },
            {
                fieldLabel: '页面名称',
                name: 'name',
                regex: /^\S+$/,
                allowBlank: false,
                maxLength: 45
            },
            {
                fieldLabel: '原型描述',
                name: 'description',
                xtype: 'textarea',
                allowBlank: false,
                maxLength: 100
            },
            {
                xtype: 'combo',
                hiddenName: 'areaType',
                fieldLabel: '原型类型',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'desc' ],
                    data: [
                        [ 'HEAD', '头' ],
                        [ 'FOOT', '尾' ],
                        [ 'BODY', 'body' ]
                    ]
                }),
                displayField: 'desc',
                valueField: 'type',
                allowBlank: false
            },
            {
                xtype: 'textarea',
                height: 400,
                fieldLabel: '页面内容',
                name: 'pageCode',
                allowBlank: false
            }
        ],
        buttons: [
            {
                text: '保存',
                handler: addPagePrototype
            },
            {
                text: '取消',
                handler: function () {
                    Ext.getCmp('pagePrototypeAdd').getForm().setValues({
                        id: 0,
                        name: '',
                        description: '',
                        areaType: '',
                        pageCode: ''
                    });
                    Ext.getCmp("pagePrototypeGrid").pageWin.hide();
                }
            }
        ]
    });

    var editPagePrototype = function () {

        commitForm(Ext.getCmp('pagePrototypeEditForm'), function () {
            Ext.getCmp("pagePrototypeGrid").getStore().reload();
            Ext.Msg.alert('信息', '修改数据成功，请尽快发布该页面');
        }, Ext.getCmp("pagePrototypeGrid").commitUrl);
    };

    this.pagePrototypeEditForm = new Ext.FormPanel({
        id: 'pagePrototypeEditForm',
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

        items: [
            {
                xtype: 'hidden',
                name: 'id',
                value: 0
            },
            {
                fieldLabel: '页面名称',
                name: 'name',
                regex: /^\S+$/,
                allowBlank: false,
                maxLength: 45
            },
            {
                fieldLabel: '原型描述',
                name: 'description',
                xtype: 'textarea',
                allowBlank: false,
                maxLength: 100
            },
            {
                xtype: 'combo',
                hiddenName: 'areaType',
                fieldLabel: '原型类型',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'desc' ],
                    data: [
                        [ 'HEAD', '头' ],
                        [ 'FOOT', '尾' ],
                        [ 'BODY', 'body' ]
                    ]
                }),
                displayField: 'desc',
                valueField: 'type',
                allowBlank: false
            },
            {
                xtype: 'textarea',
                height: 400,
                fieldLabel: '页面内容',
                name: 'pageCode',
                allowBlank: false
            }
        ],
        buttons: [
            {
                text: '保存',
                handler: editPagePrototype
            },
            {
                text: '发布',
                handler: function () {
                    commitForm(Ext.getCmp('pagePrototypeEditForm'), function () {
                        Ext.getCmp("pagePrototypeGrid").pageEditWin.hide();
                        Ext.getCmp('pagePrototypeEditForm').getForm().setValues({
                            id: 0,
                            name: '',
                            description: '',
                            areaType: '',
                            pageCode: ''
                        });
                        Ext.getCmp("pagePrototypeGrid").getStore().reload();
                        Ext.Msg.alert("成功", "原型发布成功")
                    }, 'page/prototype/release');
                }
            },
            {
                text: '取消',
                handler: function () {
                    Ext.getCmp('pagePrototypeEditForm').getForm().setValues({
                        id: 0,
                        name: '',
                        description: '',
                        areaType: '',
                        pageCode: ''
                    });
                    Ext.getCmp("pagePrototypeGrid").pageEditWin.hide();
                }
            }
        ]
    });

    //放置模板页表单的窗口
    this.pageWin = new Ext.Window({
        title: '新增页面原型',
        layout: 'fit',
        width: 800,
        height: 550,
        plain: true,
        closeAction: 'hide',
        bodyStyle: 'padding:5px;',
        items: this.pagePrototypeAdd});

    this.pageEditWin = new Ext.Window({
        title: '编辑页面原型',
        layout: 'fit',
        width: 800,
        height: 550,
        plain: true,
        closeAction: 'hide',
        bodyStyle: 'padding:5px;',
        items: this.pagePrototypeEditForm});

    //加载数据
    this.getStore().load();

    if (dev) {
        this.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            Ext.getCmp('pagePrototypeEditForm').getForm().loadRecord(record);
            grid.commitUrl = 'page/prototype/update';
            grid.pageEditWin.show('pagePrototypeGrid');
        });
    }

};

Ext.extend(PagePrototype, Ext.grid.GridPanel);