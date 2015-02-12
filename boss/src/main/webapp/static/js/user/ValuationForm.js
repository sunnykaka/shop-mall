var getValuationForm = function () {
    var form =  buildForm('user/valuation/add', [

        {
            text: '提交',
            handler: function (button) {
                commitForm(button.ownerCt.ownerCt, function () {
                    Ext.getCmp('productValuationGrid').getStore().reload();
                    form.ownerCt.close();
                })
            }
        },
        {
            text: '取消',
            handler: function (button) {
                form.ownerCt.close();
            }
        }

    ], [

        {
            xtype: 'numberfield',
            name: 'productId',
            fieldLabel: '商品ID',
            allowBlank: false
        },
        {
            name: 'userName',
            fieldLabel: '用户名',
            allowBlank: false
        },
        {
            name: 'point',
            fieldLabel: '分数',
            allowBlank: false
        },
        {
            xtype: 'textarea',
            name: 'content',
            maxLength: 1500,
            fieldLabel: '内容',
            allowBlank: false
        }
    ])

    return form;
};