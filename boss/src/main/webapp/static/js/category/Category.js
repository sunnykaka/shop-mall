Category = function (selectNode) {

    var navId = selectNode.id;

    doAjax('navigate/category/expandPaths/' + selectNode.id, function (obj) {
        Ext.each(obj.data.path, function (path) {
            checkCategoryTree.expandPath(path);
        });
    });

    var loader = new Ext.tree.TreeLoader({dataUrl: 'navigate/category/showCheckTree/' + navId, preloadChildren: true});

    var checkCategoryTree = new Ext.tree.TreePanel({
        rootVisible: false,
        lines: true,
        loader: loader,
        autoScroll: true,
        root: {
            id: -1,
            nodeType: 'async',
            expanded: true
        },
        listeners: {
            'checkchange': function (e) {
                cascadeDisable(e, e.getUI().checkbox.checked);
            }
        }
    });

    Category.superclass.constructor.call(this, {
        title: '关联类目',
        width: 600,
        height: 615,
        layout: 'fit',
        plain: true,
        buttonAlign: 'center',
        bodyStyle: 'padding:5px;',
        items: checkCategoryTree
    });
}

Ext.extend(Category, Ext.Panel);