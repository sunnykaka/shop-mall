(function () {
    var assetsDomain = EJS.StaticDomain || "assets";

    $LAB.script(function () {
        if (typeof $.fn.raty === 'undefined') {
            return EJS.StaticDomain + '/js/libs/jquery/jquery.raty.min.js';
        } else {
            return null;
        }
    })
        .script(assetsDomain + '/js/libs/jquery/jquery.jqzoom.js')
        .script(assetsDomain + '/js/libs/jquery/jquery.jcarousel.min.js')
        .wait()
        .script(assetsDomain + '/js/module/goodsSku.js');

    // 百度分享
    $(function () {
        var picUrl = $('.big_pic').attr('src') || 'http://assets.yijushang.com/stylesimg/logo-2014.png';
        window._bd_share_config = {
            common: {
                bdPic: picUrl
            },
            share: [
                {
                    "bdSize": 16
                }
            ]
        };

        with (document)0[(getElementsByTagName('head')[0] || body).appendChild(createElement('script')).src = 'http://bdimg.share.baidu.com/static/api/js/share.js?cdnversion=' + ~(-new Date() / 36e5)];
    });

}());

Ejs.rightLayerDisplay = true;
