/**
 * 详情页 - 咨询
 * Created by HuaLei.Du on 2014/5/21.
 */

var goodsDetails = window.goodsDetails || {};

(function ($, template) {

    var goodsComments = $('#goods-comments');

    // 咨询
    goodsDetails.consults = function () {
        var consultsList = $('#consults-list'),
            consultsPageBar = $('#consults-pageBar'),
            consultsUrl = consultsList.attr('data-src'),
            pageSize = 5,
            totalCount = 0,
            totalPages = 0,
            parameter = {
                category: 'all',
                pageNo: 1,
                pageSize: pageSize
            };

        if (!consultsUrl) {
            return {
                init: function () {
                    return false;
                }
            };
        }

        // 重载列表
        function reloadList() {
            goodsDetails.get(consultsUrl, parameter, function (result) {

                if (result === 'error') {
                    consultsList.find('.goods-consults-list').html('<div class="no-results">不能请求到正确的数据</div>');
                    return;
                }

                var html = template('consults-list-tpl', result),
                    pageBarHtml = '';

                totalCount = result.totalCount;
                consultsList.find('.goods-consults-list').html(html);

                if (totalCount > 0 && totalCount > pageSize) {
                    totalPages = Math.ceil(totalCount / pageSize);
                    pageBarHtml = goodsDetails.createPageBar(totalPages, parameter.pageNo);
                }

                consultsPageBar.html(pageBarHtml);
            });
        }

        // 重载图形验证码
        function reloadImageCode() {
            goodsComments.find('.image-code').attr('src', EJS.ImageCodeHttp + '?' + new Date().getTime());
        }

        goodsComments.on('click', '.image-code, .consult-form-foot a', function () {
            reloadImageCode();
        });

        // 初始化
        function init() {
            goodsDetails.get(consultsUrl, parameter, function (result) {

                if (result === 'error') {
                    consultsList.html('<div class="no-results">不能请求到正确的数据</div>');
                    return;
                }

                var html = template('consults-hd-tpl', result),
                    pageBarHtml = '';
                totalCount = result.totalCount;
                consultsList.html(html);

                if (totalCount > 0 && totalCount > pageSize) {
                    totalPages = Math.ceil(totalCount / pageSize);
                    pageBarHtml = goodsDetails.createPageBar(totalPages, 1);
                }
                consultsPageBar.html(pageBarHtml);
            });
        }

        // 分类切换
        consultsList.on('click', '.consults-hd li', function () {
            var self = $(this);

            if (!self.hasClass('current')) {
                parameter.pageNo = 1;
                parameter.category = self.attr('data-type');
                self.addClass('current').siblings('li').removeClass('current');
                reloadList();
            }

        });

        // 给分页绑定事件
        consultsPageBar.on('click', 'a', function () {
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
                $(window).scrollTop($('#consults-list').offset().top - 60);

            }
        });

        // 提交咨询
        goodsComments.on('submit', '#add-consult-form', function (E) {
            E.preventDefault();
            var self = $(this),
                url = self.attr('action'),
                type = $('input[name=consultationCategory]:checked'),
                askContent = $('textarea[name=askContent]').eq(0),
                codeText = $('#form-text-code'),
                foot = self.find('.consult-form-foot');

            if (type.length < 1) {
                Ejs.tip(foot, 'addAskErr', '请选择咨询类型！', 2, 10, 2000);
                return;
            }

            if (!$.trim(askContent.val())) {
                Ejs.tip(foot, 'addAskErr', '咨询内容不能为空！', 2, 10, 2000);
                askContent.focus();
                return;
            }

            if (!$.trim(codeText.val())) {
                Ejs.tip(foot, 'codeText', '验证码不能为空！', 2, 10, 2000);
                codeText.focus();
                return;
            }

            Ejs.UserStatus.isLogin(function () {
                $.ajax({
                    type: 'POST',
                    url: url,
                    data: self.serialize(),
                    dataType: 'json',
                    success: function (data) {
                        if (data.success) {
                            askContent.val('');
                            codeText.val('');
                            reloadImageCode();
                            Ejs.tip(foot, 'addAsk', '恭喜您提交成功，我们会尽快给您回复！', 2, 10, 2000);
                        } else {
                            Ejs.tip(foot, 'addAskErr', data.msg, 1, 10, 2000);
                        }
                    }
                });
            }, function () {
                Ejs.tip(foot, 'addAskErr', '您还没有登录请<a href="javascript:void(0);" id="consultativeLogin">点击登录</a>', 2, 10, 5000);
            });

        });

        // 弹出登录
        $('body').on('click', '#consultativeLogin', function () {
            new Ejs.UserWindow({
                actionType: 'consultative',
                backUrl: window.location.href
            });
        });

        return {
            init: init
        };

    };

    goodsDetails.consults.submit = function () {
        var form = $('#add-consult-form');
        if (form.length) {
            form.submit();
        }
    };

    setTimeout(function () {
        goodsDetails.consults().init();
    }, 600);

}(jQuery, template));
