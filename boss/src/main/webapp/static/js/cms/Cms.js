Ext.Loader.load(['/static/js/cms/CmsEdit.js', '/static/js/cms/CmsContent.js', '/static/js/cms/CmsTemplate.js']);

Desktop.CmsWindow = Ext.extend(Ext.app.Module, {

    id: 'Cms-win',

    title: '内容管理',


    createWindow: function () {
        var categoryTree = new Ext.tree.TreePanel({
            id: 'cmsExt-tree',
            collapsible: true,
            title: '栏目分类',
            autoScroll: true,
            dataUrl: 'cms/category/list',
            rootVisible: false,
            tbar: [
                {
                    xtype: 'button',
                    iconCls: 'add',
                    text: '新建',
                    handler: function () {
                        var categoryWin = new CmsEditWindow({parent: -1, mode: "add"});
                        categoryWin.show();
                    }
                },
                {
                    xtype: 'button',
                    iconCls: 'refreshTree',
                    text: '刷新',
                    handler: function () {
                        categoryTree.root.reload();
                    }
                },
                {
                    xtype: 'button',
                    text: '展开',
                    iconCls: 'down',
                    handler: function () {
                        categoryTree.expandAll();
                    }
                },
                {
                    xtype: 'button',
                    text: '收起',
                    iconCls: 'up',
                    handler: function () {
                        categoryTree.collapseAll();
                    }
                }
            ],
            root: {
                nodeType: 'async'
            }
        });

        categoryTree.on('contextmenu', function (node, e) {
            var navTreeMenu = new Ext.menu.Menu({

            });
            if (!node.isLeaf()) {
                navTreeMenu.add([
                    {
                        text: '设置子类目优先级',
                        handler: function () {
                            changePriority('cms/category/list/' + node.id, 'cms/category/priority/update', function () {
                                categoryTree.root.reload();
                                categoryTree.expandAll();
                            });

                        }
                    }
                ])
            }

            if(!node.isLeaf()){
                navTreeMenu.add([
                    {
                        text: '添加子类目',
                        handler: function () {
                            var categoryWin = new CmsEditWindow({parent: node.id, mode: "add"});
                            categoryWin.show();
                        }
                    }])
            }

            navTreeMenu.add([
                {
                    text: '编辑',
                    handler: function () {
                        var categoryWin = new CmsEditWindow({id: node.id, parent: node.parentNode.id, mode: "edit"});
                        categoryWin.show();
                    }
                }
            ]);


            navTreeMenu.add([
                {
                    text: '删除',
                    handler: function () {
                        if (!node.isLeaf()) {
                            Ext.Msg.alert("错误", "请选择叶子节点");
                            return;
                        }
                        doAjax('/cms/category/delete', function () {
                            categoryTree.root.reload();
                        }, {
                            id: node.id
                        }, "你确定删除这个栏目类别吗？");

                    }
                }
            ]);

            navTreeMenu.showAt(e.getXY());
        }, this);

        var templateModuleTree = new Ext.tree.TreePanel({
            collapsible: true,
            title: '模板管理',
            autoScroll: true,
            root: new Ext.tree.TreeNode({
                id: 'root',
                text: '模板管理',
                draggable: false,
                expanded: true
            })
        });

        var leftPanel = new Ext.Panel({
            region: 'west',
            margins: '5 0 5 5',
            split: true,
            width: 210,
            layout: 'accordion',
            items: [categoryTree, templateModuleTree]
        });

        var FunctionTab = new Ext.TabPanel({
            region: 'center',
            deferredRender: false
        });

        categoryTree.on("dblclick", function (node) {
            FunctionTab.removeAll(true);
            FunctionTab.add(new CmsContent(node.id));
            FunctionTab.activate(0);
        });

        templateModuleTree.on("dblclick", function (node) {
            FunctionTab.removeAll(true);
            FunctionTab.add(new CmsTemplate());
            FunctionTab.activate(0);
        });

        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '内容管理',
            width: 900,
            height: document.body.clientHeight * 0.85,
            layout: 'border',
            items: [ leftPanel, FunctionTab]
        });
    }
});