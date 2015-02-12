AggregationPropertyValue = function (selectNode) {

    var propertyTree = new Ext.tree.TreePanel({
        rootVisible: false,
        lines: true,
        loader: new Ext.tree.TreeLoader({dataUrl: 'navigate/category/property/tree/aggregation/' + selectNode.id}),
        autoScroll: true,
        root: {
            id: -1,
            nodeType: 'async',
            expanded: true
        }
    });


    PropertyValue.superclass.constructor.call(this, {
        title: '聚合的属性和值',
        width: 600,
        height: 615,
        layout: 'fit',
        plain: true,
        buttonAlign: 'center',
        bodyStyle: 'padding:5px;',
        items: propertyTree
    });

}

Ext.extend(AggregationPropertyValue, Ext.Panel);