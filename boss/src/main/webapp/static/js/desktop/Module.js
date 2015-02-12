Ext.app.Module = function (config) {
    Ext.apply(this, config);
    Ext.app.Module.superclass.constructor.call(this);
    this.init();
}

Ext.extend(Ext.app.Module, Ext.util.Observable, {

    init: function () {
        this.launcher = {
            text: this.title,
            handler: this.createWindow,
            scope: this
        };
    },

    getWindow: function () {
        return this.app.getDesktop().getWindow(this.id) || this.createWindow();
    }
});