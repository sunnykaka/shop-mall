
Ext.Loader.load(['/static/js/om/RotaryMeed.js', '/static/js/om/Lottery.js']);

/** 幸运抽奖 */
Desktop.RotaryWindow = Ext.extend(Ext.app.Module, {

    id: 'Rotary-win',

    title: '幸运抽奖管理',

    createWindow: function () {
        var store = createJsonStore('rotary/list', [
            { name: 'id', type: 'int' },
            { name: 'name', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'currency', type: 'String' },
            { name: 'rule', type: 'string' },
            { name: 'detailRule', type: 'string' },
            { name: 'start', type: 'string' },
            { name: 'end', type: 'string' }
        ]);

        store.load();

        var today = new Date();
        var year = today.getFullYear();
        var data = today.getDate();
        var month = today.getMonth() + 1;
        var now = year + '年' + month + '月' + data + '日 00时00分01秒';

        today.setTime(today.getTime() + 30 * 24 * 60 * 60 * 1000);
        year = today.getFullYear();
        data = today.getDate();
        month = today.getMonth() + 1;
        var time = year + '年' + month + '月' + data + '日 23时59分59秒';

        var grid = new Ext.grid.GridPanel({
            id: 'rotaryGrid',
            store: store,
            region: 'center',
            autoScroll: true,
            height: document.body.clientHeight * 0.2,
            loadMask: true,
            tbar: [
                {
                    text: '新建幸运抽奖',
                    iconCls: 'add',
                    handler: function () {
                        var form = new Ext.FormPanel({
                            baseCls:'x-plain',
                            labelWidth:80,
                            url:'rotary/add',
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
                                {
                                    fieldLabel: '幸运抽奖名',
                                    name: 'name',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '详细说明',
                                    name: 'description'
                                },
                                {
                                    fieldLabel: '每次抽奖消耗积分',
                                    name: 'currency',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '抽奖规则',
                                    name: 'rule',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '详细规则',
                                    xtype: 'textarea',
                                    name: 'detailRule'
                                },
                                {
                                    fieldLabel: '开始时间',
                                    name: 'start',
                                    value: now, // '2013年11月01日 00时00分01秒'
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '结束时间',
                                    name: 'end',
                                    value: time, // '2013年11月10日 23时59分59秒'
                                    allowBlank: false
                                }
                            ]});

                        buildWin('新建幸运抽奖', 400, form).show(this.id)
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        grid.getStore().reload();
                    }
                },
                {
                    text: '删除',
                    iconCls: 'delete',
                    handler: function () {
                        doGridRowDelete(grid, 'rotary/delete', function () {
                            grid.getStore().reload();
                            Ext.getCmp('rotaryGrid').getStore().reload();
                        });
                    }
                },
                {
                    text: '查看抽奖页面',
                    iconCls: 'run',
                    handler: function () {
                        var sm = grid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择要查看的抽奖页面');
                        } else if (sm.getSelections().length > 1) {
                            Ext.Msg.alert('错误', '请只选择一个抽奖页面');
                            return;
                        }
                        window.open("http://www.boobee.me/rotary/" + sm.getSelections()[0].get('id'));
                    }
                }
            ],
            viewConfig: {
                forceFit: true
            },
            columns: [
                { header: '抽奖id', width: 30, sortable: true, dataIndex: 'id' },
                { header: '幸运抽奖名', width: 90, sortable: true, dataIndex: 'name' },
                { header: '详细说明', width: 90, sortable: true, dataIndex: 'description' },
                { header: '消耗积分数', width: 60, sortable: true, dataIndex: 'currency' },
                { header: '抽奖规则', width: 140, sortable: true, dataIndex: 'rule' },
                /*{ header: '详细规则', dataIndex: 'detailRule' },*/
                { header: '开始时间', width: 120, sortable: true, dataIndex: 'start' },
                { header: '结束时间', width: 120, sortable: true, dataIndex: 'end' }
            ]
        });

        var tabPanel = new Ext.TabPanel({
            plain: true,
            hidden: true,
            border: false
        });

        // 单击
        grid.on('rowclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            tabPanel.reDisplayProductInfo(record);
            tabPanel.activate(0);
            tabPanel.show();
            formPanel.doLayout();

            var rotary = Ext.getCmp('Rotary-win');
            rotary.setHeight(document.body.clientHeight * 0.85);
            rotary.doLayout();
        });

        tabPanel.reDisplayProductInfo = function (record) {
            this.removeAll(true);
            var rotaryId = record.get("id");

            this.add(new RotaryMeed(rotaryId));
            var lottery = new Lottery(rotaryId);
            Desktop.RotaryWindow.lottery = lottery;
            this.add(lottery);
        };

        // 右键
        grid.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();//取消默认的浏览器右键事件
            var record = grid.getStore().getAt(rowIndex);
            var rotaryId = record.get('id');
            var menu = new Ext.menu.Menu({
                items: [
                    {
                        text:'添加中奖项',
                        handler:function () {
                            buildWin('添加中奖项', 520, buildForm('rotary/meed/add', [
                                {
                                    text:'保存',
                                    handler:function (button) {
                                        var form = button.ownerCt.ownerCt;
                                        if (!form.getForm().isValid()) {
                                            return;
                                        }
                                        form.getForm().submit({
                                            waitTitle:'进度',
                                            // 动作发生期间显示的文本信息
                                            waitMsg:'服务器正在生成',
                                            // 表单提交方式
                                            method:'post',
                                            // 数据验证通过时发生的动作
                                            success:function (form, action) {
                                                grid.getStore().reload();
                                                button.ownerCt.ownerCt.ownerCt.close();
                                                var result = Ext.util.JSON.decode(action.response.responseText);
                                                if (result.msg != "") {
                                                    if(result.success){
                                                        Ext.Msg.alert('成功', result.msg);
                                                        return;
                                                    } else{
                                                        Ext.Msg.alert('错误', result.msg);
                                                        return;
                                                    }
                                                }
                                            },
                                            failure:function (form, action) {
                                                if (action.response.status == 200) {
                                                    var result = Ext.util.JSON.decode(action.response.responseText);
                                                    Ext.Msg.alert('错误', result.msg);
                                                    return;
                                                }
                                                Ext.Msg.alert('错误', '服务器出错，错误码:' + action.response.status);
                                            }

                                        });
                                    }
                                },
                                {
                                    text:'取消',
                                    handler:function (button) {
                                        button.ownerCt.ownerCt.ownerCt.close();
                                    }
                                }
                            ], [
                                {
                                    xtype: 'hidden',
                                    name: 'rotaryId',
                                    value: rotaryId
                                },
                                {
                                    xtype: 'combo',
                                    hiddenName: 'meedType',
                                    fieldLabel: '奖品类型',
                                    mode: 'local',
                                    editable: false,
                                    triggerAction: 'all',
                                    emptyText: '请选择',
                                    store: new Ext.data.ArrayStore({
                                        fields: [ 'type', 'value' ],
                                        data: [
                                            [ '无', 'Null' ],
                                            [ '积分', 'Integral' ],
                                            [ '现金券', 'Coupon' ],
                                            [ '商品', 'Product' ]
                                        ]
                                    }),
                                    displayField: 'type',
                                    valueField: 'value',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel:'奖品值',
                                    name:'value',
                                    emptyText: '商品填(商品Id-skuId), 积分填积分数, 现金券填(面值-最低使用金额)',
                                    allowBlank:false,
                                    regex:/^\S+$/
                                },
                                {
                                    fieldLabel:'图片 url',
                                    name:'imageUrl',
                                    emptyText: 'http://...jpg',
                                    allowBlank:false,
                                    regex:/^\S+$/
                                },
                                {
                                    fieldLabel:'奖品描述',
                                    name:'description',
                                    emptyText: '价值 59 元垃圾桶...'
                                },
                                {
                                    fieldLabel:'序号',
                                    name:'meedIndex',
                                    allowBlank:false,
                                    emptyText: '同一个抽奖活动的序号请不要重复',
                                    regex:/^\S+$/,
                                    maxLength:10
                                },
                                {
                                    fieldLabel:'中奖概率',
                                    name:'meedProbability',
                                    allowBlank:false,
                                    emptyText: '万分之比例, 比如: 在 10000 里面抽中 100 次, 则此值设置为 100',
                                    regex:/^\S+$/,
                                    maxLength:10
                                }
                            ])).show(this.id);
                        }
                    },
                    {
                        text: '添加中奖名单',
                        handler: function () {
                            window.open("/rotary/lottery/add/" + rotaryId);
                        }
                    }
                ]
            });
            menu.showAt(e.getXY());
        });

        // 双击
        grid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var form = new Ext.FormPanel({
                baseCls:'x-plain',
                labelWidth:80,
                url:'rotary/update',
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
                    {
                        xtype: 'hidden',
                        name: 'id',
                        value: record.get('id')
                    },
                    {
                        fieldLabel: '幸运抽奖名',
                        name: 'name',
                        value: record.get('name'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '详细说明',
                        name: 'description',
                        value: record.get('description'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '每次抽奖消耗的积分',
                        name: 'currency',
                        value: record.get('currency'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '抽奖规则',
                        name: 'rule',
                        value: record.get('rule'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '详细规则',
                        xtype: 'textarea',
                        name: 'detailRule',
                        value: record.get('detailRule')
                    },
                    {
                        fieldLabel: '开始时间',
                        name: 'start',
                        value: record.get('start'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '结束时间',
                        name: 'end',
                        value: record.get('end'),
                        allowBlank: false
                    }
                ]});

            buildWin('修改幸运抽奖', 400, form).show(this.id);
        });

        var formPanel = new Ext.FormPanel({
            title: '幸运抽奖',
            bodyStyle: 'padding:1px',
            items: [grid, tabPanel]
        });

        var menuTab = new Ext.TabPanel({
            id: 'menuTab',
            region: 'center',
            activeTab: 0,
            items: [formPanel]
        });

        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            pageX: document.body.clientWidth * 0.2,
            pageY: document.body.clientHeight * 0.05,
            height: document.body.clientHeight * 0.3,
            width: 750,
            layout: 'border',
            items: [menuTab]
        });
    }
});