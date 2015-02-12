var Ejs = window.Ejs || {};

var validateRegExp = {
    decmal: "^([+-]?)\\d*\\.\\d+$",
    //浮点数
    decmal1: "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$",
    //正浮点数
    decmal2: "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$",
    //负浮点数
    decmal3: "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$",
    //浮点数
    decmal4: "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$",
    //非负浮点数（正浮点数 + 0）
    decmal5: "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$",
    //非正浮点数（负浮点数 + 0）
    intege: "^-?[1-9]\\d*$",
    //整数
    intege1: "^[1-9]\\d*$",
    //正整数
    intege2: "^-[1-9]\\d*$",
    //负整数
    num: "^([+-]?)\\d*\\.?\\d+$",
    //数字
    num1: "^[1-9]\\d*|0$",
    //正数（正整数 + 0）
    num2: "^-[1-9]\\d*|0$",
    //负数（负整数 + 0）
    ascii: "^[\\x00-\\xFF]+$",
    //仅ACSII字符
    chinese: "^[\\u4e00-\\u9fa5]+$",
    //仅中文
    color: "^[a-fA-F0-9]{6}$",
    //颜色
    date: "^\\d{4}(\\-|\\/|\.)\\d{1,2}\\1\\d{1,2}$",
    //日期
    email: "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$",
    //邮件
    idcard: "^[1-9]([0-9]{14}|[0-9]{17})$",
    //身份证
    ip4: "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$",
    //ip地址
    letter: "^[A-Za-z]+$",
    //字母
    letter_l: "^[a-z]+$",
    //小写字母
    letter_u: "^[A-Z]+$",
    //大写字母
    mobile: "^0?(13|15|18)[0-9]{9}$",
    //手机
    notempty: "^\\S+$",
    //非空
    password: "^[A-Za-z0-9_-]+$",
    //密码
    picture: "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$",
    //图片
    qq: "^[1-9]*[1-9][0-9]*$",
    //QQ号码
    rar: "(.*)\\.(rar|zip|7zip|tgz)$",
    //压缩文件
    tel: "^[0-9\-()（）]{7,18}$",
    //电话号码的函数(包括验证国内区号,国际区号,分机号)
    url: "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$",
    //url
    username: "^[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+$",
    //用户名
    deptname: "^[A-Za-z0-9_()（）\\-\\u4e00-\\u9fa5]+$",
    //单位名
    zipcode: "^\\d{6}$",
    //邮编
    realname: "^[A-Za-z\\u4e00-\\u9fa5]+$",
    // 真实姓名
    companyname: "^[A-Za-z0-9_()（）\\-\\u4e00-\\u9fa5]+$",
    companyaddr: "^[A-Za-z0-9_()（）\\#\\-\\u4e00-\\u9fa5]+$",
    companysite: "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&#=]*)?$"
};

getImageCode = function (obj) {
    if (typeof obj == "object") {
        obj.src = $("input[name='image_code_url']").val() + '?d=' + Math.random();
    } else {
        document.getElementById(obj).src = $("input[name='image_code_url']").val() + '?d=' + Math.random();
    }
};

Ejs.Users = function (_type) {
    if (typeof _type == 'undefined' || _type == '') {
        return false;
    }
    switch (_type) {
        case 'Login':
        {
            this.regCheckLoginFormEvent();
            break;
        }
        case 'FindPWD':
        {
            this.regCheckFindPWDEvent();
            break;
        }
        case 'Register':
        {
            this.regCheckRegisterEvent();
            break;
        }
        case 'resetPassword':
        {
            this.regResetPasswordEvent();
            break;
        }
        case 'loginPage':
        {
            this.regLoginPageEvent();
            break;
        }
    }
};

