
$(document).ready(function () {
    var pageSize = {
        width: $(".product_contrast").outerWidth(),
        height: $(".product_contrast").outerHeight()
    };
    window.parent.Ejs.ContrastWindows.reSize(pageSize);
    //移除对比
    $('.remove').each(function (i) {
        $(this).click(function (E) {
            var _this = $(this);
            E.preventDefault();
            $(".product_contrast tr").each(function () {
                $(this).find('td').eq(i).hide();
                // 从cookie中移除
                var domJSON = $.cookie('contrastData');
                if (domJSON != null) {
                    domJSON = jQuery.parseJSON($.cookie('contrastData'));
                    var domList = domJSON["list"];
                    var _id = _this.attr('pid');
                    for (var ii = 0; ii < domList.length; ii++) {
                        if (domList[ii]['id'] == _id) {
                            domList.splice(ii, 1);
                            if (domList.length > 0) {
                                $.cookie('contrastData', JSON.stringify(domJSON), { path: '/' });
                                if (domList.length == 2) {
                                    $(".product_contrast table").addClass("twoProducts");
                                } else if (domList.length == 1) {
                                    $(".product_contrast table").removeClass("twoProducts").addClass("oneProducts");
                                }
                            } else {
                                $.cookie('contrastData', null, {  path: '/' });
                                window.parent.window.Ejs.contrasts.contrastShow();
                                window.parent.window.Ejs.ContrastWindows.remove();
                            }
                        }
                    }
                    window.parent.window.Ejs.contrasts.contrastShow();
                }
            });
        })
    });
    //加入收藏
    $(".add_favorites").each(function (i) {
        var _this = $(this);
        _this.click(function (e) {
            e.preventDefault();
            if (typeof EJS.AddMyFavorites != 'undefined') {
                var _url = EJS.AddMyFavorites + "?productId=" + _this.attr("val");
                /*var _ProductUrl = EJS.ProductDetailBase + "/" + _this.attr("val");*/
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
                                Ejs.tip(_this, "addFsa", "商品收藏成功！", -1, 80, 1500);
                            } else {
                                Ejs.tip(_this, "addFss", "您已经收藏此商品！", -1, 80, 1500);
                            }
                        }
                    });
                }, function () {
                    window.parent.location.href = EJS.ToPageLogin + "?backUrl=" + encodeURIComponent(window.parent.location.href); //跳转到登录页面
                });
            }
        });
    });
    $(document).unbind('keydown');
    $(document).bind("keydown", function (event) {
        if (event.keyCode == 27) {
            window.parent.window.Ejs.ContrastWindows.remove();
        }
    });
    //阻断右侧对比
    $("body>div:gt(0)").remove();
});