/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 下午2:35
 */
Ext.define('Supplier.view.user.Add', {
    extend: 'Ext.window.Window',
    alias: 'widget.userAdd',
    title: '添加新用户',
    id: 'userAdd',
    modal: true,
    closeAction: 'destroy',
    autoShow: false,
    layout: 'fit',
    width: 320,
    height: 220,
    initComponent: function () {

        Ext.tip.QuickTipManager.init();

        this.items = [
            Ext.create('Ext.form.Panel', {
                store: 'Shop',
                id: 'userAddForm',
                forceFit: true,
                border: false,
                layout: 'form',
                //url: 'save-form.php',
                header: false,
                frame: false,
                bodyPadding: '5 5 0',
                requires: ['Ext.form.field.Text'],
                fieldDefaults: {
                    msgTarget: 'side',
                    labelWidth: 75
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: '用户名',
                        name: 'accountName',
                        itemId: 'accountName',
                        allowBlank: false,
                        blankText: '不能为空'
                    },
                    {
                        fieldLabel: '邮箱',
                        name: 'email',
                        itemId: 'email',
                        allowBlank: false,
                        blankText: '不能为空',
                        vtype: "email",
                        vtypeText: "不是有效的邮箱地址"
                    },
                    {
                        fieldLabel: '密码',
                        name: 'password',
                        itemId: 'password',
                        allowBlank: false,
                        blankText: '不能为空',
                        inputType: 'password',
                        validator: function (value) {
                            return (value.length >= 6 || value.length < 1) ? true : '密码长度不能少于6位';
                        }
                    },
                    {
                        fieldLabel: '确认密码',
                        name: 'affirmPassword',
                        itemId: 'affirmPassword',
                        allowBlank: false,
                        blankText: '不能为空',
                        inputType: 'password',
                        validator: function (value) {
                            var password = this.previousSibling('[name=password]');
                            if (value.length < 6 && value.length > 0) {
                                return '密码长度不能少于6位';
                            }
                            if (value !== password.getValue() && value.length > 0) {
                                return '两次输入的密码不一样';
                            }
                            return true;
                        }
                    }
                ],

                buttons: [
                    {
                        text: '保存',
                        itemId: 'addBtn',
                        disabled: true,
                        formBind: true
                    },
                    {
                        text: '重写',
                        itemId: 'resetBtn'
                    }
                ]
            })
        ];

        this.callParent(arguments);
    }
});