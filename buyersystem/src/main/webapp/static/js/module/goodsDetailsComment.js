/**
 * 详情页 - 评论
 * Created by HuaLei.Du on 2014/5/21.
 */

var goodsDetails = window.goodsDetails || {};

(function ($, template) {

    var assetsDomain = EJS.StaticDomain || "assets";

    goodsDetails.pageLinkNums = function (totalPage, currentPage) {
        var pre = [],
            next = [],
            numsLength = 5,
            i,
            j;

        if (totalPage < currentPage) {
            return [];
        }

        if (totalPage > 0) {

            if (currentPage <= numsLength) {

                for (j = 0; j < currentPage; j++) {
                    pre[j] = j + 1;
                }

            } else if (currentPage === totalPage) {
                pre[0] = 1;
                pre[1] = 2;
                pre[2] = -1;
                pre[3] = currentPage - 2;
                pre[4] = currentPage - 1;
                pre[5] = currentPage;
            } else {
                pre[0] = 1;
                pre[1] = 2;
                pre[2] = -1;
                pre[3] = currentPage - 1;
                pre[4] = currentPage;
            }

            if (totalPage - currentPage <= numsLength - 1) {

                for (i = 0; i < totalPage - currentPage; i++) {
                    next[i] = currentPage + i + 1;
                }

            } else if (currentPage == 1) {
                next[0] = 2;
                next[1] = 3;
                next[2] = -1;
                next[3] = totalPage - 1;
                next[4] = totalPage;
            } else {
                next[0] = currentPage + 1;
                next[1] = -1;
                next[2] = totalPage - 1;
                next[3] = totalPage;
            }
        }

        return pre.concat(next);

    };

    goodsDetails.star = function () {
        $('#goods-comments').find('.star').each(function () {
            var self = $(this),
                hintText;

            if (self.attr('hint')) {
                hintText = self.attr('hint');
                self.raty({
                    width: 92,
                    starOff: assetsDomain + '/stylesimg/star-off.png',
                    starOn: assetsDomain + '/stylesimg/star-on.png',
                    starHalf: assetsDomain + '/stylesimg/star-half.png',
                    hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                    readOnly: true,
                    score: hintText,
                    scoreName: 'score'
                });
            }

        });
    };

    goodsDetails.get = function (url, parameter, callback) {
        $.ajax({
            url: url,
            cache: false,
            data: parameter,
            dataType: 'JSON',
            success: function (results) {
                if (results.success) {
                    callback(results.data);
                }
            },
            error: function () {
                callback('error');
            }
        });
    };

    // 构建分页HTML
    goodsDetails.createPageBar = function (totalPages, currentPage) {
        var html = [],
            prevDisable = '',
            nextDisable = '',
            pageCurrent,
            linkNums = goodsDetails.pageLinkNums(totalPages, currentPage),
            linkNumsCount = linkNums.length,
            i;

        if (linkNumsCount < 1) {
            return '';
        }

        if (currentPage === 1) {
            prevDisable = ' disable';
        }

        if (currentPage === totalPages) {
            nextDisable = ' disable';
        }

        html.push('<div class="e-pagebar">');
        html.push('<a href="javascript:void(0);" class="page-prev' + prevDisable + '">上一页</a>');

        for (i = 0; i < linkNumsCount; i++) {
            pageCurrent = '';

            if (linkNums[i] === currentPage) {
                pageCurrent = ' current';
            }

            if (linkNums[i] === -1) {
                html.push('<span class="number">...</span>');
            } else {
                html.push('<a href="javascript:void(0);" class="number' + pageCurrent + '" data-page="' + linkNums[i] + '">' + linkNums[i] + '</a>');
            }
        }

        html.push('<a href="javascript:void(0);" class="page-next' + nextDisable + '">下一页</a></div>');
        html.push('</div>');

        return html.join(' ');

    };

    goodsDetails.comments = function () {
        var commentsList = $('#comments-list'),
            commentsPageBar = $('#comments-pageBar'),
            commentsUrl = commentsList.attr('data-src'),
            pageSize = 5,
            totalCount = 0,
            totalPages = 0,
            parameter = {
                likeFilter: 'all',
                pageNo: 1,
                pageSize: pageSize
            };

        if (!commentsUrl) {
            return {
                init: function () {
                    return false;
                }
            };
        }

        // 重载列表
        function reloadList() {
            goodsDetails.get(commentsUrl, parameter, function (result) {

                if (result === 'error') {
                    commentsList.find('.goods-comments-list').html('<div class="no-results">不能请求到正确的数据</div>');
                    return;
                }

                var html = template('comments-list-tpl', result),
                    pageBarHtml = '';

                totalCount = result.totalCount;
                commentsList.find('.goods-comments-list').html(html);

                if (totalCount > 0 && totalCount > pageSize) {
                    totalPages = Math.ceil(totalCount / pageSize);
                    pageBarHtml = goodsDetails.createPageBar(totalPages, parameter.pageNo);
                }

                commentsPageBar.html(pageBarHtml);
                goodsDetails.star();
            });
        }

        // 初始化
        function init() {
            goodsDetails.get(commentsUrl, parameter, function (result) {

                if (result === 'error') {
                    commentsList.html('<div class="no-results">不能请求到正确的数据</div>');
                    return;
                }

                var html = template('comments-rate-tpl', result),
                    pageBarHtml = '';
                totalCount = result.totalCount;
                commentsList.html(html);
                parameter.pageNo = 1;

                if (totalCount > 0 && totalCount > pageSize) {
                    totalPages = Math.ceil(totalCount / pageSize);
                    pageBarHtml = goodsDetails.createPageBar(totalPages, parameter.pageNo);
                }
                commentsPageBar.html(pageBarHtml);
                goodsDetails.star();

            });
        }

        // 分类切换
        commentsList.on('click', '.comments-hd li', function () {
            var self = $(this);

            if (!self.hasClass('current')) {
                parameter.pageNo = 1;
                parameter.likeFilter = self.attr('data-type');
                self.addClass('current').siblings('li').removeClass('current');
                reloadList();
            }

        });

        // 给分页绑定事件
        commentsPageBar.on('click', 'a', function () {
            var self = $(this);
            if (!self.hasClass('disable') && !self.hasClass('current')) {
                if (self.hasClass('number')) {
                    parameter.pageNo = parseInt(self.attr('data-page'), 0);
                }

                if (self.hasClass('page-prev')) {
                    parameter.pageNo -= 1;
                }

                if (self.hasClass('page-next')) {
                    parameter.pageNo += 1;
                }

                if (parameter.pageNo > totalPages) {
                    parameter.pageNo = totalPages;
                }

                if (parameter.pageNo < 1) {
                    parameter.pageNo = 1;
                }

                reloadList();
                $(window).scrollTop($('#comments-list').offset().top - 50);
            }
        });

        return {
            init: init
        };

    };

    setTimeout(function () {
        goodsDetails.comments().init();
    }, 600);


}(jQuery, template));
