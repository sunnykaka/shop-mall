PropertyValue = function (selectNode) {

    var propertyTree = new Ext.tree.TreePanel({
        rootVisible: false,
        lines: true,
        loader: new Ext.tree.TreeLoader({dataUrl: 'navigate/category/property/tree/' + selectNode.id}),
        autoScroll: true,
        root: {
            id: -1,
            nodeType: 'async',
            expanded: true
        }
    });


    propertyTree.on('contextmenu', function (node, e) {
        if (!node.leaf) {
            var navTreeMenu = new Ext.menu.Menu({
                items: [
                    {
                        text: '设置属性值优先级',
                        handler: function () {
                            changePriority('navigate/category/cpv/list/' + selectNode.id + '/' + node.id, 'navigate/category/cpv/priority/update', function () {
                                propertyTree.root.reload();

                            });

                        }
                    }
                ]
            });
            navTreeMenu.showAt(e.getXY());
        }
    }, this);

    PropertyValue.superclass.constructor.call(this, {
        title: '属性和值',
        width: 600,
        height: 615,
        layout: 'fit',
        plain: true,
        buttonAlign: 'center',
        bodyStyle: 'padding:5px;',
        items: propertyTree
    });

}

Ext.extend(PropertyValue, Ext.Panel);