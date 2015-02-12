/**
 商品选择组件
 */

var buildProductSelector = function () {

    var store = getStore('product/list', Ext.Model.Product);


    ProductCategoryTree = new Ext.tree.TreePanel({
        title: '商品类目',
        rootVisible: false,
        lines: false,
        dataUrl: 'category/tree',
        autoScroll: true,
        root: {
            nodeType: 'async'
        },
        listeners: {
            click: function (node) {

                store.proxy = new Ext.data.HttpProxy({
                    url: 'product/list',
                    failure: function (response, opts) {
                        if (response.status == 403) {
                            Ext.Msg.alert('错误', '没有设置访问权限');
                            return;
                        }
                        Ext.Msg.alert('错误', '服务器出错');
                    }
                });

                if (node.isLeaf()) {
                    store.baseParams = {
                        categoryId: node.id,
                        search: '',
                        customerId: -1,
                        brandId: -1
                    };
                    store.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: node.id,
                            customerId: -1,
                            brandId: -1,
                            search: ''
                        }
                    });
                }
            }
        }
    });

    var supplierTree = new Ext.tree.TreePanel({
        title: '商品品牌',
        rootVisible: false,
        lines: false,
        dataUrl: 'product/supplier/brand/tree',
        autoScroll: true,
        root: {
            nodeType: 'async'
        },
        listeners: {
            click: function (node) {

                store.proxy = new Ext.data.HttpProxy({
                    url: 'product/list',
                    failure: function (response, opts) {
                        if (response.status == 403) {
                            Ext.Msg.alert('错误', '没有设置访问权限');
                            return;
                        }
                        Ext.Msg.alert('错误', '服务器出错');
                    }
                });

                if (node.isLeaf()) {
                    store.baseParams = {
                        categoryId: -1,
                        customerId: -1,
                        brandId: node.id,
                        search: ''
                    };
                    store.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: -1,
                            customerId: -1,
                            brandId: node.id,
                            search: ''
                        }
                    });
                } else {
                    store.baseParams = {
                        categoryId: -1,
                        customerId: node.id,
                        brandId: -1,
                        search: ''
                    };
                    store.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: -1,
                            customerId: node.id,
                            brandId: -1,
                            search: ''
                        }
                    });
                }
            }
        }
    });


    var supplierStoreTree = new Ext.tree.TreePanel({
        title: '商家仓库',
        rootVisible: false,
        lines: false,
        dataUrl: 'product/supplier/store/tree',
        autoScroll: true,
        root: {
            nodeType: 'async'
        },
        listeners: {
            click: function (node) {

                store.proxy = new Ext.data.HttpProxy({
                    url: 'product/list',
                    failure: function (response, opts) {
                        statusTips(response)
                    }
                });

                if (node.isLeaf()) {
                    store.baseParams = {
                        categoryId: -1,
                        customerId: -1,
                        brandId: -1,
                        storeId: node.id,
                        search: ''
                    };
                    store.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: -1,
                            customerId: -1,
                            storeId: node.id,
                            brandId: -1,
                            search: ''
                        }
                    });
                }
            }
        }
    });

    var tree = new Ext.TabPanel({
        height: 250,
        region: 'north',
        items: [ProductCategoryTree, supplierTree, supplierStoreTree]
    });

    tree.activate(0);

    var hasOnline = function (value) {
        return value ? "已上架" : "未上架";
    };

    var productLink = function (value, p, record){
        if (record.get('online')) {
            return String.format('<a href="{0}/product/{1}" target="_blank">{2}</a>', window.BuyHome, record.get('id'), value);
        }
        return value;
    };

    var productToolBar = [
        {
            text: '全部',
            iconCls: 'refresh',
            handler: function () {
                var productStore = productGrid.getStore();
                productStore.proxy = new Ext.data.HttpProxy({
                    url: 'product/list',
                    failure: function (response, opts) {
                        if (response.status == 403) {
                            Ext.Msg.alert('错误', '没有设置访问权限');
                            return;
                        }
                        Ext.Msg.alert('错误', '服务器出错');
                    }
                });
                productStore.baseParams = {
                    categoryId: -1,
                    customerId: -1,
                    brandId: -1,
                    search: ''
                };
                productStore.load({
                    params: {
                        start: 0,
                        limit: 20,
                        categoryId: -1,
                        customerId: -1,
                        brandId: -1,
                        search: ''
                    }
                });
            }
        },
        '-',
        {
            text: '预览',
            iconCls: 'preview',
            handler: function () {
                if (!sm.hasSelection()) {
                    Ext.Msg.alert('错误', '请选择要预览的商品');
                } else if (sm.getSelections().length > 1) {
                    Ext.Msg.alert('错误', '请仅仅选择一行');
                } else {
                    var record = sm.getSelected();
                    window.open('product/preview/' + record.get('id'));
                }
            }
        },
        '->',
        {
            xtype: 'combo',
            name: 'orderState',
            fieldLabel: '商品状态',
            mode: 'local',
            editable: false,
            triggerAction: 'all',
            width: 80,
            emptyText: '商品状态',
            store: new Ext.data.ArrayStore({
                fields: [ 'type', 'desc' ],
                data: [
                    [ true, '已上架' ],
                    [ false, '已下架' ]
                ]
            }),
            displayField: 'desc',
            valueField: 'type',
            enableKeyEvents: true,
            listeners: {
                select: function (field) {
                    var productStore = productGrid.getStore();
                    productStore.proxy = new Ext.data.HttpProxy({
                        url: 'product/list/status',
                        failure: function (response, opts) {
                            if (response.status == 403) {
                                Ext.Msg.alert('错误', '没有设置访问权限');
                                return;
                            }
                            Ext.Msg.alert('错误', '服务器出错');
                        }
                    });
                    productStore.baseParams = {
                        start: 0,
                        limit: 20,
                        online: field.getValue()
                    };

                    productStore.load({
                        params: {
                            start: 0,
                            limit: 20,
                            online: field.getValue()
                        }
                    });
                }
            }
        },
        {
            xtype: 'textfield',
            name: 'search',
            emptyText: '商品ID号或者商品名',
            enableKeyEvents: true,
            listeners: {
                keyup: function (field) {
                    var productStore = productGrid.getStore();
                    productStore.proxy = new Ext.data.HttpProxy({
                        url: 'product/list',
                        failure: function (response, opts) {
                            if (response.status == 403) {
                                Ext.Msg.alert('错误', '没有设置访问权限');
                                return;
                            }
                            Ext.Msg.alert('错误', '服务器出错');
                        }
                    });

                    productStore.baseParams = {
                        categoryId: -1,
                        customerId: -1,
                        brandId: -1,
                        search: field.getValue()
                    };

                    productStore.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: -1,
                            customerId: -1,
                            brandId: -1,
                            search: field.getValue()
                        }
                    });
                }
            }
        },
        {
            xtype: 'textfield',
            name: 'productCode',
            emptyText: '商品编码',
            enableKeyEvents: true,
            width: 60,
            listeners: {
                keyup: function (field) {
                    var productStore = productGrid.getStore();
                    productStore.proxy = new Ext.data.HttpProxy({
                        url: 'product/list/code',
                        failure: function (response, opts) {
                            if (response.status == 403) {
                                Ext.Msg.alert('错误', '没有设置访问权限');
                                return;
                            }
                            Ext.Msg.alert('错误', '服务器出错');
                        }
                    });

                    productStore.baseParams = {
                        categoryId: -1,
                        customerId: -1,
                        brandId: -1,
                        search: field.getValue()
                    };

                    productStore.load({
                        params: {
                            start: 0,
                            limit: 20,
                            categoryId: -1,
                            customerId: -1,
                            brandId: -1,
                            code: field.getValue()
                        }
                    });
                }
            }
        }

    ];

    var sm = new Ext.grid.RowSelectionModel({
        singleSelect: true
    });

    productGrid = new Ext.grid.GridPanel({
        title: '商品列表',
        region: 'center',
        autoScroll: true,
        loadMask: true,
        ds: store,
        sm: sm,
        columns: [
            {header: "ID号", width: 85, sortable: true, dataIndex: 'id'},
            {header: "类别", width: 85, sortable: true, dataIndex: 'categoryName'},
            {header: "商品编号", width: 100, sortable: true, dataIndex: 'productCode'},
            {header: "商品品牌", width: 100, sortable: true, dataIndex: 'brandName'},
            {header: "是否上架", width: 100, sortable: true, dataIndex: 'online', renderer:hasOnline},
            {header: "商品名", width: 300, sortable: true, dataIndex: 'name', renderer:productLink}
        ],

        viewConfig: {
            forceFit: true
        },

        tbar: productToolBar,
        bbar: new Ext.PagingToolbar({
            pageSize: 20,
            store: store,
            displayInfo: true
        })
    });


    var productSelector = {
        layout: 'border',
        border: false,
        width: 450,
        split: true,
        items: [
            tree,
            productGrid
        ]
    };

    return productSelector;

}

