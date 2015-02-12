Ext.define('Supplier.model.Statement',
    {
        //不要忘了继承
        extend:'Ext.data.Model',
        fields:['brandName', 'categoryName', 'itemNo', 'productName', 'attribute', 'barCode', 'number', 'shipmentNum',
            { name: "backNum", mapping: function(raw){
                return parseInt(raw.number)-parseInt(raw.shipmentNum);
            }},
            'commodityPrice', 'salePrice','backPrice'],
        idProperty: 'good_num'
    }
)