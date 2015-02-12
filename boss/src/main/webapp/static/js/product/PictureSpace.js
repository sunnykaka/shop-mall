/**
 *  图片空间
 */
//创建图片管理窗口
Desktop.PictureSpaceWindow = Ext.extend(Ext.app.Module, {

    id: 'PictureSpace-win',

    title: '图片空间管理',

    createWindow: function () {
        var newIndex = 1;
        var tb = new Ext.Toolbar({
            items: [
                {
                    text: '添加空间',
                    iconCls: 'album-btn',
                    handler: function () {
                        var node = tree.root.appendChild(new Ext.tree.TreeNode({
                            text: 'Album ' + (++newIndex),
                            cls: 'album-node',
                            allowDrag: false
                        }));
                        tree.getSelectionModel().select(node);
                        setTimeout(function () {
                            ge.editNode = node;
                            ge.startEdit(node.ui.textNode);
                        }, 10);
                    }
                },
                {
                    text: '删除空间',
                    iconCls: 'remove',
                    handler: function () {
                        if (tree.getSelectionModel().getSelectedNode() == null) {
                            Ext.Msg.alert("信息", "请选择要删除的空间");
                            return;
                        }

                        doAjax('spacePicture/deleteSpace', function () {
                            tree.root.reload();
                            Ext.Msg.alert("信息", "删除成功");
                        }, {
                            spaceName: tree.getSelectionModel().getSelectedNode().text
                        }, "你确定删除这些空间吗？");

                    }
                }
            ]
        });

        var tree = new Ext.tree.TreePanel({
            cls: 'album-node',
            rootVisible: false,
            title: '空间列表',
            lines: false,
            dataUrl: 'spacePicture/queryAllSpace',
            root: {
                nodeType: 'async'
            },
            region: 'west',
            width: 200,
            autoScroll: true,
            tbar: tb,
            listeners: {
                dblclick: function (node) {
                    spacePanel.removeAll(true);
                    spacePanel.add(new Space(node));
                    spacePanel.activate(0);
                }
            }
        });

        var ge = new Ext.tree.TreeEditor(tree, {allowBlank: false, maxLength: 100}, {ignoreNoChange: false, listeners: {
            complete: function (treeEditor, newValue, oldValue) {
                doAjax('spacePicture/updateSpace', function () {
                    Ext.Msg.alert("成功", "操作成功");
                    tree.root.reload();
                }, {
                    oldValue: oldValue,
                    spaceName: newValue
                });

            }}
        });

        var spacePanel = new Ext.TabPanel({
            region: 'center',
            deferredRender: false
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            layout: 'border',
            frame: true,
            title: '空间图片管理',
            width: 850,
            height: height * 0.85,
            items: [tree, spacePanel]
        });

    }

});
