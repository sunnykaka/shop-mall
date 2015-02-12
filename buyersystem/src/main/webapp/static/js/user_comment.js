var UserCenter = window.UserCenter || {};

(function () {
    "use strict";
    var UC = UserCenter;

    UC.comment = function () {
        this.wrapBox = $("#product_comment");
        this.box = $("#comment_list");
        this.assetsDomain = EJS.StaticDomain;
        if (!!this.assetsDomain === false) {
            this.assetsDomain = "assets";
        }
        this.loading = function () {
            return this.wrapBox.find(".loading");
        };
        this.init();
    };

    UC.comment.prototype = {
        init: function () {

            var parent = this,
                tabs = parent.wrapBox.find(".tabs"),
                defaultUrl = tabs.find(".current").find("a").eq(0).attr("href") || "";

            if (defaultUrl !== "") {
                parent.load(defaultUrl);
            }
            //从订单详情点进来走这里
            if (tabs.length == 0) {
                parent.clickComment();
                parent.showStar();
                parent.submitComment();
                parent.textareaEvent();
                parent.clickComment();
                if ($(".c_btn").length == 0) {
                    $(".comment_item").addClass("not_c");
                }
            }
            tabs.find("a").click(function (e) {
                e.preventDefault();
                var self = $(this),
                    li = self.parents("li"),
                    url = self.attr("href");

                li.addClass("current");
                li.siblings("li").removeClass("current").removeClass("left");
                li.prevAll("li").addClass("left");
                parent.load(url);

            });

        },
        load: function (url) {
            var parent = this;
            if (this.loading().length < 1) {
                parent.wrapBox.find(".tabs").after('<div class="loading"></div>');
            } else {
                this.loading().show();
            }
            this.box.html("");

            $.ajax({
                type: "POST",
                url: url,
                cache: false,
                dataType: 'html',
                success: function (data) {            
                    parent.box.html(data);
                    parent.loading().hide();
                    parent.pageBarEvent();
                    parent.showStar();
                    parent.submitComment();
                    parent.textareaEvent();
                    //parent.goToItem();
                    parent.clickComment();
                },
                error: function () {
                    parent.box.html("error");
                }
            });
        },
        validate: function (self) {
            var txtEle = self.find("textarea.txt");
            if (txtEle.hasClass("txt_def") || $.trim(txtEle.val()).length < 10 || $.trim(txtEle.val()).length > 2000) {
                self.find(".msg").text("评价内容必须在10-2000字之间!");
                return false;
            }
            self.find(".msg").text("");
            return true;
        },
        submitComment: function () { // 评论和追加评论都通过此方法加入数据库
            var parent = this,
                commentForm = $(".comment_form");
            if (commentForm.length < 1) {
                return;
            }
            commentForm.each(function () {
                var self = $(this);
                self.submit(function (e) {
                    e.preventDefault();
                    if (parent.validate(self)) {
                        var url = self.attr("action"),
                            hint = self.find("input[name=point]").val(),
                            content = self.find("textarea.txt").val();
                        $.ajax({
                            type: "POST",
                            url: url,
                            data: self.serialize(),
                            dataType: "json",
                            success: function (data) {
                                if (data["success"]) {
                                    // 如果表单下面存在add_to则为追加
                                    if (self.parents('.add_to').length > 0) {
                                        content = '<span class="time">追加评价：' + data["data"]["appendDate"] + '</span>' + '<p>' + content + '</p>';   
                                        parent.showComment(self, null, content);
                                    } else {
                                        parent.showComment(self, hint, content);
                                    }
                                } else {
                                    self.find(".msg").html(data["msg"]);
                                    parent.loading().hide();
                                }
                            }
                        });
                    }

                });
            });
        },
        showComment: function (self, hint, content) {
            var parent = this;

            if (hint === null) {
                self.parents('li').addClass("succeed").find('.c_text').append(content);
                return;
            }
            self.find("dl").eq(0).find("dd").html('<div class="star" hint="' + hint + '"></div>' +
                '<span class="hint"></span>');
            self.find(".textarea_box").html(content);
            self.find(".sub").hide();
            self.find(".star").raty({
                width: 100,
                starOff: parent.assetsDomain + '/images/user/star-off-big.png',
                starOn: parent.assetsDomain + '/images/user/star-on-big.png',
                hints: ['1分   失望', '2分   不满', '3分   一般', '4分   满意', '5分   惊喜'],
                target: self.find(".hint").eq(0),
                targetKeep: true,
                readOnly: true,
                score: hint,
                scoreName: 'point'
            });

        },
        showStar: function () {
            var parent = this;
            $('.star').each(function () {
                var self = $(this);
                if (!!self.attr("hint")) {
                    var hint = parseInt(self.attr("hint"));
                    self.raty({
                        width: 135,
                        starOff: parent.assetsDomain + '/images/user/star-off-big.png',
                        starOn: parent.assetsDomain + '/images/user/star-on-big.png',
                        hints: ['1分   失望', '2分   不满', '3分   一般', '4分   满意', '5分   惊喜'],
                        //target: self.next(".grade_text").find(".hint").eq(0),
                        targetKeep: true,
                        readOnly: true,
                        score: hint,
                        scoreName: 'point'
                    });
                } else {
                    self.raty({
                        width: 135,
                        starOff: parent.assetsDomain + '/images/user/star-off-big.png',
                        starOn: parent.assetsDomain + '/images/user/star-on-big.png',
                        hints: ['1分   失望', '2分   不满', '3分   一般', '4分   满意', '5分   惊喜'],
                        target: self.next(".grade_text").find(".hint").eq(0),
                        targetKeep: true,
                        readOnly: false,
                        score: 5,
                        scoreName: 'point'
                    });
                }
            });
        },
        textareaEvent: function () {
            $("textarea.txt").each(function () {
                var self = $(this);
                self.focus(function () {
                    if (self.hasClass("txt_def")) {
                        self.val("").removeClass("txt_def");
                    }
                });
            });
        },
        pageBarEvent: function () {
            var parent = this,
                pageBar = parent.box.find(".pagebars");

            if (pageBar.length < 1) {
                return;
            }

            pageBar.find("a").each(function () {
                var self = $(this);
                self.click(function (E) {
                    E.preventDefault();
                    if ($(this).hasClass("current")) return;
                    var url = self.attr("href");
                    parent.load(url);
                });
            });
        },
        goToItem: function () {
            var url = window.location.href.split("?"),
                orderItemId = url.length > 1 ? url[1] : "";

            if (orderItemId !== "") {
                var docTop = $("#" + orderItemId).offset().top - 34;
                $(window).scrollTop(docTop);
            }
        },
        clickComment: function () {
            this.box.find(".show_textarea").bind("click", function () {
                $(this).parents("li").addClass("current");
            });
        }
    };
}());