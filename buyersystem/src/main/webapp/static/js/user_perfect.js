// 取字符串长度
function strLength(str) {
    return str.replace(/[^\x00-\xff]/g, "rr").length;
}

var validateRegExp = {
    email: "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", //邮件
    notempty: "^\\S+$", //非空
    password: "^[A-Za-z0-9_-]+$", //密码
    username: "^[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+$", //用户名
    realname: "^[A-Za-z\\u4e00-\\u9fa5]+$", // 真实姓名
    birthday: "^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$",
    phoneNumber: "(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)" //电话号码
};
var validateRules = {
    isNull: function (str) {
        return (str == "" || typeof str != "string");
    },
    isUid: function (str) {
        return new RegExp(validateRegExp.username).test(str);
    },
    isEmail: function (str) {
        return new RegExp(validateRegExp.email).test(str);
    },
    isRealname: function (str) {
        return new RegExp(validateRegExp.realname).test(str);
    },
    isBirthday: function (str) {
        return new RegExp(validateRegExp.birthday).test(str);
        //return /\d{4}-(((0[1-9])|(1[0-2])))(-((0[1-9])|([1-2][0-9])|(3[0-1])))?/.test(str)
    },
    isPhoneNumber: function (str) {
        return new RegExp(validateRegExp.phoneNumber).test(str);
        //return /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/.test(str)
    }
};

//logger(validateRules.isBirthday("2011-01-31"));
//logger(validateRules.isPhoneNumber("13800138000"));
var validatePrompt = {
    userName: {
        onFocus: "4-20位字符，可由中文、英文、数字及\"_\"、\" - \"组成",
        isNull: "请输入用户名",
        error: {
            beUsed: "该用户名已被使用，请使用其它用户名注册",
            badLength: "用户名长度只能在4-20位字符之间",
            badFormat: "用户名只能由中文、英文、数字及“_”、“-”组成",
            fullNumberName: "用户名不能全为数字"
        }
    },
    mail: {
        onFocus: "请输入常用的邮箱，将用来找回密码",
        succeed: "",
        isNull: "请输入邮箱",
        error: {
            beUsed: "该邮箱已被使用，请更换其它邮箱",
            badFormat: "请输入有效的邮箱地址",
            badLength: "您填写的邮箱过长，邮箱地址只能在50个字符以内"
        }
    },
    realname: {
        error: {
            badFormat: "姓名只能由中文、英文组成",
            badLength: "姓名长度只能在4-20位字符之间"
        }
    },
    birthday: {
        error: {
            badFormat: "您填写的出生日期格式不对"
        }
    },
    phoneNumber: {
        error: {
            badFormat: "您填写的电话号码格式不对"
        }
    },
    password: {
        isNull: "请输入密码",
        error: "密码长度必须在6至16个字符之间"
    },
    confirmPass: {
        error: "您两次输入的密码不一致"
    }
};

