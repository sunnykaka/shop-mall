Ext.ns('Ext.Model');

Ext.Model.Coupon = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'code',
            type: 'string'
        },
        {
            name: 'couponType',
            type: 'string'
        },
        {
            name: 'used',
            type: 'boolean'
        },
        {
            name: 'publish',
            type: 'boolean'
        },
        {
            name: 'userName',
            type: 'String'
        } ,
        {
            name: 'orderNo',
            type: 'long'
        } ,
        {
            name: 'price',
            type: 'long'
        }
        ,
        {
            name: 'miniApplyOrderPrice',
            type: 'long'
        }  ,
        {
            name: 'createDate',
            type: 'String'
        },
        {
            name: 'updateDate',
            type: 'String'
        },
        {
            name: 'startDate',
            type: 'String'
        },
        {
            name: 'expireDate',
            type: 'String'
        }

    ],

    baseParams: {
        start: 0,
        limit: 30
    }

};

Ext.Model.Account = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'userName',
            type: 'string'
        },
        {
            name: 'email',
            type: 'string'
        }
    ],

    baseParams: {
        start: 0,
        limit: 20
    }

};



Ext.Model.Consultation = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'askContent',
            type: 'String'
        },
        {
            name: 'answerContent',
            type: 'String'
        },
        {
            name: 'askTime',
            type: 'String'
        },
        {
            name: 'askedUserName',
            type: 'String'
        },
        {
            name: 'productName',
            type: 'String'
        },
        {
            name: 'productCode',
            type: 'String'
        },
        {
            name: 'consultationCategory',
            type: 'String'
        }
    ],
    baseParams: {
        start: 0,
        limit: 28
    }
};


Ext.Model.Finance = {
    recordDef: [
        {
            name: 'id',
            type: 'long'
        },
        {
            name: 'backGoodsState',
            type: 'String'
        },
        {
            name: 'canRefund',
            type: 'String'
        },
        {
            name: 'orderNo',
            type: 'long'
        },
        {
            name: 'userName',
            type: 'String'
        },
        {
            name: 'price',
            type: 'String'
        },
        {
            name: 'paybank',
            type: 'String'
        },
        {
            name: 'outerTradeNo',
            type: 'String'
        },
        {
            name: 'backReason',
            type: 'String'
        },
        {
            name: 'backDate',
            type: 'String'
        }
    ],

    baseParams: {
        action: 'WaitRefund',
        start: 0,
        limit: 28
    }

};


Ext.Model.FeedBack = {
    recordDef: [
        {name: 'id', type: 'int'},
        {name: 'type', type: 'string'},
        {name: 'content', type: 'string'},
        {name: 'information', type: 'string'},
        {name: 'fileName', type: 'string'},
        {name: 'fileType', type: 'string'}
    ],

    baseParams: {
        action: 'WaitRefund',
        start: 0,
        limit: 31
    }

};


Ext.Model.Content = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'title',
            type: 'string'
        },
        {
            name: 'content',
            type: 'string'
        },
        {
            name: 'url',
            type: 'string'
        },
        {
            name: 'categoryName',
            type: 'string'
        }
        ,
        {
            name: 'templateName',
            type: 'string'
        }
        ,
        {
            name: 'templateId',
            type: 'int'
        },
        {
            name: 'priority',
            type: 'int'
        }
    ],

    baseParams: {
        start: 0,
        limit: 15
    }

};


Ext.Model.CmsTemplate = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'templateContent',
            type: 'string'
        },
        {
            name: 'templateType',
            type: 'string'
        }
    ],

    baseParams: {
        start: 0,
        limit: 15
    }

};


Ext.Model.Customer = {
    recordDef: [
        {name: 'id', type: 'int'},
        {name: 'name', type: 'string'},
        {name: 'defaultLogistics', type: 'string'}
    ],

    baseParams: {
        start: 0,
        limit: 10
    }

};


