
function loadingOrderList(url) {
    if ($(".loadding").length < 1) {
        $("#ucOrder").append('<div class="loadding"></div>');
    }
    $(".loadding").show();
    var _box = $("#uc_order_list");
    _box.html("");
    $.ajax({
        type: "POST",
        url: url,
        async: false,
        cache: false,
        dataType: 'html',
        success: function (data) {
            _box.html(data);
            $(".loadding").fadeOut(400);

            // 合并付款
            $("#combinedPayment").click(function (E) {
                E.preventDefault();
                var orderNo = "";
                var combinedPaymentUrl = $("#combinedPayment").attr("href");
                $("input[name=orderId]:checked").each(function () {
                    if ($(this).val() != 0) {
                        if (orderNo == "") {
                            orderNo = $(this).val();
                        }
                        else {
                            orderNo += "," + $(this).val();
                        }
                    }
                });
                if (orderNo == 0 || orderNo == "") {
                    Ejs.tip($(this), "limit_tip", "请选择您要合并付款的商品", 0, 80);
                } else {
                    window.location.href = combinedPaymentUrl + "/" + orderNo;
                }
            });

            // 选中效果 - 全选 - 价格计算
            (function () {
                var ucList = $("#uc_order_list"),
                    checkBox = ucList.find("tbody").find("input[name=orderId]");
                    checkBox.click(function (e) {
                        getPrice();
                    });
                    function getPrice() {
                        var price = 0, total = ucList.find(".price");
                        for (var i = 0, len = checkBox.length; i < len ; i++) {
                            if (checkBox[i].checked) {
                                price += Number(checkBox.eq(i).attr("data-price"));
                            }
                        };
                        total.text("￥" + price.toFixed(2));
                    };

                $(".selectAll").find("input[name=orderId]").click(function () {
                    $("input[name=orderId]").attr('checked', this.checked);
                    var trArr = ucList.find("tr");
                    if ($(this)[0].checked) {
                        trArr.addClass("selected");
                    } else {
                        trArr.removeClass("selected");
                    };
                    getPrice();
                });
            })();


            // 确认收货
            confirmReceipt();

            // 取消订单操作
            var cancelUrl = EJS.OrderCancelUrl;
            var cancelItems = $("a.cancelOrder");
            cancelItems.each(function () {
                var _this = $(this);
                _this.click(function (e) {
                    e.preventDefault();
                    var _tr = _this.parents("tr");

                    Ejs.dialogConfirm(e, '确定取消吗？', function(){
                        $.ajax({
                            type: "POST",
                            url: cancelUrl,
                            data: {
                                orderId: _this.attr("data-id")
                            },
                            dataType: "json",
                            success: function (_el) {
                                if (_el["success"]) {
                                    var payNotNum = $("#payNotNum"), voidNum = $("#voidNum");
                                    payNotNum.html(+(payNotNum.text()) - 1);
                                    voidNum.html(+(voidNum.text()) + 1);
                                    _tr.find("td").fadeOut(600, function () {
                                        loadingOrderList(url);
                                    });
                                } else {
                                    Ejs.dialogAlert(_el["msg"]);
                                }
                            }
                        });
                    });

                });
            });

            // 分页
            $(".pagebars").eq(0).find("a").each(function () {
                var _this = $(this);
                _this.click(function (E) {
                    E.preventDefault();
                    if ($(this).hasClass("current")) return;
                    var pageUrl = _this.attr("href");
                    loadingOrderList(pageUrl);
                });
            });
        },
        error: function () {
            _box.html("载入失败");
            $(".loadding").fadeOut(400);
        }
    });
}

