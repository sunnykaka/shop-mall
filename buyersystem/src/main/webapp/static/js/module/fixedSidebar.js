/**
 * 网站右侧固定栏
 * Created by HuaLei.Du on 2014/7/14.
 */

var Ejs = window.Ejs || {};

Ejs.fixedSidebar = function () {

    if ($(window).width() < 1320) {
        return;
    }

    var sidebar,
        tabs,
        historyBox,
        historyData = {},
        isIE6 = window.VBArray && !window.XMLHttpRequest,
        historyNumber = 4;

    function getSidebarTemplate() {
        return '\n' +
            '<div class="fixed-sidebar" id="fixed-sidebar">' +
            '   <div class="fixed-sidebar-tabs">' +
            '       <div class="fixed-sidebar-tab fixed-sidebar-tab-history">' +
            '           <div class="fixed-sidebar-tab-title">' +
            '               <a href="/my/history" target="_blank" class="fixed-sidebar-tab-ico"><span></span></a>' +
            '               <div class="fixed-sidebar-tab-tip"><span class="fixed-sidebar-tab-tip-arr">◆</span><a href="/my/history" target="_blank">浏览历史</a></div>' +
            '           </div>' +
            '           <div class="fixed-sidebar-tab-history-bd"></div>' +
            '       </div>' +
            '       <div class="fixed-sidebar-tab fixed-sidebar-tab-cart">' +
            '           <div class="fixed-sidebar-tab-title">' +
            '               <a href="' + EJS.CartUrl + '" class="fixed-sidebar-tab-ico" target="_blank"><span class="cart-number"></span></a>' +
            '               <div class="fixed-sidebar-tab-tip"><span class="fixed-sidebar-tab-tip-arr">◆</span><a href="' + EJS.CartUrl + '" target="_blank">购物车(<span class="cart-number"></span>)</a></div>' +
            '           </div>' +
            '       </div>' +
            '       <div class="fixed-sidebar-tab fixed-sidebar-tab-follow">' +
            '           <div class="fixed-sidebar-tab-title">' +
            '               <a href="' + EJS.MyFavorites + '" class="fixed-sidebar-tab-ico" target="_blank"><span></span></a>' +
            '               <div class="fixed-sidebar-tab-tip"><span class="fixed-sidebar-tab-tip-arr">◆</span><a href="' + EJS.MyFavorites + '" target="_blank">我的关注</a></div>' +
            '           </div>' +
            '       </div>' +
            '       <div class="fixed-sidebar-tab fixed-sidebar-tab-ask">' +
            '           <div class="fixed-sidebar-tab-title">' +
            '               <a href="javascript:Ejs.showAskWindow();void(0);" class="fixed-sidebar-tab-ico"><span></span></a>' +
            '               <div class="fixed-sidebar-tab-tip"><span class="fixed-sidebar-tab-tip-arr">◆</span><a href="javascript:Ejs.showAskWindow();void(0);">在线咨询</a></div>' +
            '           </div>' +
            '       </div>' +
            '       <div class="fixed-sidebar-tab fixed-sidebar-tab-top">' +
            '           <div class="fixed-sidebar-tab-title">' +
            '               <a href="javascript:void(0);" class="fixed-sidebar-tab-ico toTop"><span></span></a>' +
            '               <div class="fixed-sidebar-tab-tip"><span class="fixed-sidebar-tab-tip-arr">◆</span><a href="javascript:void(0);" class="toTop">返回顶部</a></div>' +
            '           </div>' +
            '       </div>' +
            '   </div>' +
            '</div>';
    }

    // 根据后端数据生成浏览历史HTML
    function historyDataToHTML() {
        var list = historyData.history || [],
            listCount = list.length,
            html = '<ul class="list">',
            className = '',
            i;

        if (listCount) {

            for (i = 0; i < listCount; i++) {

                if (i === listCount - 1 || i === historyNumber - 1) {
                    className = ' class="last"';
                }

                if (i >= historyNumber) {
                    break;
                }

                html += '<li' + className + '>' +
                    '   <div class="pic">' +
                    '       <a href="' + list[i].url + '" title="' + list[i].product + '" target="_blank">' +
                    '           <img src="' + list[i].picture + '" width="50" height="50" alt="">' +
                    '       </a>' +
                    '   </div>' +
                    '   <div class="price">¥ ' + list[i].price.replace(/\.00$/, '') + '</div>' +
                    '   <div></div>' +
                    '   <div class="view">' +
                    '       <div class="pic">' +
                    '           <a href="' + list[i].url + '" title="' + list[i].product + '" target="_blank">' +
                    '               <img src="' + list[i].picture + '" width="80" height="80" alt="">' +
                    '           </a>' +
                    '       </div>' +
                    '       <div class="aside">' +
                    '           <div class="name"><a href="' + list[i].url + '" target="_blank">' + list[i].product + '</a></div>' +
                    '           <div class="price">¥ ' + list[i].price + '</div>' +
                    '           <a href="javascript:void(0);" class="remove" data-id="' + list[i].id + '" target="_blank">[删除]</a>' +
                    '       </div>' +
                    '       <span class="arr"></span>' +
                    '   </div>' +
                    '</li>';

            }

            html += '</ul><span class="fixed-sidebar-tab-history-bd-arr"></span><a href="/my/history" target="_blank" class="more">更多</a>';

            return html;
        }

        return null;
    }

    // 加载浏览历史
    function loadHistory(callback) {
        var url = EJS.BrowsingHistory + '?pageSize=' + historyNumber;

        if (typeof productId !== 'undefined') {
            url += '&productId=' + productId;
        }

        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {

                if (response.success && response.data.history.length) {
                    historyData = response.data;
                    callback(true);
                } else {
                    historyData = {};
                    callback(null);
                }

            },
            error: function () {
                callback(null);
                historyData = {};
            }
        });

    }

    // 移除浏览历史项
    function removeHistoryItem(self) {

        var url = '/delHistory?id=' + self.attr('data-id');

        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {

                if (response.success) {
                    self.parents('li').slideUp(200, function () {
                        loadHistory(function (response) {
                            var historyHTML;

                            if (response) {
                                historyHTML = historyDataToHTML();
                                historyBox.html(historyHTML);
                            } else {
                                historyBox.empty().hide();
                            }

                        });
                    });
                } else {
                    alert(response.msg);
                }

            },
            error: function () {
                alert('服务器发生错误');
            }
        });
    }

    // 得到窗口高度
    function getWindowHeight() {
        return $(window).height();
    }

    // 得到bar内容的高度
    function getYjsBarTabsHeight() {
        return tabs.height() || 0;
    }

    // 得到 window scrollTop
    function getScrollTop() {
        return $(window).scrollTop();
    }

    // 计算bar内容的marginTop值
    function countMainMarginTop() {
        var windowHeight = getWindowHeight(),
            yjsBarTabsHeight = getYjsBarTabsHeight();

        return windowHeight > yjsBarTabsHeight ? (windowHeight - yjsBarTabsHeight) / 2 : 0;
    }

    // 设置 bar 高度
    function setYjsBarHeight() {
        sidebar.height(getWindowHeight());
    }

    // 设置bar MarginTop值
    function setBarMarginTop() {
        tabs.css('margin-top', countMainMarginTop() + 'px');
    }

    // 重置bar位置
    function resetPosition() {
        setYjsBarHeight();
        setBarMarginTop();

        if (isIE6) {
            sidebar.css('top', getScrollTop() + 'px');
        }
    }

    // 绑定事件
    function bindEvent() {

        // 显示浏览历史商品基本信息事件
        historyBox.on('mouseenter', 'li', function () {
            $(this).find('.view').show();
        }).on('mouseleave', 'li', function () {
            $(this).find('.view').hide();
        });

        // 移除浏览历史项
        sidebar.on('click', '.remove', function () {
            removeHistoryItem($(this));
        });

        // 显示标题tip
        tabs.on('mouseenter', '.fixed-sidebar-tab-title', function () {
            $(this).find('.fixed-sidebar-tab-tip').show();
        }).on('mouseleave', '.fixed-sidebar-tab-title', function () {
            $(this).find('.fixed-sidebar-tab-tip').hide();
        });

        // 返回顶部
        sidebar.on('click', '.toTop', function () {
            var timer = $(window).scrollTop() > 600 ? 600 : 200;
            $('html, body').animate({ scrollTop: 0 }, timer);
        });

        // 窗口大小发生变化时，重新设置bar在的高度和里面元素的marginTop
        $(window).on('resize', function () {
            resetPosition();
        });

        if (isIE6) {
            $(window).bind('scroll', function () {
                setTimeout(function () {
                    resetPosition();
                }, 60);
            });
        }

    }

    // 重置变量值
    function resetVar() {
        sidebar = $('#fixed-sidebar');
        tabs = sidebar.find('.fixed-sidebar-tabs');
        historyBox = sidebar.find('.fixed-sidebar-tab-history-bd');
    }

    // 初始化
    (function () {

        var historyLoadStatus = false,
            running = null;

        $('body').append(getSidebarTemplate());

        resetVar();

        // 加载浏览历史
        loadHistory(function (response) {
            var historyHTML;

            if (response) {
                historyHTML = historyDataToHTML();
                historyBox.html(historyHTML);
            } else {
                historyBox.empty().hide();
            }

            historyLoadStatus = true;
        });

        Ejs.Cart && Ejs.Cart.getNumber();

        running = window.setInterval(function () {
            if (historyLoadStatus) {
                resetVar();
                sidebar.show();
                resetPosition();
                bindEvent();
                window.clearInterval(running);
            }
        }, 100);

    }());

};