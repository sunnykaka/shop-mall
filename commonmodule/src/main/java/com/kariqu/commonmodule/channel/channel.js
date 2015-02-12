$(document).ready(function () {

    $(".c_product").each(function (i) {
        $(this).find(".img").each(function () {
            $(this).hover(function () {
                $(this).find(".join_contrast").show();
            }, function () {
                $(this).find(".join_contrast").hide();
            });
        });
    });

    $(".channel_slide").each(function() {
        $(this).jQSlide({
            list: true,
            interval: 6000,
            event: "click",
            btnNext: $(this).find(".slide_btn_next"),
            btnPrev: $(this).find(".slide_btn_prev")
        });
    });

});

(function () {
    $LAB.script(EJS.StaticDomain + '/js/history.js').wait(function () {
        getHistory($(".browse_history").find("ul"));
    });
}());

Ejs.rightLayerDisplay = true;
