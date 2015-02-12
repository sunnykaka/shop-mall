var Ejs = window.Ejs || {};
// 取字符串长度
String.prototype.strLength= function () {
	return this.replace(/[^\x00-\xff]/g, "rr" ).length;
};

var validateRegExp = {
	email: "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$",
	//邮件
	notempty: "^\\S+$",
	//非空
	password: "^[A-Za-z0-9_-]+$",
	//密码
	username: "^[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+$"
	//用户名
};
var validateRules = {
	isNull:function (str) {
		return (str == "" || typeof str != "string");
	},
	betweenLength:function (str, _min, _max) {
		return (str.length >= _min && str.length <= _max);
	},
	isUid:function (str) {
		return new RegExp(validateRegExp.username).test(str);
	},
	isPwd:function (str) {
		return /^.*([\W_a-zA-z0-9-])+.*$/i.test(str);
	},
	isPwd2:function (str1, str2) {
		return (str1 == str2);
	},
	isEmail:function (str) {
		return new RegExp(validateRegExp.email).test(str);
	}
};

getImageCode = function (obj) {
	if (typeof obj == "object") {
		obj.src = $("input[name='image_code_url']").val() + '?d=' + Math.random();
	} else {
		document.getElementById(obj).src = $("input[name='image_code_url']").val() + '?d=' + Math.random();
	}
};

