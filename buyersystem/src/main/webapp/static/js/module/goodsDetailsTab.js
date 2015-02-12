/**
 * 详情 - tab
 * Created by HuaLei.Du on 2014/5/26.
 */

var goodsDetails = window.goodsDetails || {};

(function ($) {

    goodsDetails.tab = function () {

        var tab = $('.mod-detailContent'),
            tabBd = tab.find('.tab-bd'),
            tabHd = tab.find('.detail-tab-hd'),
            tabHdItem = tabHd.find('li'),
            fixedTop = parseInt(tabHd.offset().top, 0),
            windowTop = parseInt($(window).scrollTop(), 0);

        // 滚动到详情
        function rollToDetail() {
            $(window).scrollTop(fixedTop);
        }

        // 切换tab
        function changeTab(self) {
            if (windowTop > fixedTop) {
                rollToDetail();
            }

            self.addClass('current').siblings('li').removeClass('current');

            if (self.hasClass('tab-hd-parameters')) {
                tabBd.each(function () {
                    var self = $(this);
                    if (self.hasClass('goods-parameters')) {
                        self.show();
                    } else {
                        self.hide();
                    }
                });
            } else if (self.hasClass('tab-hd-comment')) {
                tabBd.each(function () {
                    var self = $(this);
                    if (self.hasClass('goods-comments')) {
                        self.show();
                        self.find('.comment-title').hide();
                    } else {
                        self.hide();
                    }
                });

            } else if (self.hasClass('tab-hd-brand')) {
                tabBd.each(function () {
                    var self = $(this);
                    if (self.hasClass('goods-brandStory')) {
                        self.show();
                    } else {
                        self.hide();
                    }
                });
            } else if (self.hasClass('tab-hd-service')) {
                tabBd.each(function () {
                    var self = $(this);
                    if (self.hasClass('goods-service')) {
                        self.show();
                    } else {
                        self.hide();
                    }
                });
            } else {
                tabBd.each(function () {
                    var self = $(this);

                    if (self.hasClass('goods-introduction') || self.hasClass('goods-brandStory') ||
                        self.hasClass('goods-comments')) {
                        self.show();
                    } else {
                        self.hide();
                    }

                });
                $('.comment-title').show();
            }
        }

        // 展开
        function showComment() {
            var self = tabHdItem.eq(2);

            if (!self.hasClass('current')) {
                changeTab(self);
            }

            rollToDetail();
        }

        // 绑定事件
        function bind() {
            tabHdItem.on('click', function (E) {
                E.preventDefault();
                var self = $(this);

                if (self.hasClass('current')) {
                    return;
                }

                changeTab(self);
            });
        }

        // 固定tabHd
        if (!_isIE6) {
            $(window).scroll(function () {
                windowTop = parseInt($(window).scrollTop());
                if (windowTop > fixedTop) {
                    tabHd.addClass("tab-fixed");
                } else {
                    tabHd.removeClass("tab-fixed");
                }
            });
        }

        return {
            init: bind,
            showComment: showComment
        };

    };

    goodsDetails.tab().init();

}(jQuery));
