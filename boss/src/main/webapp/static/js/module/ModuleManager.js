Desktop.ModuleWindow = Ext.extend(Ext.app.Module, {

    id: 'Module-win',

    title: '模块管理',

    createWindow: function () {

        var menuTab = new Ext.TabPanel({
            region: 'center',
            activeTab: 0,
            items: [new CommonModuleTab(), new PagePrototype(true)]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            width: 800,
            height: height * 0.85,
            layout: 'border',
            items: [menuTab]
        });
    }
});