$(document).ready(function(){
	//移除反馈和返回顶部
	/*$(".com_fixed_comWrapper").remove();*/

	//登陆验证 提交
	$("#PageLogin_Submit").submit(function(){
		var _url = $("input[name='log_submit_url']").val();

		if ($("#log_un").val() == "") {
			$("#un_error").text("请输入你的用户名!");
			$("#log_un").focus();
			return false;
		}else{
			$("#un_error").text("");
		}
		if ($("#log_pw").val() == "") {
			$("#pw_error").text("请输入你的登陆密码!");
			$("#log_pw").focus();
			return false;
		}else{
			$("#pw_error").text("");
		}
	});

	//第三方登陆
	$(".pop_login_page>.login_cop>.clearfix>a").click(function(e){
		e.preventDefault();
		location.href=$("#window_close_url").val()+"?actionType=redirectUrl&backUrl="+encodeURIComponent($(this).attr("href"));
	});

	//注册验证 提交
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

	var un_s = $("#un_succeed"),
		pw1_s = $("#pw1_succeed"),
		pw2_s = $("#pw2_succeed"),
		mail_s = $("#mail_succeed"),
		code_s = $("#code_succeed");


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
		un_info.removeClass("error").addClass("focus").text('4-20位字符，可由中文、英文、数字及"_"、"-"组成');
	});

	reg_un.blur(function () {
		var reg_un_val = $.trim(reg_un.val());
		if (validateRules.isNull(reg_un_val)) {
			is_reg_un_text = "请输入您的用户名";
			un_info.removeClass("focus").addClass("error").text(is_reg_un_text);
			un_s.removeClass("succeed").addClass("warning");
			is_reg_un = false;
		} else {
			if (reg_un_val.strLength() < 4 || reg_un_val.strLength() > 20) {
				is_reg_un_text = "用户名长度只能在4-20位字符之间";
				un_info.removeClass("focus").addClass("error").text(is_reg_un_text);
				un_s.removeClass("succeed").addClass("warning");
				is_reg_un = false;
			}else if(!validateRules.isUid(reg_un_val)) {
				is_reg_un_text = '用户名只能由中文、英文、数字及"_"、"-"组成';
				un_info.removeClass("focus").addClass("error").text(is_reg_un_text);
				un_s.removeClass("succeed").addClass("warning");
				is_reg_un = false;
			}else{
				var _url = $("input[name='reg_un_url']").val();
				$.ajax({
					type: "POST",
					async: true,
					url: _url,
					data:{
						userName: reg_un.val()
					},
					dataType: "json",
					success:function (_e) {
						if (_e["success"]) {
							un_info.text("");
							is_reg_un = true;
							is_reg_un_text = "";
							un_s.removeClass("warning").addClass("succeed");
						} else {
							un_info.removeClass("focus").addClass("error").text(_e["msg"]);
							is_reg_un = false;
							is_reg_un_text = _e["msg"];
							un_s.removeClass("succeed").addClass("warning");
						}
					}
				});
			}
		}
	});

	reg_pw1.focus(function () {
		pw1_info.removeClass("error").addClass("focus").text("6-16位字符，可由英文、数字及“_”组成");
	});
	reg_pw1.blur(function () {
		var reg_pw1_val = reg_pw1.val();
		if (validateRules.isNull(reg_pw1_val)) {
			is_reg_pw_text = "请输入您的登陆密码!";
			pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
			pw1_s.removeClass("succeed").addClass("warning");
			is_reg_pw = false;
		} else {
			if (reg_pw1.val().length < 6) {
				is_reg_pw_text = "密码不能少于6位!";
				pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
				pw1_s.removeClass("succeed").addClass("warning");
				return false;
			}
			if (reg_pw1.val().length > 16) {
				is_reg_pw_text = "密码不能大于16位!";
				pw1_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
				pw1_s.removeClass("succeed").addClass("warning");
				return false;
			}
			pw1_info.removeClass("focus").removeClass("error").text("");
			pw1_s.removeClass("warning").addClass("succeed");
		}
	});

	reg_pw2.focus(function () {
		pw2_info.removeClass("error").addClass("focus").text("请再次输入密码");
	});
	reg_pw2.blur(function () {
		if (reg_pw2.val() == "") {
			is_reg_pw_text = "请再次输入您的登陆密码!";
			pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
			pw2_s.removeClass("succeed").addClass("warning");
			is_reg_pw = false;
		} else {
			if (reg_pw2.val().length < 6) {
				is_reg_pw_text = "密码不能少于6位!";
				pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
				pw2_s.removeClass("succeed").addClass("warning");
				return false;
			}
			if (reg_pw2.val().length > 16) {
				is_reg_pw_text = "密码不能大于16位!";
				pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
				pw2_s.removeClass("succeed").addClass("warning");
				return false;
			}

			if (reg_pw1.val() != reg_pw2.val()) {
				is_reg_pw_text = "您两次输入的密码不一致!";
				pw2_info.removeClass("focus").addClass("error").text(is_reg_pw_text);
				is_reg_pw = false;
				pw2_s.removeClass("succeed").addClass("warning");
				return false;
			} else {
				is_reg_pw_text = "";
				pw2_info.removeClass("focus").removeClass("error").text("");
				is_reg_pw = true;
				pw2_s.removeClass("warning").addClass("succeed");
				return false;
			}
		}
	});

	reg_mail.focus(function () {
		mail_info.removeClass("error").addClass("focus").text("请输入常用邮箱，将用来找回密码，接受订单等通知");
	});
	reg_mail.blur(function () {
		var reg_mail_val = $.trim(reg_mail.val());
		if (!validateRules.isEmail(reg_mail_val)) {
			is_reg_mail_text = "请填写正确的E-mail地址!";
			mail_info.removeClass("focus").addClass("error").text(is_reg_mail_text);
			is_reg_mail = false;
			mail_s.removeClass("succeed").addClass("warning");
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
						mail_s.removeClass("warning").addClass("succeed");
						is_reg_mail = true;
					} else {
						mail_info.removeClass("focus").addClass("error").text(_e["msg"]);
						is_reg_mail_text = _e["msg"];
						mail_s.removeClass("succeed").addClass("warning");
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
			code_s.removeClass("succeed").addClass("warning");
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
						code_s.removeClass("warning").addClass("succeed");
					} else {
						code_info.removeClass("focus").addClass("error").text(_e["msg"]);
						getImageCode("reg_cimg");
						is_reg_code = false;
						code_s.removeClass("succeed").addClass("warning");

					}
				}
			});

		}
	});

	$("#reg_ok").submit(function (e) {
		/*e.preventDefault();*/
		if (!checkReg()) {
			return false;
		}
	});

	$("body>div:gt(0)").remove();
	//移除反馈和返回顶部
	$(".com_fixed_comWrapper").remove();

});
