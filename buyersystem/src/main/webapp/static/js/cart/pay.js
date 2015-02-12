/**
 * Created by HuaLei.Du on 2014/6/17.
 */

(function () {

    var page = $('.order-pay-page'),
        selectedBank = $('#selected-bank'),
        bankIMG = selectedBank.find('img').eq(0),
        paymentBox = page.find('.payment-box');

    (function () {
        var cartSteps = $('.cart-steps');
        cartSteps.find('li').addClass('strong');
        cartSteps.find('.step').addClass('step-3');
    }());

    bankIMG.attr({
        src: paymentBox.find("li[paybank=" + currentPayBank + "]").find("img").eq(0).attr('src'),
        alt: paymentBox.find("li[paybank=" + currentPayBank + "]").find("img").eq(0).attr('alt')
    });

    // 展开全部支付方式
    page.on('click', '#selected-bank .reset', function () {
        page.find('.payment-box').show();
        selectedBank.parents('.selected').hide();
    });

    // 更改支付方式
    paymentBox.on('click', 'li', function (E) {
        E.preventDefault();
        var payOrder = $("#PayOrder"),
            payMethod = $('#payMethod'),
            defaultBank = $('#defaultbank');

        paymentBox.find('li').removeClass('current');
        $(this).addClass('current');

        currentPayBank = $(this).attr("paybank");

        if (currentPayBank === 'Alipay') {
            payMethod.val('directPay');
            defaultBank.val('');
            payOrder.attr("action", EJS.ToAlipay);

        } else if (currentPayBank === 'Tenpay') {
            payMethod.val('Tenpay');
            defaultBank.val('');
            payOrder.attr('action', EJS.ToTenpay);
        } else {
            payMethod.val('bankPay');
            defaultBank.val(currentPayBank);
            payOrder.attr("action", EJS.ToAlipay);
        }

        bankIMG.attr("src", $(this).find('img').eq(0).attr('src'));
        selectedBank.parents('.selected').show();
        paymentBox.hide();
    });

    // 去付款
    page.on('click', '.btn-toPay', function () {
        var payDialog,
            html = '<div class="pay-info"><h4>请您在新打开的网上银行页面进行支付！</h4>' +
                '<p>付款成功：<a href="' + EJS.ToPayStatus + '">查看订单状态</a></p>' +
                '<p>付款失败：<a href="' + EJS.SelectPayBank + '">选择其他付款方式</a> ' +
                '<a href="javascript:Ejs.openConsult();void(0);">联系在线客服</a></p></div>';

        Ejs.dialogAlertXl(html, null, null);
        /*payDialog = new Ejs.Dialog({
            title: "提示",
            titleStyle: {},
            info: html,
            infoStyle: {
                height: 150
            },
            type: Ejs.Dialog.type.WARN,
            typeStyle: {},
            preventClosed: true,
            isBgClose: false,
            outClass: 'ejs-dialog-pay'
        });*/
    });

}());