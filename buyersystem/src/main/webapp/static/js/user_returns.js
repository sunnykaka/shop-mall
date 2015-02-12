function returnsForm() {

    $("#orderData").find("input[name=orderItemId]").each(function () {
        var _this = $(this);
        var _tr = _this.parents("tr");
        _this.click(function () {
            if ($(this)[0].checked) {
                _this.attr('checked', true);
                _tr.addClass("selected");
                _tr.find(".number").attr("disabled", false);
            } else {
                _this.attr('checked', false);
                _tr.removeClass("selected");
                _tr.find(".number").attr("disabled", true);
            }
        });
        _this.attr('checked', false);
        _tr.find(".number").attr("disabled", true);
    });

    $("#returnsStep2Form").submit(function (E) {
        var trSelected = $("tr.selected");
        var submitBtn = $(this).find(".btn_6").eq(0);
        var istrSelected = true;
        if (trSelected.length > 0) {
            trSelected.each(function (i) {
                if (!istrSelected) {
                    return false;
                }
                var _number = $(this).find(".number").eq(0)
                var _limit = _number.attr("val");
                if ($.trim(_number.val()) < 1) {
                    E.preventDefault();
                    Ejs.tip(_number, "limit_tip__" + i, "退货数量不能小于1", 30, -50);
                    _number.focus();
                    istrSelected = false;
                }
                if ($.trim(_number.val()) > _limit) {
                    E.preventDefault();
                    Ejs.tip(_number, "limit_tip__" + i, "退货数量超出可退数量", 30, -50);
                    _number.focus();
                    istrSelected = false;
                }
            });
            if (!istrSelected) {
                return false;
            }
        } else {
            E.preventDefault();
            Ejs.tip(submitBtn, "limit_tip", "请选择您要退的商品和数量", 0, 130);
            return false;
        }


        var reCause = $("#reCause");
        var regCause = /\^|\||\$|#/;

        if ($.trim(reCause.val()) == "") {
            E.preventDefault();
            Ejs.tip(submitBtn, "limit_tip", "退货原因不能为空", 0, 130);
            return false;
        }

        if ($.trim(reCause.val()).search(regCause) >= 0) {
            E.preventDefault();
            Ejs.tip(submitBtn, "limit_tip", "退款原因中不能包含“^”、“|”、“$”、“#”", 0, 130);
            return false;
        }

        var reName = $("#reName");
        if ($.trim(reName.val()) == "") {
            E.preventDefault();
            Ejs.tip(submitBtn, "limit_tip", "联系人姓名不能为空", 0, 130);
            return false;
        }

        var reNumber = $("#reNumber");
        var _regNumber = /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/;
        if (!reNumber.val() || (!_regNumber.test(reNumber.val()))) {
            E.preventDefault();
            Ejs.tip(submitBtn, "limit_tip", "请输入正确的电话/手机号码", 0, 130);
            return false;
        }
    });

}

function cancelReturns() {
    $(".cancel").each(function (i) {
        var _this = $(this);
        _this.click(function (E) {
            E.preventDefault();
            var URL = _this.attr("href");
            $.ajax({
                type: "POST",
                async: false,
                url: URL,
                dataType: "json",
                success: function (data) {
                    if (data["data"]) {
                        window.location.reload();
                    } else {
                        Ejs.tip(_this, "limit_tip", data["msg"], -30);
                    }
                }
            })
        })
    });
}

function stepMargin(len) {
    if (typeof len !== "number") {
        return;
    }
    var stepLength = $("#ucStep li").length;
    if (stepLength < len && stepLength > 0) {
        var Width = ((696 - stepLength * 166) + 134) / 2 + 20;
        $("#ucStep").css("margin-left", Width);
    }
}


