/**
 * 用户评论管理JS
 */

Ext.Loader.load(['/static/js/user/ValuationModel.js', '/static/js/user/ValuationForm.js', '/static/js/user/ImportValuationForm.js', '/static/js/user/ValuationData.js', '/static/js/user/ImportValuationData.js']);


Desktop.ValuationWindow = Ext.extend(Ext.app.Module, {

    id: 'Valuation-win',

    title: '评论管理',

    createProductList: function () {
        var productStore = getStore('user/valuation/product/list', Ext.Model.Valuation);

        var grid = new Ext.grid.GridPanel({
            id: 'productValuationGrid',
            title: '被评论的商品列表',
            region: 'west',
            width: 350,
            store: productStore,
            autoScroll: true,
            loadMask: true,
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: true
            }),
            columns: [
                {header: "ID", width: 40, sortable: true, dataIndex: 'id'},
                {header: "商品名", sortable: true, dataIndex: 'name', id: 'name'},
                {header: "评论", width: 40, sortable: true, dataIndex: 'count'}
            ],
            viewConfig: {forceFit: true},
            tbar: [
                {
                    text: '添加',
                    iconCls: 'add',
                    handler: function () {
                        buildWin('添加评论', 400, getValuationForm()).show(this.id);
                    }
                },
                {
                    text: '删除',
                    iconCls: 'remove',
                    handler: function () {
                        doGridRowDelete(grid, 'user/valuation/product/delete', function () {
                            grid.getStore().reload();
                            Ext.EJS.Valuation.view.getStore().reload();
                            Ext.EJS.Valuation.importView.getStore().reload();
                        });
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        grid.getStore().reload({
                            params: {
                                start: 0
                            }
                        });
                    }
                },
                '->',
                {
                    text: '导入',
                    iconCls: 'add',
                    handler: function () {
                        buildWin('导入评论', 400, getImportValuationForm()).show();
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'productid',
                    emptyText: '商品ID',
                    width: 80,
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function (field) {
                            var searchProductId = field.getValue().trim();
                            if (null == searchProductId || '' == searchProductId) {
                                searchProductId = 0;
                            }
                            grid.getStore().load({
                                params: {
                                    start: 0,
                                    searchProductId: searchProductId
                                }
                            });
                        }
                    }
                }
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 27,
                store: productStore
            })
        });

        grid.on('rowclick', function (grid, rowIndex, event) {
            productTab.removeAll(true);
            productTab.add(new createValuationList());
            productTab.add(new createImportValuationList());
            productTab.setActiveTab(0);

            var record = grid.getStore().getAt(rowIndex);
            Ext.EJS.Valuation.valuationStore.baseParams = {
                start: 0,
                limit: 27,
                productId: record.get('id')
            };
            Ext.EJS.Valuation.valuationStore.reload();
            Ext.EJS.Valuation.importValuationStore.baseParams = {
                start: 0,
                limit: 27,
                productId: record.get('id'),
                isImportValuation: true
            };
            Ext.EJS.Valuation.importValuationStore.reload();
        });

        return grid;
    },

    createWindow: function () {
        Ext.namespace("Ext.EJS.Valuation");

        productTab = new Ext.TabPanel({
            region: 'center',
            deferredRender: false
        });
        productTab.removeAll(true);
        productTab.add(new createValuationList());
        productTab.add(new createImportValuationList());
        productTab.setActiveTab(0);

        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            border: false,
            height: document.body.clientHeight * 0.85,
            width: 900,
            layout: 'border',
            items: [this.createProductList(), productTab]
        });
    }

});

