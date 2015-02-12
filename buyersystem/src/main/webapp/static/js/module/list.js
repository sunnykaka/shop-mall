/**
 * 列表页JS
 * Created by HuaLei.Du on 2014/5/7.
 */

$(function () {

    // 左侧边栏2、3级类目
    $('.mod-goodsList .category').on('click', 'h3', function () {
        var self = $(this),
            parent = self.parent('.category'),
            all = self.find(".icon");
        if (!self.hasClass('open')) {
            parent.find('ul').addClass('hidden');
            parent.find('.icon').removeAttr("href");
            self.next('ul').removeClass('hidden');
            parent.find('h3').removeClass('open');
            self.addClass('open');
        }
        setTimeout(function () { all.attr("href", all.attr("data-href")); }, 50);
    });

    // 鼠标移到小图会切换大图
    $('.smallImage-list').on('mouseenter ', 'img', function () {
        var self = $(this),
            dataSrc = self.attr('data-src'),
            smallImageGroup = self.parents('.smallImage-list').find('.thumb'),
            pic = self.parents('.pic').find('img').eq(0);
        smallImageGroup.removeClass('current');
        self.parent().addClass('current');
        pic.attr('src', dataSrc);
    });

    // 分页
    (function () {
        var pageBarForm = $('.e-pagebar form');
        if (pageBarForm.length > 0) {
            pageBarForm.each(function (i) {
                var self = $(this),
                    curNum = self.find('input[name=jumpto]').eq(0);

                curNum.blur(function (e) {

                    if ($(this).val() == "") {
                        $(this).val();
                    }

                });
                self.submit(function () {
                    var maxPage = parseInt($("#maxPageNumber").val());

                    if (parseInt(curNum.val()) > maxPage || curNum.val() == "") {
                        curNum.val(1);
                    }

                    location.href = self.attr('action') + "&page=" + curNum.val();
                    return false;
                });
            });
        }
    }());

    $LAB.script(EJS.StaticDomain + '/js/module/goodsCompare.js').wait(function () {
        goodsCompare.init();
    });

});