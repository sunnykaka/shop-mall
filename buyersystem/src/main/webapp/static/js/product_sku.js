/*Dom finshed*/

$(function () {

    if (!(typeof skuMap == 'undefined')) {
        Ejs.Sku.View({
            priceEle: "#productPrice",
            amountEle: "#amount",
            integralEle: "#integral",
            amountInputEle: "#buy_number",
            addToCartBtn: "#addToCart",
            hasSkuMap: {
                skuMapEle: "#choose",
                model: Ejs.Sku.Model(skuMap),
                skuMapData: skuMap
            }
        });
    } else {
        Ejs.Sku.View({
            priceEle: "#productPrice",
            amountEle: "#amount",
            amountInputEle: "#buy_number",
            addToCartBtn: "#addToCart",
            hasSkuMap: false
        });
    }


    // 商品摘要 - 图片
    if ($('#photo_thumb li').length > 4) {
        $('#photo_thumb').jcarousel({
            vertical: false,
            scroll: 3
        });
    }

    //图片放大镜效果
    $('#jqzoombox').jqzoom({
        xOffset: 10,
        yOffset: 0,
        preloadImages: false,
        zoomWidth: 350,
        zoomHeight: 350,
        title: false,
        preloadText: 'Loading Image',
        imageOpacity: 0.1
    });

    var assetsDomain = EJS.StaticDomain;
    if (!!assetsDomain == false) {
        assetsDomain = "assets";
    }

    //商品星极
    $(".product_sku").eq(0).find('.star').each(function () {
        var _this = $(this);
        if (!!_this.attr("hint")) {
            var _hint = parseInt(_this.attr("hint"));
            _this.raty({
                width: 92,
                starOff: assetsDomain + '/stylesimg/star-off.png',
                starOn: assetsDomain + '/stylesimg/star-on.png',
                hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                readOnly: true,
                score: _hint,
                scoreName: 'score'
            });
        }
    });

    // 加入收藏
    $("#addMyFavorites").click(function (E) {
        E.preventDefault();
        if (typeof EJS.AddMyFavorites != 'undefined') {
            var _url = EJS.AddMyFavorites + "?productId=" + $(this).attr("val");
            var _ProductUrl = EJS.ProductDetailBase + "/" + $(this).attr("val");
            Ejs.UserStatus.isLogin(function () {
                $.ajax({
                    type: "POST",
                    url: _url,
                    async: false,
                    dataType: 'json',
                    success: function (data) {
                        var _number = 0;
                        $.ajax({
                            type: "POST",
                            url: EJS.QueryUserFavoritesNum,
                            async: false,
                            success: function (res_num) {
                                res_num = eval("(" + res_num + ")");
                                _number = res_num["data"]["productCollectNum"];
                            }
                        });

                        if (data["success"]) {
                            new Ejs.Dialog({
                                title: "商品收藏成功",
                                type: Ejs.Dialog.type.RIGHT,
                                info: "您已经收藏" + _number + "个商品！",
                                buttons: [
                                    {
                                        buttonText: "继续购物",
                                        buttonClass: "com_button_green_c"
                                    },
                                    {
                                        buttonText: "查看收藏夹",
                                        buttonClass: "com_button_green_d",
                                        buttonStyle: {
                                            marginRight: 0
                                        },
                                        onClick: function () {
                                            location.href = EJS.MyFavorites;
                                        }
                                    }
                                ]
                            });
                        } else {
                            new Ejs.Dialog({
                                title: "您已经收藏此商品",
                                type: Ejs.Dialog.type.ERROR,
                                info: "您已经收藏" + _number + "个商品！",
                                buttons: [
                                    {
                                        buttonText: "继续购物",
                                        buttonClass: "com_button_green_c"
                                    },
                                    {
                                        buttonText: "查看收藏夹",
                                        buttonClass: "com_button_green_d",
                                        buttonStyle: {
                                            marginRight: 0
                                        },
                                        onClick: function () {
                                            location.href = EJS.MyFavorites;
                                        }
                                    }
                                ]
                            });
                        }
                    }
                });
            }, function () {
                location.href = EJS.ToPageLogin + "?backUrl=" + encodeURIComponent(_ProductUrl); //跳转到登录页面
            });
        }
    });
});