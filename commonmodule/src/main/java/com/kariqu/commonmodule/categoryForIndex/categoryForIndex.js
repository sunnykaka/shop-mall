// 首页左侧类目
(function ($) {
    'use strict';
    $('.mod-category').each(function () {
        var self = $(this),
            slide = self.find('.slide').eq(0);

        self.find('.category-main li').hover(function () {
            $(this).removeClass('items-next').addClass('items-open').next('li').addClass('items-next');
            $(this).find('.images img').each(function () {
                var dataOriginal = $(this).attr("data-original");
                if (!!dataOriginal) {
                    $(this).attr('src', dataOriginal);
                }
            });
        }, function () {
            $(this).removeClass('items-open').next('li').removeClass('items-next');
        });

        slide.jQSlide({
            list: true,
            interval: 7000,
            btnNext: slide.find(".slide_btn_next "),
            btnPrev: slide.find(".slide_btn_prev ")
        });
    });
}(jQuery));