$(function(){
    var fb_contact = $("#fb_contact");
    fb_contact.focus(function () {
        $(this).val() == "输入您的邮箱或手机号码" && $(this).removeClass("default").val("");
    });
    fb_contact.blur(function () {
        if ($(this).val().length < 1) {
            $(this).val("输入您的邮箱或手机号码").addClass("default");
        }
    });
    $("#feedbackForm").submit(function () {
        var fb_content = $("#fb_content");
        var fb_content_info = $("#fb_content_info");
        fb_content_info.text("");
        fb_content.removeClass("error");
        if (!$.trim(fb_content.val())) {
            fb_content_info.text("反馈内容不能为空。");
            fb_content.addClass("error");
            fb_content.focus();
            return false;
        }
    });
    $("#feedbackForm").find(".btn_button").eq(0).click(function () {
        window.opener = null;
        window.open('', '_self');
        window.close();
        if ($.browser.mozilla) {
            location.href = "about:blank";
        }
    });

    $("#fbInfo .fb_info_close").click(function(){
        $("#fbInfo").hide();
    });
});