/**
 * 内容管理系统的管理JS
 */

Desktop.ConsultationWindow = Ext.extend(Ext.app.Module, {

    id: 'Consultation-win',

    title: '咨询管理',


    //创建安全管理的窗口
    createWindow: function () {


        var consultationStore = getStore("product/consultation/search", Ext.Model.Consultation);


        var show = function (value) {
            if (value == 'all') {
                return '全部咨询';
            }

            if (value == 'invoice') {
                return '发票咨询';
            }
            if (value == 'pay') {
                return '支付咨询';
            }
            if (value == 'stock') {
                return '库存及配送';
            }
            if (value == 'sales') {
                return '促销及赠品';
            }
            if (value == 'product') {
                return '商品咨询';
            }
        };

        var grid = new Ext.grid.GridPanel({
            title: '信息列表',
            region: 'center',
            autoScroll: true,
            height: 420,
            store: consultationStore,
            loadMask: true,
            viewConfig: {
                forceFit: true
            },
            columns: [
                {header: "问", width: 120, sortable: true, dataIndex: 'askContent'},
                {header: "答", width: 120, sortable: true, dataIndex: 'answerContent'},
                {header: "咨询人", width: 100, sortable: true, dataIndex: 'askedUserName'},
                {header: "咨询时间", width: 100, sortable: true, dataIndex: 'askTime'},
                {header: "咨询类型", width: 60, sortable: true, dataIndex: 'consultationCategory', renderer: show},
                {header: "商品编码", width: 60, sortable: true, dataIndex: 'productCode'},
                {header: "商品名称", width: 250, sortable: true, dataIndex: 'productName', id: 'productName'}
            ],

            bbar: new Ext.PagingToolbar({
                pageSize: 28,
                store: consultationStore,
                displayInfo: true
            }),

            tbar: [
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        doGridRowDelete(grid, 'product/consultation/delete', function () {
                            grid.getStore().reload();
                        });
                    }
                },
                {
                    text: '编辑',
                    iconCls: 'edit',
                    handler: function () {
                        var sm = grid.getSelectionModel();
                        var record = sm.getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要编辑的咨询信息");
                        } else {
                            var editForm = new Ext.form.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'product/consultation/update',
                                frame: true,
                                width: 300,
                                defaults: {
                                    width: 300
                                },
                                defaultType: 'textfield',

                                items: [
                                    {
                                        fieldLabel: '咨询编号',
                                        xtype: 'hidden',
                                        name: 'id',
                                        value: record.get("id")
                                    },
                                    {
                                        readOnly: true,
                                        fieldLabel: '问',
                                        anchor: '100%',
                                        name: 'askContent',
                                        xtype: 'textarea',
                                        value: record.get("askContent")
                                    },
                                    {
                                        fieldLabel: '答',
                                        name: 'answerContent',
                                        xtype: 'textarea',
                                        value: record.get("answerContent"),
                                        anchor: '100%',
                                        height: 100,
                                        allowBlank: false
                                    }
                                ]
                            });

                            var editorC = function () {
                                commitForm(editForm, function () {
                                    editWindow.close();
                                    grid.getStore().reload();
                                });
                            };
                            var editWindow = new Ext.Window({
                                title: '信息编辑',
                                width: 500,
                                layout: 'fit',
                                plain: true,
                                buttonAlign: 'center',
                                bodyStyle: 'padding:5px;',
                                items: editForm,
                                buttons: [
                                    {
                                        text: '更新',
                                        handler: editorC
                                    },
                                    {
                                        text: '取消',
                                        handler: function () {
                                            editWindow.close();
                                        }
                                    }
                                ]
                            });

                            editWindow.show(this.id);
                        }
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        grid.getStore().reload();
                    }
                }

            ]
        });


        var searchConsultation = function () {
            var consultationCategory = form.getForm().findField('consultationCategory').getValue();
            var hasAnswer = form.getForm().findField('hasAnswer').getValue();
            var productId = form.getForm().findField('productId').getValue();
            if (productId == "") {
                productId = 0;
            }
            var myStore = grid.getStore();

            myStore.baseParams = {
                consultationCategory: consultationCategory,
                hasAnswer: hasAnswer,
                productId: productId
            };
            myStore.load({
                params: {
                    start: 0,
                    limit: 28,
                    consultationCategory: consultationCategory,
                    hasAnswer: hasAnswer
                }
            });
        };

        var form = new Ext.FormPanel({
            height: 100,
            title: '信息筛选',
            region: 'north',
            frame: true,
            items: [
                {
                    layout: 'form',
                    border: false,
                    buttonAlign: 'center',
                    keys: [
                        {
                            key: Ext.EventObject.ENTER,
                            fn: searchConsultation//执行的方法
                        }
                    ],
                    buttons: [
                        {
                            text: '查询',
                            handler: searchConsultation
                        },
                        {
                            text: '重置',
                            handler: function () {
                                form.getForm().reset();

                            }
                        }
                    ],

                    items: [
                        {
                            layout: 'column',
                            border: false,
                            labelWidth: 75,
                            items: [
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.33,
                                    items: [
                                        {
                                            name: 'consultationCategory',
                                            anchor: '80%',
                                            xtype: 'combo',
                                            hiddenName: 'consultationCategory',
                                            fieldLabel: '咨询类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: new Ext.data.ArrayStore({
                                                fields: ['id', 'type' ],
                                                data: [
                                                    [ 'invoice', '发票咨询' ],
                                                    [ 'pay', '支付咨询' ],
                                                    [ 'stock', '库存配送' ],
                                                    [ 'product', '商品咨询' ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'id'
                                        }
                                    ]
                                },
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.33,
                                    items: [
                                        {
                                            name: 'hasAnswer',
                                            anchor: '80%',
                                            xtype: 'combo',
                                            hiddenName: 'hasAnswer',
                                            fieldLabel: '是否已答',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',

                                            store: new Ext.data.ArrayStore({
                                                fields: ['id', 'type' ],
                                                data: [
                                                    [ '0', '否' ],
                                                    [ '1', '是' ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'id'

                                        }
                                    ]
                                },
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.33,
                                    items: [
                                        {
                                            name: 'productId',
                                            anchor: '80%',
                                            xtype: 'textfield',
                                            hiddenName: 'productId',
                                            fieldLabel: '商品ID'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '咨询管理',
            height: height * 0.85,
            layout: 'border',
            width: 900,
            items: [form, grid]

        });
    }
});
