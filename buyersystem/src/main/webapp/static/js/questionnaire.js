$(function () {
    var formElement = $("#questionnaire_form");
    formElement.submit(function (e) {
        
        var boxEl,
            top = 0,
            errorEl = null,
            checking = true;

        $(this).find(".error").hide();
        $(this).find(".box").removeClass("highlight");

        formElement.find(".box").each(function (i) {
            $(this).attr("id", "box_" + i);
            boxEl = $("#box_" + i);
            errorEl = boxEl.find("dt").find(".error");
            if (!$(this).attr("isChecking") || checking === false) {
                return;
            }
            if ($(this).find("input:checked").length < 1) {
                checking = false;
                boxEl.addClass("highlight");
                if (errorEl.length < 1) {
                    boxEl.find("dt").append('<san class="error">请选择此项</san>');
                } else {
                    errorEl.show();
                }
                top = boxEl.offset().top;
                top = (top < 50) ? 0 : (top - 50);
                $(window).scrollTop(top);
            }
        });
        if (!checking) {
            e.preventDefault();
        }

        /*
        if (checking) {
            var url = $(this).attr("action");
            $.ajax({
                url: url,
                data: formElement.serialize(),
                dataType: 'json',
                success: function (data) {
                    if (data["success"]) {
                        alert("提交成功");
                        location.reload();
                    } else {
                        alert(data["msg"]);
                    }
                },
                error: function () {
                    alert("程序异常");
                }
            });
        }
        */
    });
});