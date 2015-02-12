Recommend = function (productId) {

    var accessoryStore = new Ext.data.JsonStore({
        url: 'product/accessory/' + productId,
        root: 'data.accessoryList',
        fields: ['productName', 'pictureUrl', 'productId', 'recommendProductId', 'recommendType']
    });
    accessoryStore.load();

    var composeStore = new Ext.data.JsonStore({
        url: 'product/compose/' + productId,
        root: 'data.composeList',
        fields: ['productName', 'pictureUrl', 'productId', 'recommendProductId', 'recommendType']
    });
    composeStore.load();


    var sameCategoryStore = new Ext.data.JsonStore({
        url: 'product/sameCategory/' + productId,
        root: 'data.sameCategoryList',
        fields: ['productName', 'pictureUrl', 'productId', 'recommendProductId', 'recommendType']
    });
    sameCategoryStore.load();


    var tpl = new Ext.XTemplate(
        '<tpl for=".">',
        '<div class="thumb-wrap" id="{productName}">',
        '<div class="thumb"><img src="{pictureUrl}" title="{productName}"></div>',
        '<span class="x-editable">{shortName}</span></div>',
        '</tpl>',
        '<div class="x-clear"></div>'
    );

    var recommendForm = new Ext.FormPanel({
        items: [
            {
                xtype: 'hidden',
                name: 'productId',
                value: productId
            },
            {
                fieldLabel: '商品ID号',
                name: 'recommendProductId',
                allowBlank: false,
                regex: /^\S+$/,
                xtype: 'numberfield'
            },
            {
                xtype: 'combo',
                hiddenName: 'recommendType',
                fieldLabel: '推荐类型',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        ['配件', 'ACCESSORY' ],
                        ['组合', 'COMPOSE' ],
                        ['同类', 'CATEGORY' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                allowBlank: false
            }
        ],
        border: false,
        url: 'product/recommend/new',
        bodyStyle: 'background:transparent;padding:10px;',
        defaults: {
            anchor: '100%'
        }
    });

    function cutProductName(productName, len) {
        var productName_length = 0;
        var productName_len = 0;
        var productName_cut = new String();
        var productName_len = String(productName).length;
        var expression = /[u00-uFF]/;
        for (var i = 0; i < productName_len; i++) {
            var productChar = String(productName).charAt(i);
            if (!expression.test(productChar)) {
                productName_length += 2;
            } else {
                productName_length++;
            }
            if (productName_length >= len) {
                productName_cut = productName_cut.concat("...");
                return productName_cut;
            } else {
                productName_cut = productName_cut.concat(productChar);
            }
        }
        return productName_cut;
    };
    var accessoryView = new Ext.DataView({
        id: 'images-view-accessory',
        store: accessoryStore,
        tpl: tpl,
        autoHeight: true,
        multiSelect: true,
        overClass: 'x-view-over',
        itemSelector: 'div.thumb-wrap',
        emptyText: '没有配件推荐',
        prepareData: function (data) {
            data.shortName = cutProductName(data.productName, 15);
            return data;
        }
    });

    var composeView = new Ext.DataView({
        id: 'images-view-compose',
        store: composeStore,
        tpl: tpl,
        autoHeight: true,
        multiSelect: true,
        overClass: 'x-view-over',
        itemSelector: 'div.thumb-wrap',
        emptyText: '没有组合推荐',
        prepareData: function (data) {
            data.shortName = cutProductName(data.productName, 15);
            return data;
        }
    });


    var sameCategoryView = new Ext.DataView({
        id: 'images-view-sameCategory',
        store: sameCategoryStore,
        tpl: tpl,
        autoHeight: true,
        multiSelect: true,
        overClass: 'x-view-over',
        itemSelector: 'div.thumb-wrap',
        emptyText: '没有同类推荐',
        prepareData: function (data) {
            data.shortName = cutProductName(data.productName, 15);
            return data;
        }
    });
    var addRecommend = function () {
        commitForm(recommendForm, function () {
            recommendForm.getForm().reset();
            win.hide();
            accessoryStore.load();
            composeStore.load();
            sameCategoryStore.load();
        });

    };
    var win = new Ext.Window({autoHeight: true,
        width: 500,
        resizable: false,
        plain: true,
        modal: true,
        closeAction: 'hide',
        autoScroll: true,
        title: '推荐商品',
        keys: [
            {
                key: Ext.EventObject.ENTER,
                fn: addRecommend //执行的方法
            }
        ],
        buttons: [
            {
                text: '提交',
                handler: addRecommend
            }
        ],

        items: recommendForm });
    Recommend.superclass.constructor.call(this, {
        title: '关联销售',
        autoHeight: true,
        tbar: [
            {
                text: '增加推荐',
                iconCls: 'add',
                handler: function () {
                    win.show(this.id);
                }
            },
            '-',
            {
                text: '删除推荐',
                iconCls: 'remove',
                handler: function () {
                    var recommendProductIds = [];
                    var recommendTypes = [];
                    if (accessoryView.getSelectedRecords().length == 0 && composeView.getSelectedRecords().length == 0 && sameCategoryView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要删除的推荐");
                        return;
                    }
                    Ext.each(accessoryView.getSelectedRecords(), function (r) {
                        recommendProductIds.push(r.get('recommendProductId'));
                        recommendTypes.push(r.get('recommendType'));
                    });

                    Ext.each(composeView.getSelectedRecords(), function (r) {
                        recommendProductIds.push(r.get('recommendProductId'));
                        recommendTypes.push(r.get('recommendType'));
                    });

                    Ext.each(sameCategoryView.getSelectedRecords(), function (r) {
                        recommendProductIds.push(r.get('recommendProductId'));
                        recommendTypes.push(r.get('recommendType'));
                    });


                    doAjax('product/recommend/delete', function () {
                        accessoryStore.load();
                        composeStore.load();
                        sameCategoryStore.load();
                    }, {
                        productId: productId,
                        recommendProductIds: recommendProductIds,
                        recommendTypes: recommendTypes
                    }, "你确定删除这些推荐吗？");

                }
            } ,
            '-',
            {

                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    accessoryStore.load();
                    composeStore.load();
                    sameCategoryStore.load();
                }
            } ,
            '-'
        ],
        items: [
            {
                title: '配件推荐',
                items: accessoryView,
                height: 250
            },
            {
                title: '组合推荐',
                items: composeView,
                height: 250
            },
            {
                title: '同类推荐',
                items: sameCategoryView,
                height: 250
            }
        ]
    });

}

Ext.extend(Recommend, Ext.Panel);