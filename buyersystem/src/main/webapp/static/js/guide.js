/**
 * 引导动画脚本
 */

// 底部遮罩层
var maskLayer = function () {
    this.id = "maskLayer_2013";
    this.className = "mask_layer_2013";
    this.show();
};
maskLayer.prototype = {
    getId:function () {
        return $("#" + this.id);
    },
    show:function () {
        $(document).scrollTop(0);
        if (this.getId().length > 0) {
            this.getId().remove();
        }
        var $html = $("html");
        !$html.hasClass("overflow_hidden") && $html.addClass("overflow_hidden");
        $("body").append('<div id="' + this.id + '" class="' + this.className + '"></div>');
        _isIE6 && $("select").css("display", "none");
        this.getId().show().css(this.css()).animate({opacity:"0.6"}, 300);

    },
    hide:function () {
        if (this.getId().length > 0) {
            _isIE6 && $("select").css("display", "");
            this.getId().remove();
            $("html").removeClass("overflow_hidden");
        }
    },
    css:function () {
        var WW = $(window).width(),
            WH = $(window).height();
        return {
            position:"absolute", top:0, width:WW, height:WH, background:"#000000", "z-index":10000
        };
    }
};

// jQuery Easing v1.3 - http://gsgd.co.uk/sandbox/jquery/easing/
jQuery.easing['jswing'] = jQuery.easing['swing'];
jQuery.extend(jQuery.easing, {
    def:'easeOutQuad',
    swing:function (x, t, b, c, d) {
        //alert(jQuery.easing.default);
        return jQuery.easing[jQuery.easing.def](x, t, b, c, d);
    },
    easeInQuad:function (x, t, b, c, d) {
        return c * (t /= d) * t + b;
    },
    easeOutQuad:function (x, t, b, c, d) {
        return -c * (t /= d) * (t - 2) + b;
    },
    easeInOutCubic:function (x, t, b, c, d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    },
    easeInQuart:function (x, t, b, c, d) {
        return c * (t /= d) * t * t * t + b;
    }
});


// 图像幻灯片
(function ($) {
    if (typeof $.fn.imageSlide != 'undefined') return;
    $.fn.imageSlide = function (options) {
        var opts = $.extend({
            event:"mouseover",
            auto:false,
            list:false,
            btnNext:".next",
            btnPrev:".prev",
            btnClose:".close",
            interval:4000,
            bufferTime:1000
        }, options || {});

        var $this = this,
            $slides = this.find('.slide_box').children('ul'),
            $slidesCount = 4,
            $current = 0,
            $left = 0,
            $next = 0,
            running = null,
            hoverRunning = null;

        // 开始
        var _Play = function () {
            (function () {
                $current == 0 && $this.find(opts.btnPrev).fadeOut(200) || $this.find(opts.btnPrev).show();
                if ($current >= 3) {
                    $this.find(opts.btnNext).fadeOut(200);
                    $this.find(opts.btnClose).fadeOut(200);
                } else {
                    $this.find(opts.btnNext).show();
                    $this.find(opts.btnClose).show();
                }
                if ($current >= $slidesCount) {
                    return;
                }
                if ($next > 0) {
                    $left = $current * 1000;
                    $slides.stop(true, true).animate({ left:-$left }, 800, "easeInOutCubic");
                }
                $next = 1;
                $current++;
                if (opts.auto) {
                    running = setTimeout(arguments.callee, opts.interval);
                }
            })();
        };

        // 初始化
        var _Init = function () {
            _Play();

            /*(function () {
             $slides.hover(function () {
             window.clearTimeout(running);
             window.clearTimeout(hoverRunning);
             }, function () {
             hoverRunning = setTimeout(_Play, opts.interval);
             });
             })();*/

            // 按钮控制
            if (opts.btnPrev && opts.btnNext) {
                $this.find(opts.btnPrev).click(function () {
                    window.clearTimeout(running);
                    $current = $current - 2;
                    if ($current < 0) {
                        $current = $slidesCount - 1;
                    }
                    $next = 1;
                    _Play();
                });

                $this.find(opts.btnNext).click(function () {
                    window.clearTimeout(running);
                    if ($current >= $slidesCount) {
                        $current = 0;
                    }
                    _Play();
                });
            }
        };
        window.setTimeout(_Init, opts.bufferTime);

    }
})(jQuery);


function guideHide() {
    if (_maskLayer) {
        _maskLayer.hide();
        $("#guideMain").remove();
        $.cookie('guide', 'true', { path:'/', expires:365 });
    }
}


// 页面加载完后执行
//$.cookie('guide', null, {  path:'/' });
var _c_guide = !!$.cookie('guide');
var _imgUrl_1 = "http://www.boobee.me/static/images/guide_image_01.png";
var _imgUrl_2 = "http://www.boobee.me/static/images/guide_image_02.png";
var _imgUrl_3 = "http://www.boobee.me/static/images/guide_image_03.png";
var _imgUrl_4 = "http://www.boobee.me/static/images/guide_image_04.png";
if(_isIE6){
	_imgUrl_4 = "http://www.boobee.me/static/images/guide_image_04.gif";
}
if (!_c_guide) {
    $(function () {
        var imageSlideHtml = '' +
            '<div class="guide_main" id="guideMain">' +
            '   <div class="image_slide" id="imageSlide">' +
            '       <div class="slide_box">' +
            '           <ul>' +
            '               <li><img src="' + _imgUrl_1 + '" alt=""></li>' +
            '               <li><img src="' + _imgUrl_2 + '" alt=""></li>' +
            '               <li><img src="' + _imgUrl_3 + '" alt=""></li>' +
            '               <li><a href="javascript:guideHide();void(0);" title="\u7acb\u5373\u4f53\u9a8c"><img src="' + _imgUrl_4 + '" alt=""></a></li>' +
            '           </ul>' +
            '       </div>' +
            '       <div class="prev" title="\u540e\u9000">\u540e\u9000</div>' +
            '       <div class="next" title="\u7ee7\u7eed">\u7ee7\u7eed</div>' +
            '       <div CLASS="close" title="\u8df3\u8fc7\u52a8\u753b,\u7acb\u5373\u4f53\u9a8c">\u5173\u95ed</div>' +
            '   </div>' +
            '</div>';
        $("body").append(imageSlideHtml);
    });

    var _maskLayer = null;
    $(window).load(function(){
        var $html = $("html");
        !$html.hasClass("overflow_hidden") && $html.addClass("overflow_hidden");
        var _WW = $(window).width(),
            _WH = $(window).height(),
            _css = { left:0, top:0 };
        _css.left = _WW > 1000 ? parseInt((_WW - 1000) / 2) : 0;
        _css.top = _WH > 520 ? parseInt((_WH - 520) / 2) : 0;
        var guideMain = $("#guideMain"),
            slideBox = $("#imageSlide");
        if (guideMain.length > 0 && slideBox.length > 0) {
            guideMain.show().css(_css);
            _maskLayer = new maskLayer();
            slideBox.imageSlide();
            slideBox.find(".close").click(function () {
                guideHide();
            });
        }
    });
}

