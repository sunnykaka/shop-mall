Desktop.EmailWindow = Ext.extend(Ext.app.Module, {

    id: 'Email-win',

    title: '邮件发送',

    createWindow: function () {
        window.open("/email/new")
    }
})