Ejs.Users.prototype = {
    regCloseEvent:function () {
        var _root = window.parent.window.Ejs.UserWindows;
        $(".com_login_register_widow .close").click(function (e) {
            e.preventDefault();
            _root.remove();
        });
    },
    regCheckLoginFormEvent:function () { //会员登录验证
        $("#log_ok").submit(function (e) {
            //e.preventDefault();
            var _url = $("input[name='log_submit_url']").val();
            if ($("#log_un").val() == "") {
                $("#log_info").text("请输入你的用户名!");
                $("#log_un").focus();
                return false;
            }
            if ($("#log_code").val() == "") {
                $("#log_info").text("请输入你的登陆密码!");
                $("#log_code").focus();
                return false;
            }
            if ($("#log_c").val() == "") {
                $("#log_info").text("请输入验证码!");
                $("#log_c").focus();
                return false;
            }

            $(".reuse_login_register_login_btn").hide();
            $(".reuse_login_register_login_btn_disable").css("display", "inline-block");
        });
    },
    regCheckFindPWDEvent:function () { //找回密码验证

        var fp_email = $("#fp_email"),
            fp_code = $("#fp_code");

        var fp_email_info = $("#fp_email_error"),
            fp_code_info = $("#fp_code_error");

        var is_fp_email = false,
            is_fp_code = false;

        var is_fp_email_text = "",
            is_fp_code_text = "";

        fp_email.focus(function () {
            fp_email_info.removeClass("error").addClass("focus").text("请输入您注册时填写的邮箱");
            fp_email.addClass("highlight");
            is_fp_email = false
        });
        fp_email.blur(function () {
            var _hr_emali = /\w@\w*\.\w/;
            if (!_hr_emali.test($.trim(fp_email.val()))) {
                is_fp_email_text = "请填写正确的Email地址";
                fp_email_info.removeClass("focus").addClass("error").text(is_fp_email_text);
                $("#fp_email_succeed").removeClass("succeed").addClass("warning");
                fp_email.addClass("highlight");
                is_fp_email = false;
            }else{
                var _url = $("input[name='reg_mail_url']").val();
                $.ajax({
                    type: "POST",
                    async: false,
                    url: _url,
                    data:{
                        email: fp_email.val()
                    },
                    dataType: "json",
                    success: function (_e) {
                        if (_e["success"]) {
                            is_fp_email_text = _e["msg"];
                            fp_email_info.removeClass("focus").addClass("error").text();
                            $("#fp_email_succeed").removeClass("succeed").addClass("warning");
                            fp_email.addClass("highlight");
                            is_fp_email = false;
                        } else {
                            is_fp_email_text = "";
                            fp_email_info.removeClass("focus").removeClass("error").text("");
                            $("#fp_email_succeed").removeClass("warning").addClass("succeed");
                            fp_email.removeClass("highlight");
                            is_fp_email = true;
                        }

                    }
                });
            }
        });

        fp_code.focus(function () {
            fp_code_info.removeClass("error").addClass("focus").text("请输入图片中的字符，不分大小写");
            is_fp_code = false
        });
        fp_code.blur(function () {
            if (fp_code.val() == "") {
                is_fp_code_text = "请输入验证码";
                fp_code_info.removeClass("focus").addClass("error").text(is_fp_code_text);
                $("#fp_code_succeed").removeClass("succeed");
                fp_code.addClass("highlight");
                is_fp_code = false;
            } else {
                var _url = $("input[name='reg_code_url']").val();
                $.ajax({
                    type: "POST",
                    async: false,
                    url: _url,
                    data:{
                        imageCode: fp_code.val()
                    },
                    dataType: "json",
                    success:function (_e) {
                        if (_e["success"]) {
                            is_fp_code_text = "";
                            fp_code_info.removeClass("focus").addClass("error").text("");
                            $("#fp_code_succeed").removeClass("warning").addClass("succeed");
                            fp_code.removeClass("highlight");
                            is_fp_code = true;
                        } else {
                            is_fp_code_text = _e["msg"];
                            fp_code_info.removeClass("focus").addClass("error").text(is_fp_code_text);
                            $("#fp_code_succeed").removeClass("succeed").addClass("warning");
                            fp_code.addClass("highlight");
                            is_fp_code = false;
                        }
                    }
                });
            }
        });

        $("#ForgotPasswordSubmit").submit(function (e) {
            e.preventDefault();
            $("#fp_error").text("");
            if (!is_fp_email) {
                fp_email.focus();
                fp_email_info.removeClass("focus").addClass("error").text(is_fp_email_text);
                $("#fp_email_succeed").removeClass("succeed").addClass("warning");
                return false;
            }else{
                fp_email_info.text("");
                $("#fp_email_succeed").removeClass("warning").addClass("succeed");
            }

            if (!is_fp_code) {
                fp_code_info.removeClass("error").addClass("focus").text(is_fp_code_text);
                $("#fp_code_succeed").removeClass("succeed").addClass("warning");
                return false;
            } else {
                fp_code_info.text("");
                $("#fp_code_succeed").removeClass("warning").addClass("succeed");
            }

            var checkEmailurl = EJS.CheckUserForRetakePassword;
            $.ajax({
                type: "POST",
                async: false,
                url: checkEmailurl,
                data:{
                    email: fp_email.val(),
                    code: fp_code.val()
                },
                dataType:'json',
                success:function (_e) {
                    if (_e["success"]) {
                        var HTML = '<div class="succeed_mask"></div>';
                            HTML += '<div class="rp_succeed_inner"><div class="succeed_text">';
                            HTML += "系统已成功发送一封验证邮件到您的邮箱，请注意查收！";
                        if(_e["data"]["backUrl"].length > 1){
                            HTML += '<a href="'+_e["data"]["backUrl"]+'" target="_blank">点此进入</a>';
                        }
                            HTML +='</div></div>';
                        $(".forgot_password_form").eq(0).hide();
                        $("#rp_succeed").show().html(HTML);
                    } else {
                        $("#fp_error").text(_e["msg"]);
                        getImageCode("fp_cimg");
                        return false;
                    }
                }
            });
        });
    },
    regCheckRegisterEvent:function () { //会员注册验证


        var reg_un = $("#reg_un"),
            reg_pw1 = $("#reg_pw1"),
            reg_pw2 = $("#reg_pw2"),
            reg_mail = $("#reg_mail"),
            reg_code = $("#reg_code");

        var un_info = $("#un_error"),
            pw1_info = $("#pw1_error"),
            pw2_info = $("#pw2_error"),
            mail_info = $("#mail_error"),
            code_info = $("#code_error");

        var is_reg_un = false,
            is_reg_mail = false,
            is_reg_pw = false,
            is_reg_code = false;
        var is_reg_un_text = "",
            is_reg_mail_text = "",
            is_reg_pw_text = "",
            is_reg_code_text = "";


        function checkReg() {
            if (!is_reg_un) {
                reg_un.focus();
                un_info.removeClass("focus").addClass("error").text(is_reg_un_text);
                return false;
            }
            if (!is_reg_pw) {
                reg_pw1.focus();
                pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                return false;
            }
            if (!is_reg_mail) {
                reg_mail.focus();
                mail_info.removeClass("focus").addClass("error").text(is_reg_mail_text);
                return false;
            }
            if (!is_reg_code) {
                reg_code.focus();
                code_info.removeClass("focus").addClass("error").text(is_reg_code_text);
                return false;
            }
            return true;
        }

        reg_un.focus(function () {
            un_info.removeClass("error").addClass("focus").text("4-20位字符，可由中文、英文、数字及“_”组成");
        });

        reg_un.blur(function () {
            if (reg_un.val() == "") {
                is_reg_un_text = "请输入您的用户名";
                un_info.removeClass("focus").addClass("error").text(is_reg_un_text);
                $("#un_succeed").removeClass("succeed").addClass("warning");
                is_reg_un = false;
            } else {
                var _url = $("input[name='reg_un_url']").val();
                $.ajax({
                    type:"POST",
                    async: true,
                    url: _url,
                    data:{
                        userName: reg_un.val()
                    },
                    dataType:"json",
                    success:function (_e) {
                        if (_e["success"]) {
                            un_info.text("");
                            is_reg_un = true;
                            is_reg_un_text = "";
                            $("#un_succeed").removeClass("warning").addClass("succeed");
                        } else {
                            un_info.removeClass("focus").addClass("error").text(_e["msg"]);
                            is_reg_un = false;
                            is_reg_un_text = _e["msg"];
                            $("#un_succeed").removeClass("succeed").addClass("warning");
                        }
                    }
                });
            }
        });

        reg_pw1.focus(function () {
            pw1_info.removeClass("error").addClass("focus").text("6-16位字符，可由英文、数字及“_”组成");
        });
        reg_pw1.blur(function () {
            if (reg_pw1.val() == "") {
                is_reg_pw_text = "请输入您的登陆密码!";
                pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                $("#pw1_succeed").removeClass("succeed").addClass("warning");
                is_reg_pw = false;
            } else {
                if (reg_pw1.val().length < 6) {
                    is_reg_pw_text = "密码不能少于6位!";
                    pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                    $("#pw1_succeed").removeClass("succeed").addClass("warning");
                    return false;
                }
                if (reg_pw1.val().length > 16) {
                    is_reg_pw_text = "密码不能大于16位!";
                    pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                    $("#pw1_succeed").removeClass("succeed").addClass("warning");
                    return false;
                }
                pw1_info.removeClass("focus").removeClass("error").text("");
                $("#pw1_succeed").removeClass("warning").addClass("succeed");
            }
        });

        reg_pw2.focus(function () {
            pw2_info.removeClass("error").addClass("focus").text("请再次输入密码");
        });
        reg_pw2.blur(function () {
            if (reg_pw2.val() == "") {
                is_reg_pw_text = "请再次输入您的登陆密码!";
                pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                $("#pw2_succeed").removeClass("succeed").addClass("warning");
                is_reg_pw = false;
            } else {
                if (reg_pw2.val().length < 6) {
                    is_reg_pw_text = "密码不能少于6位!";
                    pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                    $("#pw2_succeed").removeClass("succeed").addClass("warning");
                    return false;
                }
                if (reg_pw2.val().length > 16) {
                    is_reg_pw_text = "密码不能大于16位!";
                    pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                    $("#pw2_succeed").removeClass("succeed").addClass("warning");
                    return false;
                }

                if (reg_pw1.val() != reg_pw2.val()) {
                    is_reg_pw_text = "您两次输入的密码不一致!";
                    pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
                    is_reg_pw = false;
                    $("#pw2_succeed").removeClass("succeed").addClass("warning");
                    return false;
                } else {
                    is_reg_pw_text = "";
                    pw2_info.removeClass("focus").removeClass("error").text("");
                    is_reg_pw = true;
                    $("#pw2_succeed").removeClass("warning").addClass("succeed");
                    return false;
                }
            }
        });

        reg_mail.focus(function () {
            mail_info.removeClass("error").addClass("focus").text("请输入常用邮箱，将用来找回密码，接受订单等通知");
        });
        reg_mail.blur(function () {
            var _hr_emali = /\w@\w*\.\w/;
            if (!_hr_emali.test($.trim(reg_mail.val()))) {
                is_reg_mail_text = "请填写正确的E-mail地址!";
                mail_info.removeClass("focus").addClass("error").text(is_reg_mail_text);
                is_reg_mail = false;
                $("#mail_succeed").removeClass("succeed").addClass("warning");

            } else {
                var _url = $("input[name='reg_mail_url']").val();
                $.ajax({
                    type: "POST",
                    async: false,
                    url:_url,
                    data:{
                        email:reg_mail.val()
                    },
                    dataType: "json",
                    success: function (_e) {
                        if (_e["success"]) {
                            mail_info.removeClass("focus").removeClass("error").text("");
                            is_reg_mail_text = "";
                            $("#mail_succeed").removeClass("warning").addClass("succeed");
                            is_reg_mail = true;
                        } else {
                            mail_info.removeClass("focus").addClass("error").text(_e["msg"]);
                            is_reg_mail_text = _e["msg"];
                            $("#mail_succeed").removeClass("succeed").addClass("warning");
                            is_reg_mail = false;
                        }

                    }
                });

            }
        });

        reg_code.focus(function () {
            code_info.removeClass("error").addClass("focus").text("请输入图片中的字符，不分大小写");
        });
        reg_code.blur(function () {

            if (reg_code.val() == "") {
                is_reg_code_text = "请输入验证码";
                code_info.removeClass("focus").addClass("error").text(is_reg_code_text);
                $("#code_succeed").removeClass("succeed").addClass("warning");
                is_reg_code = false;
            } else {
                var _url = $("input[name='reg_code_url']").val().replace('http', 'https');
                $.ajax({
                    type:"POST",
                    async:false,
                    url:_url,
                    data:{
                        imageCode:reg_code.val()
                    },
                    dataType: "json",
                    success:function (_e) {
                        if (_e["success"]) {
                            code_info.removeClass("focus").addClass("error").text("");
                            is_reg_code = true;
                            $("#code_succeed").removeClass("warning").addClass("succeed");
                        } else {
                            code_info.removeClass("focus").addClass("error").text(_e["msg"]);
                            getImageCode("reg_cimg");
                            is_reg_code = false;
                            $("#code_succeed").removeClass("succeed").addClass("warning");

                        }
                    }
                });

            }
        });

        $("#reg_ok").submit(function (e) {
            e.preventDefault();
            if (!checkReg()) {
                return false;
            } else {
                $(".register_submit").eq(0).text("正在提交...")[0].setAttribute('type','button');
                $.ajax({
                    type:"POST",
                    url: $("#reg_ok").attr("action"),
                    data: $("#reg_ok").serialize(),
                    dataType: "json",
                    success:function (_e) {
                        if (_e["success"]) {
                            var _html = '<div class="succeed_mask"></div>'
                                      + '<div class="r_succeed_inner"><div class="succeed_text">'
                                      + "恭喜您,注册成功! 激活链接已发送到您的邮箱，请在30分钟内登录激活。"
                                      + _e["data"]["loginMailDom"]
                                      +'</div></div>';
                            $("#r_succeed").show().html(_html);
                        } else {
                            $("#reg_error").text(_e["msg"]);
                            $(".register_submit").eq(0).text("同意协议并注册")[0].setAttribute('type','submit');
                        }
                    }
                });

            }
            return false;
        });
    },
    setMaskLayer:function (obj, _textVal) {
        $("#countDownText").parent().remove();
        var _target = $(obj);
        var _mask = $('<div></div>');
        var _text = $('<h3 id="countDownText"></h3>');
        if (typeof _textVal != undefined) {
            _text.text(_textVal);
        }

        _text.css({
            color:"#fff",
            'font-size':'20px',
            position:'absolute',
            left:0,
            top:_target.outerHeight() / 2,
            'text-align':'center',
            width:_target.outerWidth(),
            'z-index':110
        });

        _mask.css({
            width:_target.outerWidth(),
            height:_target.outerHeight(),
            'z-index':106,
            'background-color':'#000',
            position:'absolute',
            left:0,
            top:0,
            opacity:0.3,
            'filter':'alpha(opacity=30)'
        });

        $("body").append(_mask).append(_text);
    },
    setCountDown:function (obj, text1, text2, Sec, func) {
        var _timer = null;
        var _counter = 0;
        _timer = setInterval(function () {
                _counter += 1;
                if (_counter > Sec) {
                    _counter = Sec;
                    clearInterval(_timer);
                    func();
                }
                $(obj).text(text1 + (Sec - _counter) + text2);
            },
            1000);
    },
    regResetPasswordEvent:function () { //找回密码成功，修改密码。

        var _pwd1 = $("#reg_pw1"),
            _pwd2 = $("#reg_pw2");

        var _pwd_info = $("#nw_error"),
            _pwd1_info = $("#reg_pw1_error"),
            _pwd2_info = $("#reg_pw2_error");

        var _is_pwd1 = false,
            _is_pwd2 = false;

        var _is_pwd1_text = "",
            _is_pwd2_text = "";

        _pwd1.focus(function () {
            _pwd1_info.removeClass("error").addClass("focus").text("请输入您的新密码");
        });
        _pwd1.blur(function () {
            if (_pwd1.val().length < 1) {
                _is_pwd1_text = "请输入您的新密码!";
                _pwd1_info.removeClass("focus").addClass("error").text(_is_pwd1_text);
                _pwd1.addClass("highlight");
                $("#reg_pw1_succeed").removeClass("succeed").addClass("warning");
                _is_pwd1 = false;
            } else {
                if (_pwd1.val().length < 6) {
                    _is_pwd1_text = "密码不能少于6位!";
                    _pwd1_info.removeClass("focus").addClass("error").text(_is_pwd1_text);
                    $("#reg_pw1_succeed").removeClass("succeed").addClass("warning");
                    _pwd1.addClass("highlight");
                    _is_pwd1 = false;
                    return false;
                }
                if (_pwd1.val().length > 16) {
                    _is_pwd1_text = "密码不能大于16位!";
                    _pwd1_info.removeClass("focus").addClass("error").text(_is_pwd1_text);
                    $("#reg_pw1_succeed").removeClass("succeed").addClass("warning");
                    _pwd1.addClass("highlight");
                    _is_pwd1 = false;
                    return false;
                }
                _pwd1_info.text("");
                $("#reg_pw1_succeed").removeClass("warning").addClass("succeed");
                _pwd1.removeClass("highlight");
                _is_pwd1 = true;
            }

        });

        _pwd2.focus(function () {
            _pwd2_info.removeClass("error").addClass("focus").text("请再次输入密码");
        });
        _pwd2.blur(function () {
            if (_pwd2.val() == "") {
                _is_pwd2_text = "请再次输入您的登陆密码!";
                _pwd2_info.removeClass("focus").addClass("error").text(_is_pwd2_text);
                $("#reg_pw2_succeed").removeClass("succeed").addClass("warning");
                _pwd2.addClass("highlight");
                _is_pwd2 = false;
            } else {
                if (_pwd2.val().length < 6) {
                    _is_pwd2_text = "密码不能少于6位!";
                    _pwd2_info.removeClass("focus").addClass("error").text(_is_pwd2_text);
                    $("#reg_pw2_succeed").removeClass("succeed").addClass("warning");
                    _pwd2.addClass("highlight");
                    _is_pwd2 = false;
                    return false;
                }
                if (_pwd2.val().length > 16) {
                    _is_pwd2_text = "密码不能大于16位!";
                    _pwd2_info.removeClass("focus").addClass("error").text(_is_pwd2_text);
                    $("#reg_pw2_succeed").removeClass("succeed").addClass("warning");
                    _pwd2.addClass("highlight");
                    _is_pwd2 = false;
                    return false;
                }

                if (_pwd1.val() != _pwd2.val()) {
                    _is_pwd2_text = "您两次输入的密码不一致!";
                    _pwd2_info.removeClass("focus").addClass("error").text(_is_pwd2_text);
                    $("#reg_pw2_succeed").removeClass("succeed").addClass("warning");
                    _pwd2.addClass("highlight");
                    _is_pwd2 = false;
                    return false;
                } else {
                    _is_pwd2_text = "";
                    _pwd2_info.text("");
                    $("#reg_pw2_succeed").removeClass("warning").addClass("succeed");
                    _pwd2.removeClass("highlight");
                    _is_pwd2 = true;
                    return false;
                }
            }
        });

        $("#NewPasswordSubmit").submit(function (e) {
            e.preventDefault();
            if (!_is_pwd1) {
                _pwd1.focus().addClass("highlight");
                _is_pwd1 = false;
                return false;
            }

            if (!_is_pwd2) {
                _pwd2.focus().addClass("highlight");
                _is_pwd2 = false;
                return false;
            }

            $.ajax({
                type:"POST",
                url: $("#NewPasswordSubmit").attr("action"),
                async:false,
                data: $("#NewPasswordSubmit").serialize(),
                dataType:"json",
                success:function (_e) {
                    if (_e["success"]) {
                        var HTML = '<div class="succeed_mask"></div>';
                            HTML += '<div class="succeed_text">密码修改成功，5秒钟之后跳转到登录页, 如果页面没有跳转，请手动 <a href="'+ EJS.ToPageLogin +'">点击这里</a></div>';
                        $("#np_succeed").show().html(HTML);
                        $("#NewPasswordSubmit .btn").eq(0).hide();
                        countdownJump(5,function(){
                            window.location = EJS.ToPageLogin;
                        });
                    } else {
                        _pwd_info.text(_e["msg"]);
                    }
                }
            });
            return false;
        });
    },
    regLoginPageEvent:function () { //单页用户登录
        var _un = $("input[name='userName']");
        var _pwd = $("input[name='password']");

        var _unInfo = $("#un_error"),
            _pwInfo = $("#pw_error");

        _un.blur(function () {
            if (!$(this).val()) {
                _unInfo.text("请输入用户名");
                _un.addClass("highlight");
            } else {
                _unInfo.text("");
                _un.removeClass("highlight");
            }
        });
        _pwd.blur(function () {
            if (!$(this).val()) {
                _pwInfo.text("请输入密码");
                _pwd.addClass("highlight");
            } else {
                _pwInfo.text("");
                _pwd.removeClass("highlight");
            }
        });

        $("#PageLogin_Submit").submit(function () {
            var _loginCheck = true;
            if (!_un.val()) {
                _unInfo.text("请输入用户名");
                _un.focus();
                _un.addClass("highlight");
                _loginCheck = false;
            } else {
                _unInfo.text("");
                _un.removeClass("highlight");
            }
            if (!_pwd.val()) {
                _pwInfo.text("请输入密码");
                _pwd.focus();
                _pwd.addClass("highlight");
                _loginCheck = false;
            } else {
                _pwInfo.text("");
                _pwd.removeClass("highlight");
            }

            if(!_loginCheck){
                return false;
            }

            $("#PageLogin_Submit .login_submit").eq(0).text("正在登录...")[0].setAttribute('type','button');
            $.ajax({
                type:"POST",
                url: $("#PageLogin_Submit").attr("action"),
                async: false,
                data: $("#PageLogin_Submit").serialize(),
                dataType:"json",
                success:function (_e) {
                    if (_e["success"]) {
                        location.href = _e["data"]["backUrl"];
                    } else {
                        if (_e["data"]["resendMailDom"]) {
                            _pwInfo.html(_e["msg"] + " " + _e["data"]["resendMailDom"]);
                        } else {
                            _pwInfo.text(_e["msg"]);
                            $("#PageLogin_Submit .login_submit").eq(0).text("安全登录")[0].setAttribute('type','submit');
                        }
                    }
                }
            });
            return false;
        });
    }
};
/*
 $(document).ready(function(){
 new Ejs.Users("loginPage");
 });
 */
$(function () {
    var jumpUrl = $("#window_close_url").val();
    if (jumpUrl != "") {
        $(document).unbind('keydown');
        $(document).bind("keydown", function (event) {
            if (event.keyCode == 27) {
                location.href = jumpUrl + "?actionType=doClose";
                return false;
            }
        });
    }

    $("#sendMailforActiveUser").live("click", function (E) {
        E.preventDefault();
        var _URL = $(this).attr("href");
        $.ajax({
            type:"POST",
            url:_URL,
            async:false,
            dataType:'json',
            success:function (data) {
                if (data["success"]) {
                    alert("邮件已发送,请注意查收!");
                } else {
                    alert("系统出问题了,请稍后再试!");
                }
            }
        });
    });
});

function countdownJump(Sec, func){
    var _timer = null;
    var _counter = 0;
    _timer = setInterval(function () {
            _counter += 1;
            if (_counter > Sec) {
                _counter = Sec;
                clearInterval(_timer);
                func();
            }
        },
      1000);
}