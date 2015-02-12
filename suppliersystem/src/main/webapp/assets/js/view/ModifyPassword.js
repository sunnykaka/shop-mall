/**
 * 修改密码 View
 * User: Hualei Du
 * Date: 13-10-9
 * Time: 下午5:54
 */

Ext.define('Supplier.view.ModifyPassword', {
    extend: 'Ext.window.Window',
    alias: 'widget.modifyPassword',
    id: 'modifyPassword',
    title: '修改密码',
    iconCls: 'icon-password',
    //collapsible: true,
    //maximizable: true,
    width: 360,
    height: 180,
    fixed: true,
    layout: 'fit',
    modal: true,
    initComponent: function () {

        this.items = [
            Ext.create('Ext.form.Panel', {
                forceFit: true,
                border: false,
                layout: 'form',
                //url: 'save-form.php',
                header: false,
                frame: false,
                bodyPadding: '5 5 0',
                //requires: ['Ext.form.field.Text'],
                fieldDefaults: {
                    msgTarget: 'side',
                    labelWidth: 75
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: '原密码',
                        name: 'oldPassword',
                        itemId: 'oldPassword',
                        allowBlank: false,
                        blankText: '不能为空',
                        inputType: 'password'
                    },
                    {
                        fieldLabel: '新密码',
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
                        itemId: 'saveBtn',
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
