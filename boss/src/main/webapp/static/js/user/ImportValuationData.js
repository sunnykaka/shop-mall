createImportValuationList = function () {

    var importValuationStore = createJsonStore("/user/valuation/list", [
        { name: 'id', type: 'int' },
        { name: 'content', type: 'String' },
        { name: 'replyContent', type: 'String' },
        { name: 'appendContent', type: 'String' }
    ])

    Ext.EJS.Valuation.importValuationStore = importValuationStore;
    var ImportTpl = new Ext.XTemplate(
        '<tpl for="."><div class="view-text" id="{id}" style="height:auto;">{content}</div></tpl>'
    );


    var importView = new Ext.DataView({
        id: 'Product-Import-Valuation',
        store: importValuationStore,
        overClass: 'x-view-over',
        tpl: ImportTpl,
        loadingText: '评论加载中...',
        multiSelect: true,
        autoScroll: true,
        itemSelector: 'div.view-text',
        emptyText: '目前没有评论'
    });

    Ext.EJS.Valuation.importView = importView;


    var content = {
        title: '导入商品评论列表',
        region: 'center',
        tbar: [
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    importValuationStore.reload();
                }
            },
            {
                text: '删除评论',
                iconCls: 'remove',
                handler: function () {
                    var ids = [];
                    if (importView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要删除的评论");
                        return;
                    }
                    Ext.each(importView.getSelectedRecords(), function (r) {
                        ids.push(r.get('id'));
                    });

                    doAjax('user/valuation/delete', function () {
                        importView.getStore().reload();
                    }, {
                        ids: ids,
                        isImportValuation: true
                    }, "你确定删除这些评论吗？");

                }
            }/*,
            {
                text: '回复评论',
                iconCls: 'config',
                handler: function () {
                    if (importView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要回复的评论");
                        return;
                    }
                    var ids = [];
                    Ext.each(importView.getSelectedRecords(), function (r) {
                        ids.push(r.get('id'));
                    });

                    if (ids.length > 1) {
                        Ext.Msg.alert("信息", "只能回复一条");
                        return;
                    }

                    buildWin('回复评论', 400, buildForm('user/valuation/reply', [
                        {
                            text: '保存',
                            handler: function (button) {
                                commitForm(button.ownerCt.ownerCt, function () {
                                    importView.getStore().reload();
                                    button.ownerCt.ownerCt.ownerCt.close();
                                })
                            }
                        }
                    ], [
                        {
                            xtype: 'hidden',
                            name: 'id',
                            value: ids[0]
                        },
                        {
                            fieldLabel: '回复内容',
                            xtype: 'textarea',
                            name: 'replyContent',
                            allowBlank: false
                        }
                    ])).show();
                }
            },
            {
                text: '追加回复评价',
                iconCls: 'config',
                handler: function () {
                    if (importView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要追加的回复评价");
                        return;
                    }
                    var ids = [];
                    Ext.each(importView.getSelectedRecords(), function (r) {
                        ids.push(r.get('id'));
                    });

                    if (ids.length > 1) {
                        Ext.Msg.alert("信息", "只能追加一条");
                        return;
                    }
                    if (importView.getSelectedRecords()[0].get('replyContent') == '') {
                        Ext.Msg.alert("信息", "还未回复, 现在只回复就可以了");
                        return;
                    }
                    if (importView.getSelectedRecords()[0].get('appendContent') == '') {
                        Ext.Msg.alert("信息", "用户未追加过评价, 不需要追加回复评价");
                        return;
                    }

                    buildWin('回复追加评论', 400, buildForm('user/valuation/appendReply', [
                        {
                            text: '保存',
                            handler: function (button) {
                                commitForm(button.ownerCt.ownerCt, function () {
                                    importView.getStore().reload();
                                    button.ownerCt.ownerCt.ownerCt.close();
                                })
                            }
                        }
                    ], [
                        {
                            xtype: 'hidden',
                            name: 'id',
                            value: ids[0]
                        },
                        {
                            fieldLabel: '回复追加内容',
                            xtype: 'textarea',
                            name: 'appendReplyContent',
                            allowBlank: false
                        }
                    ])).show();
                }
            }*/,
            '->',
            {
                xtype: 'textfield',
                name: 'userName',
                emptyText: '用户名',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        importView.getStore().baseParams = {
                            start: 0,
                            limit: 27,
                            userName: field.getValue(),
                            isImportValuation: true
                        };

                        importView.getStore().load();
                    }
                }
            }
        ],
        bbar: new Ext.PagingToolbar({
            pageSize: 27,
            store: importValuationStore
        }),
        layout: 'fit',
        items: importView
    }

    return content;

}