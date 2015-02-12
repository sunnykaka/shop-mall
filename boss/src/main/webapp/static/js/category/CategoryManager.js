/**
 * 实现后台类目管理的js
 */

CategoryAddWindow = function (config) {
    this.hiddenParentId = new Ext.form.Hidden({
        name: 'parentId',
        value: config.parentId
    });

    this.categoryName = {
        fieldLabel: '类目名称',
        name: 'name',
        maxLength: 45,
        allowBlank: false,
        regex: /^\S+$/, // 不能输入空格
        xtype: 'textfield'
    };

    this.form = new Ext.FormPanel({
        items: [this.hiddenParentId, this.categoryName],
        border: false,
        url: 'category/new',
        bodyStyle: 'background:transparent;padding:10px;',
        defaults: {
            anchor: '100%'
        }
    });
    var thisObject = this;


    var addCategory = function addCategory() {

        commitForm(thisObject.form, function (action) {
            thisObject.form.getForm().reset();
            thisObject.hide();
            var tree = Ext.getCmp('category-tree');
            tree.root.reload();

            var categoryId = action.result.data.categoryId;
            Ext.Ajax.request({
                url: 'category/selectPath/' + categoryId,
                success: function (response, opts) {
                    tree.selectPath(response.responseText);
                },
                failure: function (response, opts) {
                    Ext.Msg.alert('失败', '加载数据失败');
                }
            });
        });

    };


    CategoryAddWindow.superclass.constructor.call(this, {
        autoHeight: true,
        width: 500,
        resizable: false,
        plain: true,
        modal: true,
        autoScroll: true,
        buttonAlign: 'center',
        closeAction: 'hide',
        title: '添加后台类目',
        keys: [
            {
                key: Ext.EventObject.ENTER,
                fn: addCategory //执行的方法

            }
        ],
        buttons: [
            {
                text: '添加',
                handler: addCategory
            },
            {
                text: '取消',
                handler: this.hide.createDelegate(this, [])
            }
        ],

        items: this.form
    });
}

Ext.extend(CategoryAddWindow, Ext.Window);

