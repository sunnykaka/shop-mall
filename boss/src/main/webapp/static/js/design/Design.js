(function() {

    Desktop.DesignWindow = Ext.extend(Ext.app.Module, {
        id: 'Design-win',
        title: '网站装修',
        createWindow: function() {
            //return doAjax('design/shop/address', function(obj) {
            //    return window.open(obj.data.address);
            //});
            window.open(window.designUrl + "/design/shop/1");
        }
    });

}).call(this);
