/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 下午2:14
 */
Ext.define('Supplier.controller.User', {
    extend: 'Ext.app.Controller',
    views: ['user.List', 'user.Add', 'user.Log'],
    stores: ['UserList', 'UserLog'],
    models: ['UserList', 'UserLog'],
    init: function () {
        this.control({
            '#userListGrid': {
                'selectionchange': this.disabledListDeleteBtn
            },
            'userList #deleteBtn': {
                click: this.deleteUser
            },
            'userList #searchBtn': {
                click: this.searchUser
            },
            'userList #addBtn': {
                click: this.openAddWin
            },
            'userList #reloadBtn': {
                click: this.reloadGrid
            },
            'userAdd #addBtn': {
                click: this.saveUser
            },
            'userAdd #resetBtn': {
                click: this.resetForm
            },
            '#userLogGrid': {
                'selectionchange': this.disabledLogDeleteBtn
            },
            'userLog #deleteBtn': {
                click: this.deleteLog
            },
            'userLog #searchBtn': {
                click: this.searchLog
            }
        });
    },

    // 获取已选择行
    getGridSel: function (id) {
        return Ext.getCmp(id).getSelectionModel().getSelection();
    },

    // 返回已选择行字段的数组
    getGridSelType: function (id, type) {
        var sels = this.getGridSel(id),
            arr = [];

        Ext.each(sels, function (sel) {
            sel && arr.push(sel.get(type));
        });
        return arr;
    },

    // 如果有行被选择，设置按钮为可用，否则反之
    disabledBtn: function (selected, btn) {
        if (selected.length > 0) {
            btn.setDisabled(false);
        } else {
            btn.setDisabled(true);
        }
    },

    // 设置按钮（删除已选用户）是否可用
    disabledListDeleteBtn: function (view, selected) {
        var btn = Ext.getCmp('userList').down("button[itemId=deleteBtn]");
        this.disabledBtn(selected, btn);
    },

    // 删除用户
    deleteUser: function () {
        var url = '/account/batchDelete',
            ids = this.getGridSelType('userListGrid', 'id');

        if (ids.length < 1) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请先选择要删除的用户',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {
                ids: ids.join()
            },
            success: function (response) {
                var data = Ext.JSON.decode(response.responseText);
                if (data.success) {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: '删除成功',
                        buttons: Ext.MessageBox.OK,
                        icon: 'x-message-box-info'
                    });
                    Ext.getCmp('userListGrid').getStore().load();
                } else {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: data.msg,
                        buttons: Ext.MessageBox.OK,
                        icon: 'x-message-box-error'
                    });
                }
            }
        });
    },

    // 刷新用户列表
    reloadGrid: function () {
        Ext.getCmp('userListGrid').getStore().load();
    },

    // 用户筛选
    searchUser: function (button) {
        var form = button.up('form'),
            formData = form.getValues();

        if (formData.accountName.length < 1) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请输入用户名',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        Ext.getCmp("userListGrid").getStore().load({
            params: button.up('form').getValues()
        });

    },

    // 打开添加用户窗口
    openAddWin: function () {
        Ext.getCmp('userAdd') ? Ext.getCmp('userAdd').show() : Ext.widget('userAdd').show();
        Ext.getCmp('userAddForm').getForm().reset();
    },

    // 保存新添加的用户
    saveUser: function (button) {
        var form = button.up('form').getForm(),
            formData = form.getValues(),
            url = '/account',
            data;

        if (form.isValid()) {
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                params: formData,
                success: function (response) {
                    data = Ext.JSON.decode(response.responseText);

                    if (data.success) {
                        form.reset();
                        Ext.getCmp("userListGrid").getStore().load();
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '添加用户成功，继续添加',
                            buttons: Ext.MessageBox.YESNO,
                            icon: 'x-message-box-info',
                            fn: function (btn) {
                                if (btn === 'no') {
                                    Ext.getCmp('userAdd').hide();
                                }
                            }
                        });
                    } else {
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: data.msg,
                            buttons: Ext.MessageBox.OK,
                            icon: 'x-message-box-error'
                        });
                    }
                }
            });
        }
    },

    // 重置表单
    resetForm: function (button) {
        button.up('form').getForm().reset();
    },

    // 设置按钮（删除已选日志）是否可用
    disabledLogDeleteBtn: function (view, selected) {
        var btn = Ext.getCmp('userLog').down("button[itemId=deleteBtn]");
        this.disabledBtn(selected, btn);
    },

    // 删除用户日志
    deleteLog: function () {
        var url = '/log/delete',
            ids = this.getGridSelType('userLogGrid', 'id');

        if (ids.length < 1) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请先选择要删除的日志',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {
                ids: ids.join()
            },
            success: function (response) {
                var data = Ext.JSON.decode(response.responseText);
                if (data.success) {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: '删除成功',
                        buttons: Ext.MessageBox.OK,
                        icon: 'x-message-box-info'
                    });
                    Ext.getCmp("userLogGrid").getStore().load();
                } else {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: data.msg,
                        buttons: Ext.MessageBox.OK,
                        icon: 'x-message-box-error'
                    });
                }
            }
        });
    },

    // 用户日志筛选
    searchLog: function (button) {
        var form = button.up('form'),
            formData = form.getValues();

        if (formData.content.length < 1) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请输入日志内容',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        Ext.getCmp("userLogGrid").getStore().load({
            params: button.up('form').getValues()
        });

    }
});