// 收藏加入购物车 弹出层
var followDialog = function (ele, title, content) {
    var _box = $("<div></div>"),
        _title = $("<h3></h3>"),
        _content = $("<div></div>"),
        _button = $("<div></div>");
    var _amount = '<div class="amount_box">'
        + '<dl class="amount clearfix" id="pop_amount">'
        + ' <dt>购买数量：</dt>'
        + ' <dd>'
        + '     <a href="javascript:void(0);" class="btn-sub"><span>减</span></a>'
        + '     <div class="number_wrap">'
        + '         <input type="text" name="buy_number" id="popBuyNumber"  class="text" value="1" />'
        + '     </div>'
        + '     <a href="javascript:void(0);" class="btn-add"><span>加</span></a>'
        + '     <div class="price" id="popProductPrice">价格：<span>---</span>元</div>'
        + ' </dd>'
        + '</dl>'
        + '</div>';

    _box.attr("id", "quickSelectProduct");
    _title.addClass("title").html(title).appendTo(_box);
    _content.addClass("content").html("<div class='attribute_box' id='attributeBox'>" + content + "</div>" + _amount).appendTo(_box);
    _button.addClass("button").html("<a href='#' class='common_btn' id='popAddToCart'><span>确定</span></a> ").appendTo(_box);

    var _this = this;
    _this._layer = new com.layer({
        trigger: ele,
        content: _box,
        loaded: false,
        cache: true,
        isModal: true,
        shadeColor: "#000",
        opacity: 0.3,
        outClass: "pop_select_product",
        contentClass: 'content_wrapper',
        closeClass: "close",
        closeText: '<a class="fix_ie6" href="#"></a>',
        style: {
            position: "absolute",
            backgroundColor: "transparent",
            border: "0",
            overflow: "visible",
            width: 408,
            height: 254
        },
        triggerType: com.layer.triggerType.click,
        positionType: com.layer.positionType.margin,
        showAction: com.layer.showAction.fade,
        hideAction: com.layer.hideAction.fade,
        actionSpeed: 400,
        closeTimeout: 800,
        isBgClose: true,
        offset: {
            x: -400,
            y: 0
        },
        onshowing: function () {
        },
        onclosing: function () {
            $("#" + _this._layer.getId()).remove();
        }
    });
    _this.remove = function () {
        this._layer.hide();
        $('#' + _this._layer.getId()).remove();
    }
};

