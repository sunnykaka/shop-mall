/**
 * 模块JS，含自定义模块页面需要引入
 */
var Ejs = window.Ejs || {};

// 幻灯片
(function ($) {

    if (typeof $.fn.jQSlide !== 'undefined') {
        return;
    }

    $.fn.jQSlide = function (options) {
        var opts = $.extend({
                event: "mouseover",
                auto: true,
                list: false,
                btnNext: null,
                btnPrev: null,
                interval: 4000,
                bufferTime: 1000,
                animation: "fade"
            }, options || {}),
            that = this,
            $slide = that.find('.slide_box'),
            $slides = $slide.children('li'),
            $slideList = that.find('.slide_list').children('li'),
            $slidesCount = $slides.length,
            $current = 0,
            $next = 0,
            running = null,
            hoverRunning = null,
            $slideItemWidth,
            $slideWidth,
            attrZIndex,
            play,
            init;

        this.find(".slide_box").find("img").each(function (i) {
            if ($(this).attr("src") !== $(this).attr("data-original")) {
                var dataOriginal = $(this).attr("data-original");
                if (!!dataOriginal) {
                    $(this).attr("src", dataOriginal);
                }
            }
        });

        attrZIndex = function () {
            $slides.each(function (i) {
                $(this).css('z-index', i + 1);
            });
        };

        if (opts.animation === "fade") {
            attrZIndex();
            $slides.eq(0).css("z-index", '19');
        } else if (opts.animation === "slide") {
            $slideItemWidth = $slides.eq(0).width();
            $slideWidth = $slideItemWidth * $slidesCount;
            $slide.width($slideWidth);
        }

        if ($slidesCount < 2) {
            this.find('.slide_list').remove();
            this.find('.slide_list_container').remove();
            opts.btnNext && opts.btnNext.remove();
            opts.btnPrev && opts.btnPrev.remove();
            return;
        }

        // 开始
        play = function (n) {
            (function (n) {
                if (n) {
                    $current = n;
                }

                if ($next > 0) {
                    if ($current >= $slidesCount) {
                        $current = 0;
                    }
                    if (opts.animation === "fade") {
                        $slides.stop(true, true).fadeOut(800).removeClass("current");
                        attrZIndex();
                        $slides.eq($current).css('z-index', '19').addClass("current");
                        $slides.eq($current).stop(true, true).fadeIn(1200, function () {
                            $slides.not($(".current")).stop(true, true).hide();
                        });
                    } else if (opts.animation === "slide") {
                        var left = $current * $slideItemWidth;
                        $slide.stop(true, true).animate({
                            left: -left + "px"
                        }, "swing");
                    }

                    if (opts.list) {
                        $slideList.removeClass('current');
                        $slideList.eq($current).addClass('current');
                    }
                }

                $next = 1;
                $current += 1;
                if (opts.auto && !n) {
                    running = setTimeout(arguments.callee, opts.interval);
                }

            }(n));
        };

        // 初始化
        init = function () {
            play();

            that.hover(function () {
                window.clearTimeout(running);
                window.clearTimeout(hoverRunning);
            }, function () {
                hoverRunning = setTimeout(play, opts.interval);
            });

            // 跳转到某一个
            if (opts.list) {
                $slideList.each(function (i) {
                    $(this).bind(opts.event, function () {
                        if ($current !== (i + 1)) {
                            window.clearTimeout(running);
                            $current = i;
                            play($current);
                        }
                    });
                });
            }

            // 按钮控制
            if (opts.btnPrev && opts.btnNext) {
                opts.btnPrev.click(function () {
                    window.clearTimeout(running);
                    $current = $current - 2;
                    if ($current < 0) {
                        $current = $slidesCount - 1;
                    }
                    $next = 1;
                    play();
                });

                opts.btnNext.click(function () {
                    window.clearTimeout(running);
                    if ($current >= $slidesCount) {
                        $current = 0;
                    }
                    play();
                });
            }
        };

        window.setTimeout(init, opts.bufferTime);
    };

})(jQuery);

