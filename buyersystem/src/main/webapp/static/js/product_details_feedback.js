//ProduceDes.comment();
//ProduceDes.consult();
//ProduceDes.submitConsult();

//ProduceDes.commentPaging();

//ProduceDes.consultPaging();

var ProductDes = {};
ProductDes.load = function (url, box) {
    if (!!url == false || !!box == false) {
        return
    }
    box.html('<div class="loading">正在加载...</div>');
    var _tabHd = $("#product_details").find(".tab_hd").eq(0);
    var _fixedTop = parseInt(_tabHd.offset().top);
    var _windowTop = parseInt($(window).scrollTop());
    $.ajax({
        type: "GET",
        url: url,
        dataType: 'html',
        success: function (data) {
            box.html(data);
            ProductDes.star();
            if (_windowTop > _fixedTop) {
                $(window).scrollTop(_fixedTop);
            }
        },
        error: function () {
            box.html("加载失败");
        }
    });
};

ProductDes.paging = function (box) {
    var _ele = box.find(".pagebar");
    _ele.find("a").each(function () {
        if (!$(this).hasClass("current")) {
            $(this).click(function (E) {
                E.preventDefault();
                var _url = $(this).attr("data-url");
                ProductDes.load(_url, box);
            });
        }
    });
};

ProductDes.show = function (tabs, box) {
    if (!!tabs == false || !!box == false) {
        return
    }
    tabs.find("a").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            if ($(this).hasClass("current")) {
                return;
            }
            tabs.find("a").removeClass("current");
            $(this).addClass("current");
            var _url = $(this).attr("data-url");
            ProductDes.load(_url, box);
        });
    });
    tabs.find("a").eq(0).click();
};

ProductDes.star = function () {
    //logger("star");
    var assetsDomain = EJS.StaticDomain;
    if (!!assetsDomain == false) {
        assetsDomain = "assets";
    }
    $("#productCommentList").find('.star').each(function () {
        var _this = $(this);
        if (!!_this.attr("hint")) {
            var _hint = parseInt(_this.attr("hint"));
            _this.raty({
                width: 92,
                starOff: assetsDomain + '/stylesimg/star-off.png',
                starOn: assetsDomain + '/stylesimg/star-on.png',
                hints: ['不喜欢', '不喜欢', '一般', '喜欢', '喜欢'],
                readOnly: true,
                score: _hint,
                scoreName: 'score'
            });
        }
    });
};

// 评论
ProductDes.comment = function () {
    var _tabs = $("#productCommentTabs");
    var _box = $("#productCommentList");
    ProductDes.show(_tabs, _box);
};

// 评论分页
ProductDes.commentPaging = function () {
    ProductDes.paging($("#productCommentList"));
};

// 咨询
ProductDes.consult = function () {
    var _tabs = $("#productConsultTabs");
    var _box = $("#productConsultList");
    ProductDes.show(_tabs, _box);
};

// 咨询分页
ProductDes.consultPaging = function () {
    ProductDes.paging($("#productConsultList"));
};

// 发表咨询
ProductDes.submitConsult = function () {
    var productDetails = $("#product_details");

    productDetails.on('click', '#show_consult_form', function (E) {
        E.preventDefault();
        var consultBox = $("#tab_bd_consult");
        $("#consult_form_box").slideDown(600, function () {
            consultBox.css("height", "auto");
            var tabBdMinHeight = $(".accessories_recommended ul").eq(0).height();
            tabBdMinHeight = tabBdMinHeight > 20 ? (tabBdMinHeight - 20) : tabBdMinHeight;
            if (consultBox.height() < tabBdMinHeight) {
                consultBox.height(tabBdMinHeight);
            }
        });
        $(this).stop().hide();
    });

    productDetails.on('click', '#hide_consult_form', function (E) {
        E.preventDefault();
        var consultForm = $("#consult_form_box");
        var consultBox = $("#tab_bd_consult");
        consultForm.slideUp(400);
        $("#show_consult_form").stop().slideDown(600, function () {
            consultBox.css("height", "auto");
            var tabBdMinHeight = $(".accessories_recommended ul").eq(0).height();
            tabBdMinHeight = tabBdMinHeight > 20 ? (tabBdMinHeight - 20) : tabBdMinHeight;
            if (consultBox.height() < tabBdMinHeight) {
                consultBox.height(tabBdMinHeight);
            }
        });
    });

    productDetails.on('click', '#consult_form_submit', function () {
        var _url = $("#addProductConsultation").attr("action");
        var _type = $("input[name=consultationCategory]:checked");
        var _askContent = $("textarea[name=askContent]").eq(0);
        var _productId = $("input[name=productId]").eq(0).val();
        var _submit = $("#consult_form_submit");
        if (_type.length < 1) {
            Ejs.tip(_submit, "addAskErr", "请选择咨询类型！", 1, 120, 2000);
            return;
        }
        if (!$.trim(_askContent.val())) {
            Ejs.tip(_submit, "addAskErr", "咨询内容不能为空！", 1, 120, 2000);
            _askContent.focus();
            return;
        }
        if (_productId == "") {
            Ejs.tip(_submit, "addAskErr", "程序异常，请刷新页面重试！", 1, 120, 2000);
            return;
        }

        Ejs.UserStatus.isLogin(function () {
            _productId = parseInt(_productId);
            $.ajax({
                type: "POST",
                url: _url,
                data: "consultationCategory=" + _type.val() + "&askContent=" + _askContent.val() + "&productId=" + _productId,
                dataType: 'json',
                success: function (data) {
                    if (data["success"]) {
                        _askContent.val("");
                        Ejs.tip(_submit, "addAsk", "恭喜您提交成功，我们会尽快给您回复！", 1, 120, 2000);
                    } else {
                        Ejs.tip(_submit, "addAskErr", data["msg"], 1, 120, 2000);
                    }
                }
            });
        }, function () {
            Ejs.tip(_submit, "addAskErr", "您还没有登录请<a href='javascript:void(0);' id='consultativeLogin'>点击登录</a>", 1, 100, 5000);
        });
    });

    $("body").on('click', '#consultativeLogin', function () {
        Ejs.UserWindows = new Ejs.UserWindow({
            actionType: "consultative"
        });
    });

};

ProductDes.consultFormSubmit = function () {
    var btn = $("#consult_form_submit");
    if (btn.length > 0) {
        btn.click();
    }
};

