// 配件加入购物车 弹出层
var followDialog = function (ele, title, content) {
    var _box = $("<div></div>"),
        _title = $("<h3></h3>"),
        _content = $("<div></div>"),
        _button = $("<div></div>");
    var _amount = '<div class="amount_box">' +
        '   <dl class="amount clearfix" id="pop_amount">' +
        '       <dt>购买数量：</dt>' +
        '       <dd>' +
        '           <a href="javascript:void(0);" class="btn_sub"><span>减</span></a>' +
        '           <div class="number_wrap">' +
        '               <input type="text" name="buy_number" id="popBuyNumber"  class="text" value="1" />' +
        '            </div>' +
        '           <a href="javascript:void(0);" class="btn_add"><span>加</span></a>' +
        '           <div class="price" id="popProductPrice">价格：<span>---</span>元</div>' +
        '       </dd>' +
        '   </dl>' +
        '</div>';

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
            height: 224
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
    };
};

$(function () {
    // 配件加入购物车
    $(".accessories_recommended").eq(0).find(".pic").each(function () {
        var $this = $(this);
        $this.hover(function () {
            var proToCart = $this.find(".product_to_cart");
            var proToCartUrl = proToCart.attr("href");
            var proItem = $this.parents("li");
            proToCart.show();
            proToCart.unbind();
            proToCart.bind("click", function (E) {
                E.preventDefault();
                $.ajax({
                    type: "GET",
                    url: proToCartUrl,
                    async: false,
                    dataType: 'json',
                    success: function (data) {
                        if (data["success"]) {
                            if (typeof data["data"]["cartUrl"] != "undefined") {

                                // 单SKU商品直接加入购物车
                                window.location.href = data["data"]["cartUrl"];
                            } else {

                                //多SKU需弹出选择SKU的对话框
                                if (data["data"]["canAdd"] == false) {
                                    var __skuMap = data["data"]["skuMap"];
                                    var __defaultSku = data["data"]["defaultSku"];
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
                        } else {

                            // 加入购物车失败
                            new Ejs.Dialog({
                                title: "提示信息",
                                type: Ejs.Dialog.type.ERROR,
                                info: data["msg"],
                                isConfirm: false,
                                buttons: [
                                    {
                                        buttonText: "确认",
                                        buttonClass: "button_b",
                                        onClick: function () {

                                        }
                                    }
                                ],
                                isModal: true
                            });
                        }
                    }
                });
            });
        }, function () {
            $(this).find(".product_to_cart").hide();
        });
    });
});
