(function () {
    "use strict";
    var url1 = window.location.href;
    var reUrl = /\/my\/.*/;
    try {
        var urlId = reUrl.exec(url1)[0].split("/")[2].split("?")[0];
        var highlight = false;
        $("#my_sidebar").find("a").each(function () {
            if (highlight) {
                return;
            }
            var thisUrl = $(this).attr("href");
            if (urlId === reUrl.exec(thisUrl)[0].split("/")[2].split("?")[0]) {
                $(this).addClass("cur");
                highlight = true;
            }
        });
    } catch (err) {
        logger(err);
    }
}());