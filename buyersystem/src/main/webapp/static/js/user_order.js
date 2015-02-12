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
                    if (orderNo == "") {
                        if ($(this).val() != 0) {
                            orderNo = $(this).val();
                        }
                    } else {
                        orderNo += "," + $(this).val();
                    }
                });
                if (orderNo == 0 || orderNo == "") {
                    Ejs.tip($(this), "limit_tip", "请选择您要合并付款的商品", 0, 80);
                } else {
                    window.location.href = combinedPaymentUrl + "/" + orderNo;
                }
            });

            // 选中效果 - 单选
            $("input[name=orderId]").each(function () {
                var _this = $(this);
                //console.log(_td);
                _this.click(function () {
                    var _tr = _this.parents("tr");
                    var _trs = _tr.nextAll("tr");
                    var _rowspan = _this.parents("td").attr("rowspan");
                    if (_this[0].checked) {
                        _tr.addClass("selected");
                        if (typeof _rowspan !== "undefined" && _rowspan > 1) {
                            _rowspan -= 1;
                            for (_i = 0; _i < _rowspan; _i++) {
                                _trs.eq(_i).addClass("selected");
                            }
                        }
                    } else {
                        _tr.removeClass("selected");
                        if (typeof _rowspan !== "undefined" && _rowspan > 1) {
                            _rowspan -= 1;
                            for (_i = 0; _i < _rowspan; _i++) {
                                _trs.eq(_i).removeClass("selected");
                            }
                        }
                    }
                });

            });

            // 选中效果 - 全选
            $("#selectAll").click(function () {
                $("input[name=orderId]").attr('checked', this.checked);
                var trArr = $("#uc_order_list tr");
                if ($(this)[0].checked) {
                    trArr.addClass("selected");
                } else {
                    trArr.removeClass("selected");
                }
            });

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
                    new Ejs.Dialog({
                        type: Ejs.Dialog.type.WARN,
                        title: "确定取消吗？",
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
                                $.ajax({
                                    type: "POST",
                                    url: cancelUrl,
                                    data: {
                                        orderId: _this.attr("data-id")
                                    },
                                    dataType: "json",
                                    success: function (_el) {
                                        if (_el["success"]) {
                                            _tr.find("td").fadeOut(600, function () {
                                                loadingOrderList(url);
                                            });
                                        } else {
                                            new Ejs.Dialog({
                                                title: "操作提示信息!",
                                                type: Ejs.Dialog.type.ERROR,
                                                info: _el["msg"]
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                });
            });

            // 分页
            $(".pagebar").eq(0).find("a").each(function () {
                var _this = $(this);
                _this.click(function (E) {
                    E.preventDefault();
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
            new Ejs.Dialog({
                type: Ejs.Dialog.type.WARN,
                title: "您确定已经收到货了吗？",
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
                        $.ajax({
                            type: "POST",
                            url: confirmUrl,
                            data: {
                                orderId: _this.attr("data-id")
                            },
                            dataType: "json",
                            success: function (_el) {
                                if (_el["success"]) {
                                    /*if($("#uc_order_list").length > 0){
                                     _tr.find("td").fadeOut(600, function () {
                                     loadingOrderList(url);
                                     });
                                     }else{
                                     window.location.reload();
                                     }*/
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
                    }
                }
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

(function () {
    var running = null;
    $("#uc_order_list").on("mouseover", ".view_order", function () {
        var self = this,
            viewMoreUrl = $(this).attr("href");

        self.t = new Tips2({
            obj: $(this),
            content: "正在加载...",
            style: "logistics_tips"
        });
        var orderId = $(this).attr("date-order");
        $.ajax({
            type: "POST",
            url: " /order/info/" + orderId + "?top=5",
            dataType: "json",
            success: function (data) {
                if (data["success"]) {
                    var list = data["data"]["result"],
                        html = "",
                        i;
                    html += '<table>' +
                        '<tr><th>处理时间</th><th>处理人</th><th>处理信息</th></tr>';
                    for (i = 0; i < list.length; i++) {
                        html += '<tr><td class="s1">' + list[i].date + '</td>' +
                            '<td class="s2">' + list[i].operator + '</td>' +
                            '<td class="s3">' + list[i].detail + '</td></tr>';
                    }
                    html += '<tr><td colspan="3" class="more"><a href="' + viewMoreUrl + '">查看更多&gt;</a></td></tr></table>';
                    self.t.changeContent(html);
                }
            },
            error: function () {
                self.t.changeContent("error");
            }
        });
    })
        .on("mouseout", ".view_order", function () {
            var self = this;
            if (running) {
                clearTimeout(running);
            }
            running = setTimeout(function () {
                $(document).on("mousemove click", function (e) {
                    if ($(e.target).parents(".logistics_tips").length < 1 && !$(e.target).hasClass(".view_order")) {
                        self.t.remove();
                        $(document).off("mousemove click");
                    }
                });
            }, 800);
        });
}());

function loadOrderInfo(orderId){
    "use strict";

    if(typeof orderId === "undefined"){
        return;
    }

    var box = $("#order_status");

    if(box.length < 1){
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

function loadOrderlist(orderId){
    "use strict";

    if(typeof orderId === "undefined"){
        return;
    }

    var box = $("#warranty_list");

    if(box.length < 1){
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
                html += '<h4>订单信息</h4><table class="table">'  ;
                for (i = 0; i < list.length; i++) {
                    html += '<tr><td class="s1">' + list[i].date + '</td>' +
                        '<td class="s2">' + list[i].detail + '</td></tr>';
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






