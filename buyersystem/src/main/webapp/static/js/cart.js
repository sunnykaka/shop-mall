/**
 * Created by IntelliJ IDEA.
 * User: computer2
 * Date: 12-5-29
 * Time: 上午11:44
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function () {

    //购物车按扭功能
    $(".cell>.com_btn").each(function (i) {
        var _this = $(this);
        var _inputTxt = _this.find("input");
        var _inputVal = parseInt(_inputTxt.val());

        function request() {
            var _url = $("#updateItemUrl").val();
            $.ajax({
                type: "POST",
                url: _url,
                data: {
                    cartId: $("#cartId").val(),
                    skuId: _this.find(".number_wrap").attr("skuId"),
                    number: _inputVal
                },
                async: false,
                success: function (_datas) {
                    var _dataRes = eval("(" + _datas + ")");
                    if (_dataRes["success"]) {
                        $("#cartTotalPrice").text(_dataRes["data"]["totalPrice"]);
                    }
                }
            });
        }

        _this.find(".btn_add").click(function (e) {
            e.preventDefault();
            _inputVal += 1;
            var _limit = parseInt(_inputTxt.attr("limit"));
            if (_inputVal > _limit) {
                _inputVal = _limit;
                Ejs.tip(_inputTxt, "limit_tip", "超出此商品能购买的最大数量", 30);
            }
            request();
            _inputTxt.val(_inputVal);
        });
        _this.find(".btn_sub").click(function (e) {
            e.preventDefault();
            _inputVal -= 1;
            if (_inputVal <= 1) {
                _inputVal = 1;
            }
            request();
            _inputTxt.val(_inputVal);
        });

        _inputTxt.blur(function () {
            var reg = /^[0-9]*[1-9][0-9]*$/g;
            if (!reg.test($(this).val())) {
                $(this).val(1);
                _inputVal = 1;
            }
            var _limit = parseInt(_inputTxt.attr("limit"));
            if (parseInt($(this).val()) > _limit) {
                _inputVal = _limit;
                Ejs.tip(_inputTxt, "limit_tip", "超出此商品能购买的最大数量", 30);
            } else {
                _inputVal = parseInt($(this).val());
            }
            request();
            _inputTxt.val(_inputVal);
        });
    });

    //删除商品
    $(".deleteCartItem").each(function (i) {
        var _this = $(this);
        _this.click(function (e) {
            e.preventDefault();
            new Ejs.Dialog({
                type: Ejs.Dialog.type.WARN,
                title: "确认不购买此商品吗？",
                isModal: true,
                opacity: 0,
                titleStyle: {
                    fontWeight: "normal",
                    paddingLeft: 5,
                    paddingTop: 15
                },
                titleWrapperStyle: {
                    paddingTop: 20
                },
                info: "",
                infoStyle: {
                    height: 10,
                    overflow: "hidden"
                },
                isConfirm: true,
                eventObj: e,
                afterClose: function (v) {
                    if (v) {
                        var _url = _this.attr("href");
                        $.ajax({
                            type: "POST",
                            url: _url,
                            async: false,
                            success: function (data) {
                                var _data = eval("(" + data + ")");
                                if (_data["success"]) {
                                    var _item = _this.parents('.row');
                                    var _item_li = _item.parents('li.clearfix');
                                    var _item_table = _item.parents('div.table');
                                    $("#cartTotalPrice").text(_data["data"]["totalPrice"]);
                                    _item.slideUp(600, function () {
                                        _item.remove();
                                        if (_item_li.find('.row').length < 1) {
                                            _item_li.slideUp(300, function () {
                                                _item_li.remove();
                                                if (_item_table.find("li.clearfix").length < 1) {
                                                    _item_table.slideUp(300, function () {
                                                        _item_table.remove();
                                                        if ($(".shopping_car_box_1>.table").length < 1) {
                                                            location.href = location.href;
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            var _h = (_item_li.find(".row").length - 1) * 119 + 89;
                                            _item_li.find(".tr_Head").css({
                                                lineHeight: _h + "px",
                                                height: _h + "px"
                                            });
                                        }
                                    });
                                    return false;
                                } else {
                                    new Ejs.Dialog({
                                        title: "提示信息",
                                        type: Ejs.Dialog.type.ERROR,
                                        info: _data["msg"]
                                    });
                                }
                            }
                        });
                    }
                }
            });
        });
    });

    //提交订单
    $("#toOrder").click(function (e) {
        e.preventDefault();
        Ejs.UserStatus.isLogin(function () {
            $.ajax({
                type: "GET",
                async: false,
                url: $("#toOrder").attr("checkurl"),
                success: function (_data) {
                    var _res = eval("(" + _data + ")");
                    if (_res["success"]) {
                        location.href = $("#toOrder").attr("href");
                    } else {
                        var _msg = _res["data"]["errorMsg"];
                        var _html = '';
                        $(_msg).each(function (i) {
                            _html += '<p>' + _msg[i] + '</p>';
                        });
                        new Ejs.Dialog({
                            title: "商品信息提示",
                            type: Ejs.Dialog.type.ERROR,
                            info: _html,
                            buttons: [
                                {
                                    buttonText: "修改商品数量"
                                }
                            ]
                        });
                    }
                }
            });
        }, function () {
            /*location.href = EJS.ToPageLogin + "?backUrl=" + $("#toOrder").attr("href");*/
            Ejs.UserWindows = new Ejs.UserWindow({
                backUrl: encodeURIComponent($("#toOrder").attr("href"))
            });
        });

    });

    //删除顶部购物车
    $("#header_cart").remove();

    // 关闭发货提示
    var deliveryTips = $("#delivery_tips");
    if (deliveryTips.length > 0) {
        deliveryTips.find(".close").click(function () {
            deliveryTips.fadeOut(500);
        })
    }
});
