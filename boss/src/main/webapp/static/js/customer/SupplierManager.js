/**
 *  商家管理系统的管理JS
 */

Desktop.SupplierWindow = Ext.extend(Ext.app.Module, {

    id: 'Supplier-win',

    title: '商家管理',

    createWindow: function () {

        // 创建store
        var customerStore = getStore('crm/customer/list', Ext.Model.Customer);


        var customerGrid = new Ext.grid.GridPanel({
            id: 'customerGridId',
            store: customerStore,
            region: 'center',
            autoScroll: true,
            loadMask: true,
            columns: [
                { header: '商家编号', width: 200, sortable: true, dataIndex: 'id'},
                { id: 'customerName', header: '商家名称', sortable: true, dataIndex: 'name'},
                { header: '快递公司', sortable: true, dataIndex: 'defaultLogistics', renderer: EJS.companyRender}
            ],

            viewConfig: {
                forceFit: true
            },
            tbar: [
                {
                    iconCls: 'add',
                    text: '添加商家',
                    handler: function () {
                        FB([
                            {
                                fieldLabel: '商家名称',
                                name: 'name',
                                allowBlank: false,
                                regex: /^\S+$/,
                                xtype: 'textfield',
                                maxLength: 255
                            },
                            {
                                xtype: 'combo',
                                hiddenName: 'defaultLogistics',
                                fieldLabel: '快递公司',
                                mode: 'local',
                                editable: false,
                                triggerAction: 'all',
                                emptyText: '请选择',
                                store: new Ext.data.ArrayStore({
                                    fields: [ 'type', 'desc' ],
                                    data: EJS.companyData
                                }),
                                displayField: 'desc',
                                valueField: 'type',
                                allowBlank: false
                            }
                        ], 'crm/customer/add', '添加商家', false, function () {
                            customerGrid.getStore().reload();
                        });
                    }

                } ,
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        doGridRowDelete(customerGrid, 'crm/customer/delete/batch', function () {
                            customerGrid.getStore().reload();
                            brandGrid.getStore().reload();
                            storageGird.getStore().reload();
                            customerAccountGird.getStore().reload();
                        });
                    }
                },
                {
                    text: '编辑',
                    iconCls: 'edit',
                    handler: function () {
                        var sm = customerGrid.getSelectionModel();
                        var record = sm.getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要编辑的商家");
                        } else {
                            var customerEditForm = new Ext.form.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'crm/customer/update',
                                frame: true,
                                width: 500,
                                defaults: {
                                    width: 300
                                },
                                defaultType: 'textfield',

                                items: [
                                    {
                                        fieldLabel: '商家编号',
                                        xtype: 'hidden',
                                        name: 'id',
                                        value: record.get("id")
                                    },
                                    {
                                        xtype: 'combo',
                                        hiddenName: 'defaultLogistics',
                                        fieldLabel: '快递公司',
                                        mode: 'local',
                                        editable: false,
                                        triggerAction: 'all',
                                        emptyText: '请选择',
                                        value: record.get("defaultLogistics"),
                                        store: new Ext.data.ArrayStore({
                                            fields: [ 'type', 'desc' ],
                                            data: EJS.companyData
                                        }),
                                        displayField: 'desc',
                                        valueField: 'type',
                                        allowBlank: false
                                    }
                                ]
                            });

                            var editorCustomer = function () {
                                commitForm(customerEditForm, function () {
                                    customerEditWindow.close();
                                    customerGrid.getStore().reload();
                                });
                            }

                            var customerEditWindow = new Ext.Window({
                                title: '商家编辑',
                                width: 500,
                                layout: 'fit',
                                plain: true,
                                buttonAlign: 'center',
                                bodyStyle: 'padding:5px;',
                                items: customerEditForm,
                                //热键添加
                                keys: [
                                    {
                                        key: Ext.EventObject.ENTER,
                                        fn: editorCustomer //执行的方法
                                    }
                                ],
                                buttons: [
                                    {
                                        text: '更新',
                                        handler: editorCustomer
                                    },
                                    {
                                        text: '取消',
                                        handler: function () {
                                            customerEditWindow.close();
                                        }
                                    }
                                ]
                            });

                            customerEditWindow.show();
                        }
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        customerGrid.getStore().reload();
                    }
                }

            ],
            bbar: new Ext.PagingToolbar({   // 分页
                pageSize: 10,
                store: customerStore,
                displayInfo: true
            })
        });

        // 商家品牌store
        var brandStore = new Ext.data.Store({
            baseParams: {
                customerId: 1,
                start: 0,
                limit: 5
            },
            reader: new Ext.data.JsonReader({
                totalProperty: 'data.totalCount',
                root: 'data.result'
            }, Ext.data.Record.create(
                [
                    {name: 'id', type: 'int'},
                    {name: 'name', type: 'string'},
                    {name: 'desc', type: 'string'},
                    {name: 'picture', type: 'string'},
                    {name: 'story', type: 'string'}
                ]
            ))
        });

        // 商家品牌grid
        var brandGrid = new Ext.grid.GridPanel({
            id: 'customerBrandGridId',
            store: brandStore,
            region: 'center',
            autoScroll: true,
            loadMask: true,
            height: 190,
            border: false,
            columns: [
                { header: '品牌编号', width: 200, sortable: true, dataIndex: 'id' },
                { id: 'brandName', header: '品牌名称', sortable: true, dataIndex: 'name'}
            ],
            viewConfig: {
                forceFit: true
            },
            tbar: [
                {
                    iconCls: 'add',
                    text: '添加品牌',
                    handler: function () {
                        var sm = customerGrid.getSelectionModel();
                        var customerInfo = sm.getSelected();

                        if (undefined == sm.getSelected()) {
                            Ext.Msg.alert("错误", "请选择商家");
                            return;
                        }

                        var addEditor = new Ext.form.TextArea({
                            name: 'story',
                            fieldLabel: '品牌故事',
                            anchor: '100%',
                            height: 500
                        });

                        var brandAddForm = new Ext.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                defaults: {
                                    xtype: 'textfield'
                                },
                                url: 'crm/brand/add',
                                frame: true,
                                listeners: {
                                    'render': function () {
                                        KE.app.init({
                                            renderTo: addEditor.getId(),
                                            uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                                            filePostName: 'uploadFile'
                                        });
                                    }
                                },

                                items: [
                                    {
                                        xtype: 'hidden', // 隐藏域，保存商家的编号
                                        name: 'customerId',
                                        value: customerInfo.get("id")  // 值为选中的商家编号
                                    },
                                    {
                                        fieldLabel: '品牌名称',
                                        name: 'name',
                                        allowBlank: false,
                                        anchor: '100%',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    },
                                    {
                                        fieldLabel: '品牌 Logo图',
                                        name: 'picture',
                                        anchor: '100%',
                                        maxLength: 255
                                    },
                                    {
                                        xtype: 'textarea',
                                        fieldLabel: '品牌文字描述',
                                        name: 'desc',
                                        anchor: '100%',
                                        maxLength: 2048
                                    },
                                    addEditor
                                ]
                            }
                        );

                        var addBrand = function () {

                            KE.app.getEditor(addEditor.getId()) && KE.app.getEditor(addEditor.getId()).sync();

                            commitForm(brandAddForm, function () {
                                brandGrid.getStore().reload();
                                brandWindow.close();
                            });

                        }
                        // 添加品牌窗体
                        var brandWindow = new Ext.Window({
                            title: '添加品牌',
                            width: 800,
                            layout: 'fit',
                            plain: true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px;',
                            items: brandAddForm,
                            //热键添加
                            keys: [
                                {
                                    key: Ext.EventObject.ENTER,
                                    fn: addBrand //执行的方法
                                }
                            ],
                            buttons: [
                                {
                                    text: '保存',
                                    handler: addBrand
                                },
                                {
                                    text: '取消',
                                    handler: function () {
                                        brandWindow.close();
                                    }
                                }
                            ]

                        });

                        brandWindow.show();
                    }

                } ,
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        doGridRowDelete(brandGrid, 'crm/brand/delete/batch', function () {
                            brandGrid.getStore().reload();
                        });
                    }
                },
                {
                    text: '编辑',
                    iconCls: 'edit',
                    handler: function () {
                        var sm = brandGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要编辑的品牌");
                        } else {
                            var record = sm.getSelected();
                            var editEditor = new Ext.form.TextArea({
                                name: 'story',
                                fieldLabel: '品牌故事',
                                height: 500,
                                value: record.get("story"),
                                anchor: '100%'
                            });
                            var cmRecord = customerGrid.getSelectionModel().getSelected();

                            var bandEditForm = new Ext.form.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'crm/brand/update',
                                frame: true,
                                defaultType: 'textfield',
                                listeners: {
                                    'render': function () {
                                        KE.app.init({
                                            renderTo: editEditor.getId(),
                                            uploadJson: window.BossHome + "/spacePicture/createImageUpload",
                                            filePostName: 'uploadFile'
                                        });
                                    }
                                },
                                items: [
                                    {
                                        xtype: 'hidden',
                                        name: 'id',
                                        value: record.get("id")
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: 'customerId',
                                        value: cmRecord.get("id")
                                    },
                                    {
                                        fieldLabel: '品牌 Logo图',
                                        name: 'picture',
                                        anchor: '100%',
                                        maxLength: 255,
                                        value: record.get('picture')
                                    },
                                    {
                                        xtype: 'textarea',
                                        fieldLabel: '品牌文字描述',
                                        name: 'desc',
                                        anchor: '100%',
                                        maxLength: 2048,
                                        value: record.get('desc')
                                    },
                                    editEditor
                                ]
                            });

                            var editorBrand = function () {

                                KE.app.getEditor(editEditor.getId()) && KE.app.getEditor(editEditor.getId()).sync();

                                commitForm(bandEditForm, function () {
                                    brandEditWindow.close();
                                    brandGrid.getStore().reload();
                                });

                            }

                            var brandEditWindow = new Ext.Window({
                                title: '品牌编辑',
                                width: 800,
                                layout: 'fit',
                                plain: true,
                                buttonAlign: 'center',
                                bodyStyle: 'padding:5px;',
                                items: bandEditForm,
                                //热键添加
                                keys: [
                                    {
                                        key: Ext.EventObject.ENTER,
                                        fn: editorBrand //执行的方法
                                    }
                                ],
                                buttons: [
                                    {
                                        text: '更新',
                                        handler: editorBrand
                                    },
                                    {
                                        text: '取消',
                                        handler: function () {
                                            brandEditWindow.close();
                                        }
                                    }
                                ]
                            });

                            brandEditWindow.show();
                        }
                    }
                },
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        brandGrid.getStore().reload();
                    }
                }
            ],

            // 分页
            bbar: new Ext.PagingToolbar({
                pageSize: 5,
                store: brandStore,
                displayInfo: true
            })
        })

        // 商家库位store
        var storageStore = new Ext.data.Store({
            reader: new Ext.data.JsonReader({
                totalProperty: 'data.totalCount',
                root: 'data.result'
            }, Ext.data.Record.create(
                [
                    {name: 'id', type: 'int'},
                    {name: 'consignor', type: 'string'},
                    {name: 'telephone', type: 'string'},
                    {name: 'address', type: 'string'},
                    {name: 'remarks', type: 'string'},
                    {name: 'company', type: 'string'},
                    {name: 'name', type: 'string'}
                ]
            )),
            baseParams: {
                customerId: 1,
                start: 0,
                limit: 5
            }
        });

        // 品牌库位grid
        var storageGird = new Ext.grid.GridPanel({
            id: 'customerStorageGridId',
            store: storageStore,
            loadMask: true,
            region: 'center',
            autoScroll: true,
            height: 190,
            columns: [
                { header: '库位编号', width: 200, sortable: true, dataIndex: 'id'},
                { id: 'storageName', header: '库位名称', sortable: true, dataIndex: 'name'}
            ],
            border: false,
            viewConfig: {
                forceFit: true
            },
            tbar: [
                {
                    iconCls: 'add',
                    text: '分配库位',
                    handler: function () {
                        var sm = customerGrid.getSelectionModel();
                        var customerInfo = sm.getSelected();

                        if (undefined == sm.getSelected()) {
                            Ext.Msg.alert("错误", "请选择商家");
                            return;
                        }

                        var storageAddForm = new Ext.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'crm/product/storage/add',
                                frame: true,
                                width: 500,
                                defaults: {
                                    width: 300
                                },
                                defaultType: 'textfield',

                                items: [
                                    {
                                        xtype: 'hidden',
                                        name: 'customerId',
                                        value: customerInfo.get("id")
                                    },
                                    {
                                        fieldLabel: '库位名称',
                                        name: 'name',
                                        allowBlank: false,
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    } ,
                                    {
                                        fieldLabel: '发货人',
                                        name: 'consignor',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    },
                                    {
                                        fieldLabel: '发货联系电话',
                                        name: 'telephone',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 20
                                    },
                                    {
                                        fieldLabel: '发货地址',
                                        name: 'address',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 500
                                    } ,
                                    {
                                        fieldLabel: '所属公司',
                                        name: 'company',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 225
                                    },
                                    {
                                        fieldLabel: '备注',
                                        name: 'remarks',
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 250
                                    }
                                ]

                            }

                        );

                        var addStorage = function () {

                            commitForm(storageAddForm, function () {
                                storageGird.getStore().reload();
                                storageWindow.close();
                            });

                        }

                        // 添加库位窗体
                        var storageWindow = new Ext.Window({
                            title: '添加库位',
                            width: 500,
                            //autoHeight:true,
                            layout: 'fit',
                            plain: true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px;',
                            items: storageAddForm,
                            //热键添加
                            keys: [
                                {
                                    key: Ext.EventObject.ENTER,
                                    fn: addStorage //执行的方法
                                }
                            ],
                            buttons: [
                                {
                                    text: '保存',
                                    handler: addStorage
                                },
                                {
                                    text: '取消',
                                    handler: function () {
                                        storageWindow.close();
                                    }
                                }
                            ]

                        });

                        storageWindow.show();
                    }

                } ,
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        doGridRowDelete(storageGird, 'crm/product/storage/delete/batch', function () {
                            storageGird.getStore().reload();
                        });
                    }
                },
                {
                    text: '编辑',
                    iconCls: 'edit',
                    handler: function () {
                        var sm = storageGird.getSelectionModel();
                        var record = sm.getSelected();

                        var cmRecord = customerGrid.getSelectionModel().getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要编辑的仓位");
                        } else {
                            var storageEditForm = new Ext.form.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'crm/product/storage/update',
                                frame: true,
                                width: 500,
                                defaults: {
                                    width: 300
                                },
                                defaultType: 'textfield',

                                items: [
                                    {
                                        xtype: 'hidden',
                                        name: 'id',
                                        value: record.get("id")
                                    },
                                    {
                                        fieldLabel: '仓位名称',
                                        name: 'name',
                                        value: record.get("name"),
                                        allowBlank: false,
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: 'customerId',
                                        value: cmRecord.get("id")
                                    } ,
                                    {
                                        fieldLabel: '发货人',
                                        name: 'consignor',
                                        value: record.get("consignor"),
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    },
                                    {
                                        fieldLabel: '发货联系电话',
                                        name: 'telephone',
                                        value: record.get("telephone"),
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 20
                                    },
                                    {
                                        fieldLabel: '发货地址',
                                        name: 'address',
                                        value: record.get("address"),
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 500
                                    } ,
                                    {
                                        fieldLabel: '所属公司',
                                        name: 'company',
                                        value: record.get("company"),
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 225
                                    } ,
                                    {
                                        fieldLabel: '备注',
                                        name: 'remarks',
                                        value: record.get("remarks"),
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 250
                                    }
                                ]
                            });

                            var editorStorage = function () {

                                commitForm(storageEditForm, function () {
                                    storageEditWindow.close();
                                    storageGird.getStore().reload();
                                });

                            }

                            var storageEditWindow = new Ext.Window({
                                title: '仓位编辑',
                                width: 500,
                                //autoHeight:true,
                                layout: 'fit',
                                plain: true,
                                buttonAlign: 'center',
                                bodyStyle: 'padding:5px;',
                                items: storageEditForm,
                                //热键添加
                                keys: [
                                    {
                                        key: Ext.EventObject.ENTER,
                                        fn: editorStorage //执行的方法
                                    }
                                ],
                                buttons: [
                                    {
                                        text: '更新',
                                        handler: editorStorage
                                    },
                                    {
                                        text: '取消',
                                        handler: function () {
                                            storageEditWindow.close();
                                        }
                                    }
                                ]
                            });

                            storageEditWindow.show();
                        }
                    }
                },
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        storageGird.getStore().reload();
                    }
                }
            ],
            // 分页
            bbar: new Ext.PagingToolbar({
                pageSize: 5,
                store: storageStore,
                displayInfo: true
            })

        });

        // 商家帐号
        var customerAccountStore = new Ext.data.Store({
            reader: new Ext.data.JsonReader({
                totalProperty: 'data.totalCount',
                root: 'data.result'
            }, Ext.data.Record.create(
                [
                    {name: 'id', type: 'int'},
                    {name: 'accountName', type: 'string'},
                    {name: 'password', type: 'string'},
                    {name: 'mainAccount', type: 'boolean'},
                    {name: 'email', type: 'string'}
                ]
            )),
            baseParams: {
                customerId: 1,
                start: 0,
                limit: 5
            }
        });

        var isMainAccount=function(value){
            if(value){
                return "是";
            }else{
                return "否";
            }
        }
        // 商家帐号管理grid
        var customerAccountGird = new Ext.grid.GridPanel({
            id: 'customerAccountGridId',
            region: 'center',
            store: customerAccountStore,
            loadMask: true,
            autoScroll: true,
            height: 190,
            columns: [
                { header: '编号', width: 100, sortable: true, dataIndex: 'id'},
                { id: 'accountName', header: '帐号名称', sortable: true, dataIndex: 'accountName'},
                { header: '电子邮件', sortable: true, dataIndex: 'email'},
                { header: '是否为主账号', sortable: true, dataIndex: 'mainAccount',renderer:isMainAccount}
            ],
            border: false,
            viewConfig: {
                forceFit: true
            },
            tbar: [
                {
                    iconCls: 'add',
                    text: '分配帐号',
                    handler: function () {
                        var sm = customerGrid.getSelectionModel();
                        var customerInfo = sm.getSelected();

                        if (undefined == sm.getSelected()) {
                            Ext.Msg.alert("错误", "请选择商家");
                            return;
                        }

                        var customerAccountAddForm = new Ext.FormPanel({
                            id: 'customerAccountAddForm',
                            baseCls: 'x-plain',
                            labelWidth: 80,
                            url: 'crm/customerAccount/add',
                            frame: true,
                            width: 500,
                            defaults: {
                                width: 300
                            },
                            defaultType: 'textfield',

                            items: [
                                {
                                    xtype: 'hidden',
                                    name: 'customerId',
                                    value: customerInfo.get("id")
                                },
                                {
                                    fieldLabel: '帐号名称',
                                    name: 'accountName',
                                    allowBlank: false,
                                    regex: /^\S+$/, // 不能输入空格
                                    maxLength: 255
                                },
                                {
                                    fieldLabel: '电子邮件',
                                    name: 'email',
                                    allowBlank: false,
                                    vtype: 'email',
                                    maxLength: 100
                                }
                            ]

                        });

                        var addCustomerAccount = function () {

                            commitForm(customerAccountAddForm, function () {
                                customerAccountWindow.close();
                                customerAccountGird.getStore().reload();
                            });

                        }

                        // 添加商家帐号窗体
                        var customerAccountWindow = new Ext.Window({
                            title: '添加帐号',
                            width: 500,
                            //autoHeight:true,
                            layout: 'fit',
                            plain: true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px;',
                            items: customerAccountAddForm,
                            //热键添加
                            keys: [
                                {
                                    key: Ext.EventObject.ENTER,
                                    fn: addCustomerAccount //执行的方法
                                }
                            ],
                            buttons: [
                                {
                                    text: '保存',
                                    handler: addCustomerAccount
                                },
                                {
                                    text: '取消',
                                    handler: function () {
                                        customerAccountWindow.close();
                                    }
                                }
                            ]

                        });
                        customerAccountWindow.show();
                    }
                } ,
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        doGridRowDelete(customerAccountGird, 'crm/customerAccount/delete/batch', function () {
                            customerAccountGird.getStore().reload();
                        });
                    }
                },
                {
                    text: '编辑',
                    iconCls: 'edit',
                    handler: function () {
                        var sm = customerAccountGird.getSelectionModel();
                        var record = sm.getSelected();

                        var cmRecord = customerGrid.getSelectionModel().getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要编辑的帐号");
                        } else {
                            var customerAccountEditForm = new Ext.form.FormPanel({
                                baseCls: 'x-plain',
                                labelWidth: 80,
                                url: 'crm/customerAccount/update',
                                frame: true,
                                width: 500,
                                defaults: {
                                    width: 300
                                },
                                defaultType: 'textfield',

                                items: [
                                    {
                                        xtype: 'hidden',
                                        name: 'customerId',
                                        value: cmRecord.get("id")  // 客户编号
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: 'id',
                                        value: record.get("id")   // 帐号编号
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: 'accountName',
                                        value: record.get("accountName")  // 帐号名称,传值到后台
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: 'email',
                                        value: record.get("email")  // 邮箱,传值到后台
                                    },
                                    {
                                        fieldLabel: '帐号名称',
                                        value: record.get("accountName"),
                                        disabled: true // 只在页面显示，不传值到后台
                                    },
                                    {
                                        fieldLabel: '帐号密码',
                                        name: 'password',
                                        inputType: 'password',
                                        value: record.get("password"),
                                        allowBlank: false,
                                        regex: /^\S+$/, // 不能输入空格
                                        maxLength: 255
                                    },
                                    {
                                        fieldLabel: '电子邮件',
                                        name: 'email',
                                        value: record.get("email"),
                                        disabled: true // 只在页面显示，不传值到后台
                                        //allowBlank:false,
                                        //maxLength:255
                                    }
                                ]
                            });

                            var editorCustomerAccount = function () {

                                commitForm(customerAccountEditForm, function () {
                                    customerAccountEditWindow.close();
                                    customerAccountGird.getStore().reload();
                                });

                            }

                            var customerAccountEditWindow = new Ext.Window({
                                title: '帐号编辑',
                                width: 500,
                                //autoHeight:true,
                                layout: 'fit',
                                plain: true,
                                buttonAlign: 'center',
                                bodyStyle: 'padding:5px;',
                                items: customerAccountEditForm,
                                //热键添加
                                keys: [
                                    {
                                        key: Ext.EventObject.ENTER,
                                        fn: editorCustomerAccount //执行的方法
                                    }
                                ],
                                buttons: [
                                    {
                                        text: '更新',
                                        handler: editorCustomerAccount
                                    },
                                    {
                                        text: '取消',
                                        handler: function () {
                                            customerAccountEditWindow.close();
                                        }
                                    }
                                ]
                            });

                            customerAccountEditWindow.show();
                        }
                    }
                },
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        customerAccountGird.getStore().reload();
                    }
                }
            ],
            // 分页
            bbar: new Ext.PagingToolbar({
                pageSize: 5,
                store: customerAccountStore,
                displayInfo: true
            })

        });

        // 表格点击事件, 行（row）被单击时触发
        customerGrid.on("rowclick", function (grid, rowIndex, event) {

            if (!grid.tabs) {
                Ext.getCmp("customerTabs-panel").removeAll();

                Ext.getCmp("customerTabs-panel").add([
                    {
                        title: '商家品牌',
                        id: 'brandPanel',
                        layout: 'border',
                        items: brandGrid
                    },
                    {
                        title: '品牌库位',
                        id: 'storagePanel',
                        layout: 'border',
                        items: storageGird
                    },
                    {
                        title: '商家帐号',
                        id: 'customerAccountPanel',
                        layout: 'border',
                        items: customerAccountGird
                    }
                ]);
                grid.tabs = true;
                Ext.getCmp("customerTabs-panel").doLayout();
            }

            var record = grid.getStore().getAt(rowIndex);

            brandStore.setBaseParam("customerId", record.get("id")); // 更改brandStore中原先参数customerId的值
            storageStore.setBaseParam("customerId", record.get("id")); // 更改brandStore中原先参数customerId的值
            customerAccountStore.setBaseParam("customerId", record.get("id"));

            // 品牌
            brandStore.proxy = new Ext.data.HttpProxy({url: 'crm/customer/brand/list/' + record.id});
            // 库位
            storageStore.proxy = new Ext.data.HttpProxy({url: 'crm/customer/store/list/' + record.id});
            // 帐号
            customerAccountStore.proxy = new Ext.data.HttpProxy({url: 'crm/customerAccount/list/' + record.id});

            brandStore.load({
                params: {
                    customerId: record.get("id"),
                    start: 0,
                    limit: 5
                }
            });

            storageStore.load({
                params: {
                    customerId: record.get("id"),
                    start: 0,
                    limit: 5
                }
            });

            customerAccountStore.load({
                params: {
                    customerId: record.get("id"),
                    start: 0,
                    limit: 5
                }
            });
            var supplierTabsPanel=Ext.getCmp("customerTabs-panel");
            var supplierwin=Ext.getCmp("Supplier-win");

            supplierTabsPanel.setActiveTab(0);
            supplierTabsPanel.show();
            supplierwin.setHeight(document.body.clientHeight*0.85);
            supplierwin.doLayout();

        });

        var customerTabPanel = new Ext.TabPanel({
            id: 'customerTabs-panel',
            plain: true,
            region: 'south',
            height: 300,
            activeTab: 0,
            border: false,
            hidden:true,
            items: [

            ]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '商家管理',
            width: 920,
            height: height * 0.6,
            pageX:document.body.clientWidth*0.15,
            pageY:height*0.07,
            shim: false,
            layout: 'border',
            animCollapse: false,
            constrainHeader: true,
            items: [customerGrid, customerTabPanel]
        });
    }

});