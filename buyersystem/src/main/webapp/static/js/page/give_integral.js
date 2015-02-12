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

    // 加入收藏
    (function () {
        var productItem = $('.product_item').not($('.last'));

        if (productItem.length < 1) {
            return;
        }

        function addFavorite(productId) {
            var url = EJS.AddMyFavorites + '?productId=' + productId,
                count = 0,
                title = '';

            Ejs.UserStatus.isLogin(function () {
                $.ajax({
                    type: 'POST',
                    cache: false,
                    url: url,
                    async: false,
                    dataType: 'json',
                    success: function (data) {

                        $.ajax({
                            type: 'POST',
                            cache: false,
                            url: EJS.QueryUserFavoritesNum,
                            async: false,
                            dataType: 'JSON',
                            success: function (res) {
                                count = res.data.productCollectNum;
                            },
                            error: function () {
                                dialog('<strong>服务器出错...</strong>');
                            }
                        });

                        if (data.success) {
                            title = '商品收藏成功';
                        } else {
                            title = '您已经收藏此商品';
                        }

                        dialog('<strong>' + title + '</strong><p>您已经收藏' + count + '个商品！</p>', [
                            {
                                buttonText: '继续浏览',
                                buttonClass: 'button_a'
                            },
                            {
                                buttonText: '查看收藏夹',
                                buttonClass: 'button_b',
                                buttonStyle: {
                                    marginRight: 0
                                },
                                onClick: function () {
                                    location.href = EJS.MyFavorites;
                                }
                            }
                        ]);
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

        productItem.append('<div class="favorite">收藏</div>');

        // 绑定收藏事件
        productItem.on('click', '.favorite', function (E) {
            E.preventDefault();
            var productId = $(this).parents('li').attr('data-id');
            addFavorite(productId);
        });

    }());

    // 签到，兑换现金券
    (function () {
        var signInButton = $('#sign_in_button');

        if (signInButton.length < 1) {
            return;
        }

        function signIn() {
            Ejs.UserStatus.isLogin(function () {
                var url = EJS.HomeUrl + '/user/sign';
                $.ajax({
                    type: 'GET',
                    cache: false,
                    url: url,
                    dataType: 'JSON',
                    success: function (res) {
                        if (res.success) {
                            dialog('<strong>签到成功</strong><p>' + res.msg + '</p>');
                        } else {
                            dialog('<strong>签到失败</strong><p>' + res.msg + '</p>');
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

        // 绑定签到事件
        signInButton.on('click', function (E) {
            E.preventDefault();
            signIn();
        });

        // 积分兑换现金券
        function integralConvert(integral) {
            Ejs.UserStatus.isLogin(function () {
                dialog('<strong>您确定要兑换吗？</strong>', [
                    {
                        buttonText: '是',
                        onClick: function () {
                            var url = EJS.HomeUrl + '/user/exchange/coupon?count=' + integral;
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
                        }
                    },
                    {
                        buttonText: '否',
                        buttonStyle: {
                            marginRight: 0
                        }
                    }
                ]);
            }, function () {
                Ejs.UserWindows = new Ejs.UserWindow({
                    backUrl: Ejs.url
                });
            });
        }

        // 绑定积分兑换现金券事件
        $('#integral_convert').on('click', 'area', function (E) {
            E.preventDefault();
            var integral = $(this).attr('href').replace('#', '');
            integralConvert(integral);
        });

        // 现金兑换现金券
        function moneyConvert(id) {
            if (id === 'A' || id === 'B' || id === 'C') {
                Ejs.UserStatus.isLogin(function () {
                    window.location.href = '/couponSet/' + id;
                }, function () {
                    Ejs.UserWindows = new Ejs.UserWindow({
                        backUrl: Ejs.url
                    });
                });
            }
        }

        // 绑定现金兑换现金券事件
        $('#money_convert').on('click', 'area', function (E) {
            E.preventDefault();
            var id = $(this).attr('href').replace('#', '');
            moneyConvert(id);
        });

    }());

    // 右侧悬浮导航条
    (function () {

        var nav = $('#AGI_nav');

        if (nav.length < 1) {
            return;
        }

        function highlightNav(i) {
            var subNav = nav.find('li');
            subNav.removeClass('on');
            if (i !== 0) {
                subNav.eq(i).addClass('on');
            }
        }

        $(window).on('scroll', function () {
            var scrollTop = $(window).scrollTop();
            if (scrollTop > 1960) {
                highlightNav(4);
            } else if (scrollTop > 1320) {
                highlightNav(2);
            } else if (scrollTop > 890) {
                highlightNav(1);
            } else {
                highlightNav(0);
            }
        });

        nav.on('click', 'a', function (E) {
            var href = $(this).attr('href'),
                number;
            if (href.substring(0, 1) === '#') {
                E.preventDefault();
                number = parseInt(href.replace('#', ''));
                if (number === 1) {
                    $('html, body').animate({ scrollTop: 891 }, 400);
                } else if (number === 2) {
                    $('html, body').animate({ scrollTop: 1321 }, 400);
                } else if (number === 4) {
                    $('html, body').animate({ scrollTop: 1961 }, 400);
                } else if (number === 0) {
                    $('html, body').animate({ scrollTop: 0 }, 400);
                }
            }
        });

    }());

    // 返回顶部
    (function () {
        $('#back_top').on('click', function (E) {
            E.preventDefault();
            $('html, body').animate({ scrollTop: 0 }, 600);
        });
    }());

}(jQuery));

// 上线后要删除
$("img[data-original]").lazyload({ effect: "fadeIn", threshold: 300 });
