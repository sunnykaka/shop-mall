/**
 * 实现前台类目管理的js
 */
NavCategoryAddWindow = function (config) {

    this.formItems = [];

    this.formItems.push({
        fieldLabel:'类目名称',
        name:'name',
        allowBlank:false,
        regex:/^\S+$/, // 不能输入空格
        xtype:'textfield',
        maxLength:45
    });
    this.formItems.push({
        fieldLabel:'类目描述',
        name:'description',
        xtype:'textarea',
        maxLength:100
    });
    this.formItems.push({
        fieldLabel:'关键字(逗号分隔)',
        name:'keyWord',
        xtype:'textarea',
        maxLength:100
    });

    if (config.mode == "add") {
        this.formItems.push(new Ext.form.Hidden({
            name:'parentId',
            value:config.parentId
        }));
        this.title = "添加前台类目";
        this.url = 'navigate/category/new';
    } else if (config.mode = "edit") {
        this.formItems.push(new Ext.form.Hidden({
            name:'id',
            value:config.id
        }));
        this.title = "修改前台类目";
        this.url = 'navigate/category/update';
    }

    this.form = new Ext.FormPanel({
        items:this.formItems,
        border:false,
        bodyStyle:'background:transparent;padding:10px;',
        defaults:{
            anchor:'100%'
        }
    });

    if (config.mode == "edit") {
        this.form.load({
            url:'navigate/category/' + config.id,
            method:"GET",
            success:function (form, action) {
                this.form.getForm().setValues(action.result.data.object);
            },
            scope:this
        });
    }

    CategoryAddWindow.superclass.constructor.call(this, {
        autoHeight:true,
        width:500,
        resizable:false,
        plain:true,
        modal:true,
        autoScroll:true,
        buttonAlign:'center',
        closeAction:'hide',
        title:this.title,

        buttons:[
            {
                text:'提交',
                handler:function (button) {
                    button.setDisabled(true);
                    if (!this.form.getForm().isValid()) {
                        return;
                    }
                    this.form.getForm().submit({
                        url:this.url,
                        success:function (form, action) {
                            this.form.getForm().reset();

                            this.close();

                            var tree = Ext.getCmp('nav-category-tree');

                            var newId = action.result.data.newId;

                            tree.root.reload();

                            Ext.Ajax.request({
                                url:'navigate/category/selectPath/' + newId,
                                success:function (response, options) {
                                    tree.selectPath(response.responseText);
                                },
                                failure:function () {
                                    Ext.Msg.alert('失败', '服务器错误');
                                }
                            });
                        },
                        failure:function (form, action) {
                            if (action.response.status != 200) {
                                Ext.Msg.alert("错误", "服务器错误");
                            } else {
                                Ext.Msg.alert("错误", action.result.msg);
                            }
                        },
                        scope:this
                    });
                },
                scope:this
            },
            {
                text:'取消',
                handler:this.close.createDelegate(this, [])
            }
        ],

        items:this.form
    });
}

Ext.extend(NavCategoryAddWindow, Ext.Window);

