/**
 * 实现安全管理的js
 */

Desktop.AccountWindow = Ext.extend(Ext.app.Module, {

    id:'Account-win',

    title:'安全管理',

    createRoleConfigGrid:function () {

        var accountStore = getStore("account/grid", Ext.Model.Account);

        var accountTbar = [];

        accountTbar.push({
            text:'刷新',
            iconCls:'refresh',
            handler:function () {
                accountGrid.getStore().reload();
            }
        });

        var accountGrid = new Ext.grid.GridPanel({
                title:'角色配置',
                loadMask:true,
                store:accountStore,
                columns:[
                    {
                        header:'ID',
                        dataIndex:'id'
                    },
                    {
                        header:'用户名',
                        dataIndex:'userName'
                    },
                    {
                        header:'电子邮件',
                        dataIndex:'email'
                    }
                ],
                viewConfig:{
                    forceFit:true
                },
                tbar:accountTbar,
                bbar:new Ext.PagingToolbar({
                    pageSize:20,
                    store:accountStore,
                    displayInfo:true
                })
            }
        );

        accountGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var hasRoleIds = [];//已有的角色ID

            Ext.Ajax.request({
                url:'account/roleList/' + record.get('id'),
                success:function (response, options) {
                    var roleList;
                    var roleCheck = [];
                    Ext.each(Ext.util.JSON.decode(response.responseText), function (role) {
                        hasRoleIds.push(role.id);
                    });
                    Ext.Ajax.request({
                        url:'role/list',
                        success:function (response, options) {
                            roleList = Ext.util.JSON.decode(response.responseText);
                            Ext.each(roleList, function (role) {
                                if (hasRoleIds.indexOf(role.id) != -1) {
                                    roleCheck.push({boxLabel:role.roleName, name:'roleId', inputValue:role.id, checked:true})
                                } else {
                                    roleCheck.push({boxLabel:role.roleName, name:'roleId', inputValue:role.id})
                                }
                            });
                            var formItems = [];
                            formItems.push({
                                xtype:'hidden',
                                name:'id',
                                value:record.get('id')
                            });
                            formItems.push({
                                fieldLabel:'用户名称',
                                name:'userName',
                                value:record.get('userName'),
                                disabled:true
                            });
                            formItems.push({
                                fieldLabel:'邮件名称',
                                name:'email',
                                value:record.get('email'),
                                disabled:true
                            });

                            if (roleList.length > 0) {
                                var roleGroup = {
                                    xtype:'checkboxgroup',
                                    fieldLabel:'分配角色',
                                    columns:3,
                                    items:roleCheck,
                                    allowBlank:true
                                };
                                formItems.push(roleGroup);
                            }

                            var userEditForm = new Ext.FormPanel({
                                baseCls:'x-plain',
                                labelWidth:80,
                                url:'account/update',
                                frame:true,
                                width:500,
                                autoHeight:true,
                                defaults:{
                                    width:380
                                },
                                defaultType:'textfield',

                                items:formItems

                            });

                            var updateAccount = function () {
                                commitForm(userEditForm, function () {
                                    accountGrid.getStore().reload();
                                    win.close();
                                    Ext.Msg.alert("成功", "更新成功");
                                });
                            };

                            var win = new Ext.Window({
                                title:'修改账号',
                                width:500,
                                autoHeight:true,
                                layout:'fit',
                                plain:true,
                                buttonAlign:'center',
                                bodyStyle:'padding:5px;',
                                items:userEditForm,
                                //热键添加
                                keys:[
                                    {
                                        key:Ext.EventObject.ENTER,
                                        fn:updateAccount //执行的方法
                                    }
                                ],
                                buttons:[
                                    {
                                        text:'保存',
                                        handler:updateAccount
                                    },
                                    {
                                        text:'取消',
                                        handler:function () {
                                            win.close();
                                        }
                                    }
                                ]
                            });
                            win.show(this.id);
                        },
                        failure:function () {
                            Ext.Msg.alert("错误", "读取数据失败");
                        }});
                },
                failure:function () {
                    Ext.Msg.alert("错误", "读取数据失败");
                }});


        });

        return accountGrid;
    },


    createRoleGrid:function () {


        var roleStore = createJsonStore("role/grid", [
            {
                name:'id',
                type:'int'
            },
            {
                name:'roleName',
                type:'string'
            },
            {
                name:'functionSet',
                type:'string'
            },
            {
                name:'urlPermissions'
            }
        ]);


        roleStore.load({
            params:{
                start:0,
                limit:20
            }
        });

        var roleGrid = new Ext.grid.GridPanel({
                title:'角色列表',
                loadMask:true,
                region:'center',
                store:roleStore,
                columns:[
                    {
                        header:'角色名',
                        dataIndex:'roleName',
                        width:30
                    }
                ],
                viewConfig:{
                    forceFit:true
                },
                tbar:[
                    {
                        text:'添加',
                        iconCls:'add',
                        handler:function () {
                            var permissionList;
                            var permissionCheckGroup = {};
                            Ext.Ajax.request({
                                url:'permission/list',
                                success:function (response, options) {
                                    permissionList = Ext.util.JSON.decode(response.responseText);
                                    Ext.each(permissionList, function (permission) {

                                        if (!permissionCheckGroup[permission.category]) {
                                            permissionCheckGroup[permission.category] = [];
                                        }

                                        permissionCheckGroup[permission.category].push({boxLabel:permission.permissionName, name:'permissionId', inputValue:permission.id})
                                    });
                                    var formItems = [];
                                    formItems.push({
                                        fieldLabel:'角色名称',
                                        name:'roleName',
                                        allowBlank:false,
                                        regex:/^\S+$/, // 不能输入空格
                                        maxLength:100
                                    });
                                    if (permissionList.length > 0) {
                                        for (var p in permissionCheckGroup) {

                                            formItems.push({
                                                xtype:'fieldset',
                                                title:p,
                                                collapsible:true,
                                                autoHeight:true,
                                                width:450,
                                                items:{
                                                    xtype:'checkboxgroup',
                                                    columns:3,
                                                    items:permissionCheckGroup[p]
                                                }
                                            });
                                        }
                                    }


                                    var roleAddForm = new Ext.FormPanel({
                                        baseCls:'x-plain',
                                        labelWidth:80,
                                        height:document.body.clientHeight*0.8,
                                        url:'role/add',
                                        autoScroll:true,
                                        frame: true,
                                        width:500,
                                        defaults:{
                                            width:380
                                        },
                                        defaultType:'textfield',

                                        items:formItems

                                    });

                                    var addRose = function () {
                                        commitForm(roleAddForm, function () {
                                            roleGrid.getStore().reload();
                                            win.close();
                                        });
                                    }

                                    var win = new Ext.Window({
                                        title:'添加角色',
                                        width:500,
                                        layout:'fit',
                                        plain:true,
                                        buttonAlign:'center',
                                        bodyStyle:'padding:5px;',
                                        items:roleAddForm,
                                        //热键添加
                                        keys:[
                                            {
                                                key:Ext.EventObject.ENTER,
                                                fn:addRose //执行的方法
                                            }
                                        ],
                                        buttons:[
                                            {
                                                text:'保存',
                                                handler:addRose
                                            },
                                            {
                                                text:'取消',
                                                handler:function () {
                                                    win.close();
                                                }
                                            }
                                        ]
                                    });
                                    win.show(this.id);
                                },
                                failure:function () {
                                    Ext.Msg.alert("错误", "读取数据失败");
                                }
                            });

                        }
                    },
                    {
                        text:'删除',
                        iconCls:'remove',
                        handler:function () {
                            doGridRowDelete(roleGrid, 'role/delete/batch', function () {
                                roleGrid.getStore().reload();
                            }, function (record) {
                                if (record.get('roleName') == "超级管理员") {
                                    Ext.Msg.alert('错误', '不能删除超级管理员');
                                    return true;
                                }
                            });
                        }
                    },
                    {
                        text:'配置',
                        iconCls:'config',
                        handler:function () {
                            var sm = roleGrid.getSelectionModel();
                            if (!sm.hasSelection()) {
                                Ext.Msg.alert('错误', '请选择要配置的角色');
                                return;
                            } else if (sm.getSelections().length > 1) {
                                Ext.Msg.alert('错误', '请只选择一个角色');
                                return;
                            }

                            var role = sm.getSelected();

                            var roleScopeForm = new Ext.FormPanel({
                                baseCls:'x-plain',
                                labelWidth:80,
                                url:'role/config/save',
                                frame:true,
                                width:500,
                                autoHeight:true,
                                defaults:{
                                    width:380
                                },
                                defaultType:'textfield',

                                items:[
                                    {
                                        xtype:'hidden',
                                        name:'roleId',
                                        value:role.get('id')
                                    },
                                    {
                                        xtype:'combo',
                                        hiddenName:'resource',
                                        fieldLabel:'资源名称',
                                        mode:'local',
                                        editable:false,
                                        triggerAction:'all',
                                        emptyText:'请选择',
                                        store:new Ext.data.ArrayStore({
                                            fields:[ 'type', 'value' ],
                                            data:EJS.Resource
                                        }),
                                        displayField:'type',
                                        valueField:'value',
                                        allowBlank:false,
                                        regex:/^\S+$/ // 不能输入空格
                                    },
                                    {
                                        fieldLabel:'资源脚本',
                                        name:'resourceAuthScript',
                                        xtype:'textarea',
                                        value:'def resourceAuth(resource, account,roleScope) {return true}',
                                        allowBlank:false
                                    },
                                    {
                                        fieldLabel:'界面脚本',
                                        name:'uiAuthScript',
                                        xtype:'textarea',
                                        value:'def uiAuth(resource, account,roleScope) {return true}',
                                        allowBlank:false
                                    },
                                    {
                                        fieldLabel:'域值',
                                        name:'scopeValue',
                                        xtype:'textarea',
                                        maxLength:100
                                    }
                                ]

                            });

                            var win = new Ext.Window({
                                title:'为用户配置资源',
                                width:500,
                                autoHeight:true,
                                layout:'fit',
                                plain:true,
                                buttonAlign:'center',
                                bodyStyle:'padding:5px;',
                                items:roleScopeForm,
                                buttons:[
                                    {
                                        text:'保存',
                                        handler:function () {
                                            commitForm(roleScopeForm, function () {
                                                win.close();
                                                Ext.getCmp("roleScopeGrid").getStore().reload();
                                            });

                                        }
                                    },
                                    {
                                        text:'取消',
                                        handler:function () {
                                            win.close();
                                        }
                                    }
                                ]
                            });
                            win.show(this.id);
                        }
                    },
                    {
                        text:'刷新',
                        iconCls:'refresh',
                        handler:function () {
                            roleGrid.getStore().reload();
                        }
                    }
                ],
                bbar:new Ext.PagingToolbar({
                    pageSize:8,
                    store:roleStore,
                    displayInfo:true
                })
            }
        );

        //双击一行数据打开编辑窗口
        roleGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var hasPermissionIds = [];//已有的权限ID
            Ext.each(record.get('urlPermissions'), function (permission) {
                hasPermissionIds.push(permission.id);
            });
            var permissionList;
            var permissionCheckGroup = {};
            Ext.Ajax.request({
                url:'permission/list',
                success:function (response, options) {
                    permissionList = Ext.util.JSON.decode(response.responseText);
                    Ext.each(permissionList, function (permission) {

                        if (!permissionCheckGroup[permission.category]) {
                            permissionCheckGroup[permission.category] = [];
                        }
                        //如果已经有了就设置为勾选状态
                        if (hasPermissionIds.indexOf(permission.id) != -1) {
                            permissionCheckGroup[permission.category].push({boxLabel:permission.permissionName, name:'permissionId', inputValue:permission.id, checked:true})
                        } else {
                            permissionCheckGroup[permission.category].push({boxLabel:permission.permissionName, name:'permissionId', inputValue:permission.id})
                        }

                    });
                    var formItems = [];
                    formItems.push(
                        {
                            xtype:'hidden',
                            name:'id',
                            value:record.get('id')
                        }
                    );
                    formItems.push({
                        fieldLabel:'角色名称',
                        name:'roleName',
                        value:record.get('roleName'),
                        disabled:true
                    });

                    if (permissionList.length > 0) {

                        for (var p in permissionCheckGroup) {

                            formItems.push({
                                xtype:'fieldset',
                                title:p,
                                collapsible:true,
                                autoHeight:true,
                                width:450,
                                items:{
                                    xtype:'checkboxgroup',
                                    columns:3,
                                    items:permissionCheckGroup[p]
                                }
                            });
                        }

                    }

                    var roleEditForm = new Ext.FormPanel({
                        baseCls:'x-plain',
                        labelWidth:80,
                        height:document.body.clientHeight*0.8,
                        url:'role/update',
                        frame:true,
                        width:500,
                        autoScroll:true,
                        defaults:{
                            width:380
                        },
                        defaultType:'textfield',

                        items:formItems

                    });

                    var updateRole = function () {

                        commitForm(roleEditForm, function () {
                            roleGrid.getStore().reload();
                            win.close();
                        });

                    };

                    var win = new Ext.Window({
                        title:'修改角色',
                        width:500,
                        layout:'fit',
                        plain:true,
                        buttonAlign:'center',
                        bodyStyle:'padding:5px;',
                        items:roleEditForm,
                        //热键添加
                        keys:[
                            {
                                key:Ext.EventObject.ENTER,
                                fn:updateRole //执行的方法
                            }
                        ],
                        buttons:[
                            {
                                text:'更新',
                                handler:updateRole
                            },
                            {
                                text:'取消',
                                handler:function () {
                                    win.close();
                                }
                            }
                        ]
                    });
                    win.show(this.id);
                },
                failure:function () {
                    Ext.Msg.alert("错误", "读取数据失败");
                }});

        });


        var functionStore = createJsonStore('', [
            {
                name:'functionSet',
                type:'string'
            },
            {
                name:'functionSetName',
                type:'string'
            },
            {
                name:'hasExist',
                type:'boolean'
            }
        ]);
        var mark=0;
        roleGrid.on('rowclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            functionStore.proxy = new Ext.data.HttpProxy({
                url:'/role/roleFunctionSetList/' + record.get('id'),
                failure:function (response, opts) {
                    if (response.status == 403) {
                        Ext.Msg.alert('错误', '没有设置访问权限');
                        return;
                    }
                    Ext.Msg.alert('错误', '服务器出错');
                }
            });
            functionSetPanel.show();
            rolePanel.doLayout();
            functionStore.reload();
            functionStore.sort('hasExist', 'DESC');
            mark=0;
        });

        var tpl = new Ext.XTemplate(
            '<tpl for=".">',
            '{image}',
            '<span class="x-editable">{shortName}</span></div>',
            '</tpl>',
            '<div class="x-clear"></div>'
        );

        var functionSetView = new Ext.DataView({
            store:functionStore,
            tpl:tpl,
            multiSelect:true,
            overClass:'x-view-over',
            itemSelector:'div.thumb-wrap',
            emptyText:'没有功能集合',
            prepareData:function (data) {
                data.shortName = Ext.util.Format.ellipsis(data.functionSetName, 5);

                if (data.hasExist) {
                    data.image = '<div class="thumb-wrap" id="'+data.functionSetName+'"><div class="thumb"><img src="/static/images/app/' + data.functionSet + '.png" title="' + data.functionSet + '"></div>';
                } else {
                    if(mark==0){
                        mark=mark+1;
                        data.image = '<hr class="x-clear"><div class="thumb-wrap" id="'+data.functionSetName+'"><div class="thumb gray"><img src="/static/images/app/' + data.functionSet + '.png" title="' + data.functionSet + '"></div>';
                    }else{
                        data.image = '<div class="thumb-wrap" id="'+data.functionSetName+'"><div class="thumb gray"><img src="/static/images/app/' + data.functionSet + '.png" title="' + data.functionSet + '"></div>';
                    }
                }
                return data;
            }
        });


        var functionSetPanel = new Ext.Panel({
            id:'images-functionSetPanel',
            title:'功能集合',
            border:false,
            autoScroll:true,
            hidden:true,
            items:functionSetView,
            collapsible:true,
            tbar:[
                {
                    text:'激活',
                    iconCls:'config',
                    handler:function () {
                        functionSetUpdate(true);
                    }
                },
                '-',
                {
                    text:'取消',
                    iconCls:'config',
                    handler:function () {
                        functionSetUpdate(false);
                    }
                }
            ],
            height:document.body.clientHeight * 0.45,
            region:'south'
        });

        var functionSetUpdate = function (hasActivate) {
            var functionSets = [];
            var hasEx = false;
            Ext.each(functionSetView.getSelectedRecords(), function (r) {
                if (r.get('hasExist') && hasActivate) {
                    hasEx = true;
                    return;
                }

                if (!r.get('hasExist') && !hasActivate) {
                    hasEx = true;
                    return;
                }
                functionSets.push(r.get('functionSet'));
            });
            var text = hasActivate ? "激活" : "取消";
            if (hasEx) {
                Ext.Msg.alert("提示", "功能集合中已经" + text + ",不需要再次" + text + ",请重新选择");
                return;
            }
            var record = roleGrid.getSelectionModel().getSelections();

            doAjax('role/updateRoleFunctionSet', function (obj) {
                Ext.Msg.alert("消息", obj.success ? "操作成功" : obj.msg);
                functionSetView.getStore().reload();
                roleGrid.getStore().reload();
                mark=0;
            }, {'roleId':record[0].get('id'), 'functionSets':functionSets, 'hasActivate':hasActivate}, "确定要" + text + "以下功能集合吗?");
        };

        var rolePanel = new Ext.Panel({
            title:'角色管理',
            layout:'border',
            items:[roleGrid, functionSetPanel]
        });


        return rolePanel;


    },


    createPermissionGrid:function () {

        var permissionStore = new Ext.data.GroupingStore({
            proxy:new Ext.data.HttpProxy({
                url:"permission/grid",
                failure:statusTips
            }),
            totalProperty:'data.totalCount',
            root:'data.result',
            groupField:'category',
            reader:new Ext.data.JsonReader({
                totalProperty:'data.totalCount',
                root:'data.result'
            }, Ext.data.Record.create([
                {
                    name:'id',
                    type:'int'
                },
                {
                    name:'resource',
                    type:'string'
                },
                {
                    name:'permissionName',
                    type:'string'
                },
                {
                    name:'path',
                    type:'string'
                },
                {
                    name:'category',
                    type:'string'
                }
            ]))
        });


        permissionStore.load({
            params:{
                start:0,
                limit:20
            }
        });

        var permissionGrid = new Ext.grid.GridPanel({
                title:'权限管理',
                loadMask:true,
                store:permissionStore,
                columns:[
                    {
                        header:'ID',
                        dataIndex:'id'
                    },
                    {
                        header:'权限名称',
                        dataIndex:'permissionName'
                    },
                    {
                        header:'资源名',
                        dataIndex:'resource',
                        renderer:EJS.ResourceRenderFunction
                    },
                    {
                        header:'权限路径',
                        dataIndex:'path'
                    },
                    {
                        header:'类别',
                        dataIndex:'category',
                        hidden:true
                    }
                ],
                view:new Ext.grid.GroupingView({
                    forceFit:true,
                    groupTextTpl:'{text}'
                }),

                tbar:[
                    {
                        text:'添加',
                        iconCls:'add',
                        handler:function () {

                            buildWin('添加权限', 500, buildForm('permission/add', [
                                {
                                    text:'保存',
                                    handler:function (button) {
                                        commitForm(button.ownerCt.ownerCt, function () {
                                            permissionGrid.getStore().reload();
                                            button.ownerCt.ownerCt.ownerCt.close();
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
                                    xtype:'combo',
                                    hiddenName:'resource',
                                    fieldLabel:'资源名称',
                                    mode:'local',
                                    editable:false,
                                    triggerAction:'all',
                                    emptyText:'请选择',
                                    store:new Ext.data.ArrayStore({
                                        fields:[ 'type', 'value' ],
                                        data:EJS.Resource
                                    }),
                                    displayField:'type',
                                    valueField:'value',
                                    allowBlank:false,
                                    regex:/^\S+$/ // 不能输入空格
                                },
                                {
                                    fieldLabel:'权限名称',
                                    name:'permissionName',
                                    allowBlank:false,
                                    regex:/^\S+$/, // 不能输入空格
                                    maxLength:100
                                },
                                {
                                    fieldLabel:'权限路径',
                                    name:'path',
                                    allowBlank:false,
                                    regex:/^\S+$/, // 不能输入空格
                                    maxLength:100
                                },
                                {
                                    fieldLabel:'类别',
                                    name:'category'
                                }
                            ])).show(this.id);
                        }
                    },
                    {
                        text:'删除',
                        iconCls:'remove',
                        handler:function () {
                            doGridRowDelete(permissionGrid, 'permission/delete/batch', function () {
                                permissionGrid.getStore().reload();
                            });
                        }
                    },
                    {
                        text:'刷新',
                        iconCls:'refresh',
                        handler:function () {
                            permissionGrid.getStore().reload();
                        }
                    }
                ],
                bbar:new Ext.PagingToolbar({
                    pageSize:20,
                    store:permissionStore,
                    displayInfo:true
                })
            }
        );

        //双击一行数据打开编辑窗口
        permissionGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);

            var form = buildForm('permission/update', [
                {
                    text:'更新',
                    handler:function (button) {
                        commitForm(button.ownerCt.ownerCt, function () {
                            permissionGrid.getStore().reload();
                            button.ownerCt.ownerCt.ownerCt.close();
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
                    xtype:'hidden',
                    name:'id'
                },
                {
                    fieldLabel:'权限名称',
                    name:'permissionName',
                    disabled:true
                },
                {
                    xtype:'combo',
                    hiddenName:'resource',
                    fieldLabel:'资源名称',
                    mode:'local',
                    editable:false,
                    triggerAction:'all',
                    emptyText:'请选择',
                    store:new Ext.data.ArrayStore({
                        fields:[ 'type', 'value' ],
                        data:EJS.Resource
                    }),
                    displayField:'type',
                    valueField:'value',
                    allowBlank:false
                },
                {
                    fieldLabel:'权限路径',
                    name:'path',
                    allowBlank:false,
                    regex:/^\S+$/ // 不能输入空格
                },
                {
                    fieldLabel:'类别',
                    name:'category'
                }
            ]);

            form.getForm().loadRecord(record);

            buildWin('修改权限', 500, form).show(this.id);

        });


        return permissionGrid;


    },


    createRoleScopeGrid:function () {

        var roleScopeStore = createJsonStore("role/scope/grid", [
            {
                name:'id',
                type:'int'
            },
            {
                name:'roleName',
                type:'string'
            },
            {
                name:'resource',
                type:'string'
            },
            {
                name:'resourceAuthScript',
                type:'string'
            },
            {
                name:'uiAuthScript',
                type:'string'
            },
            {
                name:'scopeValue',
                type:'string'
            }
        ]);


        roleScopeStore.load({
            params:{
                start:0,
                limit:20
            }
        });


        var roleScopeGrid = new Ext.grid.GridPanel({
                id:'roleScopeGrid',
                title:'资源配置',
                loadMask:true,
                store:roleScopeStore,
                columns:[
                    {
                        header:'ID',
                        dataIndex:'id'
                    },
                    {
                        header:'角色名',
                        dataIndex:'roleName'
                    },
                    {
                        header:'操作资源',
                        dataIndex:'resource',
                        renderer:EJS.ResourceRenderFunction
                    }
                ],
                viewConfig:{
                    forceFit:true
                },
                tbar:[
                    {
                        text:'删除',
                        iconCls:'remove',
                        handler:function () {
                            doGridRowDelete(roleScopeGrid, 'role/scope/delete/batch', function () {
                                roleScopeGrid.getStore().reload();
                            });
                        }
                    },
                    {
                        text:'刷新',
                        iconCls:'refresh',
                        handler:function () {
                            roleScopeGrid.getStore().reload();
                        }
                    }
                ],
                bbar:new Ext.PagingToolbar({
                    pageSize:20,
                    store:roleScopeStore,
                    displayInfo:true
                })
            }
        );

        //双击一行数据打开编辑窗口
        roleScopeGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var roleScopeForm = new Ext.FormPanel({
                baseCls:'x-plain',
                labelWidth:80,
                url:'role/scope/update',
                frame:true,
                width:500,
                autoHeight:true,
                defaults:{
                    width:380
                },
                defaultType:'textfield',

                items:[
                    {
                        xtype:'hidden',
                        name:'id',
                        value:record.get('id')
                    },
                    {
                        xtype:'combo',
                        hiddenName:'resource',
                        fieldLabel:'资源名称',
                        mode:'local',
                        editable:false,
                        triggerAction:'all',
                        emptyText:'请选择',
                        store:new Ext.data.ArrayStore({
                            fields:[ 'type', 'value' ],
                            data:EJS.Resource
                        }),
                        displayField:'type',
                        valueField:'value',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'资源脚本',
                        name:'resourceAuthScript',
                        xtype:'textarea',
                        height:100,
                        value:'def resourceAuth(resource, account, role,roleScope) {return true}',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'界面脚本',
                        name:'uiAuthScript',
                        xtype:'textarea',
                        height:100,
                        value:'def uiAuth(resource, account, role,roleScope) {return true}',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'域值',
                        name:'scopeValue',
                        xtype:'textarea'
                    }
                ]

            });
            roleScopeForm.getForm().loadRecord(record);

            var win = new Ext.Window({
                title:'修改用户配置资源',
                width:500,
                autoHeight:true,
                layout:'fit',
                plain:true,
                buttonAlign:'center',
                bodyStyle:'padding:5px;',
                items:roleScopeForm,
                buttons:[
                    {
                        text:'更新',
                        handler:function () {
                            commitForm(roleScopeForm, function () {
                                win.close();
                                roleScopeGrid.getStore().reload();
                                Ext.Msg.alert("成功", "配置成功");
                            });
                        }
                    },
                    {
                        text:'取消',
                        handler:function () {
                            win.close();
                        }
                    }
                ]
            });
            win.show(this.id);
        });

        return roleScopeGrid;


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
                items:[this.createRoleConfigGrid(), this.createRoleGrid(), this.createPermissionGrid(), this.createRoleScopeGrid()]
            })
        });

    }
})
;