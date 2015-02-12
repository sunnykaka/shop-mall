/**
 * 用户管理系统的管理JS
 */


Ext.Loader.load(['/static/js/user/UserModel.js', '/static/js/user/UserForm.js']);


Desktop.UserWindow = Ext.extend(Ext.app.Module, {

    id:'User-win',

    title:'用户管理',

    createSearchForm:function () {
        var searchFunction = function () {
            //var userName = form.getForm().findField('userName').getValue();
            var phone = form.getForm().findField('phone').getValue();
            var email = form.getForm().findField('email').getValue();
            var type = form.getForm().findField('type').getValue();
            var sortMode = form.getForm().findField('sortMode').getValue();
            var startDate = form.getForm().findField('startDate').getValue();
            var endDate = form.getForm().findField('endDate').getValue();

            var myStore = Ext.getCmp("userManagerGrid").getStore();


            myStore.baseParams = {
                // userName:userName,
                phone:phone,
                email:email,
                startDate:startDate,
                endDate:endDate,
                type:type,
                sortMode:sortMode
            };
            myStore.load({
                params:{
                    start:0,
                    limit:26,
                    //userName:userName,
                    phone:phone,
                    email:email,
                    startDate:startDate,
                    endDate:endDate,
                    type:type,
                    sortMode:sortMode
                }
            });
        };

        var form = new Ext.FormPanel({
            region:'north',
            height:150,
            frame:true,
            border:true,
            items:[
                {
                    layout:'form',
                    border:false,
                    buttonAlign:'center',
                    keys:[
                        {
                            key:Ext.EventObject.ENTER,
                            fn:searchFunction//执行的方法
                        }
                    ],
                    buttons:[
                        {
                            text:'查询',
                            handler:searchFunction
                        },
                        {
                            text:'重置',
                            handler:function () {
                                form.getForm().reset();
                            }
                        }
                    ],

                    items:[
                        {
                            layout:'column',
                            border:false,
                            labelWidth:55,
                            items:[
                                {
                                    layout:'form',
                                    border:false,
                                    columnWidth:0.5,
                                    items:[
                                        /*{
                                            fieldLabel:'用户名',
                                            xtype:'textfield',
                                            anchor:'90%',
                                            name:'userName'
                                        },*/
                                        {
                                            fieldLabel:'手机号',
                                            xtype:'textfield',
                                            anchor:'90%',
                                            name:'phone'
                                        },
                                        {
                                            anchor:'80%',
                                            xtype:'datefield',
                                            fieldLabel:'开始日期',
                                            anchor:'90%',
                                            name:'startDate'
                                        },
                                        {
                                            name:'type',
                                            anchor:'90%',
                                            xtype:'combo',
                                            hiddenName:'type',
                                            fieldLabel:'排序类型',
                                            mode:'local',
                                            editable:false,
                                            triggerAction:'all',
                                            emptyText:'请选择',
                                            store:new Ext.data.ArrayStore({
                                                fields:['id', 'type' ],
                                                data:[
                                                    [ 'loginTime', '最近登陆时间' ],
                                                    [ 'registerDate', '注册时间' ],
                                                    [ 'loginCount', '活跃度' ]

                                                ]
                                            }),
                                            displayField:'type',
                                            valueField:'id'
                                        }
                                    ]
                                }  ,
                                {
                                    layout:'form',
                                    border:false,
                                    columnWidth:0.5,
                                    items:[
                                        {
                                            fieldLabel:'电子邮箱',
                                            xtype:'textfield',
                                            anchor:'90%',
                                            name:'email'

                                        },
                                        {
                                            anchor:'80%',
                                            xtype:'datefield',
                                            fieldLabel:'结束日期',
                                            anchor:'90%',
                                            name:'endDate'
                                        },
                                        {
                                            fieldLabel:'排序方式',
                                            name:'sortMode',
                                            anchor:'90%',
                                            xtype:'combo',
                                            hiddenName:'sortMode',
                                            mode:'local',
                                            editable:false,
                                            triggerAction:'all',
                                            emptyText:'请选择',
                                            store:new Ext.data.ArrayStore({
                                                fields:['id', 'type' ],
                                                data:[
                                                    [ 'asc', '升序' ],
                                                    [ 'desc', '倒叙' ]

                                                ]
                                            }),
                                            displayField:'type',
                                            valueField:'id'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        });

        return form;
    },

    createUserGrid:function () {
        var userStore = getStore("/user/list", Ext.Model.User);

        var showIsActive = function (value) {
            if (value) {
                return "已激活";
            }
            return "未激活";
        };

        var showHasForbidden = function (value) {
            if (value) {
                return "是";
            }
            return "否";
        };

        function updateStatus(id, hasForbidden) {
            doAjax("user/updateStatus", function () {
                userGrid.getStore().reload();
            }, {
                id:id,
                hasForbidden:hasForbidden
            }, "你确定要修改该用户状态么？");
        }

        var userGrid = new Ext.grid.GridPanel({
            id:'userManagerGrid',
            height:530,
            region:'center',
            store:userStore,
            autoScroll:true,
            sm:new Ext.grid.RowSelectionModel({
                singleSelect:true
            }),
            loadMask:true,
            viewConfig:{
                forceFit:true,
                getRowClass:function (record, index, p, ds) {
                    var cls = '';
                    if (record.data['hasForbidden'] == true) {
                        cls = 'x-grid-record-red';
                    }
                    return cls;
                }
            },
            columns:[
                {header:'id', name:'id', width:45, sortable:true, dataIndex:'id'},
                {header:"用户名", width:100, sortable:true, dataIndex:'userName'},
                {header:"等级", width:100, sortable:true, dataIndex:'grade'},
                {header:"积分", width:100, sortable:true, dataIndex:'pointTotal'},
                {header:"电子邮箱", width:150, sortable:true, dataIndex:'email'},
                {header:"手机号", width:100, sortable:true, dataIndex:'phone'},
                {header:"登陆次数", width:80, sortable:true, dataIndex:'loginCount'},
                {header:"是否激活", width:80, sortable:true, dataIndex:'active', renderer:showIsActive},
                {header:"是否禁用", width:80, sortable:true, dataIndex:'hasForbidden', renderer:showHasForbidden},
                {header:"注册时间", width:150, sortable:true, dataIndex:'registerDate'},
                {header:"最近登陆时间", width:150, sortable:true, dataIndex:'lastLoginTime'}
            ],
            bbar:new Ext.PagingToolbar({
                pageSize:26,
                store:userStore,
                displayInfo:true
            }),
            tbar:[
                {
                    iconCls:'add',
                    text:'新增',
                    handler:function () {
                        buildWin('增加用户', 450, getUserForm()).show(this.id);
                    }
                },
                {
                    text:'禁用账户',
                    iconCls:'run',
                    handler:function () {
                        var sm = userGrid.getSelectionModel();
                        var record = sm.getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要修改的用户");
                            return;
                        }
                        var hasForbidden = record.get("hasForbidden");
                        if (hasForbidden) {
                            Ext.Msg.alert("错误", "该用户已属于禁用状态，不需要更改");
                            return;
                        }
                        updateStatus(record.get("id"), true);

                    }
                },
                {
                    text:'解除禁用',
                    iconCls:'config',
                    handler:function () {
                        var sm = userGrid.getSelectionModel();
                        var record = sm.getSelected();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要修改的用户");
                            return;
                        }
                        var hasForbidden = record.get("hasForbidden");
                        if (!hasForbidden) {
                            Ext.Msg.alert("错误", "该用户不属于禁用状态，不能进行此操作");
                            return;
                        }
                        updateStatus(record.get("id"), false);
                    }
                },
                {
                    text:'手动激活',
                    iconCls:'config',
                    handler:function () {
                        var sm = userGrid.getSelectionModel();

                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择要激活的用户");
                            return;
                        }
                        var record = sm.getSelected();
                        var active = record.get("active");
                        if (active) {
                            Ext.Msg.alert("错误", "该用户不属于未激活状态，不能进行此操作");
                            return;
                        }

                        doAjax("user/activeUser", function () {
                            userGrid.getStore().reload();
                        }, {
                            id:record.get('id')
                        }, "你确定要激活这个用户么？");
                    }
                },
                {
                    text:'等级设置',
                    iconCls:'config',
                    handler:function () {
                        buildWin('等级设置', 500, getGradeConfigForm()).show(this.id);
                    }
                },
                {
                    text:'刷新',
                    iconCls:'refresh',
                    handler:function () {
                        userGrid.getStore().reload();
                    }
                },
                {
                    text:'送积分',
                    iconCls:'config',
                    handler:function () {
                        var sm = userGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择用户");
                            return;
                        }
                        var record = sm.getSelected();

                        var form = buildForm('/user/changePoint', [
                            {
                                text:'提交',
                                handler:function () {
                                    commitForm(form, function (action, obj) {
                                        form.ownerCt.close();
                                        userGrid.getStore().reload();
                                    })
                                }
                            }
                        ], [
                            {
                                xtype:'hidden',
                                name:'id',
                                value:record.get('id')
                            },
                            {
                                fieldLabel:'积分',
                                name:'point',
                                xtype:'numberfield',
                                allowBlank:false
                            }
                        ]);


                        buildWin('输入积分信息', 400, form).show(this.id);

                    }
                },
                {
                    text:'送现金券',
                    iconCls:'config',
                    handler:function () {
                        var sm = userGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert("错误", "请选择用户");
                            return;
                        }
                        var record = sm.getSelected();

                        var form = buildForm('/user/assignCoupon', [
                            {
                                text:'提交',
                                handler:function () {
                                    commitForm(form, function (action, obj) {
                                        form.ownerCt.close();
                                        Ext.Msg.alert('成功', '成功赠送' + obj.data.number + '张');
                                    })
                                }
                            }
                        ], [
                            {
                                xtype:'hidden',
                                name:'userId',
                                value:record.get('id')
                            },
                            {
                                fieldLabel:'金额',
                                name:'money',
                                xtype:'numberfield',
                                allowBlank:false
                            },
                            {
                                fieldLabel:'数量',
                                name:'number',
                                xtype:'numberfield',
                                allowBlank:false
                            }
                        ]);


                        buildWin('输入现金券信息', 400, form).show(this.id);


                    }
                },
                '->',
                {
                    xtype:'textfield',
                    name:'userId',
                    emptyText:'用户Id(数字)',
                    width: 100,
                    enableKeyEvents:true,
                    listeners:{
                        keyup:function (field) {
                            var userStore = userGrid.getStore();
                            userStore.proxy = new Ext.data.HttpProxy({
                                url:'user/list',
                                failure:function (response, opts) {
                                    if (response.status == 403) {
                                        Ext.Msg.alert('错误', '没有设置访问权限');
                                        return;
                                    }
                                    Ext.Msg.alert('错误', '服务器出错');
                                }
                            });

                            userStore.baseParams = {
                                userId:field.getValue()==''?0:field.getValue()
                            };

                            userStore.load({
                                params:{
                                    start:0,
                                    limit:26
                                }
                            });
                        }
                    }
                },
                {
                    xtype:'textfield',
                    name:'userName',
                    emptyText:'用户名',
                    width: 105,
                    enableKeyEvents:true,
                    listeners:{
                        keyup:function (field) {
                            var userStore = userGrid.getStore();
                            userStore.proxy = new Ext.data.HttpProxy({
                                url:'user/list',
                                failure:function (response, opts) {
                                    if (response.status == 403) {
                                        Ext.Msg.alert('错误', '没有设置访问权限');
                                        return;
                                    }
                                    Ext.Msg.alert('错误', '服务器出错');
                                }
                            });

                            userStore.baseParams = {
                                userName:field.getValue()==''?0:field.getValue()
                            };

                            userStore.load({
                                params:{
                                    start:0,
                                    limit:26
                                }
                            });
                        }
                    }
                }
            ]

        });


        return userGrid;
    },

    createWindow:function () {
        return this.app.getDesktop().createWindow({
            id:this.id,
            title:this.title,
            border:false,
            height:document.body.clientHeight * 0.85,
            width:750,
            layout:'border',
            items:[this.createSearchForm(), this.createUserGrid()]
        });
    }

});