Ext.Model.Log = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'title',
            type: 'String'
        },
        {
            name: 'content',
            type: 'String'
        },
        {
            name: 'operator',
            type: 'String'
        },
        {
            name: 'roleName',
            type: 'String'
        },
        {
            name: 'date',
            type: 'String'
        },
        {
            name: 'ip',
            type: 'String'
        }
    ],

    baseParams: {
        start: 0,
        limit: 28
    }

};


Ext.Model.BackGoods = {
    recordDef: [
        {
            name: 'id',
            type: 'long'
        },
        {
            name: 'orderNo',
            type: 'long'
        },
        {
            name: 'orderId',
            type: 'long'
        },
        {
            name: 'orderState',
            type: 'String'
        },
        {
            name: 'orderStateDesc',
            type: 'String'
        },
        {
            name: 'backGoodsState',
            type: 'String'
        },
        {
            name: 'backGoodsStateDesc',
            type: 'String'
        },
        {
            name: 'userName',
            type: 'String'
        },
        {
            name: 'backPhone',
            type: 'String'
        },
        {
            name: 'price',
            type: 'String'
        },
        {
            name: 'paybank',
            type: 'String'
        },
        {
            name: 'waybillNumber',
            type: 'String'
        },
        {
            name: 'invoice',
            type: 'bool'
        },
        {
            name: 'backDate',
            type: 'String'
        },
        {
            name: 'uploadFiles',
            type: 'String'
        },
        {
            name: 'hasAttach',
            type: 'bool'
        },
        {
            name: 'backReason',
            type: 'String'
        },
        {
            name: 'backReasonReal',
            type: 'String'
        }
    ],

    baseParams: {
        start: 0,
        limit: 10,
        backState: 'Create'
    }

};

Ext.Model.LimitedTimeDiscount = {
    recordDef: [
        {
            name: 'id',
            type: 'long'
        },
        {
            name: 'productName',
            type: 'string'
        },
        {
            name: 'productId',
            type: 'int'
        },
        {
            name: 'discount',
            type: 'string'
        },
        {
            name: 'discountType',
            type: 'string'
        },
        {
            name: 'startDate',
            type: 'string'
        },
        {
            name: 'endDate',
            type: 'string'
        }
    ],

    baseParams: {
        start: 0,
        limit: 18
    }

};

Ext.Model.Lottery = {
    recordDef: [
        { name: 'id', type: 'int' },
        { name: 'rotaryId', type: 'int' },
        { name: 'rotaryMeedId', type: 'int' },
        { name: 'userName', type: 'string' },
        { name: 'meedType', type: 'string' },
        { name: 'meedValue', type: 'string' },
        { name: 'value', type: 'string' },
        { name: 'create', type: 'string' },
        { name: 'really', type: 'boolean' },
        { name: 'consigneeName', type: 'string' },
        { name: 'consigneePhone', type: 'string' },
        { name: 'consigneeAddress', type: 'string' },
        { name: 'sendOut', type: 'boolean' }
    ],
    baseParams: {
        start: 0,
        limit: 13
    }
};

Ext.Model.ProductIntegralConversion = {
    recordDef: [
        { name: 'id', type: 'int' },
        { name: 'productId', type: 'int' },
        { name: 'skuId', type: 'int' },
        { name: 'currency', type: 'float' },
        { name: 'userBuyCount', type: 'int' },
        { name: 'start', type: 'string' },
        { name: 'end', type: 'string' },
        { name: 'mockSale', type: 'int' }
    ],
    baseParams: {
        start: 0,
        limit: 13
    }
};

Ext.Model.ProductSuperConversion = {
    recordDef: [
        { name: 'id', type: 'int' },
        { name: 'productId', type: 'int' },
        { name: 'skuId', type: 'int' },
        { name: 'currency', type: 'float' },
        { name: 'moneyForPrice', type: 'float' },
        { name: 'userBuyCount', type: 'int' },
        { name: 'start', type: 'string' },
        { name: 'end', type: 'string' },
        { name: 'mockSale', type: 'int' }
    ],
    baseParams: {
        start: 0,
        limit: 13
    }
};
