/**
 * 设置专题内容区背景
 * Created by HuaLei.Du on 2014/7/1.
 */

$(function () {
    var pageBgColor = $('.pageBackgroundColor').eq(0),
        pageBgImg = $('.pageBackgroundImg').eq(0),
        pagePadding = $('.pageBackgroundPadding').eq(0),
        pageRepeat = $('.pageBackgroundLoop').eq(0),
        pageBgColorVal = '',
        pageBgImgVal = '',
        pagePaddingVal = '',
        pageRepeatText = 'no-repeat',
        background = '',
        paddingTop = '',
        css = {};

    if (pageBgColor) {
        pageBgColorVal = pageBgColor.val();
        if (pageBgColorVal !== '' && !/\s+$/.test(pageBgColorVal)) {
            background = pageBgColorVal + ' ';
        }
    }

    if (pageBgImg) {
        pageBgImgVal = pageBgImg.val();

        if (pageRepeat && pageRepeat.val() === 'true') {
            pageRepeatText = 'repeat';
        }

        if (pageBgImgVal !== '' && !/\s+$/.test(pageBgImgVal)) {
            background += 'url(' + pageBgImgVal + ') ' + pageRepeatText + ' 50% 0';
        }
    }

    if (pagePadding) {
        pagePaddingVal = pagePadding.val();
        if (pagePaddingVal !== '' && !/\s+$/.test(pagePaddingVal)) {
            paddingTop = pagePaddingVal + 'px';
        }
    }

    pageBgColor.parents('.e_module').hide();

    if (background !== '' || paddingTop !== '') {
        css.background = background;
        css.paddingTop = paddingTop;
        pageBgColor.parents('.e_region').css(css);
    }

});
