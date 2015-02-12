(function ($) {

    // 分页
    var pageBarForm = $("form.pagebarForm");
    if (pageBarForm.length > 0) {
        pageBarForm.each(function (i) {
            var _form = $(this);
            var curNum = _form.find('input[name="jumpto"]').eq(0);
            curNum.blur(function (e) {
                if ($(this).val() == "") {
                    $(this).val();
                }
            });
            _form.submit(function () {
                var maxPage = parseInt($("#maxPageNumber").val());

                if (parseInt(curNum.val()) > maxPage || curNum.val() == "") {
                    curNum.val(1);
                }
                location.href = $("#pageBaseUrl").val() + "&page=" + curNum.val();
                return false;
            });
        });
    }

    // 搜索表单验证
    var searchForm = $("#search_form");
    if (searchForm.length > 0) {
        searchForm.submit(function (E) {
            E.preventDefault();
            var _value = $.trim($(this).find('input[name=keyword]').eq(0).val());
            if (!_value || _value == "搜索您需要的商品") {
                window.location = EJS.SearchUrl + '?keyword=*';
            } else {
                window.location = EJS.SearchUrl + '?keyword=' + encodeURIComponent(_value);
            }
        });
    }

})(jQuery);