function perfectInformationValidate() {

    var userName = $("#userName"),
        email = $("#email"),
        password = $("#password"),
        confirmPass = $("#confirmPass"),
        name = $("#name"),
        phoneNumber = $("#phoneNumber");
    var userNameInfo = $("#userName_error"),
        emailInfo = $("#email_error"),
        passwordInfo = $("#password_error"),
        confirmPassInfo = $("#confirmPass_error"),
        nameInfo = $("#name_error"),
        phoneNumberInfo = $("#phoneNumber_error");

    var Info = $("#formInfo");

    // 验证用户名
    var isUsername = false,
        usernameType = 'default';

    var checkUserName = function () {
        isUsername = false;
        var userNameVal = $.trim(userName.val()),
            checkUserNameVal = $.trim($("#checkUserName").val());

        if (validateRules.isNull(userNameVal)) {
            userNameInfo.text(validatePrompt.userName.isNull);
        } else {

            if (validateRules.isEmail(userNameVal) || validateRules.isPhoneNumber(userNameVal)) {
                usernameType = 'email_phone';
            } else {
                usernameType = 'default';
            }

            // 用户名未改变直接返回
            if (checkUserNameVal === userNameVal) {
                isUsername = true;
                return;
            }

            if (strLength(userNameVal) < 4 || strLength(userNameVal) > 20) {
                userNameInfo.text(validatePrompt.userName.error.badLength);
                return;
            }

            if (!validateRules.isUid(userNameVal)) {
                userNameInfo.text(validatePrompt.userName.error.badFormat);
                return;
            }

            // 检查用户名是否存在
            var _url = $("input[name=reg_un_url]").eq(0).val();
            if (!!_url) {
                $.ajax({
                    type: "POST",
                    async: false,
                    url: _url,
                    data: {
                        userName: userNameVal
                    },
                    dataType: "json",
                    success: function (Data) {
                        if (Data["success"]) {
                            userNameInfo.text("");
                            isUsername = true;
                        } else {
                            userNameInfo.text(Data["msg"]);
                        }
                    }
                });
            } else {
                userNameInfo.text("");
                isUsername = true;
            }

        }
    };

    if (userName.length > 0) {
        userName.blur(function () {
            checkUserName();
        });
    } else {
        checkUserName = function () {
            isUsername = true;
        };
    }

    // 验证邮箱
    var isMail = false;
    var checkEmail = function () {
        isMail = false;
        var emailVal = $.trim(email.val());
        if (validateRules.isNull(emailVal)) {

            emailInfo.text(validatePrompt.mail.isNull)//.hide();

        } else if (!validateRules.isEmail(emailVal)) {
            emailInfo.text(validatePrompt.mail.error.badFormat).show();
        } else if (strLength(emailVal) > 50) {
            emailInfo.text(validatePrompt.mail.error.badLength).show();
        } else {
            // 检查邮箱是否已经注册过
            //logger("email:true");
            isMail = true;
            emailInfo.text("").hide();
            var _url = $("input[name=reg_mail_url]").eq(0).val();
            if (!!_url) {
                $.ajax({
                    type: "POST",
                    async: false,
                    url: _url,
                    data: {
                        email: emailVal
                    },
                    dataType: "json",
                    success: function (Data) {
                        if (Data["success"]) {
                            emailInfo.text("").hide();

                            isMail = true;
                        } else {
                            emailInfo.text(Data["msg"]);

                        }
                    }
                });
            } else {

                userNameInfo.text("").hide();
                isMail = true;
            }
        }
    };

    if (email.length > 0) {
        email.blur(function () {
            checkEmail();
        });
    } else {
        checkEmail = function () {
            isMail = true;
        };
    }

    var isPassword = false;
    var checkPassword = function () {
        var passwordVal = $.trim(password.val());

        if (validateRules.isNull(passwordVal)) {
            passwordInfo.text(validatePrompt.password.isNull);
        } else if (passwordVal.length < 6 || passwordVal.length > 16) {
            passwordInfo.text(validatePrompt.password.error);
        } else {
            isPassword = true;
            passwordInfo.text("");
        }

    };

    if (password.length > 0) {
        password.blur(function () {
            checkPassword();
        });
    } else {
        checkPassword = function () {
            isPassword = true;
        };
    }

    var isConfirmPass = false;
    var checkConfirmPass = function () {
        var passwordVal = $.trim(password.val()),
            confirmPassVal = $.trim(confirmPass.val());

        if (confirmPassVal !== passwordVal) {
            confirmPassInfo.text(validatePrompt.confirmPass.error);
        } else {
            isConfirmPass = true;
            confirmPassInfo.text("");
        }
    };

    if (password.length > 0) {
        confirmPass.blur(function () {
            checkConfirmPass();
        });
    } else {
        checkConfirmPass = function () {
            isConfirmPass = true;
        };
    }

    // 验证姓名
    var isName = false;
    var checkName = function () {
        isName = false;
        var nameVal = $.trim(name.val());
        var nameLength = strLength(nameVal);
        if (validateRules.isNull(nameVal)) {
            name.val("");
            isName = true;
        } else if (nameLength > 20 || nameLength < 4) {
            nameInfo.text(validatePrompt.realname.error.badLength);
        } else if (!validateRules.isRealname(nameVal)) {
            nameInfo.text(validatePrompt.realname.error.badFormat);
        } else {
            nameInfo.text("");
            isName = true;
        }

    };
    name.blur(function () {
        checkName();
    });

    // 联系电话
    var isPhoneNumber = false;
    var checkPhoneNumber = function () {
        isPhoneNumber = false;
        var phoneNumberVal = $.trim(phoneNumber.val());
        if (validateRules.isNull(phoneNumberVal)) {
            phoneNumber.val("");
            isPhoneNumber = true;
        } else if (!validateRules.isPhoneNumber(phoneNumberVal)) {
            phoneNumberInfo.text(validatePrompt.phoneNumber.error.badFormat).show();
        } else {
            phoneNumberInfo.text("").hide();
            isPhoneNumber = true;
        }
    };
    phoneNumber.blur(function () {
        checkPhoneNumber();
    });


    var clearInput = function (o, p) {
        if (o) {
            var parent = o.parents("li"),
                val;
            if (p === "parent") {
                parent.remove();
            } else {
                val = o.val();
                parent.find("input").remove();
                parent.find("span").remove();
                parent.append('<span class="val">' + val + '</span>');
            }
        }
    };

    var Form = $("#bindingAccountForm");
    Form.submit(function (e) {
        e.preventDefault();
        checkUserName();
        checkEmail();
        checkPassword();
        checkConfirmPass();
        checkName();
        checkPhoneNumber();
        if (isUsername && isMail && isPassword && isConfirmPass && isName && isPhoneNumber) {
            var url = Form.attr("action");
            $.ajax({
                type: "POST",
                url: url,
                data: Form.serialize(),
                dataType: 'json',
                success: function (data) {


                    if (data.success) {
                        var data =  data.data;
                        var htmlBox = $('<span class="error"></span>')
                        var html = '首次完善资料,已赠送您'
                            + '<em>' + data.registerCount + '</em><a href="">积分</a>(1积分=1元)';
                        htmlBox.attr('id','firstTip').html(html).appendTo('.btn_w');
                        $('#firstTip').find('a').attr('href',EJS.MyPoint);

                        Info.html("");
                        if (userName.length > 0) {
                            if (usernameType !== 'email_phone') {
                                clearInput(userName);
                            }
                            clearInput(password, "parent");
                            clearInput(confirmPass, "parent");
                        }

                        if (email.length > 0) {
                            clearInput(email);
                        }

                        if(data.firstUpdate){
                            $('#firstTip').fadeIn().css({display:"inline-block"});//setTimeout
                        }else {
                            Ejs.tip(Form.find(".btn_3"),"tip_change_password", "帐号设置成功", 0, 90);
                            $('#firstTip').fadeOut().css({display:"none"})
                        }

                    } else {
                        Info.html(data.msg);
                    }
                }
            });
        }
    });
};