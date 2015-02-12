Ext.define('Supplier.model.OrderList',
    {
        //不要忘了继承
        extend:'Ext.data.Model',
        fields:[
            'order_num', 'brand', 'order_state', 'good_name', 'good_attr', 'order_amount', 'good_price', 'order_price', 'buyer_id', 'receiver', 'phone', 'address',
            'expressage', 'logistics_num', 'give_amount', 'return_amount', 'buery_message', 'seller_note', 'order_date', 'deal_date', 'check_date', 'print_date', 'examine_date', 'give_date',
            'receiver_date', 'checker', 'printer', 'examiner', 'giver', 'site'
        ],
        idProperty: 'order_num'
    }
)