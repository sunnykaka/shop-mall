var getImportValuationForm = function () {
    var form = new Ext.FormPanel({
        baseCls: 'x-plain',
        region: 'north',
        bodyStyle: 'padding:10px 10px 50px 10px',
        labelWidth: 80,
        buttonAlign: 'center',
        defaults: {
            anchor: '100%',
            xtype: 'textfield'
        },
        fileUpload: true,
        items: [
            new Ext.ux.form.FileUploadField({
                id: 'uploadValuationFile',
                name: 'uploadValuationFile',
                fieldLabel: '商品评论文件',
                allowBlank: false,
                buttonText: '请选择Excel文件'
            })
        ],
        buttons: [
            {
                text: '提交',
                handler: function () {
                    if (null != Ext.getCmp("uploadValuationFile").getValue() && "" != Ext.getCmp("uploadValuationFile").getValue()) {
                        Ext.MessageBox.wait('导入中...', '请等待');
                    } else {
                        Ext.Msg.alert("信息", "请选择要上传的文件");
                    }
                    commitForm(form, function (action, obj) {
                        Ext.MessageBox.hide();
                        if (action.success) {
                            Ext.Msg.alert("信息", "导入成功");
                            Ext.getCmp('productValuationGrid').getStore().reload();
                            form.ownerCt.close();
                        } else {
                            Ext.Msg.alert("信息", action.msg);
                        }
                    }, '/user/valuation/import')
                }
            },
            {
                text: '取消',
                handler: function (button) {
                    form.ownerCt.close();
                }
            }
        ]
    });
    return form;
}
