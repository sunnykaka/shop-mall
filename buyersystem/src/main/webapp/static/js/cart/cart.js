/**
 * 购物车页面
 * Created by HuaLei.Du on 2014/6/10.
 */
(function ($, Ejs) {

    var cartPage = $('.cart-page');

    (function () {
        var cartSteps = $('.cart-steps');
        cartSteps.find('li').eq(0).addClass('strong');
        cartSteps.find('.step').addClass('step-1');
    }());

    // 加入关注
    function addFollow(url, callback) {

        Ejs.UserStatus.isLogin(function () {
            $.ajax({
                type: 'POST',
                url: url,
                //async: false,
                dataType: 'JSON',
                success: function (result) {

                    if (result.success) {
                        callback();
                        Ejs.dialogAlert('成功转为关注', Ejs.Dialog.type.RIGHT);
                    } else {
                        Ejs.dialogAlert(result.msg);
                    }

                },
                error: function () {
                    Ejs.dialogAlert();
                }
            });
        }, function () {
            Ejs.dialogAlert('您还没有登录，无法转为关注！');
        });

    }

    // 显示空的购物车
    function showEmptyCart() {
        cartPage.find('.cart-head').hide();
        cartPage.find('.cart-count').hide();
        cartPage.find('.cart-btn').hide();
        cartPage.find('.cart-body').html('<div class="no-goods"><strong>购物车内暂时没有商品~</strong><br>' +
            '<a href="' + EJS.HomeUrl + '">去首页挑选喜欢的商品&gt;&gt;</a></div>');
    }

    // 更新价格
    function updatePrice(data) {
        cartPage.find('.total-price').text('¥' + data.totalPrice);
        cartPage.find('.total-number').text(data.totalNumber);
        cartPage.find('.preferential').text('-¥' + data.discountPrice);
    }

    // 删除商品
    function deleteGoods(item, url) {
        $.ajax({
            type: 'POST',
            url: url,
            //async: false,
            dataType: 'JSON',
            success: function (result) {
                var list = item.parents('.list');

                if (result.success) {
                    updatePrice(result.data);
                    item.remove();

                    if (list.find('.row').length < 1) {
                        list.slideUp(400, function () {
                            list.remove();

                            if (cartPage.find('.list').length < 1) {
                                showEmptyCart();
                            }

                        });
                    }

                } else {
                    Ejs.dialogAlert(result.msg);
                }

            },
            error: function () {
                Ejs.dialogAlert();
            }
        });
    }

    // 删除所有商品
    function deleteAllGoods(url) {
        $.ajax({
            type: 'POST',
            url: url,
            dataType: 'JSON',
            success: function (result) {

                if (result.success) {
                    cartPage.find('.list').hide();
                    showEmptyCart();
                } else {
                    Ejs.dialogAlert(result.msg);
                }

            },
            error: function () {
                Ejs.dialogAlert();
            }
        });
    }

    // 更新数量
    function updateNumber(ele) {
        var numberInput = ele.parent('.amount').find('.text-number'),
            val = parseInt(numberInput.val(), 0),
            limit = parseInt(numberInput.attr('limit'), 0),
            price = parseInt(numberInput.attr('data-price'), 0);

        // 同步更改到数据库
        function sync() {
            var url = $('#updateItemUrl').val();
            $.ajax({
                type: 'POST',
                url: url,
                data: {
                    cartId: $('#cartId').val(),
                    skuId: ele.parent('.amount').attr('data-skuId'),
                    number: val
                },
                async: false,
                dataType: 'JSON',
                success: function (result) {
                    if (result.success) {
                        updatePrice(result.data);
                    }
                }
            });
        }

        function setTotalPrice(val) {
            val = val.toFixed(2);
            ele.parents('.row').find('.col5').text('¥' + val);
        }

        return {
            add: function () {
                val += 1;

                if (val > limit) {
                    val = limit;
                    Ejs.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                }

                numberInput.val(val);

                setTotalPrice(val * price);
                sync();

            },
            remove: function () {
                val -= 1;

                if (val < 1) {
                    val = 1;
                }

                numberInput.val(val);
                setTotalPrice(val * price);
                sync();
            },
            checkValue: function () {

                if (!/^[1-9]*\d*$/g.test(val)) {
                    val = 1;
                }

                if (val > limit) {
                    val = limit;
                    Ejs.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                }

                numberInput.val(val);
                setTotalPrice(val * price);
                sync();
            }
        };

    }

    // 验证购物车
    function checkCart(url, callback) {
        $.ajax({
            type: 'GET',
            url: url,
            dataType: 'JSON',
            success: function (result) {

                if (result.success) {
                    callback(true);
                } else {
                    callback(false, result.data.errorMsg);
                }

            }
        });
    }

    // 展示发货提示(用于不能正常发货时，给予提示)
    function showDeliveryTips() {
        var html = '<h4><span>温馨提示：</span>春节期间发货安排</h4>' +
            '<p>' +
            '   2月2日<strong class="strong">16:00</strong>前付款 - 订单<strong class="strong">当日</strong>即可发出<br/>' +
            '   2月2日<strong class="strong">16:00</strong>后付款 - 订单于<strong class="strong">2月16日初七</strong>按订' +
            '单先后顺序快速发出。因此给您带来的不便请您谅解' +
            '</p>';
        $('#delivery-tips').html(html).slideDown(400);
    }

    // 绑定删除商品事件
    cartPage.on('click', '.link-delete', function (e) {
        var self = $(this),
            url,
            item;

        url = self.attr('data-deleteUrl');
        item = self.parents('.row');

        Ejs.dialogConfirm(e, '确认不购买此商品吗？', function () {
            deleteGoods(item, url);
        });

    });

    // 清空购物车事件
    cartPage.on('click', '.link-deleteAll', function (e) {
        var self = $(this);

        Ejs.dialogConfirm(e, '确认要清空购物车吗？', function () {
            deleteAllGoods(self.attr('data-deleteUrl'));
        });
    });

    // 绑定关注事件
    cartPage.on('click', '.link-follow', function (e) {
        var self = $(this);

        Ejs.dialogConfirm(e, '确认要移除商品转为关注吗？', function () {
            addFollow(self.attr('data-followUrl'), function () {
                deleteGoods(self.parents('.row'), self.attr('data-deleteUrl'));
            });
        });

    });

    // 增加数量事件
    cartPage.on('click', '.btn-add', function () {
        updateNumber($(this)).add();
    });

    // 减小数量事件
    cartPage.on('click', '.btn-sub', function () {
        updateNumber($(this)).remove();
    });

    // 手动输入数量验证
    cartPage.on('blur', '.text-number', function () {
        updateNumber($(this)).checkValue();
    });

    // 提交订单事件
    $('#toOrder').on('click', function (e) {
        e.preventDefault();
        var self = $(this);

        Ejs.UserStatus.isLogin(function () {

            // 检查购物车的商品库存是否充足
            checkCart(self.attr("data-checkUrl"), function (success, data) {
                var html = '';

                if (success) {
                    window.location.href = self.attr('href');
                } else {
                    $(data).each(function (i) {
                        html += '<p>' + data[i] + '</p>';
                    });

                    Ejs.dialogAlertXl(html, null, [
                        {
                            buttonText: '修改商品数量',
                            buttonClass: 'e-btn btn-default'
                        }
                    ]);

                }

            });

        }, function () {
            new Ejs.UserWindow({
                backUrl: encodeURIComponent($('#toOrder').attr('href'))
            });
        });
    });

}(jQuery, Ejs));