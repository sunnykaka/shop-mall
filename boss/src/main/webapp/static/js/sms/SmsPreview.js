SmsPreview = function (smsSends) {
    var myData = [
    ];
    for(var i=0;i<smsSends.length;i++){
        var content=[];
        content.push(smsSends[i].smsContent);
        myData.push(content);
    }

    var store = new Ext.data.ArrayStore({
        fields: [
            {name: 'smsContent'}
        ]
    });

    store.loadData(myData);
    var tpl = new Ext.XTemplate(
        '<tpl for="."><div class="view-text">{smsContent}</div></tpl>'
    );

    var view = new Ext.DataView({
        store: store,
        tpl: tpl,
        loadingText: '加载进行中....',
        multiSelect: true
    });

    SmsPreview.superclass.constructor.call(this, {
        title: '短信预览',
        border: false,
        autoScroll: true,
        height: 400,
        items: view
    });
}
Ext.extend(SmsPreview, Ext.Panel);