/**
 * Created by HuaLei.Du on 2014/5/22.
 */

(function ($) {

    var goodsSku = $('.mod-goodsSku');

    // 选择SKU
    if (!(typeof skuMap == 'undefined')) {
        Ejs.Sku.View({
            priceEle: '#product-price',
            amountEle: '#amount',
            integralEle: '#integral',
            descEle: '#skuDesc',//2014-07-29新增描述
            amountInputEle: '#buy-number',
            addToCartBtn: '#addToCart',
            hasSkuMap: {
                skuMapEle: '#choose-sku',
                model: Ejs.Sku.Model(skuMap),
                skuMapData: skuMap
            }
        });
    } else {
        Ejs.Sku.View({
            priceEle: '#product-price',
            amountEle: '#amount',
            amountInputEle: '#buy-number',
            addToCartBtn: '#addToCart',
            hasSkuMap: false
        });
    }

    // 商品摘要 - 图片
    (function () {
        var photoThumb = $('#photo-thumb');
        if (photoThumb.find('li').length > 4) {
            photoThumb.jcarousel({
                vertical: false,
                scroll: 3
            });
        }
    }());

    // 图片放大镜
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

    // 商品星级
    (function () {
        var assetsDomain = EJS.StaticDomain || 'assets';

        goodsSku.eq(0).find('.star').each(function () {
            var self = $(this),
                hint;

            if (self.attr('hint')) {
                hint = self.attr('hint');
                self.raty({
                    width: 92,
                    starOff: assetsDomain + '/stylesimg/star-off.png',
                    starOn: assetsDomain + '/stylesimg/star-on.png',
                    starHalf: assetsDomain + '/stylesimg/star-half.png',
                    hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                    readOnly: true,
                    score: hint,
                    scoreName: 'score'
                });
            }

        });

        goodsSku.on('click', '.star-count', function () {
            goodsDetails.tab().showComment();
        });

    }());


    goodsSku.find('.share-wrap').hover(function () {
        $(this).find('.bdsharebuttonbox').show();
    }, function () {
        $(this).find('.bdsharebuttonbox').hide();
    });

    // 加入收藏
    $('.btn-follow').on('click', function () {

        if (typeof EJS.AddMyFavorites !== 'undefined') {

            var self = $(this),
                url,
                productUrl,
                dialogButtons;

            if (self.hasClass('btn-follow-selected')) {
                return;
            }

            url = $(this).attr('data-url');
            productUrl = window.location.href;
            dialogButtons = [
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
            ];

            Ejs.UserStatus.isLogin(function () {
                $.ajax({
                    type: 'POST',
                    url: url,
                    dataType: 'json',
                    success: function (data) {

                        var count = data.data.productCollectNum;
                        if (data.success) {
                            // 收藏成功
                            self.addClass('btn-follow-selected');
                            new Ejs.Dialog({
                                title: "商品收藏成功",
                                type: Ejs.Dialog.type.RIGHT,
                                info: "您已经收藏" + count + "个商品！",
                                buttons: dialogButtons
                            });
                        } else {
                            // 已经收藏此商品
                            new Ejs.Dialog({
                                title: "您已经收藏此商品",
                                type: Ejs.Dialog.type.ERROR,
                                info: "您已经收藏" + count + "个商品！",
                                buttons: dialogButtons
                            });
                        }

                    }
                });
            }, function () {
                new Ejs.UserWindow({
                    actionType: 'updateUserInfo',
                    backUrl: window.location.href
                });
            });
        }

    });

}(jQuery));