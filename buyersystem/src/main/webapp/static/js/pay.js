$(document).ready(function () {

    var selectedBank = $("#selected_bank"),
        bankIMG = selectedBank.find("img").eq(0),
        paymentBox = $("#payment_box");

    bankIMG.attr({
        src:paymentBox.find("li[paybank=" + currentPayBank + "]").find("img").eq(0).attr('src'),
        alt:paymentBox.find("li[paybank=" + currentPayBank + "]").find("img").eq(0).attr('alt')
    });

    var payTips = $(".recommand_bank").eq(0),
        payMethod = $("#payMethod"),
        defaultBank = $("#defaultbank");

    selectedBank.find(".reset").live("click", function (E) {
        E.preventDefault();
        paymentBox.show();
        paymentBox.find("li").removeClass("cur");
        paymentBox.find("li[paybank=" + currentPayBank + "]").addClass("cur");
        selectedBank.find("a").hide();
    });

    var PayOrder = $("#PayOrder");
    paymentBox.find("li").live("click", function (E) {
        E.preventDefault();
        currentPayBank = $(this).attr("paybank");
        if (currentPayBank == 'Alipay') {
            payMethod.val("directPay");
            defaultBank.val("");
            payTips.html("推荐淘宝用户使用");
            PayOrder.attr("action", EJS.ToAlipay);

        } else if (currentPayBank == 'Tenpay') {
            payMethod.val("Tenpay");
            defaultBank.val("");
            payTips.html("推荐腾讯用户使用");
            PayOrder.attr("action", EJS.ToTenpay);
        } else {
            payMethod.val("bankPay");
            defaultBank.val(currentPayBank);
            payTips.html("由支付宝提供接口，确保您的资金安全");
            PayOrder.attr("action", EJS.ToAlipay);
        }
        bankIMG.attr("src", $(this).find('img').eq(0).attr('src'));
        selectedBank.find("a").show();
        paymentBox.hide();
    });

    $("ul.bank_list>li").each(function (i) {
        var _this = $(this);
        _this.hover(function () {
            _this.addClass("hover");
        }, function () {
            _this.removeClass("hover");
        })
    });

    $("#ToPay").click(function () {
        var payDialog = new Ejs.Dialog({
            title:"请您在新打开的网上银行页面进行支付！",
            titleStyle:{

            },
            info:'支付完成前请不要关闭此窗口,完成付款后请根据您的情况点击下面的按钮！',
            infoStyle:{
                height:45
            },
            type:Ejs.Dialog.type.WARN,
            typeStyle:{

            },
            preventClosed:true,
            isBgClose:false,
            buttons:[
                {
                    buttonText:"已完成支付",
                    buttonClass:"button_a",
                    buttonStyle:{

                    },
                    onClick:function (ex) {
                        window.location.href = EJS.ToPayStatus;
                    }
                },
                {
                    buttonText:"支付遇到问题",
                    buttonClass:"button_a",
                    buttonStyle:{
                        marginRight:0
                    },
                    onClick:function (ex) {
                        window.open(EJS.ToSubmitPayQuestion, "PayQuestion");
                    }
                }
            ]
        });

        payDialog.getClose().hide();

        var getBackBank = $("<div></div>");
        var toLink = '<a href="';
        toLink += EJS.SelectPayBank;
        toLink += '">';
        toLink += '<< 返回重新选择银行';
        toLink += '</a>';
        getBackBank.css({
            textAlign:"left",
            paddingTop:5
        }).html(toLink).appendTo(payDialog.getContent().find(".button_wrapper").parent());

    });
});