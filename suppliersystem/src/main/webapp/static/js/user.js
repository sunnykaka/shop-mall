$(function () {
    'use strict';

    $('#btn_cancel').click(function () {
        window.parent.pageHander.Dialog.close();
    });

    function isEmpty(str) {
        return !!(!!str.length < 1);
    }

    function tip(str) {
        var obj = $("#user_tip");
        obj.html(str).stop().slideDown(600);
        window.setTimeout(function () {
            obj.stop().slideUp(200, function () {
                obj.html("");
            });
        }, 1200);
    }

    function ajaxPost(url, data, callBack) {
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: url,
            data: data,
            success: function (resData) {
                if (resData['success']) {
                    callBack();
                } else {
                    tip(resData['msg']);
                }
            },
            error: function () {
                tip("服务端出错了~~");
            }
        });
    }

    // 添加用户
    $('#addUserForm').submit(function (e) {
        e.preventDefault();

        var accountName = $("input[name=accountName]"),
            email = $("input[name=email]"),
            password = $("input[name=password]"),
            rePassword = $("input[name=rePassword]");

        if (isEmpty(accountName.val())) {
            tip("请填写用户名");
            accountName.focus();
            return;
        }

        if (isEmpty(email.val())) {
            tip("请填写邮箱");
            email.focus();
            return;
        }

        if (!/^[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?$/.test($.trim(email.val()))) {
            tip("邮箱格式不正确");
            email.focus();
            return;
        }

        if (isEmpty(password.val())) {
            tip("请填写密码");
            password.focus();
            return;
        }

        if (password.val() !== rePassword.val()) {
            rePassword.focus();
            tip("两次密码不一致");
            return;
        }

        var url = $(this).attr("action"),
            data = $(this).serialize();

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("添加成功!");
            window.parent.location.reload();
        });

    });

    // 分配角色
    $('#updateUserForm').submit(function (e) {
        e.preventDefault();
        var url = $(this).attr("action"),
            data = $(this).serialize();

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("更新成功!");
            window.parent.location.reload();
        });
    });

    // 添加角色
    $('#addRoleForm').submit(function (e) {
        e.preventDefault();
        var url = $(this).attr("action"),
            data = $(this).serialize();

        var roleName = $("input[name=roleName]");
        if (isEmpty(roleName.val())) {
            tip("请填写角色名称");
            roleName.focus();
            return;
        }

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("添加成功!");
            window.parent.location.reload();
        });
    });

    // 修改角色
    $('#editRoleForm').submit(function (e) {
        e.preventDefault();
        var url = $(this).attr("action"),
            data = $(this).serialize();

        var roleName = $("input[name=roleName]");
        if (isEmpty(roleName.val())) {
            tip("请填写角色名称");
            roleName.focus();
            return;
        }

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("编辑成功!");
            window.parent.location.reload();
        });
    });



    function checkCompetenceForm(){
        var permissionName = $("input[name=permissionName]"),
            resource = $("input[name=resource]"),
            path = $("input[name=path]"),
            category = $("input[name=category]");

        if (isEmpty(permissionName.val())) {
            permissionName.focus();
            tip("请填写权限名");
            return false;
        }
        if (isEmpty(resource.val())) {
            resource.focus();
            tip("请填写资源名");
            return false;
        }
        if (isEmpty(path.val())) {
            path.focus();
            tip("请填写权限路径");
            return false;
        }
        if (isEmpty(category.val())) {
            category.focus();
            tip("请填写权限类别");
            return false;
        }
        return true;
    }

    // 添加权限
    $('#addCompetenceForm').submit(function (e) {
        e.preventDefault();

        if(!checkCompetenceForm()){
            return;
        }

        var url = $(this).attr("action"),
            data = $(this).serialize();

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("添加成功!");
            window.parent.location.reload();
        });

    });

    // 修改权限
    $('#editCompetenceForm').submit(function (e) {
        e.preventDefault();

        if(!checkCompetenceForm()){
            return;
        }

        var url = $(this).attr("action"),
            data = $(this).serialize();

        // 发送到服务器
        ajaxPost(url, data, function () {
            alert("编辑成功!");
            window.parent.location.reload();
        });

    });

});

