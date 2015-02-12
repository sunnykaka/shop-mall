/**
 * 优惠劵js
 */
Desktop.CouponWindow = Ext.extend(Ext.app.Module, {

    id:'Coupon-win',
    title:'优惠劵管理',
    notUsedCouponGrid:function () {
        var couponColumns = [];
        couponColumns.push({
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'优惠劵编码',
                dataIndex:'code'
            },
            {
                header:'订单编号',
                dataIndex:'orderNo'
            },
            {
                header:'优惠劵类型',
                dataIndex:'couponType',
                renderer:function(value) {
                    return (value == 'Normal') ? "金额" : "折扣";
                }
            },
            {
                header:'优惠劵金额',
                dataIndex:'price'
            },
            {
                header:'最小订单金额',
                dataIndex:'miniApplyOrderPrice'
            },
            {
                header:'拥有者',
                dataIndex:'userName'
            },
            {
                header:'生效时间',
                dataIndex:'startDate'
            },
            {
                header:'过期时间',
                dataIndex:'expireDate'
            });

        var couponStore = getStore("coupon/notUsed/grid", Ext.Model.Coupon);

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

        var couponTbar = [];
        couponTbar.push({
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    couponGrid.getStore().reload();
                }
            }, {
                text:'添加',
                iconCls:'add',
                handler:function () {
                    buildWin('添加优惠劵', 500, buildForm('coupon/add', [
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
                                        couponGrid.getStore().reload();
                                        button.ownerCt.ownerCt.ownerCt.close();
                                        var result = Ext.util.JSON.decode(action.response.responseText);
                                        if (result.msg != "") {
                                            Ext.Msg.alert(result.success ? '成功' : '错误', result.msg);
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
                            xtype: 'combo',
                            hiddenName: 'couponType',
                            fieldLabel: '优惠方式',
                            mode: 'local',
                            editable: false,
                            triggerAction: 'all',
                            emptyText: '请选择',
                            store: new Ext.data.ArrayStore({
                                fields: [ 'type', 'value' ],
                                data: [
                                    [ '金额(若订单价格是150,此优惠券值50,则只需要支付100)', 'Normal' ],
                                    [ '比例(若订单价格是150,此优惠券值80,则只需要支付150*0.8=120)', 'Ratio' ]
                                ]
                            }),
                            displayField: 'type',
                            valueField: 'value',
                            allowBlank: false
                        },
                        {
                            fieldLabel:'请输入数值',
                            name:'price',
                            allowBlank:false,
                            regex:/^\S+$/// 不能输入空格
                        },
                        {
                            fieldLabel:'请输入个数',
                            name:'number',
                            allowBlank:false,
                            regex:/^\S+$/, // 不能输入空格
                            maxLength:10
                        },
                        {
                            fieldLabel:'最少订单金额',
                            name:'miniApplyOrderPrice',
                            allowBlank:false,
                            regex:/^\S+$/, // 不能输入空格
                            maxLength:10
                        },
                        {
                            type:'combo',
                            fieldLabel:'生效时间',
                            name:'startDate',
                            allowBlank:false,
                            value:now
                        },
                        {
                            fieldLabel:'过期时间',
                            name:'expireDate',
                            allowBlank:false,
                            value:time
                        }
                    ])).show(this.id);
                }
            },
            {
                text:'删除',
                iconCls:'remove',
                handler:function () {
                    doGridRowDelete(couponGrid, 'coupon/delete/batch', function () {
                        couponGrid.getStore().reload();
                    });
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'userName',
                emptyText: '拥有者',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            userName: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                userName: field.getValue()
                            }
                        });
                    }
                }
            },
            {
                xtype: 'textfield',
                name: 'code',
                emptyText: '优惠券编码',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            code: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                code: field.getValue()
                            }
                        });
                    }
                }
            }
        );
        var couponGrid = new Ext.grid.GridPanel({
            id:'usedCouponGrid',
            title:'未使用',
            loadMask:true,
            store:couponStore,
            columns:couponColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:couponTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:30,
                store:couponStore,
                displayInfo:true
            })
        });
        return couponGrid;
    },

    usedCouponGrid:function () {
        var couponColumns = [];
        couponColumns.push({
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'优惠劵编码',
                dataIndex:'code'
            },
            {
                header:'订单编号',
                dataIndex:'orderNo'
            },
            {
                header:'优惠劵类型',
                dataIndex:'couponType',
                renderer:function(value) {
                    return (value == 'Normal') ? "金额" : "折扣";
                }
            },
            {
                header:'优惠劵金额',
                dataIndex:'price'
            },
            {
                header:'最小订单金额',
                dataIndex:'miniApplyOrderPrice'
            },
            {
                header:'拥有者',
                dataIndex:'userName'
            },
            {
                header:'生效时间',
                dataIndex:'startDate'
            },
            {
                header:'过期时间',
                dataIndex:'expireDate'
            });

        var couponStore = getStore("coupon/used/grid", Ext.Model.Coupon);

        var couponTbar = [];

        couponTbar.push({
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    couponGrid.getStore().reload();
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'userName',
                emptyText: '拥有者',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            userName: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                userName: field.getValue()
                            }
                        });
                    }
                }
            },
            {
                xtype: 'textfield',
                name: 'code',
                emptyText: '优惠券编码',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            code: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                code: field.getValue()
                            }
                        });
                    }
                }
            });
        var couponGrid = new Ext.grid.GridPanel({
            title:'已使用',
            loadMask:true,
            store:couponStore,
            columns:couponColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:couponTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:30,
                store:couponStore,
                displayInfo:true
            })
        });
        return couponGrid;
    },

    expiredCouponGrid:function () {
        var couponColumns = [];
        couponColumns.push({
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'优惠劵编码',
                dataIndex:'code'
            },
            {
                header:'订单编号',
                dataIndex:'orderNo'
            },
            {
                header:'优惠劵类型',
                dataIndex:'couponType',
                renderer:function(value) {
                    return (value == 'Normal') ? "金额" : "折扣";
                }
            },
            {
                header:'优惠劵金额',
                dataIndex:'price'
            },
            {
                header:'最小订单金额',
                dataIndex:'miniApplyOrderPrice'
            },
            {
                header:'拥有者',
                dataIndex:'userName'
            },
            {
                header:'生效时间',
                dataIndex:'startDate'
            },
            {
                header:'过期时间',
                dataIndex:'expireDate'
            });

        var couponStore = getStore("coupon/expire/grid", Ext.Model.Coupon);

        var couponTbar = [];

        couponTbar.push({
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    couponGrid.getStore().reload();
                }
            },
            {
                text:'删除',
                iconCls:'remove',
                handler:function () {
                    doGridRowDelete(couponGrid, 'coupon/delete/batch', function () {
                        couponGrid.getStore().reload();
                    });
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'userName',
                emptyText: '拥有者',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            userName: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                userName: field.getValue()
                            }
                        });
                    }
                }
            },
            {
                xtype: 'textfield',
                name: 'code',
                emptyText: '优惠券编码',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            code: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                code: field.getValue()
                            }
                        });
                    }
                }
            });

        var couponGrid = new Ext.grid.GridPanel({
            title:'已过期',
            loadMask:true,
            store:couponStore,
            columns:couponColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:couponTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:30,
                store:couponStore,
                displayInfo:true
            })
        });
        return couponGrid;
    },

    allocationCouponGrid:function () {
        var couponColumns = [];
        couponColumns.push({
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'优惠劵编码',
                dataIndex:'code'
            },
            {
                header:'订单编号',
                dataIndex:'orderNo'
            },
            {
                header:'优惠劵类型',
                dataIndex:'couponType',
                renderer:function(value) {
                    console.log(value);
                    return (value == 'Normal') ? "金额" : "折扣";
                }
            },
            {
                header:'优惠劵金额',
                dataIndex:'price'
            },
            {
                header:'最小订单金额',
                dataIndex:'miniApplyOrderPrice'
            },
            {
                header:'拥有者',
                dataIndex:'userName'
            },
            {
                header:'生效时间',
                dataIndex:'startDate'
            },
            {
                header:'过期时间',
                dataIndex:'expireDate'
            });

        var couponStore = getStore("coupon/allocation/grid", Ext.Model.Coupon);

        var couponTbar = [];

        couponTbar.push({
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    couponGrid.getStore().reload();
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'code',
                emptyText: '优惠券编码',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            code: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                code: field.getValue()
                            }
                        });
                    }
                }
            });


        var couponGrid = new Ext.grid.GridPanel({
            title:'可分配',
            loadMask:true,
            store:couponStore,
            columns:couponColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:couponTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:30,
                store:couponStore,
                displayInfo:true
            })
        });

        couponGrid.on('rowcontextmenu', function (grid, rowIndex, event) {
            event.preventDefault();//取消默认的浏览器右键事件
            var record = grid.getStore().getAt(rowIndex);
            var items = [
                {
                    text:'分配给用户',
                    iconCls:'config',
                    handler:function () {
                        buildWin('优惠劵分配', 400, buildForm('coupon/allocationUserName', [
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
                                        waitMsg:'服务器正在分配',
                                        // 表单提交方式
                                        method:'post',
                                        // 数据验证通过时发生的动作
                                        success:function (form, action) {
                                            couponGrid.getStore().reload();
                                            Ext.getCmp('usedCouponGrid').getStore().reload();
                                            button.ownerCt.ownerCt.ownerCt.close();
                                            var result = Ext.util.JSON.decode(action.response.responseText);
                                            if (result.msg != "") {
                                                Ext.Msg.alert('错误', result.msg);
                                                return;
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
                                fieldLabel:'优惠劵编号',
                                xtype:'hidden',
                                name:'id',
                                value:record.get("id")
                            },
                            {
                                fieldLabel:'请输入用户id 或 用户名',
                                labelWidth:90,
                                name:'userName',
                                allowBlank:false,
                                blankText: '第三方不确定用户名的可以使用 用户id',
                                regex:/^\S+$/, // 不能输入空格
                                maxLength:100
                            }
                        ])).show(this.id);
                    }
                }
            ];

            var treeMenu = new Ext.menu.Menu({
                items:items
            });
            treeMenu.showAt(event.getXY());
        });


        return couponGrid;
    },

    allUMPayCouponGrid:function () {
        var couponColumns = [];
        couponColumns.push({
                header:'ID',
                dataIndex:'id'
            },
            {
                header:'优惠劵编码',
                dataIndex:'code'
            },
            {
                header:'订单编号',
                dataIndex:'orderNo'
            },
            {
                header:'优惠劵类型',
                dataIndex:'couponType',
                renderer:function(value) {
                    console.log(value);
                    return (value == 'Normal') ? "金额" : "折扣";
                }
            },
            {
                header:'优惠劵金额',
                dataIndex:'price'
            },
            {
                header:'最小订单金额',
                dataIndex:'miniApplyOrderPrice'
            },
            {
                header:'拥有者',
                dataIndex:'userName'
            },
            {
                header:'生效时间',
                dataIndex:'startDate'
            },
            {
                header:'过期时间',
                dataIndex:'expireDate'
            });

        var couponStore = getStore("coupon/umpay/grid", Ext.Model.Coupon);

        var couponTbar = [];

        couponTbar.push({
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    couponGrid.getStore().reload();
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'code',
                emptyText: '优惠券编码',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        couponStore.baseParams = {
                            start: 0,
                            limit: 30,
                            code: field.getValue()
                        };

                        couponStore.load({
                            params: {
                                start: 0,
                                limit: 30,
                                code: field.getValue()
                            }
                        });
                    }
                }
            });


        var couponGrid = new Ext.grid.GridPanel({
            title:'联动优势',
            loadMask:true,
            store:couponStore,
            columns:couponColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:couponTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:30,
                store:couponStore,
                displayInfo:true
            })
        });

        couponGrid.on('rowcontextmenu', function (grid, rowIndex, event) {
            event.preventDefault();//取消默认的浏览器右键事件
            var record = grid.getStore().getAt(rowIndex);
            var items = [
                {
                    text:'分配给用户',
                    iconCls:'config',
                    handler:function () {
                        buildWin('优惠劵分配', 400, buildForm('coupon/allocationUserName', [
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
                                        waitMsg:'服务器正在分配',
                                        // 表单提交方式
                                        method:'post',
                                        // 数据验证通过时发生的动作
                                        success:function (form, action) {
                                            couponGrid.getStore().reload();
                                            Ext.getCmp('usedCouponGrid').getStore().reload();
                                            button.ownerCt.ownerCt.ownerCt.close();
                                            var result = Ext.util.JSON.decode(action.response.responseText);
                                            if (result.msg != "") {
                                                Ext.Msg.alert('错误', result.msg);
                                                return;
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
                                fieldLabel:'优惠劵编号',
                                xtype:'hidden',
                                name:'id',
                                value:record.get("id")
                            },
                            {
                                fieldLabel:'请输入用户id 或用户名',
                                labelWidth:90,
                                name:'userName',
                                blankText: '第三方不确定用户名的可以使用 用户id',
                                allowBlank:false,
                                regex:/^\S+$/, // 不能输入空格
                                maxLength:100
                            }
                        ])).show(this.id);
                    }
                }
            ];

            var treeMenu = new Ext.menu.Menu({
                items:items
            });
            treeMenu.showAt(event.getXY());
        });


        return couponGrid;
    },

    createWindow:function () {
        return this.app.getDesktop().createWindow({
            id:this.id,
            title:this.title,
            width:800,
            border:false,
            height:document.body.clientHeight * 0.85,
            layout:'border',
            items:new Ext.TabPanel({
                region:'center',
                activeTab:0,
                items:[this.notUsedCouponGrid(), this.usedCouponGrid(), this.expiredCouponGrid(), this.allocationCouponGrid(),this.allUMPayCouponGrid()]
            })
        });

    }
});