// 大屏滚动插件
(function ($) {

    if (typeof $.fn.kvSlide !== 'undefined') {
        return;
    }

    $.fn.kvSlide = function (options) {
        var opts = $.extend({
            auto: true,
            hoverStop: true,
            kvPicWrap: '.kv-pic-wrap',
            kvPics: '.kvs',
            kvPic: 'li.kv',
            kvListsWrap: '.kv-list-wrap',
            kvLists: '.kv-list',
            kvList: 'li',
            interval: 4000,
            bufferTime: 300
        }, options || {});

        var current = 0,
            kvWrap = this,
            kvPicWrap = $(opts.kvPicWrap, kvWrap),
            kvPics = $(opts.kvPics, kvPicWrap),
            kvPic = $(opts.kvPic, kvPics),
            kvListsWrap = $(opts.kvListsWrap, kvWrap),
            kvLists = $(opts.kvLists, kvListsWrap),
            kvList = $(opts.kvList, kvLists),
            leftMaskWidth = 0,
            uWidth = 0,
            prev = null,
            next = null,
            kvLength = 0;

        initSlider();
        createControl();
        attachEvent();

        $(window).resize(function () {
            initSlider();
            prev.width(leftMaskWidth + 1);
            next.width(leftMaskWidth + 1);
        });

        function initSlider() {
            kvPic.find("img").each(function () {
                if ($(this).attr("src").length < 4) {
                    $(this).attr("src", $(this).attr("data-original"));
                }
            });
            var winWidth = jQuery(window).width();
            uWidth = kvPic.find("img").eq(0).width();
            winWidth = jQuery.browser.msie ? winWidth : winWidth;
            //alert(this.jHtml);
            if (winWidth > 1000) {
                kvPicWrap.width(winWidth);
            } else {
                kvPicWrap.width(1000);
            }
            kvPicWrap.css('left', -1 * (parseInt((winWidth - uWidth) / 2, 10)));
            kvPics.css('left', -1 * (uWidth - parseInt((winWidth - uWidth) / 2, 10)));
            leftMaskWidth = Math.abs(kvPicWrap.position().left);
            if (winWidth > 1000) {
                if (kvPicWrap.width() % 2 === 1) {
                    kvPicWrap.css('left', -1 * (leftMaskWidth + 1));
                }
                if (jQuery.browser.msie && (jQuery.browser.version == "6.0") && !jQuery.support.style) {
                    kvPicWrap.parent().css('left', leftMaskWidth + 1);
                }
            } else {
                kvPicWrap.css('left', 0);
            }
        }

        function createControl() {
            var html = [
                '<div class="prev"><div class="mask"></div><a  class="p ie6png"></a></div>',
                '<div class="next"><div class="mask"></div><a  class="n ie6png"></a></div>'
            ].join('');
            kvPicWrap.append(html);
            prev = kvPicWrap.children('.prev');
            next = kvPicWrap.children('.next');
            wrapLeft = leftMaskWidth;
            prev.width(wrapLeft + 1);
            next.width(wrapLeft + 1);
            var cloneEles = kvPic.clone();
            cloneEles.addClass('clone');
            kvPics.append(cloneEles);
            var ele = kvPics, curPos = ele.position().left, kv = ele.children('li');
            kvLength = kv.length;
            ele.css('left', leftMaskWidth - (kvLength / 2) * (uWidth));
            current = kvLength / 2;
        }

        function switchToReady(direction) {
            if (!kvPics.is(":animated")) {
                if (direction === "left") {
                    if (current <= 1) {
                        current = current + (kvLength / 2);
                        kvPics.css('left', leftMaskWidth - (kvLength / 2 + 1) * uWidth);
                    }
                    current--;
                } else if (direction === "right") {
                    if (current >= kvLength - 2) {
                        current = current - (kvLength / 2);
                        kvPics.css('left', leftMaskWidth - (kvLength / 2 - 2) * uWidth);
                    }
                    current++;
                } else {

                }
                switchTo();
            }
        }

        function switchTo() {
            if (!kvPics.is(":animated")) {
                kvPics.animate({left: -current * uWidth + leftMaskWidth}, opts.bufferTime);
            }
            var listIndex = current < (kvLength / 2) ? current : current - (kvLength / 2);
            kvList.removeClass("current");
            kvList.eq(listIndex).addClass("current");
        }

        function attachEvent() {
            var prev = kvPicWrap.find('.prev .p'),
                next = kvPicWrap.find('.next .n');
            prev.bind('click', function () {
                switchToReady("left");
                return false;
            });
            next.bind('click', function () {
                switchToReady("right");
                return false;
            });
            prev.hover(function () {
                clearInterval(timer);
            }, function () {
                timer = setInterval(autoPlay, opts.interval);
            });
            next.hover(function () {
                clearInterval(timer);
            }, function () {
                timer = setInterval(autoPlay, opts.interval);
            });
            jQuery(document).keydown(function (e) {
                switch (e.keyCode) {
                    case 37:
                        switchToReady("right");
                        break;
                    case 39:
                        switchToReady("left");
                        break;
                }
            });

            kvList.bind('click', function () {
                var index = kvList.index($(this)) + (kvLength / 2);
                if (index === current && index === (current + (kvLength / 2))) return false;
                if (index === kvLength-1){
                    current = kvLength / 2-1;
                    kvPics.css('left', (-current+1) * uWidth + leftMaskWidth);
                }else{
                    current = index;
                }
                switchTo();
            });
        }

        var timer;

        if (opts.auto == true) {
            timer = setInterval(autoPlay, opts.interval);
        }

        if (opts.hoverStop == true && opts.auto == true) {
            kvPics.hover(function () {
                clearInterval(timer);
            }, function () {
                timer = setInterval(autoPlay, opts.interval);
            });
        }

        function autoPlay() {
            switchToReady('right');
        }
    };

})(jQuery);