Desktop.NavigateCategoryWindow = Ext.extend(Ext.app.Module, {
    id:'NavigateCategory-win',

    title:'前台类目管理',

    //创建类目管理窗口
    createWindow:function () {
        var selectNode;

        var navCategoryTree = new Ext.tree.TreePanel({
            id:'nav-category-tree',
            title:'前台类目',
            rootVisible:false,
            lines:false,
            dataUrl:'navigate/category/tree',
            autoScroll:true,
            root:{
                id:-1,
                nodeType:'async',
                expanded:true
            },
            listeners:{
                click:function (node) {
                    selectNode = node;
                }
            },
            tools:[
                {
                    id:'refresh',
                    on:{
                        click:function () {
                            var tree = Ext.getCmp('nav-category-tree');
                            tree.root.reload();
                        }
                    }
                }
            ],
            tbar:[
                {
                    text:'添加前台根类目',
                    iconCls:'add',
                    handler:function () {

                        var navCategoryWin = new NavCategoryAddWindow({parentId:-1, mode:"add"});
                        navCategoryWin.show();
                    }
                },
                {
                    text:'设置根类目优先级',
                    iconCls:'config',
                    handler:function () {
                        changePriority('navigate/category/root/list/', 'navigate/category/priority/update', function () {
                        });

                    }
                },
                {
                    text:'推送全部类目',
                    iconCls:'sync',
                    handler:function () {
                        Ext.MessageBox.show({
                            title:'推送中',
                            msg : '正在推送，请稍后...',
                            width : 300,
                            wait : true,
                            progress : true,
                            closable : true,
                            waitConfig : {
                                interval : 400
                            },
                            icon : Ext.Msg.INFO
                        });
                        Ext.Ajax.request({
                            method:'POST',
                            url:'category/push',
                            success:function (response, opts) {
                                Ext.MessageBox.hide();
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert("成功", obj.msg);
                            },
                            failure:function (response, opts) {
                                Ext.MessageBox.hide();
                                Ext.Msg.alert("失败", "推送失败");
                            }
                        });
                    }
                }
            ]
        });

        //前台类目右键菜单
        navCategoryTree.on('contextmenu', function (node, e) {
            var navTreeMenu = new Ext.menu.Menu({
                items:[
                    {
                        text:'编辑',
                        handler:function () {
                            var categoryWin = new NavCategoryAddWindow({id:node.id, mode:"edit"});
                            categoryWin.show();
                        }
                    }
                ]
            });
            selectNode = node;

            var menuArray = [
                {
                    text:'添加子类目',
                    handler:function () {
                        var categoryWin = new NavCategoryAddWindow({parentId:selectNode.id, mode:"add"});
                        categoryWin.show();
                    }
                },
                {
                    text:'关联后台类目',
                    handler:function () {
                        var navId = selectNode.id;

                        doAjax('navigate/category/expandPaths/' + selectNode.id, function (obj) {
                            Ext.each(obj.data.path, function (path) {
                                checkCategoryTree.expandPath(path);
                            });
                        });

                        doAjax('navigate/category/checkParentHaveTree/' + navId, function (obj) {
                            if (!obj.success) {
                                Ext.Msg.alert('错误', obj.msg);
                                return;
                            }
                        });

                        var loader = new Ext.tree.TreeLoader({dataUrl:'navigate/category/checkTree/' + navId, preloadChildren:true});

                        var checkCategoryTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:true,
                            loader:loader,
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true
                            },
                            listeners:{
                                'checkchange':function (e) {
                                    cascadeDisable(e, e.getUI().checkbox.checked);
                                }
                            }
                        });

                        var relevanceCategory = function () {
                            var categoryIds = '', selNodes = checkCategoryTree.getChecked();

                            Ext.each(selNodes, function (node) {
                                if (categoryIds.length > 0) {
                                    categoryIds += ',';
                                }
                                categoryIds += node.id;
                            });

                            if (selNodes.length == 0) {
                                Ext.Msg.alert("错误", "请选择要关联的类目");
                                return;
                            }

                            doAjax('navigate/category/association', function () {
                                Ext.Msg.alert("成功", "关联成功");

                                /*刷新右边关联后台类目的TabPanel*/
                                eastPanel.removeAll();
                                eastPanel.add(new Category(selectNode));
                                eastPanel.add(new PropertyValue(selectNode));
                                eastPanel.add(new AggregationPropertyValue(selectNode));

                                eastPanel.activate(0);
                            }, 'navId=' + navId + '&categoryIds=' + categoryIds);

                            categoryTreeWindow.close();
                        }

                        var categoryTreeWindow = new Ext.Window({
                            width:600,
                            height:500,
                            layout:'fit',
                            plain:true,
                            buttonAlign:'center',
                            bodyStyle:'padding:5px;',
                            items:checkCategoryTree,
                            //热键添加
                            keys:[
                                {
                                    key:Ext.EventObject.ENTER,
                                    fn:relevanceCategory //执行的方法
                                }
                            ],
                            buttons:[
                                {
                                    text:'保存',
                                    handler:relevanceCategory
                                },
                                {
                                    text:'关闭',
                                    handler:function () {
                                        categoryTreeWindow.close();
                                    }
                                }
                            ]
                        });
                        categoryTreeWindow.setTitle('为前台类目' + selectNode.text + '关联后台类目');
                        categoryTreeWindow.show();
                    }
                },
                {
                    text:'聚合属性和值',
                    handler:function () {
                        var propertyTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'查看已设置',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/aggregation/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true
                            }
                        });
                        propertyTree.expandAll();


                        var propertyCheckTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'设置属性值',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/forAggregation/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true,
                                checked:true
                            }
                        });
                        propertyCheckTree.expandAll();

                        //树展开的时候把级联选择事件监听设定到节点上
                        propertyCheckTree.on("expandnode", function (node) {
                            node.on("checkchange", cascadeCheck);
                        });

                        var selectPropertyAndValue = function () {
                            var pvNode = propertyCheckTree.getChecked();
                            // 因为还有个根节点 -1
                            if (pvNode.length == 1) {
                                Ext.Msg.alert("错误", "请选择属性值");
                                return;
                            }
                            var valueIds = [];

                            Ext.each(pvNode, function (node) {
                                if (node.isLeaf()) {
                                    valueIds.push(node.id);
                                }
                            });


                            doAjax('navigate/category/property/aggregation', function () {
                                Ext.Msg.alert("成功", "设定成功");

                                /*刷新右边关联后台类目的TabPanel*/
                                eastPanel.removeAll();
                                eastPanel.add(new Category(selectNode));
                                eastPanel.add(new PropertyValue(selectNode));
                                eastPanel.add(new AggregationPropertyValue(selectNode));

                                eastPanel.activate(0);
                            }, {navId:selectNode.id, valueIds:valueIds});

                            propertyTreeWindow.close();
                        }

                        var propertyTreeWindow = new Ext.Window({
                            width:600,
                            height:500,
                            layout:'column',
                            plain:true,
                            buttonAlign:'center',
                            border:false,
                            items:[propertyCheckTree, propertyTree],
                            //热键添加
                            keys:[
                                {
                                    key:Ext.EventObject.ENTER,
                                    fn:selectPropertyAndValue //执行的方法
                                }
                            ],
                            buttons:[
                                {
                                    text:'保存',
                                    handler:selectPropertyAndValue
                                },
                                {
                                    text:'关闭',
                                    handler:function () {
                                        propertyTreeWindow.close();
                                    }
                                }
                            ]
                        });
                        propertyTreeWindow.setTitle('为前台类目' + selectNode.text + '聚合属性值');
                        propertyTreeWindow.show();
                    }
                },
                {
                    text:'选择属性和值',
                    handler:function () {
                        var propertyTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'查看已选择',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true
                            }
                        });
                        propertyTree.expandAll();


                        var propertyCheckTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'选择新的属性值',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/check/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true,
                                checked:true
                            }
                        });
                        propertyCheckTree.expandAll();

                        //树展开的时候把级联选择事件监听设定到节点上
                        propertyCheckTree.on("expandnode", function (node) {
                            node.on("checkchange", cascadeCheck);
                        });

                        var selectPropertyAndValue = function () {
                            var pvNode = propertyCheckTree.getChecked();
                            // 因为还有个根节点 -1
                            if (pvNode.length == 1) {
                                Ext.Msg.alert("错误", "请选择属性值");
                                return;
                            }
                            var valueIds = [];

                            Ext.each(pvNode, function (node) {
                                if (node.isLeaf()) {
                                    valueIds.push(node.id);
                                }
                            });

                            doAjax('navigate/category/property/association', function () {
                                Ext.Msg.alert("成功", "设定成功");

                                /*刷新右边关联后台类目的TabPanel*/
                                eastPanel.removeAll();
                                eastPanel.add(new Category(selectNode));
                                eastPanel.add(new PropertyValue(selectNode));
                                eastPanel.add(new AggregationPropertyValue(selectNode));

                                eastPanel.activate(0);
                            }, {navId:selectNode.id, valueIds:valueIds});

                            propertyTreeWindow.close();
                        }

                        var propertyTreeWindow = new Ext.Window({
                            width:600,
                            height:500,
                            layout:'column',
                            plain:true,
                            buttonAlign:'center',
                            border:false,
                            items:[propertyCheckTree, propertyTree],
                            //热键添加
                            keys:[
                                {
                                    key:Ext.EventObject.ENTER,
                                    fn:selectPropertyAndValue //执行的方法
                                }
                            ],
                            buttons:[
                                {
                                    text:'保存',
                                    handler:selectPropertyAndValue
                                },
                                {
                                    text:'关闭',
                                    handler:function () {
                                        propertyTreeWindow.close();
                                    }
                                }
                            ]
                        });
                        propertyTreeWindow.setTitle('为前台类目' + selectNode.text + '设定属性和值');
                        propertyTreeWindow.show();
                    }
                },
                {
                    text:'设定筛选属性',
                    handler:function () {
                        var propertyTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'查看已设置筛选属性',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/searchable/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true
                            }
                        });
                        propertyTree.expandAll();


                        var propertyCheckTree = new Ext.tree.TreePanel({
                            rootVisible:false,
                            lines:false,
                            columnWidth:.50,
                            height:425,
                            title:'设置筛选属性',
                            loader:new Ext.tree.TreeLoader({dataUrl:'navigate/category/property/tree/forSearch/' + selectNode.id}),
                            autoScroll:true,
                            root:{
                                id:-1,
                                nodeType:'async',
                                expanded:true,
                                checked:true
                            }
                        });


                        var selectProperty = function () {
                            var pvNode = propertyCheckTree.getChecked();
                            // 因为还有个根节点 -1
                            if (pvNode.length == 1) {
                                Ext.Msg.alert("错误", "请选择属性值");
                                return;
                            }
                            var propertyIds = [];

                            Ext.each(pvNode, function (node) {
                                if (node.isLeaf()) {
                                    propertyIds.push(node.id);
                                }
                            });


                            doAjax('navigate/category/property/setterSearchable', function () {
                                Ext.Msg.alert("成功", "设定成功");

                                /*刷新右边关联后台类目的TabPanel*/
                                eastPanel.removeAll();
                                eastPanel.add(new Category(selectNode));
                                eastPanel.add(new PropertyValue(selectNode));
                                eastPanel.add(new AggregationPropertyValue(selectNode));

                                eastPanel.activate(0);
                            }, {navId:selectNode.id, propertyIds:propertyIds});


                            propertyTreeWindow.close();
                        }

                        var propertyTreeWindow = new Ext.Window({
                            width:600,
                            height:500,
                            layout:'column',
                            plain:true,
                            buttonAlign:'center',
                            border:false,
                            items:[propertyCheckTree, propertyTree],
                            //热键添加
                            keys:[
                                {
                                    key:Ext.EventObject.ENTER,
                                    fn:selectProperty //执行的方法
                                }
                            ],
                            buttons:[
                                {
                                    text:'保存',
                                    handler:selectProperty
                                },
                                {
                                    text:'关闭',
                                    handler:function () {
                                        propertyTreeWindow.close();
                                    }
                                }
                            ]
                        });
                        propertyTreeWindow.setTitle('为前台类目' + selectNode.text + '设定筛选属性');
                        propertyTreeWindow.show();
                    }
                },
                {
                    text:'设置属性优先级',
                    handler:function () {

                        changePriority('navigate/category/cp/list/' + selectNode.id, 'navigate/category/cp/priority/update', function () {
                            /*刷新右边关联后台类目的TabPanel*/
                            eastPanel.removeAll();
                            eastPanel.add(new Category(selectNode));
                            eastPanel.add(new PropertyValue(selectNode));
                            eastPanel.add(new AggregationPropertyValue(selectNode));

                            eastPanel.activate(0);
                        });
                    }
                }
            ];

            navTreeMenu.add(menuArray);

            if (node.leaf) {
                navTreeMenu.add({
                    text:'设置LOGO图',
                    handler:function () {
                        var form = buildForm('navigate/category/setCategoryLogoImg', [
                            {
                                text:'提交',
                                handler:function (button) {
                                    button.setDisabled(true);

                                    commitForm(form, function () {
                                        form.ownerCt.close();
                                        Ext.Msg.alert("成功", "设置成功");
                                    });
                                }
                            }
                        ], [
                            {
                                xtype:'hidden',
                                name:'navId',
                                value:selectNode.id
                            },
                            {
                                fieldLabel:'LOGO图',
                                name:'logoImg',
                                allowBlank:true
                            }
                        ]);

                        form.load({
                            url:'navigate/category/settings/' + selectNode.id,
                            method:"GET",
                            success:function (f, action) {
                                form.getForm().setValues(action.result.data.object);
                            }
                        });

                        buildWin('设置LOGO图', 400, form).show(this.id);
                    }
                });
                /*navTreeMenu.add({
                    text:'设置推荐商品',
                    handler:function () {
                        var form = buildForm('navigate/category/setCategoryRecommendProduct', [
                            {
                                text:'提交',
                                handler:function (button) {
                                    button.setDisabled(true);

                                    commitForm(form, function () {
                                        form.ownerCt.close();
                                        Ext.Msg.alert("成功", "设置成功");
                                    });
                                }
                            }
                        ], [
                            {
                                xtype:'hidden',
                                name:'navId',
                                value:selectNode.id
                            },
                            {
                                fieldLabel:'商品ID',
                                name:'minorProduct',
                                allowBlank:false
                            }
                        ]);

                        form.load({
                            url:'navigate/category/settings/' + selectNode.id,
                            method:"GET",
                            success:function (f, action) {
                                form.getForm().setValues(action.result.data.object);
                            }
                        });

                        buildWin('设置推荐商品', 400, form).show(this.id);
                    }
                });*/
                navTreeMenu.add({
                    text:node.attributes.hot ? '取消热点' : '设为热点',
                    handler:function () {
                        Ext.Ajax.request({
                            url:'navigate/category/setHot',
                            success:function (response, options) {
                                var obj = Ext.decode(response.responseText);
                                if (!obj.success) {
                                    Ext.Msg.alert("失败", obj.msg);
                                } else {
                                    var tree = Ext.getCmp('nav-category-tree');
                                    Ext.Ajax.request({
                                        url:'navigate/category/selectPath/' + selectNode.id,
                                        success:function (response, opts) {
                                            tree.selectPath(response.responseText);
                                        },
                                        failure:function (response, opts) {
                                            Ext.Msg.alert("失败", "加载数据失败");
                                        }
                                    });
                                    tree.root.reload();
                                }
                            },
                            failure:function () {
                                Ext.Msg.alert("失败，更新失败");
                            },
                            params:{
                                navId:selectNode.id,
                                hot:node.attributes.hot ? 0 : 1
                            }
                        });
                    }
                });

            } else {
                navTreeMenu.add({
                    text:'设置子类目优先级',
                    handler:function () {

                        changePriority('navigate/category/subCategories/list/' + selectNode.id, 'navigate/category/priority/update', function () {
                            eastPanel.removeAll();

                            eastPanel.add(new Category(selectNode));
                            eastPanel.add(new PropertyValue(selectNode));
                            eastPanel.add(new AggregationPropertyValue(selectNode));

                            eastPanel.activate(0);
                        });

                    }
                });
                navTreeMenu.add({
                    text:'设置频道推广图',
                    handler:function () {

                        doAjax('/navigate/category/settings/mainImg/number/' + selectNode.id, function (obj) {
                            var formItems = [];

                            formItems.push({
                                xtype:'hidden',
                                name:'navId',
                                value:selectNode.id
                            });
                            formItems.push({
                                /*fieldLabel:'主推商品',*/
                                name:'mainProduct',
                                xtype:'hidden',
                                value:0
                                //xtype:'numberfield',
                            });
                            formItems.push({
                                fieldLabel:'频道 banner',
                                name:'mainImg[0]',
                                allowBlank:false
                            });
                            formItems.push({
                                fieldLabel:'列表广告图',
                                name:'adImg[0]'
                                //allowBlank:false
                            });
                            formItems.push({
                                fieldLabel:'图片链接',
                                name:'mainImgLink[0]',
                                allowBlank:false
                            });

                            if (obj.data.number > 1) {
                                for (var i = 1; i < obj.data.number; i++) {
                                    formItems.push({
                                        fieldLabel:'频道 banner',
                                        name:String.format('mainImg[{0}]', i),
                                        allowBlank:false
                                    });
                                    formItems.push({
                                        fieldLabel:'列表广告图',
                                        name:String.format('adImg[{0}]', i)
                                        //allowBlank:false
                                    });
                                    formItems.push({
                                        fieldLabel:'图片链接',
                                        name:String.format('mainImgLink[{0}]', i),
                                        allowBlank:false
                                    });
                                }
                            }

                            var form = buildForm('navigate/category/setChannelImg', [
                                {
                                    text:'提交',
                                    handler:function (button) {
                                        commitForm(form, function () {
                                            form.ownerCt.close();
                                            Ext.Msg.alert("成功", "设置成功");
                                        });
                                    }
                                },
                                {
                                    text:'多条',
                                    handler:function () {
                                        var base = (form.items.length - 2) / 2
                                        form.add([
                                            {
                                                fieldLabel:'频道 banner',
                                                name:String.format('mainImg[{0}]', base),
                                                allowBlank:false
                                            },
                                            {
                                                fieldLabel:'列表广告图',
                                                name:String.format('adImg[{0}]', base)
                                                //allowBlank:false
                                            },
                                            {
                                                fieldLabel:'图片链接',
                                                name:String.format('mainImgLink[{0}]', base),
                                                allowBlank:false
                                            }
                                        ]);
                                        form.doLayout();
                                    }
                                },
                                {
                                    text:'删除',
                                    handler:function () {
                                        var base = (form.items.length - 2) / 2;
                                        if(base > 1){
                                            form.remove(form.items.length - 1);
                                            form.remove(form.items.length - 1);
                                            form.remove(form.items.length - 1);
                                            form.doLayout();
                                        }else{
                                            Ext.Msg.alert("提示", "最少保留一张频道主图");
                                        }
                                    }
                                }
                            ], formItems);

                            form.load({
                                url:'navigate/category/settings/' + selectNode.id,
                                method:"GET",
                                success:function (f, action) {
                                    var object = action.result.data.object;
                                    if (object.mainImg) {
                                        for (var i = 0; i < object.mainImg.length; i++) {
                                            object[String.format('mainImg[{0}]', i)] = object.mainImg[i];
                                        }
                                    }
                                    if (object.adImg) {
                                        for (var i = 0; i < object.adImg.length; i++) {
                                            object[String.format('adImg[{0}]', i)] = object.adImg[i];
                                        }
                                    }
                                    if (object.mainImgLink) {
                                        for (var i = 0; i < object.mainImgLink.length; i++) {
                                            object[String.format('mainImgLink[{0}]', i)] = object.mainImgLink[i];
                                        }
                                    }
                                    form.getForm().setValues(object);
                                }
                            });

                            buildWin('设置频道推广图', 500, form).show(this.id);
                        });
                    }
                });
            }

            navTreeMenu.add([
                {
                    text: '设置SEO推广信息',
                    handler: function () {
                        SEO(selectNode.text,selectNode.id,'CHANNEL');
                    }
                },
                {
                    text:'删除',
                    handler:function () {
                        if (!selectNode.isLeaf()) {
                            Ext.Msg.alert("错误", "不能删除有子类目的当前类目, 请选择子类目!");
                            return;
                        }
                        Ext.Msg.confirm("确认", "你确定删除这个类目吗？", function (btn) {
                            if (btn == 'yes') {
                                Ext.Ajax.request({
                                    url:'navigate/category/delete',
                                    success:function (response, options) {
                                        var obj = Ext.decode(response.responseText);
                                        if (!obj.success) {
                                            Ext.Msg.alert("失败", obj.msg);
                                        } else {
                                            var tree = Ext.getCmp('nav-category-tree');
                                            //tree.root.reload();

                                            Ext.Ajax.request({
                                                url:'navigate/category/selectPathAsDelete/' + selectNode.parentNode.id,
                                                success:function (response, opts) {
                                                    tree.selectPath(response.responseText);
                                                },
                                                failure:function (response, opts) {
                                                    Ext.Msg.alert("失败", "加载数据失败");
                                                }
                                            });

                                            tree.root.reload();
                                        }
                                    },
                                    failure:function () {
                                        Ext.Msg.alert("失败，更新失败");
                                    },
                                    params:{
                                        navId:selectNode.id
                                    }
                                });
                            }
                        });
                    }
                }
            ]);

            navTreeMenu.showAt(e.getXY());
        }, this);

        /** 左边Panel */
        var westPanel = new Ext.Panel({
            region:'west',
            width:400,
            border:false,
            layout:'fit',
            items:navCategoryTree
        });

        /** 右边部分 */
        var eastPanel = new Ext.TabPanel({
            id:'tabPanel',
            region:'center',
            width:600,
            title:'右边',
            enableTabScroll:true,
            animScroll:true
        });

        //前台类目点击
        navCategoryTree.on('click', function (node, e) {
            eastPanel.removeAll();

            eastPanel.add(new Category(selectNode));
            eastPanel.add(new PropertyValue(selectNode));
            eastPanel.add(new AggregationPropertyValue(selectNode));

            eastPanel.activate(0);
        });

        var height = document.body.clientHeight;
        return  this.app.getDesktop().createWindow({
            id:this.id,
            border:false,
            frame:true,
            title:'前台类目树表',
            width:900,
            height:height * 0.85,
            layout:'border',
            items:[westPanel, eastPanel]
        });
    }

});

