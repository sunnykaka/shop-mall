$(function () {
    // 取字符串长度
    function strLength(str) {
        return str.replace(/[^\x00-\xff]/g, "rr").length;
    }

    var oldPwd = $("#oldPassword"),
        Pwd = $("#newPassword"),
        Pwd2 = $("#password");

    var Info = $("#sub_info"),
        oldPwdInfo = $("#oldPWD_info"),
        newPwdInfo = $("#newPWD_info"),
        rePwdInfo = $("#rePWD_info");

    var isPwd = false,
        isOldPwd = false,
        isPwd2 = false;

    Info.addClass("error");
    oldPwdInfo.addClass("error");
    newPwdInfo.addClass("error");
    rePwdInfo.addClass("error");

    var checkOldPwd = function () {
        isOldPwd = false;
        var oldPwdVal = $.trim(oldPwd.val());
        if (oldPwdVal.length < 1) {
            oldPwdInfo.html("原密码不能为空");
        } else if (strLength(oldPwdVal) < 6 || strLength(oldPwdVal) > 16) {
            oldPwdInfo.html("原密码长度只能在6-16位字符之间");
        } else {
            oldPwdInfo.html("");
            isOldPwd = true;
        }
    };

    oldPwd.blur(function () {
        checkOldPwd();
    });

    var checkPwd = function () {
        isPwd = false;
        var PwdVal = $.trim(Pwd.val());
        if (PwdVal.length < 1) {
            newPwdInfo.html("新密码不能为空");
        } else if (strLength(PwdVal) < 6 || strLength(PwdVal) > 16) {
            newPwdInfo.html("新密码长度只能在6-16位字符之间");
        } else {
            newPwdInfo.html("");
            isPwd = true;
        }
    };

    Pwd.blur(function () {
        checkPwd();
    });

    var checkPwd2 = function () {
        isPwd2 = false;
        var PwdVal = $.trim(Pwd.val());
        var Pwd2Val = $.trim(Pwd2.val());
        if (PwdVal !== Pwd2Val) {
            rePwdInfo.html("两次输入密码不一致");
        } else {
            rePwdInfo.html("");
            isPwd2 = true;
        }
    };

    Pwd2.blur(function () {
        checkPwd2();
    });

    var Form = $("#changePasswordForm");

    Form.submit(function (e) {
        e.preventDefault();
        checkOldPwd();
        checkPwd();
        checkPwd2();
        if (isOldPwd && isPwd && isPwd2) {
            var url = Form.attr("action");
            $.ajax({
                type: "POST",
                url: url,
                data: Form.serialize(),
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        Form[0].reset();
                        Ejs.tip(Form.find(".btn_3"), "tip_change_password", "密码修改成功", 0, 90);
                    } else {
                        $("#sub_info").html(data.msg);
                    }
                }
            });
        }
    });

});
