Desktop.ConstWindow = Ext.extend(Ext.app.Module, {

    id: 'Const-win',

    title: '常数设置',

    createWindow: function () {
        window.open("/const/list")
    }
})