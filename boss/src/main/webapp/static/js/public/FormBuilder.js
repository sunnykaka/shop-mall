/**
 * 表单构建
 */

var FB = function (formItems, submitUrl, title, enterSubmit, successCallBack) {

    var form = new Ext.FormPanel({
        baseCls: 'x-plain',
        labelWidth: 60,
        url: submitUrl,
        frame: true,
        defaults: {anchor: '100%'},
        autoHeight: true,
        items: formItems
    });

    var handler = function () {
        commitForm(form, function () {
            Ext.Msg.alert("成功", "操作成功");
            win.close();
            successCallBack();
        });
    };

    var keys = [];
    if (enterSubmit) {
        keys.push({
            key: Ext.EventObject.ENTER,
            fn: handler //执行的方法
        });
    }

    var win = new Ext.Window({
        title: title,
        width: 400,
        autoHeight: true,
        layout: 'fit',
        plain: true,
        buttonAlign: 'center',
        bodyStyle: 'padding:5px;',
        items: form,
        keys: keys,
        buttons: [
            {
                text: '保存',
                handler: handler
            },
            {
                text: '取消',
                handler: function () {
                    win.close();
                }
            }
        ]
    });
    win.show();

    return form;

};
