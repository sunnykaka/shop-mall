
RotaryMeed =  function (rotaryId) {
    var meedStore = createJsonStore('/rotary/meed/' + rotaryId, [
        { name: 'meedType', type: 'string' },
        { name: 'meedValue', type: 'string' },
        { name: 'value', type: 'string' },
        { name: 'currency', type: 'string' },
        { name: 'imageUrl', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'meedIndex', type: 'string' },
        { name: 'meedProbability', type: 'string' },
        { name: 'id', type: 'int' },
        { name: 'rotaryId', type: 'int' }
    ]);
    meedStore.load();

    function meedInfo(value, p, record) {
        if (record.get('meedType') == 'Product')
            return '<a href="http://www.boobee.me/product/' + record.get('meedValue') + '" target="_blank">' + value + '</a>';
        return value;
    }

    RotaryMeed.superclass.constructor.call(this, {
        title: '奖品项',
        border: false,
        store: meedStore,
        autoScroll: true,
        height: 308,
        loadMask: true,
        tbar: [
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    meedStore.reload();
                }
            }
        ],
        columns: [
            { header: '奖品项id', sortable: true, dataIndex: 'id', width: 80 },
            {
                header: '奖品类型', sortable: true, dataIndex: 'meedType', width: 60,
                renderer:function(value) {
                    if (value == 'Null') return "无";
                    if (value == 'Product') return "商品";
                    if (value == 'Coupon') return "现金券";
                    return "积分";
                }
            },
            { header: '奖品值', sortable: true, dataIndex: 'value', width: 220, renderer: meedInfo },
            { header: '图片 url', sortable: true, dataIndex: 'imageUrl', width: 400 },
            { header: '奖品描述', sortable: true, dataIndex: 'description', width: 140 },
            { header: '序号', sortable: true, dataIndex: 'meedIndex', width: 45 },
            { header: '中奖概率(万分之比例)', sortable: true, dataIndex: 'meedProbability', width: 135 }
        ],
        listeners: {
            // 双击
            'rowdblclick': function (grid, rowIndex) {
                var record = grid.getStore().getAt(rowIndex);
                var form = new Ext.FormPanel({
                    baseCls:'x-plain',
                    labelWidth:80,
                    url:'rotary/meed/update',
                    fileUpload: true,
                    buttonAlign:'center',
                    autoHeight:true,
                    defaults:{
                        anchor:'100%',
                        xtype:'textfield'
                    },
                    buttons:[{
                        text: '提交',
                        handler: function () {
                            commitForm(form, function () {
                                form.ownerCt.close();
                                grid.getStore().reload();
                            })
                        }
                    }],
                    items:[
                        { xtype: 'hidden', name: 'id', value: record.get('id') },
                        { xtype: 'hidden', name: 'rotaryId', value: record.get('rotaryId') },
                        {
                            xtype: 'combo',
                            fieldLabel: '奖品类型',
                            hiddenName: 'meedType',
                            emptyText:'请选择',
                            triggerAction: 'all',
                            mode: 'local',
                            store: new Ext.data.ArrayStore({
                                fields: [ 'type', 'value' ],
                                data: [
                                    [ '无', 'Null' ],
                                    [ '商品', 'Product' ],
                                    [ '现金券', 'Coupon' ],
                                    [ '积分', 'Integral' ]
                                ]
                            }),
                            displayField: 'type',
                            valueField: 'value',
                            value: record.get('meedType'),
                            allowBlank: false
                        },
                        {
                            fieldLabel: '奖品值',
                            name: 'value',
                            value: record.get('currency'),
                            allowBlank: false
                        },
                        {
                            fieldLabel: '图片 url',
                            name: 'imageUrl',
                            value: record.get('imageUrl'),
                            allowBlank: false
                        },
                        {
                            fieldLabel: '奖品描述',
                            name: 'description',
                            value: record.get('description'),
                            allowBlank: false
                        },
                        {
                            fieldLabel: '序号',
                            name: 'meedIndex',
                            value: record.get('meedIndex'),
                            allowBlank: false
                        },
                        {
                            fieldLabel: '中奖概率',
                            name: 'meedProbability',
                            value: record.get('meedProbability'),
                            allowBlank: false
                        }
                    ]});

                buildWin('修改幸运抽奖', 400, form).show(this.id);
            },

            // 右键
            'rowcontextmenu': function (grid, rowIndex, event) {
                event.preventDefault();// 取消默认的浏览器右键事件
                var record = grid.getStore().getAt(rowIndex);
                var items = [
                    {
                        text: '删除',
                        iconCls: 'delete',
                        handler: function () {
                            doGridRowDelete(grid, 'rotary/meed/delete', function () {
                                meedStore.reload()
                            })
                        }
                    }
                ];

                var treeMenu = new Ext.menu.Menu({
                    items:items
                });
                treeMenu.showAt(event.getXY());
            }
        }
    });
}

Ext.extend(RotaryMeed, Ext.grid.GridPanel);
