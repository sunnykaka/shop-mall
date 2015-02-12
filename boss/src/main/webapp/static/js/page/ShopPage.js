/**
 * 页面管理系统的管理JS
 */

Desktop.PageWindow = Ext.extend(Ext.app.Module, {

    id: 'Page-win',
    title: '页面管理',

    //创建页面管理的窗口
    createWindow: function () {
        var shopPageStore = createJsonStore('page/shop/list', [
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'pageType',
                type: 'string'
            },
            {
                name: 'pageStatus',
                type: 'string'
            },
            {
                name: 'release',
                type: 'bool'
            },
            {
                name: 'title',
                type: 'string'
            },
            {
                name: 'keywords',
                type: 'string'
            },
            {
                name: 'description',
                type: 'string'
            }
        ]);
        shopPageStore.load();

        var shopPageGrid = new Ext.grid.GridPanel({
            id: 'shopPageGrid',
            title: '店铺页面',
            loadMask: true,
            store: shopPageStore,
            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    width: 25
                },
                {
                    header: '页名',
                    dataIndex: 'name'
                },
                {
                    header: '类型',
                    dataIndex: 'pageType',
                    renderer: function (value) {
                        if (value == 'index') {
                            return '首页';
                        } else if (value == 'searchList') {
                            return '列表页';
                        } else if (value == 'detail') {
                            return "详情页";
                        } else if (value == 'detailIntegral') {
                            return "积分兑换详情页";
                        } else if (value == 'channel') {
                            return "频道页";
                        } else if (value == 'meal_detail') {
                            return "套餐详情";
                        } else if (value == 'other') {
                            return "其他页面";
                        }
                        return value;
                    }
                },
                {
                    header: '发布状态',
                    dataIndex: 'release',
                    renderer: function (value) {
                        return value ? "已发布" : "未发布";
                    }
                },
                {
                    header: '在线状态',
                    dataIndex: 'pageStatus',
                    renderer: function (value) {
                        return (value == "NORMAL") ? "在线中" : "已下线";
                    }
                }
            ],
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: false
            }),
            viewConfig: {
                forceFit: true
            },
            tbar: [
                {
                    text: '装修',
                    iconCls: 'run',
                    handler: function () {
                        var sm = Ext.getCmp("shopPageGrid").getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择店铺页面');
                        } else if (sm.getSelections().length > 1) {
                            Ext.Msg.alert('错误', '请仅仅选择一个页面');
                        } else {
                            var record = sm.getSelected();
                            //doAjax('design/shop/page/address', function (obj) {
                            //    window.open(obj.data.address + record.get('id'));
                            //});
                            window.open(window.designUrl + "/design/page/" + record.get('id'));
                        }
                    }
                },
                '-',
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        Ext.getCmp("shopPageGrid").getStore().reload();
                    }
                },
                '-',
                {
                    text: '查看',
                    iconCls: 'run',
                    handler: function () {
                        var sm = Ext.getCmp("shopPageGrid").getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择店铺页面');
                        } else if (sm.getSelections().length > 1) {
                            Ext.Msg.alert('错误', '请仅仅选择一个页面');
                        } else {
                            var record = sm.getSelected();

                            doAjax('design/shop/page/view', function (obj) {
                                window.open(obj.data.address + record.get('id'));
                            });

                        }
                    }
                },
                '-',
                {
                    text: '发布',
                    iconCls: 'run',
                    handler: function () {
                        var sm = Ext.getCmp("shopPageGrid").getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择店铺页面');
                        } else {
                            var records = sm.getSelections();
                            var ids = [];
                            for (var i = 0; i < records.length; i++) {
                                var record = records[i];
                                ids.push(record.get('id'));
                            }

                            doAjax('design/page/shop/publish/ajax', function () {
                                Ext.Msg.alert("成功", "发布成功");
                            }, { pageId:ids }, "你确定要发布这些页面吗？");
                        }
                    }
                },
                '-',
                {
                    text: '下线页面',
                    iconCls: 'config',
                    handler: function () {
                        var sm = Ext.getCmp("shopPageGrid").getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择店铺页面');
                            return;
                        }
                        if (sm.getSelections().length > 1) {
                            Ext.Msg.alert('错误', '请仅仅选择一个页面');
                            return;
                        }

                        var record = sm.getSelected();
                        if (record.get('pageType') != "other" || !record.get("release")) {
                            Ext.Msg.alert("错误", "不能下线主页面和未发布的页面");
                            return;
                        }
                        doAjax('design/page/shop/setToInvalid', function () {
                            Ext.Msg.alert("成功", "操作成功");
                            Ext.getCmp("shopPageGrid").getStore().reload();
                        }, {
                            pageId: record.get('id')
                        }, "你确定要失效这个页面吗？");
                    }
                },
                '-',
                {
                    text: '删除',
                    iconCls: 'remove',
                    handler: function () {
                        doGridRowDelete(Ext.getCmp("shopPageGrid"), 'page/shop/delete', function () {
                            Ext.getCmp("shopPageGrid").getStore().reload();
                        }, function (record) {
                            var type = record.get('pageType');
                            if (type != "other") {
                                Ext.Msg.alert("错误", "不能删除非其他类型的页面");
                                return true;
                            }
                        });
                    }
                }
            ]
        });

        shopPageGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var updatePageStoreForm = new Ext.FormPanel({
                labelWidth: 80,
                baseCls: 'x-plain',
                frame: true,
                buttonAlign: 'center',
                url: 'page/shop/updateShopPage',
                defaults: {
                    width: 350
                },
                items: [
                    {
                        xtype: 'hidden',
                        name: 'id',
                        value: record.get('id')
                    },
                    {
                        xtype: 'textfield',
                        name: 'title',
                        fieldLabel: '主题',
                        value: record.get('title'),
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
                        value: record.get('keywords'),
                        anchor: '100%',
                        height: 100
                    },
                    {
                        name: 'description',
                        xtype: 'textarea',
                        fieldLabel: '描述信息',
                        value: record.get('description'),
                        maxLength: 200,
                        anchor: '100%',
                        height: 100
                    }
                ]
            });
            var winPage = new Ext.Window({
                title: '修改页面元数据',
                items: updatePageStoreForm,
                height: 330,
                width: 520,
                layout: 'fit',
                plain: true,
                buttonAlign: 'center',
                bodyStyle: 'padding:5px;',
                buttons: [
                    {
                        text: '保存',
                        handler: function () {
                            commitForm(updatePageStoreForm, function () {
                                winPage.close();
                                Ext.getCmp("shopPageGrid").getStore().reload();
                            });
                        }
                    },
                    {
                        text: '取消',
                        handler: function () {
                            winPage.close();
                        }
                    }
                ]
            });
            winPage.show(this.id);
        });

        var menuTab = new Ext.TabPanel({
            region: 'center',
            activeTab: 0,
            items: [new PagePrototype(false), shopPageGrid]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            width: 800,
            height: height * 0.85,
            layout: 'border',
            items: [menuTab]
        });
    }
});