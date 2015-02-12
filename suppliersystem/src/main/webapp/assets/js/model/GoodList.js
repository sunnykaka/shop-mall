Ext.define('Supplier.model.GoodList',
    {
        //不要忘了继承
        extend:'Ext.data.Model',
        fields:['id','itemNo', 'productName', 'skuAttribute', 'barCode', 'categoryName', 'unitPrice', 'shipmentNum', 'stockQuantity', 'brandName'],
        idProperty: 'id'
    }
)