$(document).ready(function () {
    //分页
    var _pageNum = $("#PageNumber");
    var _regNum = /^\d+$/;
    _pageNum.blur(function () {
        if (!_regNum.test(_pageNum.val())) {
            _pageNum.val(1);
        }
        if (parseInt(_pageNum.val()) > parseInt(_pageNum.attr("val"))) {
            _pageNum.val(_pageNum.attr("val"));
        }
    });

    //关注 单个删除
    $('.cancelProductCollect').on('click', function (E) {
        E.preventDefault();

        var self = $(this),
            cancelFavUrl = self.attr('href'),
            tr = self.parents('tr');

        Ejs.dialogConfirm(E, '确定要删除?', function () {
            $.ajax({
                type: "POST",
                url: cancelFavUrl,
                async: false,
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        tr.find('td').fadeOut(600, function () {
                            window.location.reload();
                        });
                    } else {
                        Ejs.dialogAlert(data.msg);
                    }
                }
            });
        });

    });

    // 加入购物车
    $(".addbuy").each(function () {
        var proToCart = $(this);
        var proToCartUrl = proToCart.attr("href");
        var proItem = proToCart.parents(".col6");
        proToCart.bind("click", function (E) {
            E.preventDefault();
            $.ajax({
                type: "GET",
                url: proToCartUrl,
                async: false,
                dataType: 'json',
                success: function (data) {

                    if (data["success"]) {

                        if (typeof data["data"]["cartUrl"] != "undefined") { // 单SKU商品直接加入购物车
                            Ejs.dialogAlert('物品已经成功添加到购物车', null, [
                                {
                                    buttonText: "继续购物",
                                    buttonClass: "e-btn btn-grey",
                                    buttonStyle: {
                                        'margin-right': '10px'
                                    },
                                    onClick: function () {
                                        Ejs.Cart.getNumber();
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
                        } else { //多SKU需弹出选择SKU的对话框
                            if (data["data"]["canAdd"] == false) {
                                //alert("此商品有多个SKU");
                                var __skuMap = data["data"]["skuMap"];
                                var __defaultSku = data["data"]["defaultSku"];
                                //alert(__skuMap.toSource());
                                //console.log(data);
                                //console.log(__skuMap);
                                var __domUrl = data["data"]["skuUrl"];
                                $.ajax({
                                    type: "GET",
                                    url: __domUrl,
                                    async: false,
                                    dataType: 'html',
                                    success: function (HTML) {
                                        var _followDialog = new followDialog(proItem, "请选择您所需要的商品属性！", HTML);
                                        Ejs.Sku.View({
                                            priceEle: "#popProductPrice span",
                                            amountEle: "#pop_amount",
                                            amountInputEle: "#popBuyNumber",
                                            addToCartBtn: "#popAddToCart",
                                            isDirectBuy: false,
                                            buyCallback: function () {
                                                _followDialog.remove();
                                                Ejs.Cart.getNumber();
                                            },
                                            hasSkuMap: {
                                                skuMapEle: "#attributeBox",
                                                model: Ejs.Sku.Model(__skuMap),
                                                skuMapData: __skuMap
                                            }
                                        });

                                    }
                                });

                            }
                        }

                    } else { // 加入购物车失败
                        Ejs.dialogAlert(data.msg, null);
                    }

                }
            });
        });
    });

    //IE6显示加入购物车按钮
    if ($.ie6()) {
        $(".table").find("tr").hover(function () {
            $(this).addClass("current");
        }, function () {
            $(this).removeClass("current");
        });
    }

    //关注 全选删除
    (function () {
        var tbody = $("#favoritesList").find("tbody");
        if (tbody.length == 0) return;
        var checkBoxEle = $(".odd_check_box"),
            checkBoxBtn = $(".a_checkbox");

        checkAll(checkBoxEle, checkBoxBtn);

        function checkAll(checkBoxEle, checkBoxBtn) {
            var sw = false, sw2 = true;

            function test(boole) {
                for (var i = 0, len = checkBoxEle.length; i < len; i++) {
                    checkBoxEle[i].checked = boole;
                }
            }

            function checkBox(onself) {
                if ((checkBoxBtn[0].checked || checkBoxBtn[1].checked) && sw2) {
                    sw = true;
                    sw2 = false;
                }
                else {
                    sw = false;
                    sw2 = true;
                }
                checkBoxBtn[0].checked = checkBoxBtn[1].checked = sw;
                onself.checked ? test(true) : test(false);
            }
            for (var i = 0, len = checkBoxEle.length; i < len; i++) {
                checkBoxEle[i].onclick = function () {
                    checkBoxBtn[0].checked = checkBoxBtn[1].checked = false;
                };
            }

            checkBoxBtn[0].onclick = checkBoxBtn[1].onclick = function () {
                checkBox(this);
            };
        }

        $(".delete_btn").click(function (E) {
            E.preventDefault();
            var ids = [], trIndex = [], sw = false, _tr = tbody.find("tr");

            checkBoxEle.each(function (i) {
                if (checkBoxEle[i].checked) {
                    trIndex.push(i);
                    ids.push(checkBoxEle[i].value);
                    sw = true;
                }
            });

            if (sw) {
                Ejs.dialogConfirm(E, '确定要删除?', function () {
                    $.ajax({
                        type: "POST",
                        url: "/my/favorites/delete/batch",
                        dataType: "json",
                        data: "ids=" + ids.join(","),
                        success: function (data) {
                            if (data["success"]) {
                                for (var i = 0; i < trIndex.length; i++) {
                                    _tr.eq(trIndex[i]).fadeOut(600, function () {
                                        window.location.reload();
                                    });
                                };
                            }
                        }
                    });
                });

            } else {
                Ejs.dialogAlert('请选择需要删除的商品');
            }

        });
    })();
});