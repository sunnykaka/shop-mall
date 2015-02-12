Ext.define('Supplier.model.Inventory',
    {
        //不要忘了继承
        extend:'Ext.data.Model',
        fields:['brandName', 'categoryName', 'itemNo', 'productName', 'attribute', 'barCode', 'stockQuantity', 'stockQuantity', 'stockQuantity', 'storageName'],
        idProperty: 'good_num'
    }
)