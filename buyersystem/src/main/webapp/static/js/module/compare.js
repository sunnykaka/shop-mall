/**
 * 对比页Js
 * Created by HuaLei.Du on 2014/5/9.
 */

$(function () {

    $LAB.script(EJS.StaticDomain + '/js/module/goodsCompare.js').wait(function () {
        /*
         var dataIds = goodsCompare.getIds().join(),
         ids = window.location.href.match(/ids\=[\d,]+/);

         if (ids) {
         ids = ids[0].replace('ids=', '');
         if (ids != dataIds) {
         window.location.href = EJS.ProductCompare + '?ids=' + dataIds;
         }
         }
         */

        $('.mod-compare').on('click', ' .remove', function () {
            var self = $(this),
                pid = self.attr('pid'),
                index = self.attr('index'),
                table = self.parents('table');

            if (pid) {
                goodsCompare.removeItem(pid);
                index = parseInt(index, 10);
                table.find('tr').each(function (i) {
                    $(this).find('td').eq(index).remove();
                    if (i === 0) {
                        $(this).append('<td><img src="' + EJS.StaticDomain + '/images/compare-goods-empty.png" alt=""></td>');
                    } else {
                        $(this).append('<td></td>');
                    }
                });

                $('.mod-compare .remove').each(function (j) {
                    $(this).attr('index', j);
                });
            }

        });
    });

});