CPVWindow = function (node, root) {
    var cid = node.id;
    this.store = new Ext.data.Store({
        autoDestroy: true,

        proxy: new Ext.data.HttpProxy({
            url: 'category/cpv/list/' + cid,
            failure: function (response, opts) {
                if (response.status == 403) {
                    Ext.Msg.alert('错误', '没有设置访问权限');
                    return;
                }
                Ext.Msg.alert('错误', '服务器出错');
            }
        }),
        reader: new Ext.data.JsonReader({
            totalProperty: 'data.totalCount',
            root: 'data.result'
        }, Ext.data.Record.create([
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'cid',
                type: 'int'
            },
            {
                name: 'pid',
                type: 'int'
            },
            {
                name: 'priority',
                type: 'int'
            },
            {
                name: 'property',
                type: 'string'
            },
            {
                name: 'value',
                type: 'string'
            },
            {
                name: 'propertyType',
                type: 'string'
            },
            {
                name: 'multi',
                type: 'bool'
            },
            {
                name: 'compareable',
                type: 'bool'
            }
        ]))
    });
    this.store.load();

    this.sm = new Ext.grid.CheckboxSelectionModel();

    this.cm = new Ext.grid.ColumnModel({
        defaults: {
            sortable: true
        },
        columns: [this.sm,
            {
                header: '属性名',
                dataIndex: 'property',
                width: 100
            },
            {
                id: 'value',
                header: '属性值',
                dataIndex: 'value',
                width: 220
            },
            {
                header: '优先级(小优先)',
                dataIndex: 'priority',
                width: 100,
                editor: new Ext.form.NumberField({
                    allowBlank: false
                })
            },
            {
                header: '属性类型',
                dataIndex: 'propertyType',
                width: 80,
                renderer: function (value) {
                    if (value == 'SELL_PROPERTY')
                        return "销售属性";
                    else if (value == 'KEY_PROPERTY')
                        return "关键属性";
                }
            },
            {
                header: '是否多值',
                dataIndex: 'multi',
                renderer: function (value) {
                    if (value)
                        return "是";
                    else
                        return "否";
                },
                width: 80
            },
            {
                header: '是否对比',
                dataIndex: 'compareable',
                renderer: function (value) {
                    if (value)
                        return "是";
                    else
                        return "否";
                },
                width: 100
            }
        ]
    });


    var tbar = [];
    //只有根节点才允许设定属性和值，其他节点都是选择和删除，不能自增和修改，每个节点都可以调整属性优先级
    if (root) {
        tbar.push({
            text: '设定属性',
            iconCls: 'add',
            handler: function () {
                var propertyTree = new Ext.tree.TreePanel({
                    rootVisible: false,
                    lines: false,
                    loader: new Ext.tree.TreeLoader({dataUrl: 'category/property/tree/' + cid}),
                    height: 425,
                    autoScroll: true,
                    root: {
                        id: -1,
                        nodeType: 'async',
                        expanded: true
                    },
                    tbar: [
                        {
                            text: '增加属性',
                            iconCls: 'add',
                            handler: function () {
                                var cpAddForm = new Ext.FormPanel({
                                    baseCls: 'x-plain',
                                    labelWidth: 80,
                                    url: 'category/cp/new',
                                    frame: true,
                                    width: 500,
                                    defaults: {
                                        width: 380
                                    },
                                    defaultType: 'textfield',

                                    items: [
                                        {
                                            xtype: 'hidden',
                                            name: 'categoryId',
                                            value: cid
                                        },
                                        {
                                            fieldLabel: '属性名称',
                                            name: 'name',
                                            allowBlank: false,
                                            regex: /^\S+$/, // 不能输入空格
                                            maxLength: 10
                                        },
                                        {
                                            xtype: 'combo',
                                            hiddenName: 'propertyType',
                                            fieldLabel: '属性类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'value' ],
                                                data: [
                                                    [ '销售属性', 'SELL_PROPERTY' ],
                                                    [ '关键属性', 'KEY_PROPERTY' ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'value',
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'checkbox',
                                            name: 'multiValue',
                                            checked: true,
                                            fieldLabel: '是否多值',
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'checkbox',
                                            name: 'compareable',
                                            checked: true,
                                            fieldLabel: '是否对比',
                                            allowBlank: false
                                        },
                                        {
                                            fieldLabel: '属性值',
                                            name: 'value'
                                        }
                                    ]

                                });

                                var addProperty = function () {

                                    commitForm(cpAddForm, function () {
                                        propertyTree.root.reload();
                                        currentGrid.getStore().reload();
                                        win.close();
                                    });

                                }

                                var win = new Ext.Window({
                                    title: '增加属性',
                                    width: 500,
                                    height: 200,
                                    layout: 'fit',
                                    plain: true,
                                    buttonAlign: 'center',
                                    bodyStyle: 'padding:5px;',
                                    items: cpAddForm,
                                    //热键添加
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: addProperty //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: addProperty
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
                        },
                        {
                            text: '刷新',
                            iconCls: 'refresh',
                            handler: function () {
                                propertyTree.root.reload();
                            }
                        }
                    ]
                });

                propertyTree.on("contextmenu", function (node, e) {
                    var treeMenu = new Ext.menu.Menu();
                    treeMenu.add({
                        text: '删除',
                        handler: function () {
                            if (node.leaf) {
                                doAjax('category/cpv/delete', function () {
                                    node.remove();
                                    currentGrid.getStore().reload();
                                }, {
                                    cid: cid,
                                    pid: node.parentNode.id,
                                    vid: node.id
                                }, "你确定删除这个值吗？");

                            } else {
                                doAjax('category/cp/delete', function () {
                                    node.remove();
                                    currentGrid.getStore().reload();
                                }, {
                                    cid: cid,
                                    pid: node.id
                                }, "你确定删除这个属性吗？");
                            }
                        }
                    });
                    treeMenu.add({
                        text: '修改',
                        handler: function () {
                            //如果是叶子节点修改属性值
                            if (node.leaf) {
                                var cpvEditForm = new Ext.FormPanel({
                                    baseCls: 'x-plain',
                                    labelWidth: 80,
                                    url: 'category/cpv/update',
                                    frame: true,
                                    width: 500,
                                    defaults: {
                                        width: 380
                                    },
                                    defaultType: 'textfield',

                                    items: [
                                        {
                                            xtype: 'hidden',
                                            name: 'id'
                                        },
                                        {
                                            fieldLabel: '值名称',
                                            name: 'name',
                                            allowBlank: false,
                                            regex: /^\S+$/ // 不能输入空格
                                        }
                                    ]

                                });

                                var updatePropertyValue = function () {

                                    commitForm(cpvEditForm, function () {
                                        propertyTree.root.reload();
                                        currentGrid.getStore().reload();
                                        win.close();
                                    })
                                }

                                var win = new Ext.Window({
                                    title: '修改属性值',
                                    width: 500,
                                    height: 200,
                                    layout: 'fit',
                                    plain: true,
                                    buttonAlign: 'center',
                                    bodyStyle: 'padding:5px;',
                                    items: cpvEditForm,
                                    //热键添加
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: updatePropertyValue //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: updatePropertyValue
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
                                cpvEditForm.load({
                                    url: 'category/cpv/' + cid + '/' + node.parentNode.id + '/' + node.id,
                                    success: function (form, action) {
                                        cpvEditForm.getForm().setValues(action.result.data.object);
                                    }
                                });
                            } else {
                                //如果是父亲节点则是修改类目属性
                                var cpEditForm = new Ext.FormPanel({
                                    baseCls: 'x-plain',
                                    labelWidth: 80,
                                    url: 'category/cp/update',
                                    frame: true,
                                    width: 500,
                                    defaults: {
                                        width: 380
                                    },
                                    defaultType: 'textfield',

                                    items: [
                                        {
                                            xtype: 'hidden',
                                            name: 'id'
                                        },
                                        {
                                            fieldLabel: '属性名称',
                                            name: 'name',
                                            readOnly: true,
                                            allowBlank: false,
                                            regex: /^\S+$/, // 不能输入空格
                                            maxLength: 10
                                        },
                                        {
                                            xtype: 'combo',
                                            hiddenName: 'propertyType',
                                            fieldLabel: '属性类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'value' ],
                                                data: [
                                                    [ '销售属性', 'SELL_PROPERTY' ],
                                                    [ '关键属性', 'KEY_PROPERTY' ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'value',
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'checkbox',
                                            name: 'multiValue',
                                            checked: true,
                                            fieldLabel: '是否多值',
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'checkbox',
                                            name: 'compareable',
                                            fieldLabel: '是否对比',
                                            allowBlank: false
                                        }
                                    ]

                                });

                                var updateProperty = function () {

                                    commitForm(cpEditForm, function () {
                                        propertyTree.root.reload();
                                        currentGrid.getStore().reload();
                                        win.close();
                                    })

                                }

                                var win = new Ext.Window({
                                    title: '修改属性',
                                    width: 500,
                                    height: 200,
                                    layout: 'fit',
                                    plain: true,
                                    buttonAlign: 'center',
                                    bodyStyle: 'padding:5px;',
                                    items: cpEditForm,
                                    //热键添加
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: updateProperty //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: updateProperty
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
                                cpEditForm.load({
                                    url: 'category/cp/' + cid + '/' + node.id,
                                    success: function (form, action) {
                                        cpEditForm.getForm().setValues(action.result.data.object);
                                    }
                                });
                            }
                        }
                    });
                    if (!node.leaf) {
                        treeMenu.add({
                            text: '添加属性值',
                            handler: function () {
                                var cpvAddForm = new Ext.FormPanel({
                                    baseCls: 'x-plain',
                                    labelWidth: 80,
                                    url: 'category/cpv/new',
                                    frame: true,
                                    width: 500,
                                    defaults: {
                                        width: 380
                                    },
                                    defaultType: 'textfield',

                                    items: [
                                        {
                                            xtype: 'hidden',
                                            name: 'categoryId',
                                            value: cid
                                        },
                                        {
                                            xtype: 'hidden',
                                            name: 'propertyId',
                                            value: node.id
                                        },
                                        {
                                            fieldLabel: '值名称',
                                            name: 'name',
                                            allowBlank: false,
                                            regex: /^\S+$/
                                        }
                                    ]

                                });

                                var addPropertyValue = function () {

                                    commitForm(cpvAddForm, function () {
                                        propertyTree.root.reload();
                                        currentGrid.getStore().reload();
                                        win.close();
                                    })

                                }

                                var win = new Ext.Window({
                                    title: '增加属性值',
                                    width: 500,
                                    height: 100,
                                    layout: 'fit',
                                    plain: true,
                                    buttonAlign: 'center',
                                    bodyStyle: 'padding:5px;',
                                    items: cpvAddForm,
                                    //热键添加
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: addPropertyValue //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: addPropertyValue
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
                        });
                    }
                    treeMenu.showAt(e.getXY());
                });

                var win = new Ext.Window({
                    title: '设定类目的属性和值',
                    width: 500,
                    height: 350,
                    plain: true,
                    layout: 'fit',
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px;',
                    items: [propertyTree]

                });
                win.show(this.id);
            }
        });
    }

    tbar.push({
        text: '刷新',
        iconCls: 'refresh',
        handler: function () {
            currentGrid.getStore().reload();
            currentGrid.getStore().rejectChanges();
        }
    });
    tbar.push({
        text: '删除',
        iconCls: 'remove',
        handler: function () {
            var sm = currentGrid.getSelectionModel();
            if (!sm.hasSelection()) {
                Ext.Msg.alert('错误', '请选择要删除的行');
            } else {
                var records = sm.getSelections();
                var cps = [];
                for (var i = 0; i < records.length; i++) {
                    var pid = records[i].get('pid');
                    cps.push(records[i].get('cid') + "-" + pid);
                }
                if (cps.length > 0) {
                    doAjax('category/cps/delete', function () {
                        currentGrid.getStore().reload();
                    }, {
                        cps: cps
                    }, "你确定删除这些属性吗？");
                }
            }
        }
    });


    this.grid = new Ext.grid.EditorGridPanel({
        border: false,
        store: this.store,
        cm: this.cm,
        sm: this.sm,
        loadMask: true,
        width: 600,
        height: 400,
        viewConfig: {forceFit: true},
        clicksToEdit: 1,
        tbar: tbar
    });

    this.grid.on('afteredit', function (e) {
        doAjax('category/cp/priority/update', function () {
            currentGrid.getStore().reload();
        }, {
            id: e.record.get('id'),
            priority: e.record.get('priority')
        });

    });

    this.grid.on('rowcontextmenu', function (grid, rowIndex, e) {
        e.preventDefault();//取消默认的浏览器右键事件

        var record = grid.getStore().getAt(rowIndex);

        var treeMenu = new Ext.menu.Menu({
            items: [
                {
                    text: '设置类目属性值优先级',
                    handler: function () {
                        changePriority("product/category/property/value/list/" + record.get('cid') + "/" + record.get('pid'), "category/cpv/priority/update", function () {
                        })
                    }
                }
            ]
        });
        treeMenu.showAt(e.getXY());

    });

    currentGrid = this.grid;

    CPVWindow.superclass.constructor.call(this, {
        title: '类目属性值编辑器',
        width: 610,
        height: 450,
        layout: 'fit',
        items: this.grid
    });

    this.addEvents({add: true});
}

Ext.extend(CPVWindow, Ext.Window);


PVDWindow = function (cid) {

    this.store = new Ext.data.GroupingStore({
        autoDestroy: true,

        groupField: 'property',

        proxy: new Ext.data.HttpProxy({
            url: 'category/pvds/' + cid,
            failure: function (response, opts) {
                if (response.status == 403) {
                    Ext.Msg.alert('错误', '没有设置访问权限');
                    return;
                }
                Ext.Msg.alert('错误', '服务器出错');
            }
        }),
        reader: new Ext.data.JsonReader({
            totalProperty: 'data.totalCount',
            root: 'data.result'
        }, Ext.data.Record.create([
            {
                name: 'property',
                type: 'string'
            },
            {
                name: 'value',
                type: 'string'
            },
            {
                name: 'pictureUrl',
                type: 'string'
            },
            {
                name: 'description',
                type: 'string'
            }
        ]))
    });
    this.store.load();

    var editor = new Ext.ux.grid.RowEditor({
        saveText: '更新',
        cancelText: '取消'
    });

    editor.on('afteredit', function (grid, object, record, index) {
        doAjax('category/pvd/update', function () {
            currentGrid.getStore().reload();
            currentGrid.getStore().rejectChanges();
        }, {property: record.get('property'), value: record.get('value'), pictureUrl: record.get('pictureUrl'), description: record.get('description')});

    });

    this.grid = new Ext.grid.GridPanel({
        border: false,
        store: this.store,
        region: 'center',
        width: 600,
        plugins: [editor],
        view: new Ext.grid.GroupingView({
            forceFit: true,
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "个" : "Item"]})'
        }),
        columns: [
            {
                header: '属性',
                dataIndex: 'property',
                width: 100,
                hidden: true,
                sortable: true
            },
            {
                header: '属性值',
                dataIndex: 'value',
                width: 100,
                sortable: true
            },
            {
                id: 'pictureUrl',
                header: '图片地址',
                dataIndex: 'pictureUrl',
                renderer: function (value) {
                    if (value == "") {
                        return "没有图片";
                    }
                    return "<img src=" + value + ">";
                },
                width: 220,
                sortable: true,
                editor: {
                    xtype: 'textfield',
                    allowBlank: true
                }
            },
            {
                header: '描述',
                dataIndex: 'description',
                width: 220,
                sortable: true,
                editor: {
                    xtype: 'textarea',
                    allowBlank: true
                }
            }
        ]
    });

    currentGrid = this.grid;


    PVDWindow.superclass.constructor.call(this, {
        title: '类目属性值Detail编辑器',
        width: 610,
        height: 450,
        layout: 'border',
        items: this.grid
    });
}

Ext.extend(PVDWindow, Ext.Window);


Desktop.CategoryWindow = Ext.extend(Ext.app.Module, {
    id: 'Category-win',

    title: '后台类目管理',


    //创建类目管理窗口
    createWindow: function () {

        var selectNode;

        //后台类目树
        var categoryTree = new Ext.tree.TreePanel({
            id: 'category-tree',
            title: '后台类目',
            rootVisible: false,
            lines: false,
            dataUrl: 'category/tree',
            autoScroll: true,
            height: 900,
            tools: [
                {
                    id: 'refresh',
                    on: {
                        click: function () {
                            var tree = Ext.getCmp('category-tree');
                            tree.root.reload();
                        }
                    }
                }
            ],
            root: {
                id: -1,
                nodeType: 'async',
                expanded: true
            },
            listeners: {
                click: function (node) {
                    selectNode = node;
                }
            }
        });
        //后台类目编辑器
        var categoryTreeEditor = new Ext.tree.TreeEditor(categoryTree, {allowBlank: false, regex: /^\S+$/, maxLength: 45}, {ignoreNoChange: true, listeners: {
            complete: function (treeEditor, newValue, oldValue) {

                doAjax('category/update', function () {
                    Ext.Msg.alert("成功", "更新成功");
                }, {
                    categoryId: selectNode.id,
                    name: newValue
                });

            }}
        });
        //后台类目右键菜单
        categoryTree.on('contextmenu', function (node, e) {
            var treeMenu = new Ext.menu.Menu({
                items: [
                    {
                        text: '编辑',
                        handler: function () {
                            categoryTreeEditor.triggerEdit(selectNode);
                        }
                    }

                ]
            });
            selectNode = node;

            Ext.Ajax.request({
                url: 'category/info',
                method: 'GET',
                success: function (response, options) {
                    var result = Ext.util.JSON.decode(response.responseText);
                    if (!result.data.leaf) {
                        treeMenu.add({
                            text: '添加子类目',
                            handler: function () {
                                var categoryWin = new CategoryAddWindow({parentId: selectNode.id});
                                categoryWin.show();
                            }
                        });
                    }
                    if (!result.data.root) {
                        treeMenu.add({
                            text: '选择属性和值',
                            handler: function () {
                                var propertyTree = new Ext.tree.TreePanel({
                                    rootVisible: false,
                                    lines: false,
                                    title: '查看旧的',
                                    loader: new Ext.tree.TreeLoader({dataUrl: 'category/property/tree/' + selectNode.id}),
                                    columnWidth: .50,
                                    height: 425,
                                    autoScroll: true,
                                    root: {
                                        id: -1,
                                        nodeType: 'async',
                                        expanded: true
                                    }
                                });
                                propertyTree.expandAll();

                                var propertyCheckTree = new Ext.tree.TreePanel({
                                    rootVisible: false,
                                    lines: false,
                                    title: '选择新的',
                                    loader: new Ext.tree.TreeLoader({dataUrl: 'category/property/tree/check/' + selectNode.id}),
                                    columnWidth: .50,
                                    height: 425,
                                    autoScroll: true,
                                    root: {
                                        id: -1,
                                        nodeType: 'async',
                                        expanded: true,
                                        checked: true
                                    }
                                });
                                propertyCheckTree.expandAll();

                                //树展开的时候把级联选择事件监听设定到节点上
                                propertyCheckTree.on("expandnode", function (node) {
                                    node.on("checkchange", cascadeCheck);
                                });

                                var selectPropertyAndValue = function () {

                                    var pvNode = propertyCheckTree.getChecked();

                                    // 因为有个根节点  -1
                                    if (pvNode.length == 1) {
                                        Ext.Msg.alert("错误", "请选择属性和值");
                                        return;
                                    }

                                    var valueIds = [];

                                    Ext.each(pvNode, function (node) {
                                        if (node.isLeaf()) {
                                            valueIds.push(node.id);
                                        }
                                    });

                                    doAjax('category/property/inherited', function () {
                                        Ext.Msg.alert("成功", "设定成功");
                                    }, {categoryId: selectNode.id, valueIds: valueIds});

                                    propertyTreeWindow.close();
                                }

                                var propertyTreeWindow = new Ext.Window({
                                    width: 600,
                                    height: 500,
                                    layout: 'column',
                                    plain: true,
                                    buttonAlign: 'center',
                                    border: false,
                                    items: [propertyCheckTree, propertyTree],
                                    //热键添加
                                    keys: [
                                        {
                                            key: Ext.EventObject.ENTER,
                                            fn: selectPropertyAndValue //执行的方法
                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: '保存',
                                            handler: selectPropertyAndValue
                                        },
                                        {
                                            text: '关闭',
                                            handler: function () {
                                                propertyTreeWindow.close();
                                            }
                                        }
                                    ]
                                });
                                propertyTreeWindow.setTitle('为后台类目' + selectNode.text + '继承属性和值');
                                propertyTreeWindow.show();
                            }
                        });
                    }
                    treeMenu.add([
                        {
                            text: '设置SEO推广信息',
                            handler: function () {
                                SEO(selectNode.text,selectNode.id,'CATEGORY');
                            }
                        },
                        {
                            text: '管理属性和值',
                            handler: function () {
                                var cpvWin = new CPVWindow(selectNode, result.data.root);
                                cpvWin.setTitle('设置' + selectNode.text + '的属性和属性值');
                                cpvWin.show();
                            }
                        },
                        {
                            text: '设置属性细节',
                            handler: function () {
                                var pvdWin = new PVDWindow(selectNode.id);
                                pvdWin.setTitle('设置' + selectNode.text + '的属性和属性值细节');
                                pvdWin.show();
                            }
                        },

                        {
                            text: '删除',
                            handler: function () {
                                if (!selectNode.isLeaf()) {
                                    Ext.Msg.alert("错误", "请选择叶子节点进行删除");
                                    return;
                                }

                                doAjax('category/delete', function (obj) {
                                    if (obj.data.prodExist) {
                                        Ext.Msg.alert("失败", "这个类目上已经发布了商品，不能删除");
                                    } else {
                                        var tree = Ext.getCmp('category-tree');
                                        doAjax('category/selectPathAsDelete/' + selectNode.parentNode.id, function (obj, text) {
                                            tree.selectPath(obj.msg);
                                        });
                                        tree.root.reload();
                                    }
                                }, {
                                    categoryId: selectNode.id
                                }, "你确定删除这个类目吗？");

                            }
                        }
                    ]);
                    treeMenu.showAt(e.getXY());
                },
                failure: function () {
                    Ext.Msg.alert("失败", "读取后台数据失败");
                },
                params: {
                    categoryId: selectNode.id
                }
            });


        }, this);

        var height = document.body.clientHeight;
        return  this.app.getDesktop().createWindow({
            id: this.id,
            title: '后台类目树表',
            width: 900,
            height: height * 0.85,
            tbar: [
                {
                    text: '添加后台根类目',
                    iconCls: 'add',
                    handler: function () {
                        var categoryWin = new CategoryAddWindow({parentId: -1});
                        categoryWin.show();
                    }
                },
                {
                    text: '推送全部类目',
                    iconCls: 'sync',
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
                            url:'category/push',
                            success:function (response, opts) {
                                Ext.MessageBox.hide();
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert("成功", obj.msg);
                            },
                            failure:function (response, opts) {
                                Ext.MessageBox.hide();
                                Ext.Msg.alert("失败", "推送失败");
                            }
                        });
                    }
                }

            ],
            border: false,
            items: [categoryTree]
        });

    }

});

