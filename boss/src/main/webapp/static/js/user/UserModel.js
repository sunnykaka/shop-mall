Ext.Model.User = {
    recordDef: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'userName',
            type: 'String'
        },
        {
            name: 'email',
            type: 'String'
        },
        {
            name: 'phone',
            type: 'String'
        },
        {
            name: 'lastLoginTime',
            type: 'String'
        } ,
        {
            name: 'registerDate',
            type: 'String'
        }
        ,
        {
            name: 'loginCount',
            type: 'int'
        },
        {
            name: 'hasForbidden',
            type: 'boolean'
        },
        {
            name: 'active',
            type: 'boolean'
        },
        {
            name: 'grade',
            type: 'String'
        },
        {
            name: 'pointTotal',
            type: 'String'
        }
    ],
    baseParams: {
        start: 0,
        limit: 26,
        type: "",
        sortMode: ""
    }
};
