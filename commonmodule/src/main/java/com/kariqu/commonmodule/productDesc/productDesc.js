// 商品详情信息模块
(function ($LAB) {
    var assetsDomain = EJS.StaticDomain || "assets";

    $LAB.script(function () {
        if (typeof $.fn.raty === 'undefined') {
            return EJS.StaticDomain + '/js/libs/jquery/jquery.raty.min.js';
        } else {
            return null;
        }
    })
        .script(assetsDomain + '/js/libs/template-native.js')
        .script(assetsDomain + '/js/module/goodsDetailsTab.js')
        .wait()
        .script(assetsDomain + '/js/module/goodsDetailsComment.js')
        .wait()
        .script(assetsDomain + '/js/module/goodsDetailsConsults.js');
}($LAB));
