// 商品SKU
$(function () {
    // 商品摘要 - 图片
    if ($('#photo_thumb li').length > 4) {
        $('#photo_thumb').jcarousel({
            vertical: false,
            scroll: 3
        });
    }

    //图片放大镜效果
    $('#jqzoombox').jqzoom({
        xOffset: 10,
        yOffset: 0,
        preloadImages: false,
        zoomWidth: 350,
        zoomHeight: 350,
        title: false,
        preloadText: 'Loading Image',
        imageOpacity: 0.1
    });

    // 添加到购物车
    $("#directBuyCombination").click(function () {
        var form = $("#buyCombinationForm");
        Ejs.UserStatus.isLogin(function () {
            form.submit();
        }, function () {
            var backUrl = window.location.href;
            Ejs.UserWindows = new Ejs.UserWindow({
                backUrl: encodeURIComponent(backUrl),
                actionType: "combination"
            });
        });
    });

});

