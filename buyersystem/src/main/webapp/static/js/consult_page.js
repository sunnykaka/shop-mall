;
(function () {
    var _box = $("#productConsultation");
    var _tabs = _box.find(".consult_tabs");
    var tabHd = $("#product_details").find(".tab_hd").eq(0);
    var fixedTop = parseInt(tabHd.offset().top);
    var windowTop = parseInt($(window).scrollTop());
    _tabs.find("a").each(function () {
        $(this).click(function (E) {
            E.preventDefault();
            if ($(this).hasClass("current")) {
                return;
            }
            var _url = $(this).attr("val");
            if (_url) {
                $.ajax({
                    type:"GET",
                    url:_url,
                    dataType:'html',
                    success:function (data) {
                        _box.html(data);
                        if(windowTop > fixedTop){
                            $(window).scrollTop(fixedTop);
                        }
                    }
                });
            }
        });
    });

    var pageBar = _box.find(".pagebar");
    pageBar.find("a").each(function () {
        if ($(this).hasClass("number") || $(this).hasClass("page_next") || $(this).hasClass("page_start")) {
            if (!$(this).hasClass("current")) {
                $(this).click(function (E) {
                    E.preventDefault();
                    var _url = $(this).attr("val");
                    if (_url) {
                        $.ajax({
                            type:"GET",
                            url:_url,
                            dataType:'html',
                            success:function (data) {
                                _box.html(data);
                                if(windowTop > fixedTop){
                                    $(window).scrollTop(fixedTop);
                                }
                            }
                        });
                    }
                });
            }
        }
    });
    var consultBox = $("#tab_bd_consult");
    consultBox.css("height", "auto");
    var tabBdMinHeight = $(".accessories ul").eq(0).height();
    tabBdMinHeight = tabBdMinHeight > 20 ? (tabBdMinHeight - 20) : tabBdMinHeight;
    if (consultBox.height() < tabBdMinHeight) {
        consultBox.height(tabBdMinHeight);
    }

    $("#productConsultationGo").click(function (E) {
        E.preventDefault();
        var pageNumber = $("#PageNumber");
        var Url = $("#productConsultationGoUrl").val();
        var Val = parseInt(pageNumber.val());
        var PageSize = pageNumber.attr("val");
        if (typeof Val == "number") {
            if (Val > PageSize) {
                Val = PageSize;
            }
            if (Url) {
                if (Url.substring(0, 4) == "http") {
                    $.ajax({
                        type:"GET",
                        url:Url + Val,
                        dataType:'html',
                        success:function (data) {
                            _box.html(data);
                            if(windowTop > fixedTop){
                                $(window).scrollTop(fixedTop);
                            }
                        }
                    });
                }
            }
        }
    });

})();