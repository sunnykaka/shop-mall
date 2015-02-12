(function () {

    var self = $(".special_restaurant"),
        tabHead;

    self.find(".slide").each(function (i) {
        var btnNext = $(this).find(".slide_btn_next"),
            btnPrev = $(this).find(".slide_btn_prev");

        if (btnNext.length < 1) {
            btnNext = null;
        }

        if (btnPrev.length < 1) {
            btnPrev = null;
        }

        $(this).jQSlide({
            list: true,
            interval: 6000,
            animation: "slide",
            btnNext: btnNext,
            btnPrev: btnPrev
        });

        $(this).find(".slide_list").find("li:last").addClass("last");
    });

    function showTabContent(i) {
        var allContent = self.find(".floor_3").find(".tab_content"),
            content = allContent.eq(i);
        allContent.hide();
        content.show();
        if (!content.attr("lazy")) {
            content.find(".lazy").each(function () {
                $(this).attr("src", $(this).attr("data-original"));
            });
            content.attr("lazy", "true");
        }
    }

    tabHead = self.find(".floor_3").find(".tab_hd").find("a");
    tabHead.each(function (i) {
        $(this).hover(function (e) {
            e.preventDefault();
            tabHead.removeClass("current");
            $(this).addClass("current");
            showTabContent(i);
        });
    });

}());