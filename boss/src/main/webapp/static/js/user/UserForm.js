/**
 * 用户表单
 * @type {*}
 */

Ext.apply(Ext.form.VTypes, {

    password: function (val, field) {
        //如果是第二个密码框
        if (field.initialPassField) {
            var pwd = Ext.getCmp(field.initialPassField);
            return (val == pwd.getValue());//比较值
        }
        return true;
    },

    passwordText: '两次输入不一致' //提示文本
});


var getUserForm = function () {
    var form = buildForm(

        'user/add',

        [
            {
                text: '提交',
                handler: function (button) {
                    commitForm(form, function () {
                        form.ownerCt.close();
                        Ext.getCmp('userManagerGrid').getStore().reload();
                    })
                }
            },
            {
                text: '重置',
                handler: function (button) {
                    form.getForm().reset();
                }
            }
        ],

        [
            {
                fieldLabel: '用户名',
                name: 'userName',
                allowBlank: false,
                emptyText: '用户名或邮箱或者手机号(密码会使用后六位, 请保证长度够)',
                minLength: 2,
                minLengthText: '长度最少2位！',
                maxLength: 40,
                maxLengthText: '长度最多20位！',
                regex: /^\S+$/
            }
            /*,
            {
                fieldLabel: '新密码',
                name: 'password',
                minLength: 6,
                minLengthText: '密码长度最少6位！',
                maxLength: 40,
                maxLengthText: '密码长度最多20位！',
                inputType: 'password',
                allowBlank: false,
                id: 'onePass'
            },
            {
                fieldLabel: '确认密码',
                name: 'password1',
                minLength: 6,
                minLengthText: '密码长度最少6位！',
                maxLength: 40,
                maxLengthText: '密码长度最多20位！',
                inputType: 'password',
                allowBlank: false,
                vtype: 'password',
                initialPassField: 'onePass'
            }
            */
        ]);

    return form;
};


var getGradeConfigForm = function () {
    var form = buildForm('user/changeGradeRule', [
        {
            text: '提交',
            handler: function (button) {
                commitForm(form, function () {
                    form.getForm().reset();
                    form.ownerCt.close();
                });
            }
        },
        {
            text: '重置',
            handler: function () {
                form.getForm().reset();
            }
        }
    ], [

        {
            xtype: 'combo',
            hiddenName: 'grade',
            fieldLabel: '选择等级',
            mode: 'local',
            editable: false,
            triggerAction: 'all',
            emptyText: '请选择',
            store: new Ext.data.ArrayStore({
                fields: [ 'type', 'value' ],
                data: [
                    [ '一级', 'A' ],
                    [ '二级', 'B' ],
                    [ '三级', 'C' ],
                    [ '四级', 'D'],
                    [ '五级', 'E'],
                    [ '六级', 'F']
                ]
            }),
            listeners: {
                select: function (field) {
                    doAjax('user/getGradeRule', function (obj) {
                        form.getForm().setValues(obj.data.rule);
                    }, {
                        grade: field.getValue()
                    });

                }
            },
            displayField: 'type',
            valueField: 'value',
            allowBlank: false
        },
        {
            fieldLabel: '等级名称',
            name: 'name',
            allowBlank: false
        },
        {
            fieldLabel: '累计消费金额',
            xtype: 'numberfield',
            name: 'totalExpense',
            allowBlank: false
        },
        {
            fieldLabel: '一次性消费金额',
            xtype: 'numberfield',
            name: 'onceExpense',
            allowBlank: false
        },
        {
            fieldLabel: '评价比例',
            xtype: 'numberfield',
            name: 'valuationRatio',
            allowBlank: false
        },
        {
            fieldLabel: '等级图标',
            name: 'gradePic',
            allowBlank: false
        },
        {
            fieldLabel: '等级描述',
            xtype: 'htmleditor',
            name: 'gradeDescription'
        }

    ]);

    return form;
};