// tab懒加载

(function ($) {

    Ejs.scrollTab = function (options) {
        this.wrapEle = $(options.wrapEle);
        this.tabTitle = $('.tab_title', this.wrapEle);
        this.tabTitleLists = $('.tab_title_list', this.tabTitle);
        this.tabCon = $('.tab_con', this.wrapEle);
        this.tabConLists = $('.tab_con_list', this.tabCon);
        this.hasInit = false;
        var corrdsY = this.wrapEle.offset().top;
        this.getCorrdsY = function () {
            return corrdsY;
        };
        var height = this.wrapEle.height();
        this.getHeight = function () {
            return height;
        };
        this.init();
    };

    Ejs.scrollTab.prototype = {
        init: function () {
            Ejs.scrollTab.Arrs.push(this);
            var scrollTop = Ejs.scrollTab.config.scrollTop;
            var winHegiht = Ejs.scrollTab.config.winHegiht;
            var top = this.getCorrdsY();
            var height = this.getHeight();
            var eleMaxTop = top + height;
            var winMaxTop = scrollTop + winHegiht;
            if (!((eleMaxTop < scrollTop) || (winMaxTop < top))) {
                this.tabInit();
            }
        },
        tabInit: function () {
            var tabTitleLists = this.tabTitleLists;
            var tabConLists = this.tabConLists;

            if (this.hasInit) return false;
            var length = tabTitleLists.length,
                timer = null,
                current = 0,
                index = 0;

            //tab移入切换
            tabTitleLists.hover(
                function () {
                    clearInterval(timer);
                    index = tabTitleLists.index($(this));
                    tabChange(index);
                    current = index + 1;
                }, function () {
                    autoRun();
                }
            );

            //鼠标停在内容区不切换
            this.tabCon.hover(function () {
                clearInterval(timer);
            }, function () {
                autoRun();
            });

            function tabChange(index) {
                if (index > length - 1) index = current = 0;
                tabTitleLists.removeClass("cur");
                tabTitleLists.eq(index).addClass("cur");
                tabConLists.removeClass("cur");
                tabConLists.eq(index).addClass("cur");
                current++;
                tabConLists.eq(index).find("img").each(function () {
                    if ($(this).attr("src").length < 5) {
                        var dataOriginal = $(this).attr("data-original");
                        if (!!dataOriginal) {
                            $(this).attr("src", dataOriginal);
                        }
                    }
                });
            }

            tabChange(0);

            function autoRun() {
                timer = setInterval(function () {
                    tabChange(current);
                }, 8000);
            }

            autoRun();

            $(".slide_banner", this.wrapEle).each(function (i) {
                $(this).jQSlide({
                    list: true,
                    interval: 6000,
                    btnNext: $(this).find(".slide_btn_next "),
                    btnPrev: $(this).find(".slide_btn_prev ")
                });
                $(this).find(".slide_list").find("li:last").addClass("last");
            });

            this.hasInit = true;

        }
    };

    Ejs.scrollTab.Arrs = [];


    Ejs.scrollTab.config = {
        scrollTop: $(document).scrollTop(),
        winHegiht: $(window).height(),
        refreshTop: function () {
            this.scrollTop = $(document).scrollTop();
        },
        refreshHeight: function () {
            this.winHegiht = $(window).height();
        }
    };
    var arrs = Ejs.scrollTab.Arrs;

    $(window).bind('resize.scroll', function () {
        Ejs.scrollTab.config.refreshTop();
        Ejs.scrollTab.config.refreshHeight();
        $("html" + ( !$.browser.opera ? ",body" : "")).animate({scrollTop: Ejs.scrollTab.config.scrollTop + 5}, 0);
    });

    setTimeout(function(){
        if (Ejs.scrollTab.Arrs.length == 0) $(window).unbind('resize.scroll');
    },1000);

    $(window).bind("scroll.lazy", function () {
        if (arrs.length > 0) {
            var hasFinsh = true;
            var scrollTop = $(document).scrollTop();
            var winHeight = Ejs.scrollTab.config.winHegiht;
            for (var i = 0, j = arrs.length; i < j; i++) {
                var temp = arrs[i];
                var top = temp.getCorrdsY();
                var height = temp.getHeight();
                var eleMaxTop = top + height;
                var winMaxTop = scrollTop + winHeight;
                if (!((eleMaxTop < scrollTop) || (winMaxTop < top)) && temp.hasInit == false) {

                    temp.tabInit();
                }
                hasFinsh = (hasFinsh && arrs[i].hasInit);
            }
            if (hasFinsh == true) {
                $(window).unbind("scroll.lazy");
            }
        }
    });

})(jQuery);


