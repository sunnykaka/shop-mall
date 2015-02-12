Ext.Model.Product = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'categoryId',
            type: 'int'
        },
        {
            name: 'productCode',
            type: 'string'
        },
        {
            name: 'brandName',
            type: 'string'
        },
        {
            name: 'categoryName',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'customerId',
            type: 'int'
        },
        {
            name: 'brandId',
            type: 'int'
        },
        {
            name: 'online',
            type: 'bool'
        },
        {
            name: 'tagType',
            type: 'string'
        }
    ],

    baseParams: {
        categoryId: -1,
        customerId: -1,
        brandId: -1,
        start: 0,
        limit: 20,
        search: ''
    }

};