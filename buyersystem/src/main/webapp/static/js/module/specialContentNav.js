/**
 * 专题页右侧浮动内容导航
 * Created by HuaLei.Du on 2014/7/1.
 */
Ejs = window.Ejs || {};

(function () {


    Ejs.rightFixedNav = function (ele) {

        var navItems = [],
            minTop = 200;

        $('.special-nav-title').each(function (i) {
            var self = $(this),
                title = self.attr('title'),
                offsetTop = self.offset().top,
                firstClass = '';

            if (i === 0) {
                minTop = offsetTop - 10;
                firstClass = ' class="first"';
            }

            if (title) {
                navItems.push('<li' + firstClass + '><a href="javascript:void(0);" data-offsetTop="' + offsetTop + '">' +
                    title + '</a></li>');
            }

        });

        navItems.push('<li class="back-top"><a href="javascript:void(0);" data-offsetTop="0">返回顶部</a></li>');

        function getScrollTop() {
            return $(window).scrollTop();
        }

        function isDisplay() {
            return getScrollTop() > minTop && $(window).width() > 1400;
        }

        function showNav() {
            var top;

            if (_isIE6) {
                // IE6下采用绝对定位，所以要计算TOP值
                top = getScrollTop() + 10;
                ele.css('top', top + 'px');
            }

            ele.stop().show();
        }

        function init() {

            ele.html('<div class="mod-special-nav-body"><ul class="list">' + navItems.join('\n') + '</ul></div>');

            if (isDisplay()) {
                showNav();
            } else {
                ele.stop().hide();
            }
        }

        ele.on('click', '.list a', function () {
            $('html, body').animate({ scrollTop: $(this).attr('data-offsetTop') }, 200);
        });

        init();

        $(window).bind('scroll resize', function () {
            init();
        });

    };

}());
