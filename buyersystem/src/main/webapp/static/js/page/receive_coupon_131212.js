/**
 * 领取优惠券
 * User: HuaLei Du
 * Date: 13-12-09
 */
/*
 $LAB.script(EJS.StaticDomain + '/js/order_pop.js')
 .wait()
 .script(EJS.StaticDomain + '/js/page/receive_coupon_131212.js');
 */
(function ($) {

    // 对话框
    function dialog(content, buttons) {

        var buttonConfig = [
            {
                buttonText: '关闭',
                buttonClass: 'button_a',
                buttonStyle: {
                    marginRight: 0
                },
                onClick: function () {
                }
            }
        ];

        if (buttons && typeof buttons === 'object') {
            buttonConfig = buttons;
        }

        new Ejs.Dialog({
            title: ' ',
            type: Ejs.Dialog.type.RIGHT,
            info: content,
            outClass: 'com_lottery_window',
            border: '1px solid #f3ae00',
            width: 360,
            height: 200,
            isModal: true,
            backgroundColor: '#fff',
            buttons: buttonConfig
        });
    }

    // 领取优惠券
    function receive_coupon(money) {
        Ejs.UserStatus.isLogin(function () {
            var url = EJS.HomeUrl + '/user/receive?count=' + money;
            $.ajax({
                type: 'GET',
                cache: false,
                url: url,
                dataType: 'JSON',
                success: function (res) {
                    if (res.success) {
                        dialog('<strong>领取成功</strong><p>' + res.msg + '，<a href="/my/coupons" target="_blank">查看我的现金券&gt;&gt;</a></p>');
                    } else {
                        dialog('<strong>领取失败</strong><p>' + res.msg + '</p>');
                    }
                },
                error: function () {
                    dialog('<strong>服务器出错...</strong>');
                }
            });
        }, function () {
            Ejs.UserWindows = new Ejs.UserWindow({
                backUrl: Ejs.url
            });
        });
    }

    // 绑定事件
    $('#receive_coupon_btn_10, #receive_coupon_btn_50, #receive_coupon_btn_100').on('click', function (E) {
        E.preventDefault();
        var integral = $(this).attr('href').replace('#', '');
        receive_coupon(integral);
    });

}(jQuery));