function orderInit() {
    var odTabs = $("#ucOrder").find(".tabs").eq(0);
    var firstUrl = odTabs.find(".current").find("a").eq(0).attr("href");
    if (firstUrl) {
        loadingOrderList(firstUrl);
    }
    odTabs.find("a").each(function () {
        var $this = $(this);
        $this.click(function (E) {
            E.preventDefault();
            var li = $this.parents("li");
            li.addClass("current");
            li.siblings("li").removeClass("current").removeClass("left");
            li.prevAll("li").addClass("left");
            var url = $(this).attr("href");
            loadingOrderList(url);
        });
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

// 确认收货
function confirmReceipt() {
    var confirmUrl = EJS.ConfirmReceipt;
    var confirmItems = $(".confirmReceipt");
    confirmItems.each(function () {
        var _this = $(this);
        _this.click(function (e) {
            e.preventDefault();
            var _tr = _this.parents("tr");

            Ejs.dialogConfirm(e, '您确定已经收到货了吗？', function(){
                $.ajax({
                    type: "POST",
                    url: confirmUrl,
                    data: {
                        orderId: _this.attr("data-id")
                    },
                    dataType: "json",
                    success: function (_el) {
                        if (_el["success"]) {
                            window.location.href = _el["data"]["url"];
                        } else {
                            new Ejs.Dialog({
                                title: "操作提示信息!",
                                type: Ejs.Dialog.type.ERROR,
                                info: _el["msg"]
                            });
                        }
                    }
                });
            });

        });
    });
}


function orderComplete() {

    var assetsDomain = EJS.StaticDomain;
    if (!!assetsDomain == false) {
        assetsDomain = "assets";
    }
    $('.star').each(function () {
        var _this = $(this);
        if (!!_this.attr("hint")) {
            var _hint = parseInt(_this.attr("hint"));
            _this.raty({
                width: 200,
                starOff: assetsDomain + '/stylesimg/star-off-big.png',
                starOn: assetsDomain + '/stylesimg/star-on-big.png',
                hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                target: _this.next(".hint").eq(0),
                targetKeep: true,
                readOnly: true,
                score: _hint,
                scoreName: 'point'
            });
        } else {
            _this.raty({
                width: 200,
                starOff: assetsDomain + '/stylesimg/star-off-big.png',
                starOn: assetsDomain + '/stylesimg/star-on-big.png',
                hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                target: _this.next(".hint").eq(0),
                targetKeep: true,
                readOnly: false,
                score: 5,
                scoreName: 'point'
            });
        }
    });

    $("textarea[name=content]").each(function () {
        var _this = $(this);
        _this.focus(function () {
            if (_this.hasClass("txt_def")) {
                _this.val("").removeClass("txt_def");
            }
        });
    });

    $(".comment_form").each(function () {
        var _this = $(this);
        _this.submit(function (e) {
            e.preventDefault();
            var _txtEle = _this.find("textarea[name=content]");
            if (_txtEle.hasClass("txt_def") || $.trim(_txtEle.val()).length < 10 || $.trim(_txtEle.val()).length > 2000) {
                _this.find(".msg").text("评价内容必须在10-2000字之间!");
            } else {
                _this.find(".msg").text("");
                var _url = _this.attr("action");
                var _hint = _this.find("input[name=point]").val();
                var _content = _this.find("textarea[name=content]").val();
                $.ajax({
                    type: "POST",
                    url: _url,
                    data: _this.serialize(),
                    dataType: "json",
                    success: function (_el) {
                        if (_el["success"]) {
                            //提交成功
                            _this.find("dl").eq(0).find("dd").html('<div class="star" hint="' + _hint + '"></div><span class="hint"></span>');
                            _this.find("dl").eq(1).find("dd").html(_content);
                            _this.find(".star").raty({
                                width: 200,
                                starOff: assetsDomain + '/stylesimg/star-off-big.png',
                                starOn: assetsDomain + '/stylesimg/star-on-big.png',
                                hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                                target: _this.find(".hint").eq(0),
                                targetKeep: true,
                                readOnly: true,
                                score: _hint,
                                scoreName: 'point'
                            });

                        } else {
                            _this.find(".msg").html(_el["msg"]);
                        }
                    }
                });
            }

        });
    });
}

//查看订单状态
(function () {
    $("#uc_order_list").on("mouseover", ".view_order", function () {
        var self = this, parent = $(this).parent();
        var orderId = $(this).attr("date-order");
        $(".tips").remove();
        self.time = null;
        clearTimeout(self.time);
        self.time = setTimeout(function () {
            $.ajax({
                type: "POST",
                url: "/order/info/" + orderId + "?top=5",
                dataType: "json",
                success: function (data) {
                    if (data["success"]) {
                        var list = data["data"]["result"],
                            html = "",
                            i;
                        html += "<div class=\"tips\">"
                            + "<span class=\"ico\"></span>"
                            + "<div class=\"tips_let\">"
                            + "<span>" + list[0].detail + "</span>"
                            + "<h4>订单跟踪</h4>"
                            + "<table width=\"100%\">"
                            + "<thead>"
                            + "<tr>"
                            + "<td>处理时间</td>"
                            + "<td>处理记录</td>"
                            + "</tr>"
                            + "</thead>"
                            + "<tbody>";

                        for (i = list.length - 1; i >= 0; i--) {
                            html += "<tr>"
                                 + "<td>" + list[i].date + "</td>"
                                 + "<td>" + list[i].detail + "</td>"
                                 + "</tr>";
                        }
                        html += "</tbody>"
                            + "</table>"
                            + "</div>"
                            + "</div>";
                        parent.find(".tips").remove();
                        parent.append(html);
                    }
                },
                error: function () {
                    var html = "<div class=\"tips\">"
                            + "<span class=\"ico\"></span>"
                            + "<div class=\"tips_let\">"
                            + "error"
                            + "</div>"
                            + "</div>";
                    parent.find(".tips").remove();
                    parent.append(html);
                }
            });
        }, 200);

    })
        .on("mouseout", ".view_order", function () {
            self.time = setTimeout(function () {
                $(".tips").remove();
            }, 300);
        });
}());

function loadOrderInfo(orderId) {
    "use strict";

    if (typeof orderId === "undefined") {
        return;
    }

    var box = $("#order_status");

    if (box.length < 1) {
        return;
    }

    $.ajax({
        type: "POST",
        url: " /order/info/" + orderId,
        dataType: "json",
        success: function (data) {
            if (data["success"]) {
                var list = data["data"]["result"],
                    html = "",
                    i;
                html += '<h4>订单信息</h4><table class="table">' +
                    '<tr><th class="s1">处理时间</th><th class="s2">处理人</th><th class="s3">处理信息</th></tr>';
                for (i = 0; i < list.length; i++) {
                    html += '<tr><td class="s1">' + list[i].date + '</td>' +
                        '<td class="s2">' + list[i].operator + '</td>' +
                        '<td class="s3">' + list[i].detail + '</td></tr>';
                }
                html += '</table>';
                box.html(html);
            }
        },
        error: function () {
            box.html("订单信息加载失败");
        }
    });

}

function loadOrderlist(orderId) {
    "use strict";

    if (typeof orderId === "undefined") {
        return;
    }

    var box = $("#warranty_list");

    if (box.length < 1) {
        return;
    }

    $.ajax({
        type: "POST",
        url: "/order/info/" + orderId,
        dataType: "json",
        success: function (data) {

            if (data["success"]) {
                var list = data["data"]["result"],
                    html = "",
                    firstClass = "",
                    i;
                for (i = list.length - 1; i >= 0; i--) {
                    firstClass = i == 0 ? "my_warranty_first" : "";
                    html += "<li class=\"" + firstClass + " clearfix\">"
                         + "<span class=\"warranty_time\">" + list[i].date + "</span>"
                         + "<span class=\"warranty_text\">" + list[i].detail + "</span>"
                         + "</li>";
                };
                box.html(html);
            }
        },
        error: function () {
            box.html("订单信息加载失败");
        }
    });
}

//再次购买/重新购买
$(".addbuy").click(function (e) {
    e.preventDefault();
    $.ajax({
        type: "GET",
        url: $(this).attr("href"),
        async: false,
        dataType: 'json',
        success: function (data) {
            if (data["success"]) {

                Ejs.dialogAlert('物品已经成功添加到购物车', null, [
                    {
                        buttonText: "继续购物",
                        buttonClass: "e-btn btn-grey",
                        buttonStyle: {
                            'margin-right': '10px'
                        },
                        onClick: function () {

                        }
                    },
                    {
                        buttonText: "去结算",
                        buttonClass: "e-btn btn-default",
                        onClick: function () {
                            window.location.href = data["data"]["cartUrl"];
                        }
                    }
                ]);
            }
            else {
                Ejs.dialogAlert(data.msg, null);
            }
        }
    });
});
