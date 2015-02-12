$(function () {

    // 商品详情 - tab
    $(window).scrollTop(0);
    var tab = $("#product_details");
    var tabBd = tab.find(".tab_bd");
    var tabHd = tab.find(".tab_hd");
    var tabHdItem = tabHd.find("li");
    var fixedTop = parseInt(tabHd.offset().top);
    var windowTop = parseInt($(window).scrollTop());
    //logger(fixedTop);
    var tabBdMinHeight = $(".accessories_recommended ul").eq(0).height();
    tabBdMinHeight = tabBdMinHeight > 20 ? (tabBdMinHeight - 20) : tabBdMinHeight;

    tabHdItem.each(function (i) {
        var _this = $(this);
        _this.click(function () {
            if (_this.hasClass("current")) {
                return;
            }
            tabHdItem.removeClass("current");
            tabHdItem.eq(i).addClass("current");
            tabBd.hide();
            tabBd.eq(i).show();

            if (tabBd.eq(i).height() < tabBdMinHeight) {
                tabBd.eq(i).height(tabBdMinHeight);
            }

            if (_this.attr("data-id") == "consult") {
                if (!!_this.attr("XHR") == false) {
                    var _box = tabBd.eq(i);
                    var _url = _this.attr("data-url");
                    if (!!_url) {
                        $.ajax({
                            type:"GET",
                            url:_url,
                            dataType:'html',
                            success:function (data) {
                                _box.css("height", "auto");
                                _box.html(data);
                                _this.attr("XHR","true");
                                if (windowTop > fixedTop) {
                                    $(window).scrollTop(fixedTop);
                                }
                            }
                        });
                    }
                }
            } else if ($(this).attr("data-id") == "brand") {
                var _brandBox = tabBd.eq(i).find(".brand_main");
                if (!!_brandBox.attr("XHR") == false) {
                    var _brandUrl = $("#BrandStoryUrl").val();
                    if (!!_brandUrl) {
                        _brandBox.html('<div class="loading">正在加载...</div>');
                        $.ajax({
                            type:"GET",
                            url:_brandUrl,
                            dataType:'html',
                            success:function (data) {
                                _brandBox.html(data);
                                _brandBox.attr("XHR", "true");
                                tabBd.eq(i).css("height","auto");
                                if (windowTop > fixedTop) {
                                    $(window).scrollTop(fixedTop);
                                }
                            },
                            error: function () {
                                _brandBox.removeClass("loading");
                                _brandBox.html("加载失败");
                            }
                        });
                    }
                }
            } else {
                if (windowTop > fixedTop) {
                    $(window).scrollTop(fixedTop);
                }
            }
        });
    });

    $(window).scroll(function () {
        windowTop = parseInt($(window).scrollTop());
        if (windowTop > fixedTop) {
            tabHd.addClass("tab_fixed");
        } else {
            tabHd.removeClass("tab_fixed");
        }
    });

    function openComment(){
        $(window).scrollTop(fixedTop);
        tabHdItem.eq(2).click();
    }
    
    $(".star_count").eq(0).click(function(e){
        e.preventDefault();
        openComment();
    });

    // 添加到购物车
    $("#directBuyCombination").click(function(){
        var form = $("#buyCombinationForm");
        Ejs.UserStatus.isLogin(function () {
            form.submit();
        }, function () {
            var backUrl = window.location.href;
            Ejs.UserWindows = new Ejs.UserWindow({
                backUrl: encodeURIComponent(backUrl),
                actionType: "combination"
            });
        });
    });

    $(window).load(function(){
        var hash = location.hash;
        if(hash === "#comment"){
            openComment();
        }
    });

});


