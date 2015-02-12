/**
 * 专题页右侧浮动导航
 * Created by HuaLei.Du on 2014/6/24.
 */
Ejs = window.Ejs || {};

(function ($, Ejs) {

    Ejs.rightFixedNav = function (ele, minTop) {

        minTop = minTop || 720;

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
            if (isDisplay()) {
                showNav();
            } else {
                ele.stop().hide();
            }
        }

        init();

        $(window).bind('scroll resize', function () {
            init();
        });

    };

}(jQuery, Ejs));