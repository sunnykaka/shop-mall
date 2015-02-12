// 楼层
(function ($) {
    'use strict';
    var modFloor = $('.mod-floor');

    modFloor.on('mouseenter', '.head li', function (e) {
        e.preventDefault();
        var self = $(this),
            activityList = $('#' + self.attr('index'));

        $(this).addClass('current').siblings().removeClass('current');
        activityList.show().siblings('.list').hide();
        activityList.find('img').each(function () {
            var dataOriginal = $(this).attr("data-original");
            if (!!dataOriginal) {
                $(this).attr('src', dataOriginal);
            }
        });
    });

    function hotGoodsHtml(data, index) {
        var result = data.result,
            htmlHead = '',
            htmlBody = '',
            resultCount = result.length,
            headItemClass,
            htmlBodyItemDisplay,
            html,
            i,
            j;

        for (i = 0; i < resultCount; i++) {
            headItemClass = '';
            htmlBodyItemDisplay = ' style="display: none"';

            if (i === 0) {
                headItemClass = ' class="current"';
                htmlBodyItemDisplay = '';
            }

            htmlHead += '<li' + headItemClass + ' index="floor-list-' + index + '-' + i + '">' + result[i].navName + '</li>';
            htmlBody += '<div class="clearfix list"' + htmlBodyItemDisplay + '" id="floor-list-' + index + '-' + i + '"><ul>';
            for (j = 0; j < result[i].hotSellProductList.length; j++) {
                htmlBody += '<li>' +
                    '<div class="aside">' +
                    '   <div class="name"><a target="_blank" href="' + result[i].hotSellProductList[j].url + '">' + result[i].hotSellProductList[j].name + '</a></div>' +
                    '   <div class="price">¥ ' + result[i].hotSellProductList[j].price + '</a></div>' +
                    '</div>' +
                    '   <a class="pic animate-bounceInRight" target="_blank" href="' + result[i].hotSellProductList[j].url + '"><img class="lazy"' +
                    '   data-original="' + result[i].hotSellProductList[j].picture + '"' +
                    '   src="http://www.boobee.me/static/images/white.gif" alt="" width="80" height="80"><span class="number n' + (j + 1) + '"></span></a>' +
                    '</li>';
            }
            htmlBody += '</ul></div>';
        }

        if (resultCount < 2) {
            htmlHead = '';
        }

        html = '<div class="head"><h4 class="pull-left title">热销排行榜</h4><ul class="pull-right">' +
            htmlHead + '</ul></div><div class="body">' + htmlBody + '</div>';

        return html;
    }

    // 加载热销排行榜
    function loadHotGoods(tabs, index, errorHtml) {
        var url = tabs.attr('data-url');
        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {
                if (response.success) {
                    var html = hotGoodsHtml(response.data, index);
                    tabs.html(html);
                    tabs.find('img.lazy').lazyload({ threshold: 300 });
                } else {
                    tabs.html('<div class="info">' + response.msg + '</div>');
                }
            },
            error: function () {
                tabs.html(errorHtml);
            }
        });
    }

    modFloor.each(function (index) {
        var self = $(this),
            tabs = self.find('.tabs'),
            url,
            errorHtml = '<div class="info">热销排行榜加载出错</div>';

        tabs.html('<div class="ajax-loader">&nbsp;</div>');
        if (tabs.length && tabs.attr('data-url')) {
            setTimeout(function () {
                loadHotGoods(tabs, index, errorHtml);
            }, 800);
        } else {
            tabs.html(errorHtml);
        }
    });

}(jQuery));
