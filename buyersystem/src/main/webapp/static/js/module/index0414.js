/**
 * Created by HuaLei.Du on 14-4-15.
 */

// 网站导航
/*(function ($) {
 'use strict';
 var running = true;
 $('.e-nav .category-all-main .items').height($('.e-nav .category-all-main').height());

 $('.e-nav .category-all').hover(function () {
 if (running) {
 running = false;
 $(this).find('.category-all-main').stop().slideDown(400, function () {
 running = true;
 });
 }
 }, function () {
 if (running) {
 running = false;
 $(this).find('.category-all-main').stop().slideUp(300, function () {
 running = true;
 });
 }
 });

 $('.e-nav .category-all .btn-close').click(function () {
 running = false;
 $('.e-nav .category-all-main').slideUp(300, function () {
 running = true;
 });
 });

 }(jQuery));*/

// 顶部购物车
/*(function ($) {
 'use strict';
 var running = true;
 $('.e-header .minicart').hover(function () {
 $(this).addClass('open');
 if (running) {
 running = false;
 $(this).find('.minicart-main').stop().slideDown(400, function () {
 running = true;
 });
 }
 }, function () {
 var self = $(this);
 if (running) {
 running = false;
 self.find('.minicart-main').stop().slideUp(300, function () {
 running = true;
 self.removeClass('open');
 });
 }
 });

 }(jQuery));*/

// 我的易居尚
/*(function ($) {
 'use strict';
 var running = true;
 $('.e-header .myejs').hover(function () {
 $(this).addClass('open');
 if (running) {
 running = false;
 $(this).find('.myejs-main').stop().slideDown(400, function () {
 running = true;
 });
 }
 }, function () {
 var self = $(this);
 if (running) {
 running = false;
 self.find('.myejs-main').stop().slideUp(300, function () {
 running = true;
 self.removeClass('open');
 });
 }
 });
 }(jQuery));*/

// 热销推荐
(function ($) {
    $LAB.script(function () {
        if (typeof $.fn.jcarousel === 'undefined') {
            return EJS.StaticDomain + '/js/jquery.jcarousel.js';
        }
        return null;
    })
        .wait(function () {
            var carousel = $('.mod-hot-recommend .e-carousel');
            carousel.each(function () {
                var self = $(this);
                if (self.find('li').length > 7) {
                    self.jcarousel({
                        vertical: false,
                        scroll: 7
                    });
                }
            });
            carousel.find('img').hover(function () {
                $(this).attr('src', $(this).attr('data-img'));
            }, function () {
                $(this).attr('src', $(this).attr('data-original'));
            });
        });
}(jQuery));

// 首页左侧类目
(function ($) {
    'use strict';
    $('.mod-category').each(function () {
        var self = $(this),
            slide = self.find('.slide').eq(0);

        self.find('.category-main li').hover(function () {
            $(this).removeClass('items-next').addClass('items-open').next('li').addClass('items-next');
            $(this).find('.images img').each(function () {
                var dataOriginal = $(this).attr('data-original');
                if (!!dataOriginal) {
                    $(this).attr('src', dataOriginal);
                }
            });
        }, function () {
            $(this).removeClass('items-open').next('li').removeClass('items-next');
        });

        slide.jQSlide({
            list: true,
            interval: 15000,
            btnNext: slide.find(".slide_btn_next "),
            btnPrev: slide.find(".slide_btn_prev ")
        });
    });
}(jQuery));

// 楼层
$LAB.script(EJS.StaticDomain + '/js/module/modFloor.js');


