Title = function (product) {

    titleForm = this.titleForm = new Ext.FormPanel({
        bodyStyle: 'padding:15px',
        labelWidth: 80,
        url: 'product/title/update',
        frame: true,
        buttonAlign: 'center',
        defaults: {
            width: 350
        },
        items: [
            {
                xtype: 'hidden',
                name: 'productId',
                value: product.get('id')
            },
            {
                fieldLabel: '商品编号',
                name: 'productCode',
                value: product.get("productCode"),
                allowBlank: false,
                xtype: 'textfield',
                maxLength: 100
            },
            {
                fieldLabel: '商品标题',
                name: 'name',
                value: product.get("name"),
                allowBlank: false,
                xtype: 'textarea',
                maxLength: 100
            },
            {
                fieldLabel: '推荐理由',
                name: 'description',
                value: product.get('description'),
                xtype: 'textarea',
                maxLength: 100
            },
            {
                fieldLabel: '标签类型',
                hiddenName: 'tagType',
                name: 'tagType',
                value: product.get('tagType'),
                xtype : 'combo',
                multiSelect : false,
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        [ '无', 'DEFAULT' ],
                        [ '新品', 'NEW' ],
                        [ '热销', 'HOT' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                allowBlank: true
            }
        ],
        buttons: [
            {
                text: '更新',
                handler: function () {
                    commitForm(titleForm, function () {
                        Ext.Msg.alert("说明", "更新成功! 提示: 若您修改了商品编号, 请务必同步修改 sku 里的编码!");
                        productGrid.getStore().reload();
                    });
                }
            }
        ]
    });


    Title.superclass.constructor.call(this, {
        title: '标签管理',
        items: this.titleForm
    });


}

Ext.extend(Title, Ext.Panel);