/**
 * 日志管理系统的管理JS
 */

Desktop.LogWindow = Ext.extend(Ext.app.Module, {

    id: 'Log-win',

    title: '日志管理',

    //创建日志管理的窗口
    createWindow: function () {

        var logStore = getStore('log/search', Ext.Model.Log);

        var logGrid = new Ext.grid.GridPanel({
            title: '日志列表',
            region: 'center',
            autoScroll: true,
            height: 705,
            store: logStore,
            loadMask: true,
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: false
            }),
            viewConfig: {
                forceFit: true
            },
            columns: [
                {header: "时间", width: 150, sortable: true, dataIndex: 'date'},
                {header: "操作人", width: 80, sortable: true, dataIndex: 'operator'},
                {header: "角色", width: 80, sortable: true, dataIndex: 'roleName'},
                {header: "IP", width: 100, sortable: true, dataIndex: 'ip'},
                {header: "标题", width: 150, sortable: true, dataIndex: 'title'},
                {header: "内容", width: 250, sortable: true, dataIndex: 'content', id: 'content'}
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 28,
                store: logStore,
                displayInfo: true
            }),

            tbar: [
                {
                    iconCls: 'remove',
                    text: '删除',
                    handler: function () {
                        var sm = logGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择要删除的日志信息');
                        } else {
                            var records = sm.getSelections();
                            var ids = [];

                            for (var i = 0; i < records.length; i++) {
                                ids.push(records[i].get('id'));
                            }
                            doAjax("log/delete", function () {
                                logGrid.getStore().reload();
                            }, { ids: ids }, "你确定删除这些日志信息吗？");
                        }
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        logGrid.getStore().reload();
                    }
                }
            ]
        });

        var searchLog = function () {
            var ip = form.getForm().findField('ip').getValue();
            var title = form.getForm().findField('title').getValue();
            var myStore = logGrid.getStore();

            myStore.baseParams = {
                ip: ip,
                title: title

            };
            myStore.load({
                params: {
                    start: 0,
                    limit: 28,
                    ip: ip,
                    title: title
                }
            });
        };

        var form = new Ext.FormPanel({
            region: 'north',
            height: 100,
            title: '日志筛选',
            frame: true,
            items: [
                {
                    layout: 'form',
                    border: false,
                    buttonAlign: 'center',
                    keys: [
                        {
                            key: Ext.EventObject.ENTER,
                            fn: searchLog//执行的方法
                        }
                    ],
                    buttons: [
                        {
                            text: '查询',
                            handler: searchLog
                        },
                        {
                            text: '重置',
                            handler: function () {
                                form.getForm().reset();

                            }
                        }
                    ],

                    items: [
                        {
                            layout: 'column',
                            border: false,
                            labelWidth: 75,
                            items: [
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.5,
                                    items: {
                                        fieldLabel: '标题',
                                        anchor: '80%',
                                        regex: /^\S+$/,
                                        xtype: 'textfield',
                                        name: 'title'
                                    }
                                },
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.5,
                                    items: {
                                        fieldLabel: 'IP',
                                        anchor: '80%',
                                        regex: /^\S+$/,
                                        xtype: 'textfield',
                                        name: 'ip'
                                    }
                                }
                            ]
                        }
                    ]
                }

            ]
        });

        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '日志管理',
            height: document.body.clientHeight * 0.85,
            width: 750,
            layout: 'border',
            items: [form, logGrid]
        });

    }
});