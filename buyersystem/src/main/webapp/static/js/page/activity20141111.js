/**
 * 签到送积分
 * User: Hualei Du
 * Date: 13-10-29
 */
/*
 $LAB.script(EJS.StaticDomain + '/js/order_pop.js')
 .wait()
 .script(EJS.StaticDomain + '/js/page/give_integral.js');
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


    // 积分兑换现金券
    function integralConvert(money) {
        Ejs.UserStatus.isLogin(function () {
            var url = EJS.HomeUrl + '/user/receive?count=' + money;
            $.ajax({
                type: 'GET',
                cache: false,
                url: url,
                dataType: 'JSON',
                success: function (res) {
                    if (res.success) {
                        dialog('<strong>兑换成功</strong><p>' + res.msg + '，<a href="/my/coupons" target="_blank">查看我的现金券&gt;&gt;</a></p>');
                    } else {
                        dialog('<strong>兑换失败</strong><p>' + res.msg + '</p>');
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

    // 绑定积分兑换现金券事件
    $('.activity1111-head').on('click', '.coupon-link', function (E) {
        E.preventDefault();
        integralConvert($(this).attr('href').replace('#', ''));
    });


    $(function () {
        // 倒计时
        function countDown(time, id) {
            var end_time = new Date(time).getTime(),
                ele = $(id);

            var timer = setInterval(function () {
                var nowTime = new Date().getTime(),
                    surplus_ms_number = (end_time - nowTime),
                    sys_second = surplus_ms_number / 1000,
                    hour;

                if (sys_second > 1) {
                    hour = Math.floor((sys_second / 3600)) + 24;
                    ele.text(Math.floor(hour / 24));
                } else {
                    clearInterval(timer);
                }
            }, 180);

        }

        countDown('2014/11/11', "#activity1111-timer");
    });

}(jQuery));

