/*
 *弹出注册登陆找回密码
 */

Ejs = window.Ejs || {};

Ejs.UserWindows = null; //公用用户弹出句柄,方便调用

//弹出登陆框 注册框
Ejs.UserWindow = function (options) {
    this.options = options;
    this.showBar = null;
    this.initialise();
};

Ejs.UserWindow.defaultOptions = {
    type: 'Login',
    //'Register'
    width: 400,
    height: 345,
    Src: '',
    beforeShow: function () {

    },
    beforeClose: function () {

    }
};

Ejs.UserWindow.prototype = {
    initialise: function () {
        this.setOptions(this.options);
        this.popWindow();
        this.evenEsc();
    },
    setOptions: function (opt) {
        $.extend(true, Ejs.UserWindow.defaultOptions, opt || {});
    },
    getLoginUrl: function () {
        if (Ejs.UserWindow.defaultOptions.actionType) {
            return EJS.ToWindowLogin + '?backUrl=&actionType=' + Ejs.UserWindow.defaultOptions.actionType;
        } else {
            return EJS.ToWindowLogin + '?backUrl=' + encodeURIComponent(this.getReturnPageUrl());
        }
    },
    getRegisterUrl: function () {
        if (Ejs.UserWindow.defaultOptions.actionType) {
            return EJS.ToWindowRegister + '?backUrl=&actionType=' + Ejs.UserWindow.defaultOptions.actionType;
        } else {
            return EJS.ToWindowRegister + '?backUrl=' + encodeURIComponent(this.getReturnPageUrl());
        }
    },
    getReturnPageUrl: function () {
        return Ejs.UserWindow.defaultOptions.backUrl || window.location.href;
    },
    popWindow: function () {
        var _src = '';
        var _root = this;
        var _close = '<span class="close_button"><a href="#"></a></span>';
        if (Ejs.UserWindow.defaultOptions.type == 'Login') {
            _src = _root.getLoginUrl();
        } else if (Ejs.UserWindow.defaultOptions.type == 'Register') {
            _src = _root.getRegisterUrl();
        } else {
            _src = _root.getLoginUrl();
        }
        var _titleTab = '<div class="switchPanel">';
        _titleTab += '<a href="#" class="cur ie6png" id="toWindowLogin">登陆</a>';
        _titleTab += '<a href="#" class="ie6png" id="toWindowRegister">注册</a>';
        _titleTab += '</div>';
        _root.showBar = new com.layer({
            isModal: true,
            title: _titleTab,
            titleClass: 'pop_title',
            content: '',
            src: _src,
            closeText: _close,
            closeStyle: {
                position: 'absolute',
                top: '-16px',
                right: '-16px',
                width: 36,
                height: 36,
                'line-height': '26px',
                'cursor': 'pointer'
            },
            contentStyle: {
                overflow: "hidden"
            },
            loaded: false,
            cache: false,
            opacity: 0.2,
            style: {
                position: "absolute",
                backgroundColor: "transparent",
                border: "2px solid #945c27",
                overflow: "visible",
                width: Ejs.UserWindow.defaultOptions.width,
                height: Ejs.UserWindow.defaultOptions.height,
                borderRadius: 4
            },
            positionType: com.layer.positionType.center,
            isBgClose: true,
            onshowing: function () {
                Ejs.UserWindow.defaultOptions.beforeShow();
                $("#toWindowLogin").click(function (ea) {
                    ea.preventDefault();
                    $("div.switchPanel>a").removeClass("cur");
                    $(this).addClass("cur");
                    _root.showBar.setSrc(_root.getLoginUrl());
                    _root.showBar.setHeight(345);
                });

                $("#toWindowRegister").click(function (ea) {
                    ea.preventDefault();
                    $("div.switchPanel>a").removeClass("cur");
                    $(this).addClass("cur");
                    _root.showBar.setSrc(_root.getRegisterUrl());
                    _root.showBar.setHeight(550);
                });
            },
            onclosing: function () {
                Ejs.UserWindow.defaultOptions.beforeClose();
                $("#" + _root.showBar.getId()).remove();
            },
            outClass: "pop_com_wrapper"
        });

        _root.showBar.show();
    },
    remove: function () {
        this.showBar.hide();
    },
    evenEsc: function () {
        var _root = this;
        $(document).unbind('keydown');
        $(document).bind("keydown", function (event) {
            if (event.keyCode == 27) {
                _root.remove();
            }
        });
    }
};
