/**
 * 公共JS代码，每个页面都需要引入
 */

/**
 * Lazy Load - jQuery plugin for lazy loading images
 *
 * Copyright (c) 2007-2013 Mika Tuupola
 *
 *  Version:  1.8.4
 *
 */
(function (a, b, c, d) {
    var e = a(b);
    a.fn.lazyload = function (c) {
        function i() {
            var b = 0;
            f.each(function () {
                var c = a(this);
                if (h.skip_invisible && !c.is(":visible"))return;
                if (!a.abovethetop(this, h) && !a.leftofbegin(this, h))if (!a.belowthefold(this, h) && !a.rightoffold(this, h))c.trigger("appear"), b = 0; else if (++b > h.failure_limit)return!1
            })
        }

        var f = this, g, h = {threshold: 0, failure_limit: 0, event: "scroll", effect: "show", container: b, data_attribute: "original", skip_invisible: !0, appear: null, load: null};
        return c && (d !== c.failurelimit && (c.failure_limit = c.failurelimit, delete c.failurelimit), d !== c.effectspeed && (c.effect_speed = c.effectspeed, delete c.effectspeed), a.extend(h, c)), g = h.container === d || h.container === b ? e : a(h.container), 0 === h.event.indexOf("scroll") && g.bind(h.event, function (a) {
            return i()
        }), this.each(function () {
            var b = this, c = a(b);
            b.loaded = !1, c.one("appear", function () {
                if (!this.loaded) {
                    if (h.appear) {
                        var d = f.length;
                        h.appear.call(b, d, h)
                    }
                    a("<img />").bind("load", function () {
                        c.hide().attr("src", c.data(h.data_attribute))[h.effect](h.effect_speed), b.loaded = !0;
                        var d = a.grep(f, function (a) {
                            return!a.loaded
                        });
                        f = a(d);
                        if (h.load) {
                            var e = f.length;
                            h.load.call(b, e, h)
                        }
                    }).attr("src", c.data(h.data_attribute))
                }
            }), 0 !== h.event.indexOf("scroll") && c.bind(h.event, function (a) {
                b.loaded || c.trigger("appear")
            })
        }), e.bind("resize", function (a) {
            i()
        }), /iphone|ipod|ipad.*os 5/gi.test(navigator.appVersion) && e.bind("pageshow", function (b) {
            b.originalEvent.persisted && f.each(function () {
                a(this).trigger("appear")
            })
        }), a(b).load(function () {
            i()
        }), this
    }, a.belowthefold = function (c, f) {
        var g;
        return f.container === d || f.container === b ? g = e.height() + e.scrollTop() : g = a(f.container).offset().top + a(f.container).height(), g <= a(c).offset().top - f.threshold
    }, a.rightoffold = function (c, f) {
        var g;
        return f.container === d || f.container === b ? g = e.width() + e.scrollLeft() : g = a(f.container).offset().left + a(f.container).width(), g <= a(c).offset().left - f.threshold
    }, a.abovethetop = function (c, f) {
        var g;
        return f.container === d || f.container === b ? g = e.scrollTop() : g = a(f.container).offset().top, g >= a(c).offset().top + f.threshold + a(c).height()
    }, a.leftofbegin = function (c, f) {
        var g;
        return f.container === d || f.container === b ? g = e.scrollLeft() : g = a(f.container).offset().left, g >= a(c).offset().left + f.threshold + a(c).width()
    }, a.inviewport = function (b, c) {
        return!a.rightoffold(b, c) && !a.leftofbegin(b, c) && !a.belowthefold(b, c) && !a.abovethetop(b, c)
    }, a.extend(a.expr[":"], {"below-the-fold": function (b) {
        return a.belowthefold(b, {threshold: 0})
    }, "above-the-top": function (b) {
        return!a.belowthefold(b, {threshold: 0})
    }, "right-of-screen": function (b) {
        return a.rightoffold(b, {threshold: 0})
    }, "left-of-screen": function (b) {
        return!a.rightoffold(b, {threshold: 0})
    }, "in-viewport": function (b) {
        return a.inviewport(b, {threshold: 0})
    }, "above-the-fold": function (b) {
        return!a.belowthefold(b, {threshold: 0})
    }, "right-of-fold": function (b) {
        return a.rightoffold(b, {threshold: 0})
    }, "left-of-fold": function (b) {
        return!a.rightoffold(b, {threshold: 0})
    }})
})(jQuery, window, document)

/**
 * jQuery cookie Plugin
 */
jQuery.cookie = function (name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString();
        }
        var path = options.path ? '; path=' + (options.path) : '';
        var domain = options.domain ? '; domain=' + (options.domain) : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else {
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};

/**
 * http://www.JSON.org/json2.js
 * 2008-05-25
 */
if (!this.JSON) {
    JSON = function () {

        function f(n) {
            return n < 10 ? '0' + n : n;
        }

        Date.prototype.toJSON = function (key) {
            return this.getUTCFullYear() + '-' + f(this.getUTCMonth() + 1) + '-' + f(this.getUTCDate()) + 'T' + f(this.getUTCHours()) + ':' + f(this.getUTCMinutes()) + ':' + f(this.getUTCSeconds()) + 'Z';
        };

        var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
            escapeable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
            gap, indent, meta = {
                '\b': '\\b',
                '\t': '\\t',
                '\n': '\\n',
                '\f': '\\f',
                '\r': '\\r',
                '"': '\\"',
                '\\': '\\\\'
            },
            rep;

        function quote(string) {
            escapeable.lastIndex = 0;
            return escapeable.test(string) ? '"' + string.replace(escapeable, function (a) {
                var c = meta[a];
                if (typeof c === 'string') {
                    return c;
                }
                return '\\u' + ('0000' + (+(a.charCodeAt(0))).toString(16)).slice(-4);
            }) + '"' : '"' + string + '"';
        }

        function str(key, holder) {
            var i,
                k,
                v,
                length, mind = gap,
                partial, value = holder[key];

            if (value && typeof value === 'object' && typeof value.toJSON === 'function') {
                value = value.toJSON(key);
            }

            if (typeof rep === 'function') {
                value = rep.call(holder, key, value);
            }

            switch (typeof value) {
                case 'string':
                    return quote(value);
                case 'number':
                    return isFinite(value) ? String(value) : 'null';
                case 'boolean':
                case 'null':
                    return String(value);
                case 'object':

                    if (!value) {
                        return 'null';
                    }

                    gap += indent;
                    partial = [];

                    if (typeof value.length === 'number' && !(value.propertyIsEnumerable('length'))) {

                        length = value.length;
                        for (i = 0; i < length; i += 1) {
                            partial[i] = str(i, value) || 'null';
                        }

                        v = partial.length === 0 ? '[]' : gap ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']' : '[' + partial.join(',') + ']';
                        gap = mind;
                        return v;
                    }

                    if (rep && typeof rep === 'object') {
                        length = rep.length;
                        for (i = 0; i < length; i += 1) {
                            k = rep[i];
                            if (typeof k === 'string') {
                                v = str(k, value, rep);
                                if (v) {
                                    partial.push(quote(k) + (gap ? ': ' : ':') + v);
                                }
                            }
                        }
                    } else {

                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = str(k, value, rep);
                                if (v) {
                                    partial.push(quote(k) + (gap ? ': ' : ':') + v);
                                }
                            }
                        }
                    }

                    v = partial.length === 0 ? '{}' : gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}' : '{' + partial.join(',') + '}';
                    gap = mind;
                    return v;
            }
        }

        return {
            stringify: function (value, replacer, space) {
                var i;
                gap = '';
                indent = '';

                if (typeof space === 'number') {
                    for (i = 0; i < space; i += 1) {
                        indent += ' ';
                    }
                } else if (typeof space === 'string') {
                    indent = space;
                }

                rep = replacer;
                if (replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')) {
                    throw new Error('JSON.stringify');
                }

                return str('', {
                    '': value
                });
            },

            parse: function (text, reviver) {

                var j;

                function walk(holder, key) {
                    var k, v, value = holder[key];
                    if (value && typeof value === 'object') {
                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = walk(value, k);
                                if (v !== undefined) {
                                    value[k] = v;
                                } else {
                                    delete value[k];
                                }
                            }
                        }
                    }
                    return reviver.call(holder, key, value);
                }

                cx.lastIndex = 0;
                if (cx.test(text)) {
                    text = text.replace(cx, function (a) {
                        return '\\u' + ('0000' + (+(a.charCodeAt(0))).toString(16)).slice(-4);
                    });
                }

                if (/^[\],:{}\s]*$/.
                    test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
                        replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
                        replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

                    j = eval('(' + text + ')');

                    return typeof reviver === 'function' ? walk({
                            '': j
                        },
                        '') : j;
                }

                throw new SyntaxError('JSON.parse');
            }
        };
    }();
}

/*!
 * jQuery TextChange Plugin
 * http://www.zurb.com/playground/jquery-text-change-custom-event
 *
 * Copyright 2010, ZURB
 * Released under the MIT License
 */
 (function(a){a.event.special.textchange={setup:function(){a(this).data("lastValue",this.contentEditable==="true"?a(this).html():a(this).val());a(this).bind("keyup.textchange",a.event.special.textchange.handler);a(this).bind("cut.textchange paste.textchange input.textchange",a.event.special.textchange.delayedHandler)},teardown:function(){a(this).unbind(".textchange")},handler:function(){a.event.special.textchange.triggerIfChanged(a(this))},delayedHandler:function(){var c=a(this);setTimeout(function(){a.event.special.textchange.triggerIfChanged(c)},
 25)},triggerIfChanged:function(a){var b=a[0].contentEditable==="true"?a.html():a.val();b!==a.data("lastValue")&&(a.trigger("textchange",[a.data("lastValue")]),a.data("lastValue",b))}};a.event.special.hastext={setup:function(){a(this).bind("textchange",a.event.special.hastext.handler)},teardown:function(){a(this).unbind("textchange",a.event.special.hastext.handler)},handler:function(c,b){b===""&&b!==a(this).val()&&a(this).trigger("hastext")}};a.event.special.notext={setup:function(){a(this).bind("textchange",
 a.event.special.notext.handler)},teardown:function(){a(this).unbind("textchange",a.event.special.notext.handler)},handler:function(c,b){a(this).val()===""&&a(this).val()!==b&&a(this).trigger("notext")}}})(jQuery);

/**
 * 弹出层
 */
var com = window.com || {};
(function () {
    com.layer = function (options, deep) {
        this.id = "com_layer_div_" + (com.layer.__layerNumber++);
        this.contentBgFrameId = "com_layer_bg_content_iframe" + com.layer.__layerNumber;
        var opt = {};
        $.extend(true, opt, com.layer.defaultOptions);
        this.options = opt;
        this.setOptions(options, deep);
        this._init();
    };
    com.layer.__bgIframeId = "com_layer_bg_iframe";
    com.layer.__bgDivId = "com_layer_bg_shade";
    com.layer.__layerNumber = 0;
    com.layer.triggerType = {
        hover: function () {
            var trigger = this.options.trigger;
            var wrapperId = this.getId();
            var src = this;
            var over = false;
            var curTrigger;
            var timeoutId;
            $("#" + wrapperId).hover(function () {
                    clearTimeout(timeoutId);
                    over = true;
                },
                function () {
                    over = false;
                    timeoutId = setTimeout(close, src.options.closeTimeout);
                });
            $(trigger).hover(function () {
                    clearTimeout(timeoutId);
                    over = true;
                    if (curTrigger == this && src.alreadyShow) {
                        return;
                    }
                    src.options.positionType.call(src, this);
                    src.show(this);
                    curTrigger = this;
                },
                function () {
                    over = false;
                    timeoutId = setTimeout(close, src.options.closeTimeout);
                });

            function close() {
                if (!over) {
                    src.hide();
                }
            }
        },
        click: function () {
            var trigger = this.options.trigger;
            var wrapperId = this.getId();
            var src = this;
            var curTrigger;
            $(trigger).click(function (e) {
                e.stopPropagation();
                if (curTrigger == this && src.alreadyShow) {
                    return;
                }
                src.show(this);
            });
            $("#" + wrapperId).click(function (e) {
                e.stopPropagation();
            });
        }
    };
    com.layer.positionType = {
        center: function () {
            var wrapper = $("#" + this.getId());
            var optOffset = this.options.offset;
            var top = $(window).scrollTop() + $(window).height() / 2;
            var pos = {
                position: "absolute",
                left: "50%",
                top: top,
                marginLeft: -(wrapper.width() / 2) + optOffset.x,
                marginTop: -(wrapper.height() / 2) + optOffset.y
            };
            wrapper.css(pos);
            if ($.ie6()) {
                $("#" + this.contentBgFrameId).css(pos).css({
                    width: wrapper.width(),
                    height: wrapper.height()
                });
            }
        },
        margin: function (trigger) {
            if (!trigger) {
                return;
            }
            trigger = trigger || this.options.trigger;
            var wrapper = $("#" + this.getId());
            var trigger = $(trigger);
            var optOffset = this.options.offset;
            var triggerOffset = trigger.offset();
            var pos = {
                position: "absolute",
                left: triggerOffset.left + optOffset.x,
                top: triggerOffset.top + optOffset.y
            };
            wrapper.css(pos);
            if ($.ie6()) {
                $("#" + this.contentBgFrameId).css(pos).css({
                    width: wrapper.outerWidth(),
                    height: wrapper.outerHeight()
                });
            }
        }
    };
    com.layer.showAction = {
        normal: function () {
            $("#" + this.getId()).show();
        },
        fade: function () {
            $("#" + this.getId()).fadeIn(this.options.actionSpeed);
        },
        slideV: function () {
            $("#" + this.getId()).slideDown(this.options.actionSpeed);
        },
        expand: function () {
            $("#" + this.getId()).show(this.options.actionSpeed);
        }
    };
    com.layer.hideAction = {
        normal: function () {
            $("#" + this.getId()).hide();
        },
        fade: function () {
            $("#" + this.getId()).fadeOut(this.options.actionSpeed);
        },
        slideV: function () {
            $("#" + this.getId()).slideUp(this.options.actionSpeed);
        },
        expand: function () {
            $("#" + this.getId()).hide(this.options.actionSpeed);
        }
    };
    com.layer.defaultOptions = {
        trigger: "",
        content: '',
        src: "",
        loaded: false,
        cache: true,
        title: '',
        closeText: "",
        isModal: false,
        shadeColor: "#000000",
        opacity: 0.3,
        outClass: "",
        titleClass: "",
        contentClass: "",
        closeClass: "",
        style: {
            position: "absolute",
            backgroundColor: "#f0f0f0",
            border: "5px solid #d0d0d0",
            overflow: "hidden",
            width: 300,
            height: 120
        },
        titleStyle: {},
        closeStyle: {},
        contentStyle: {},
        triggerType: com.layer.triggerType.click,
        positionType: com.layer.positionType.margin,
        showAction: com.layer.showAction.fade,
        hideAction: com.layer.hideAction.slideV,
        actionSpeed: 400,
        closeTimeout: 800,
        isBgClose: true,
        offset: {
            x: 0,
            y: 0
        },
        onshowing: function () {
        },
        onclosing: function () {
        },
        afterShow: function () {
        }
    };
    com.layer.prototype = {
        setOptions: function (options, deep) {
            if (typeof deep == "boolean" && !deep) {
                $.extend(this.options, options || {});
            }
            $.extend(true, this.options, options || {});
        },
        _init: function () {
            if (!!this._hasInit) {
                return;
            }
            this._createContentBgIframe();
            var opt = this.options;
            if (opt.isModal) {
                this._createShade();
            }
            var wrapperDiv = $("<div>");
            wrapperDiv.attr("id", this.getId());
            wrapperDiv.css(opt.style);
            wrapperDiv.css("zIndex", $.getMaxIndex() + 10);
            wrapperDiv.addClass(opt.outClass);
            if (opt.title != "") {
                var titleDiv = $("<div>");
                titleDiv.attr("id", this.getTitleId());
                titleDiv.css(opt.titleStyle);
                titleDiv.addClass(opt.titleClass);
                titleDiv.html(opt.title);
                titleDiv.appendTo(wrapperDiv);
            }
            if (!!opt.closeText) {
                var src = this;
                var closeDiv = $("<div>");
                closeDiv.attr("id", this.getCloseId());
                closeDiv.css(opt.closeStyle);
                closeDiv.addClass(opt.closeClass);
                closeDiv.html(opt.closeText);
                closeDiv.click(function (e) {
                    e.preventDefault();
                    src.hide();
                });
                closeDiv.appendTo(wrapperDiv);
            }
            var contentDiv = $("<div>");
            contentDiv.attr("id", this.getContentId());
            contentDiv.css(opt.contentStyle);
            contentDiv.addClass(opt.contentClass);
            if (this.isIframeContent()) {
                var iframe = $("<iframe>");
                iframe.attr({
                    frameborder: 0,
                    allowtransparency: true
                });
                if (opt.loaded) {
                    iframe.attr("src", opt.src + (opt.cache ? "" : (/\?/.test(opt.src) ? "&_=" : "?_=") + Math.random()));
                }
                iframe.appendTo(contentDiv);
            } else {
                contentDiv.html($(opt.content));
            }
            contentDiv.appendTo(wrapperDiv);
            wrapperDiv.hide().appendTo($(document.body));
            this._resizeIframe();
            if (this.hasTrigger()) {
                opt.triggerType.apply(this);
            }
            if (this.options.isModal && this.options.isBgClose) {
                var src = this;
                $("#" + com.layer.__bgDivId).click(function (e) {
                    src.hide();
                    alreadyShow = false;
                });
            }
            this._hasInit = true;
        },
        getSize: function () {
            return {
                width: this.options.style.width,
                height: this.options.style.height
            }
        },
        reload: function () {
            var iframe = this.getIframe();
            if (iframe != null) {
                iframe[0].reload();
            }
        },
        setWidth: function (width) {
            this.options.style.width = width;
            this.getWrapper().width(width);
            this._resizeIframe();
        },
        setHeight: function (height) {
            this.options.style.height = height;
            this.getWrapper().height(height);
            this._resizeIframe();
        },
        _resizeIframe: function () {
            var iframe = this.getIframe();
            if (iframe != null) {
                var size = this.getSize();
                iframe.css({
                    width: size.width,
                    height: size.height + ( !!this.options.title ? -$("#" + this.getTitleId()).height() : 0)
                });
            }
        },
        _showContentBgFrame: function () {
            if ($.ie6()) {
                $("#" + this.contentBgFrameId).show();
            }
        },
        _hideContentBgFrame: function () {
            if ($.ie6()) {
                $("#" + this.contentBgFrameId).hide();
            }
        },
        _createContentBgIframe: function () {
            if ($.ie6()) {
                var iframe = $("<iframe>");
                iframe.css({
                    position: "absolute",
                    opacity: 0,
                    display: "none"
                });
                iframe.attr({
                    id: this.contentBgFrameId,
                    frameborder: 0
                });
                iframe.appendTo($("body"));
            }
        },
        _createShade: function () {
            var __hiddenIframeId = com.layer.__bgIframeId;
            if ($("#" + __hiddenIframeId).length == 0) {
                var opt = this.options;
                var maxIndex = $.getMaxIndex();
                var iframe = $("<iframe>");
                iframe.css({
                    position: "absolute",
                    top: 0,
                    left: 0,
                    zIndex: maxIndex++,
                    opacity: 0,
                    display: "none"
                });
                iframe.attr({
                    id: __hiddenIframeId,
                    frameborder: 0
                });
                iframe.appendTo($("body"));
                var bgDiv = '<div id="' + com.layer.__bgDivId + '" style="background:' + opt.shadeColor + '; filter:alpha(opacity=' + (opt.opacity * 100) + '); opacity: ' + opt.opacity + '; z-index:' + maxIndex + '; position:absolute; left:0px; top:0px;display:none;"></div>';
                $("body").append(bgDiv);
                $(window).resize(function () {
                    var bgIframe = $("#" + com.layer.__bgIframeId);
                    if (bgIframe.length == 0) return;
                    var bw = $(window).width();
                    var bh = $(document.body).height() > $(window).height() ? $(document).height() : $(window).height();
                    bgIframe.width(bw);
                    bgIframe.height(bh);
                    var bgDiv = $("#" + com.layer.__bgDivId);
                    bgDiv.width(bw);
                    bgDiv.height(bh);
                });
                $(window).resize();
            }
        },
        _showShade: function () {
            $("#" + com.layer.__bgDivId).show();
            $("#" + com.layer.__bgIframeId).show();
        },
        _hideShade: function () {
            $("#" + com.layer.__bgDivId).hide();
            $("#" + com.layer.__bgIframeId).hide();
        },
        isIframeContent: function () {
            return !!this.options.src;
        },
        getIframe: function () {
            if (this.isIframeContent()) {
                return this.getWrapper().find("iframe");
            }
            return null;
        },
        setTitle: function (html) {
            this.options.title = html;
            $("#" + this.getTitleId()).html(html);
        },
        setContent: function (html) {
            this.options.content = html;
            $("#" + this.getContentId()).html(html);
        },
        getId: function () {
            return this.id;
        },
        getCloseId: function () {
            return this.getId() + "_close";
        },
        getTitleId: function () {
            return this.getId() + '_title';
        },
        getContentId: function () {
            return this.getId() + "_content";
        },
        getTriggers: function () {
            if (this.hasTrigger) {
                return $(this.options.trigger)
            }
        },
        getWrapper: function () {
            return $("#" + this.getId());
        },
        hasTrigger: function () {
            return !!this.options.trigger;
        },
        setSrc: function (src) {
            this.options.src = src;
            this.reload();
        },
        reload: function () {
            var iframe = this.getIframe();
            if (iframe != null) {
                var opt = this.options;
                iframe.attr("src", opt.src + (opt.cache ? "" : (/\?/.test(opt.src) ? "&_=" : "?_=") + Math.random()));
                this.options.loaded = true;
            }
        },
        show: function (trigger) {
            if (!!trigger) {
                this.lastTrigger = trigger;
            }
            if (typeof this.options.onshowing == "function") this.options.onshowing.call(this, trigger);
            if (!this.options.loaded) {
                this.reload();
            }
            if (this.alreadyShow) {
                return;
            }
            this.options.positionType.call(this, trigger);
            if (this.options.isModal) {
                this._showShade();
            }
            this._showContentBgFrame();
            this.options.showAction.apply(this);
            this.alreadyShow = true;
            if (typeof this.options.afterShow == "function") this.options.afterShow.call(this, trigger);
        },
        hide: function () {
            if (!this.alreadyShow) {
                return;
            }
            if (this.options.isModal) {
                this._hideShade();
            }
            this._hideContentBgFrame();
            this.options.hideAction.apply(this);
            this.alreadyShow = false;
            if (typeof this.options.onclosing == "function") {
                this.options.onclosing.call(this);
            }
        },
        unbind: function (type) {
            var trigger = this.options.trigger;
            var wrapperId = this.getId();
            $(trigger).unbind(type);
            $("#" + wrapperId).unbind(type);
        },
        abandon: function () {
            this.options.triggerType.unbind.apply(this);
            $("#" + this.getId()).remove();
        }
    };
}());

(function ($) {
    $.extend({
        getMaxIndex: function () {
            var index = 0;
            $('*').each(function (i, n) {
                var tem = parseInt($(n).css("z-index"));
                if (tem > 0) {
                    if (tem > index) {
                        index = tem + 1;
                    }
                }
            });
            return index;
        },
        ie6: function () {
            return $.browser.msie && $.browser.version == "6.0";
        }
    });
})(jQuery);


var Ejs = window.Ejs || {};
var _isIE6 = window.VBArray && !window.XMLHttpRequest;

function logger(msg) {
    if (!!window.console) {
        console.log(msg);
    }
}

// 提示信息
Ejs.tip = function (ele, id, msg, x, y, timer) {
    var _timer = 1200;
    var _x = 0;
    var _y = 0;
    if (typeof x === "number") {
        _x = x;
    }
    if (typeof y === "number") {
        _y = y;
    }
    if (typeof timer === "number") {
        _timer = timer;
    }
    var getId = $("#" + id),
        Left = ele.offset().left + parseInt(_y),
        Top = ele.offset().top + parseInt(_x),
        Css = {
            position: "absolute",
            left: Left,
            top: Top,
            display: "none"
        };

    if (getId.length > 0) {
        getId.hide();
    } else {
        getId = $("<div>" + msg + "</div>")
            .attr("id", id)
            .addClass("com_tip");
        $("body").append(getId);
    }
    getId.css(Css).stop().fadeIn(600);

    window.setTimeout(function () {
        getId.fadeOut(400, function () {
            getId.remove();
        });
    }, _timer);
};

Ejs.url = window.location.href;

Ejs.get = function (paras) {
    var _url = Ejs.url,
        paraString = _url.substring(_url.indexOf("?") + 1, _url.length).split("&"),
        paraObj = {},
        i, j;
    for (i = 0; j = paraString[i]; i++) {
        paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
    }
    var returnValue = paraObj[paras.toLowerCase()];
    if (typeof(returnValue) == "undefined") {
        return '';
    } else {
        return returnValue;
    }
};

// 网站导航
Ejs.nav = function () {
    "use strict";
    var nav = $('.e-nav');

    nav.find('.category-all-main .items').height($('.e-nav .category-all-main').height());

    nav.find('.category-all').hover(function () {
        $(this).addClass('open');
        $(this).find('.category-all-main').stop().show();
    }, function () {
        $(this).removeClass('open');
        $(this).find('.category-all-main').stop().hide();
    });

    nav.on('click', '.category-all .btn-close', function () {
        $(this).removeClass('open');
        $('.e-nav .category-all-main').hide();
    });
};

// 网站类目
Ejs.category = function () {
    "use strict";
    var nav = $("#common_category").find(".category_main"),
        navChildren = nav.find("li");

    if (nav.length < 1) {
        return;
    }

    navChildren.each(function (i) {
        var that = $(this),
            subNavWidth = 0,
            dls = that.find("dl"),
            dds,
            ddsLength,
            subNavLeft,
            navOffsetLeft,
            thatOffsetLeft,
            thatLeft;

        that.hover(function () {
            dls.last().addClass("last");
            navOffsetLeft = nav.offset().left;
            thatOffsetLeft = that.offset().left;
            thatLeft = parseInt(thatOffsetLeft - navOffsetLeft);
            navChildren.removeClass("current");
            that.addClass("current");

            if (dls.length < 1) {
                return;
            }

            var imgElement = $(this).find("img"),
                dataOriginal = imgElement.attr("data-original");
            if (imgElement.attr("src") !== dataOriginal) {
                imgElement.attr("src", dataOriginal);
            }

            if (!that.attr("data")) {
                dls.each(function () {
                    dds = $(this).find("dd");
                    ddsLength = dds.length;
                    if (ddsLength > 0) {
                        $(this).width(Math.ceil(dds.length / 6) * 95);
                    } else {
                        $(this).width(95);
                    }
                    subNavWidth += $(this).outerWidth();
                });
                subNavWidth += dls.length * 25;
                subNavWidth += (150 + 30);
                that.find(".subnav_main").width(subNavWidth);
                subNavWidth += 25;
                that.find(".subnav_inner").width(subNavWidth);
                subNavLeft = thatLeft + subNavWidth;

                if (subNavLeft > 1000) {
                    subNavLeft = subNavWidth - (1000 - thatLeft);
                    that.find(".subnav_inner").css("left", -subNavLeft + 'px');
                }

                that.attr("data", "true");
            }
            that.find(".subnav").stop().fadeIn(600);
        }, function () {
            that.removeClass("current");
            that.find(".subnav").hide();
        });
    });
};

// 网站类目高亮当前项
Ejs.categoryHighlight = function () {
    var _nav = $("#common_category");
    if (_nav.length < 1) {
        return
    }
    var _url = window.location.href;
    var _id = 0;
    if (_url.indexOf("channel") > 0) {
        _id = _url.substring(_url.indexOf("channel") + 8, _url.length);
        if (typeof(_id) == "undefined") {
            return
        }
    } else if (_url.indexOf("products") > 0) {
        _id = Ejs.get("cat");
    }
    if (!/^[1-9][0-9]*$/.test(_id)) {
        return
    }
    var _navHighlight = false;
    _nav.find("li").each(function () {
        if (_navHighlight) {
            return
        }
        var _cid = $(this).attr("channelId");
        if (!!_cid == false) {
            return
        }
        if (parseInt(_cid) == _id) {
            $(this).addClass("cur");
            _navHighlight = true;
        }
    });
};

// 网站公用弹出框对话框
Ejs.Dialog = function (options) {
    this.options = options || {};
    this.retuenVal = false;
    this.defaultOptions = {
        buttons: [],
        type: Ejs.Dialog.type.INFO,
        outClass: 'com_pop_window',
        border: '0',
        backgroundColor: "transparent",
        typeStyle: {},
        title: "",
        titleStyle: {},
        titleWrapperStyle: {},
        info: "",
        infoStyle: {},
        width: 408,
        height: 224,
        isModal: true,
        isConfirm: false,
        isLeftHand: false,
        isBgClose: true,
        opacity: 0.3,
        eventObj: null,
        eventOffset: {
            x: 0,
            y: 0
        },
        afterClose: function (value) {
        },
        beforeShow: function () {
        },
        preventClosed: false
    };
    this.initialise();
};

Ejs.Dialog.type = {
    WARN: "warn",
    RIGHT: "right",
    ERROR: "error"
};

Ejs.Dialog.prototype = {
    initialise: function () {
        $.extend(true, this.defaultOptions, this.options);
        this.generateDialog();
    },
    generateDialog: function () {
        var _root = this;
        _root._dialog = new com.layer({
            positionType: com.layer.positionType.center,
            isModal: _root.defaultOptions.isModal,
            opacity: _root.defaultOptions.opacity,
            isBgClose: _root.defaultOptions.isBgClose,
            outClass: _root.defaultOptions.outClass,
            closeClass: "close",
            closeText: '<a class="fix_ie6" href="#"></a>',
            contentClass: 'content_wrapper',
            content: '',
            onshowing: function () {
                _root.defaultOptions.beforeShow.call(_root);

            },
            afterShow: function () {
                if (_root.defaultOptions.isConfirm) {
                    _root.reSetConfirm();
                }
            },
            onclosing: function () {
                $("#" + _root._dialog.getId()).remove();
                _root.defaultOptions.afterClose.call(_root, _root.retuenVal);
            },
            style: {
                width: _root.defaultOptions.width,
                height: _root.defaultOptions.height,
                border: _root.defaultOptions.border,
                backgroundColor: _root.defaultOptions.backgroundColor,
                overflow: "visible"
            }
        });

        _root.generateHTML().appendTo($("#" + _root._dialog.getContentId()));
        _root._dialog.show();
    },
    generateHTML: function () {
        var _root = this;
        var html = $("<div></div>");
        var title = $("<div></div>");
        var info = $("<div></div>");
        var button = $("<div></div>");
        title.addClass("title_wrapper");
        title.css(this.defaultOptions.titleWrapperStyle);

        var _type = $("<div></div>");
        _type.addClass(this.defaultOptions.type);
        _type.css(this.defaultOptions.typeStyle);
        _type.appendTo(title);
        $("<div></div>").addClass("title").css(this.defaultOptions.titleStyle).html(this.defaultOptions.title).appendTo(title);
        title.appendTo(html);

        info.addClass("info_wrapper");
        info.css(this.defaultOptions.infoStyle);
        $("<div></div>").addClass("info").html(this.defaultOptions.info).appendTo(info);
        info.appendTo(html);

        button.addClass("button_wrapper");

        if (this.defaultOptions.isConfirm) {
            var button_yes = $("<a class='e-btn btn-default'>确定</a> ");
            var button_no = $("<a class='e-btn btn-grey'>取消</a> ");

            if (this.defaultOptions.isLeftHand) {
                button_no.css({
                    marginRight: 0
                });
                button_yes.appendTo(button);
                button_no.appendTo(button);
            } else {
                button_yes.css({
                    marginRight: 0
                });
                button_no.appendTo(button);
                button_yes.appendTo(button);
            }

            button_yes.click(function (e) {
                e.preventDefault();
                _root.retuenVal = true;
                _root._dialog.hide();
            });
            button_no.click(function (e) {
                e.preventDefault();
                _root.retuenVal = false;
                _root._dialog.hide();
            });

        } else {
            if (this.defaultOptions.buttons.length < 1) {
                var button_close = $("<input type='button' class='button_b' value='关闭'/> ");
                button_close.css({
                    marginRight: 0
                });
                button_close.appendTo(button);
                button_close.click(function (e) {
                    e.preventDefault();
                    _root._dialog.hide();
                });
            } else {
                for (var i = 0; i < this.defaultOptions.buttons.length; i++) {
                    _root.generateButton(button, this.defaultOptions.buttons[i]);
                }
            }
        }

        button.appendTo(html);

        return html;
    },
    generateButton: function (button, ele) {
        var _root = this;
        var cloneButton = {
            buttonText: "确定",
            buttonClass: "button_a",
            buttonStyle: {

            },
            onClick: function (v) {

            }
        };
        $.extend(true, cloneButton, ele || {});
        var newButton = $("<input type='button'/>");
        newButton.val(cloneButton.buttonText);
        newButton.addClass(cloneButton.buttonClass);
        newButton.css(cloneButton.buttonStyle);
        newButton.appendTo(button);
        newButton.click(function (e) {
            e.preventDefault();
            if (!_root.defaultOptions.preventClosed) {
                _root._dialog.hide();
            }
            cloneButton.onClick.call(this);
        });
    },
    getContent: function () {
        return $("#" + this._dialog.getContentId());
    },
    getClose: function () {
        return $("#" + this._dialog.getCloseId());
    },
    reSetConfirm: function () {
        var _root = this;
        $("#" + _root._dialog.getId()).removeClass("com_pop_window").addClass("com_pop_close com_pop_window");
        _root._dialog.setHeight(148);
        _root._dialog.setWidth(270);
        if (this.defaultOptions.eventObj != null) {
            _root._dialog.getWrapper().css({
                left: _root.defaultOptions.eventObj.pageX - 110 + _root.defaultOptions.eventOffset.x,
                top: _root.defaultOptions.eventObj.pageY + 110 + _root.defaultOptions.eventOffset.y
            });
        }
    },
    remove: function(){
        this._dialog.hide();
    }
};

// 用户状态公用类
Ejs.UserStatus = {};

// 用户是否登陆判断函数
Ejs.UserStatus.isLogin = function (successFunction, failFunction) {
    var _checkIsLoginUrl = EJS.CheckLoginStatus + '?callback=?';
    $.getJSON(_checkIsLoginUrl, function (data) {
        if (data[0].success) {
            typeof successFunction === 'function' && successFunction();
        } else {
            typeof failFunction === 'function' && failFunction();
        }
    });
};

// 网站搜索框
Ejs.search = function () {

    var hotKeyWords = ['炒锅', '汤锅', '奶锅', '水果刀', '油壶', '筷子', '茶壶', '水杯', '凳子', '垃圾桶'],
        form = $('#header_search_form'),
        keywordInput = $('#e-keyword'),
        defaultKeyword = Ejs.get('keyword'),
        value = '',
        listEle = $('#search-keyword-list'),
        isKeyPress = false,
        activityListItemIndex,
        listCount = 0,
        searchUrl = EJS.SearchUrl,
        searchSuggestUrl = EJS.SearchSuggestUrl;

    function getKeywords() {
        var k = Math.floor(Math.random() * hotKeyWords.length);
        return hotKeyWords[k];
    }

    value = getKeywords();
    if (defaultKeyword == '') {
        keywordInput.val(value);
        keywordInput.addClass('auto');
    } else {
        defaultKeyword = defaultKeyword.replace(/\+/g,' ');
        keywordInput.val(decodeURIComponent(defaultKeyword));
    }

    form.submit(function () {
        if (keywordInput.val().length < 1) {
            keywordInput.val(value);
        }
    });

    keywordInput.on('keydown', function (event) {
        if (event.keyCode === 13) {
            form.submit();
        } else {
            keywordInput.removeClass('auto');
        }
    });

    keywordInput.on('focus', function () {
        if (keywordInput.hasClass('auto')) {
            keywordInput.val('');
        }
    });

    keywordInput.on('blur', function () {
        if (keywordInput.val().length < 1) {
            keywordInput.val(value);
            keywordInput.addClass('auto');
        }
    });

    // 选中item
    function selectedItem(i) {
        var activityListItem;

        if (i >= listCount) {
            i = 0;

        }

        if (i < 0) {
            i = listCount - 1;
        }

        activityListItemIndex = i;

        activityListItem = listEle.find('li').eq(i);
        activityListItem.addClass('activity').siblings('li').removeClass('activity');

        keywordInput.val(activityListItem.find('a').text());

        isKeyPress = true;
    }

    // 键盘移动事件
    function bindEvent() {
        $(document).unbind('keydown');
        $(document).bind('keydown', function (e) {
            switch (e.keyCode) {
                case 40:
                    activityListItemIndex++;
                    selectedItem(activityListItemIndex);
                    break;
                case 38:
                    activityListItemIndex--;
                    selectedItem(activityListItemIndex);
                    break;
                default :
                    isKeyPress = false;
            }
        });
    }

    // 加载列表
    function loadList(keyword) {

        $.getJSON(searchSuggestUrl, {keyword: keyword}, function (data) {
            var listArr,
                i = 0,
                html = [];

            if (data.success) {
                listArr = data.data.textSuggestResults;
                listCount = listArr.length;

                if (listCount < 1) {
                    listEle.hide();
                    return;
                }

                for (i; i < listCount; i++) {
                    html.push('<li><a href="' + searchUrl + '?keyword=' + listArr[i] + '">' + listArr[i] + '</a></li>');
                }

                listEle.html('<ul>' + html.join('\n') + '</ul>').show();
            }
        });

        bindEvent();

    }

    // 展开提示列表
    function showList() {
        listEle.show();
        isKeyPress = false;
        bindEvent();
    }

    // 隐藏提示列表
    function hideList() {
        listEle.hide();
        isKeyPress = false;
        $(document).unbind('keydown');
    }

    $(document).on('click', function (e) {
        if (e.target.id === 'e-keyword') {
            if (e.target.value !== '' && !$(e.target).hasClass('auto')) {
                showList();
            }
            return;
        }

        hideList();
    });

    listEle.on('mouseleave', function () {
        hideList();
    });

    keywordInput.bind('textchange', function (event, previousText) {

        if (isKeyPress) {
            return;
        }

        listCount = 0;
        activityListItemIndex = -1;
        loadList($(this).val());

    });

};

// 获取用户信息
Ejs.userInfo = function () {
    if (!EJS) {
        return;
    }
    var checkUrl = EJS.AcquireLoginUserName + "?callback=?",
        loginUserInfo = $(".e-toolbar").find(".userinfo"),
        pageLogin = EJS.ToPageLogin + "?backUrl=",
        html;

    if ($("#isRedirect").val() !== "false") {
        pageLogin += encodeURIComponent(location.href);
    }

    $.getJSON(checkUrl, function (data) {
        if (data[0].success) {
            html = '欢迎您 <a href="/my/account" target="_blank" class="username">' + data[0].data.loginUserName + '</a>' +
                ' <a href="' + EJS.LoginOut + '" class="logout">退出</a>';
            loginUserInfo.html(html);
        } else {
            html = '欢迎您 <a href="' + pageLogin + '">登录</a> <a href="' + EJS.UserRegister + '">注册</a>';
            loginUserInfo.html(html);
        }
    });
};

// 我的易居尚
Ejs.myEjs = function () {
    var self = $('.e-header .myejs'),
        body = self.find('.myejs-main'),
        pageLogin = EJS.ToPageLogin + "?backUrl=",
        html = '';

    if ($("#isRedirect").val() !== "false") {
        pageLogin += encodeURIComponent(location.href);
    }    
    self.hover(function () {
        var switchFn = function () {
            var main = $(".switch");
            main.each(function () {
                var cneter = $(this).find(".s_center"),
                    lis = $(this).find("li"),
                    now = 0,
                    len = lis.length,
                    width = lis[0].offsetWidth;
                this.onmousedown = function () { return false };
                lis.eq(len - 1).addClass("the_end");
                len <= 5 && $(this).find(".arrow").addClass("not");
                $(this).click(function (e) {
                    if (e.target.className == "prev_arrow") {
                        now && now--;
                    }
                    if (e.target.className == "next_arrow") {
                        now >= len - 5 ? now = 0 : now++;
                    }
                    cneter.stop().animate({ "margin-left": -now * width });
                });
                lis.click(function () {
                    window.location.href = $(this).attr("data-url");
                });
            });
        };
        var getCount = function(num) {
            return num && num !== 0 ? '(' + num + ')' : '';
        };
        self.addClass('open');
        body.html('<div class="ajax-loading">&nbsp;</div>').show();
        $.ajax({
            type: 'GET',
            cache: false,
            url: '/myself', 
            data: { "orderSize": "10", "followSize": "10" },
            dataType: 'JSON',
            success: function (data) {
                if (data.success) {
                    var orderList = "", collectList = "", userNavList = [];
                    var userData = data.data.result;
                    for (var m = 0; m < userData.length; m++) {
                        if (userData[m].type == "order") {
                            var orderData = userData[m];
                            if (orderData.orderList && orderData.orderList.length) {
                                orderList = [];
                                for (var i = 0; i < orderData.orderList.length; i++) {
                                    orderList[i] = "<li data-url=\"" + orderData.orderList[i].orderURL + "\">" +
                                                    "<a href=\"javascript:\"><img src=\"" + orderData.orderList[i].skuMainPicture + "\" width=\"58\" height=\"58\" /></a>" +
                                                    "<div class=\"state\"><span>" + orderData.orderList[i].orderState + "</span><b></b></div>" +
                                                    "</li>";
                                };
                                orderList = "<div class=\"switch\">" +
                                            "<div class=\"s_main\">" +
                                            "<ul class=\"s_order s_center\">" + orderList.join("") + "</ul>" +
                                            "</div>"+
                                            "<div class=\"arrow\">" +
                                            "<s class=\"prev_arrow\"></s>" +
                                            "<s class=\"next_arrow\"></s>" +
                                            "</div>" +
                                            "</div>";
                            }
                        }
                        if (userData[m].type == "follow") {
                            var collectData = userData[m];
                            if (collectData.productCollectList && collectData.productCollectList.length) {
                                collectList = [];
                                for (var j = 0; j < collectData.productCollectList.length; j++) {
                                    collectList[j] = "<li data-url=\"" + collectData.productCollectList[j].productCollectURL + "\" title=\"" + collectData.productCollectList[j].productName + "\">" +
                                                    "<a href=\"javascript:\"><img src=\"" + collectData.productCollectList[j].productMainPicture + "\" width=\"58\" height=\"58\" /></a>" +
                                                    "<div class=\"text\"><span>" + (collectData.productCollectList[j].productName).slice(0, 8) + "</span><br /><span>¥" + collectData.productCollectList[j].unitPrice + "</span><b></b></div>" +
                                                    "</li>";
                                };
                                collectList =   "<div class=\"switch\">" +
                                                "<div class=\"s_main\">" +
                                                "<ul class=\"s_center s_focus\">" + collectList.join("") + "</ul>" +
                                                "</div>" +
                                                "<div class=\"arrow\">" +
                                                "<s class=\"prev_arrow\"></s>" +
                                                "<s class=\"next_arrow\"></s>" +
                                                "</div>" +
                                                "</div>";
                            }
                        }
                        if (userData[m].type !== "order" && userData[m].type !== "follow") {
                            userNavList[m] = "<li><a href=\"" + userData[m].url + "\">" + userData[m].name + "" + getCount(userData[m].number) + "</a></li>";
                        }
                    };                    
                    html = "<div class=\"order-row\">" +
                            "<a href=\"" + orderData.url + "\" class=\"look_all\">查看全部订单</a>  " +
                            "<a href=\"" + orderData.url + "\">" + orderData.name + "" + getCount(orderData.number) + "</a>"
                            + orderList +
                            "</div>" +
                            "<ul class=\"clearfix user_nav\">" + userNavList.join("") + "</ul>" +
                            "<div class=\"follow-row\">" +
                            "<a href=\"" + collectData.url + "\" class=\"look_all\">查看全部关注</a>" +
                            "<a href=\"" + collectData.url + "\">" + collectData.name + "" + getCount(collectData.number) + "</a>"
                            + collectList +
                            "</div>";
                    body.html(html);
                    switchFn();
                }
                else {
                    html = '<div class="not-login">您好，请 <a href="' + pageLogin + '" class="e-btn btn-default btn-sm">登录易居尚</a></div>';
                    body.html(html);
                }
            },
            error: function () {
                body.html('<div class="not-login">请求出错</div>');
            }
        });
    }, function () {
        self.removeClass('open');
        body.hide().html('');
    });
};

// 顶部购物车鼠标悬停
Ejs.Cart = function () {
    if (typeof EJS === "undefined") {
        return;
    }
    this.jsonUrl = EJS.JsonCart;
    this.removeUrl = EJS.DeleteCartItem;
    this.ProDetail = EJS.ProductDetailBase;
    this.cartId = $('.minicart').eq(0);
    this.cartNumberBox = this.cartId.find('.cart-total');
    this.cartBox = this.cartId.find('.minicart-main');
    //this.cartHTML = $('#header_cart_data');
    //this.loading = $('#header_cart_loading');
    this.running = true;
    this.init();
};
Ejs.Cart.prototype = {
    init: function () {
        var parent = this;
        parent.cartId.hover(function () {
            parent.show(true);
        }, function () {
            parent.hide();
        });
        parent.bindRemoveEvent();
    },
    show: function (animation) {
        var parent = this;

        if (!parent.running) {
            return;
        }

        parent.running = false;
        parent.cartId.addClass('open');

        if (animation) {
            parent.cartBox.html('<div class="ajax-loading">&nbsp;</div>').show();
        }

        $.ajax({
            type: 'GET',
            cache: false,
            url: parent.jsonUrl,
            dataType: 'JSON',
            success: function (data) {
                var html,
                    skuList,
                    skuListLength,
                    i;

                if (data.success) {

                    skuList = data.data.skuList;
                    skuListLength = skuList.length;

                    if (skuListLength > 0) {
                        html = "<div class=\"cart-product-list\" style='display: none'><ul>";
                        for (i = 0; i < skuListLength; i++) {

                            if (i >= 5) {
                                break;
                            }

                            html += '<li  class="item clearfix">' +
                                '<div class="pic"><a href="' + parent.ProDetail + '/' + skuList[i].productId +
                                '-' + skuList[i].skuId + '">' +
                                '   <img src="' + skuList[i].imgUrl + '" alt="" /></a>' +
                                '</div>' +
                                '<div class="body">' +
                                '   <div class="name"><a href="' + parent.ProDetail + '/' + skuList[i].productId +
                                '-' + skuList[i].skuId + '">' + skuList[i].name + '</a></div>' +
                                '   <div class="price"><strong>¥' + skuList[i].price + '</strong>×' + skuList[i].number + '</div>' +
                                '   <div class="del"><a href="' + parent.removeUrl + '?skuId=' + skuList[i].skuId + '&cartId=' + data.data.cartId +
                                '" class="cart_del">删除×</a></div>' +
                                '</div>' +
                                '</li>';
                        }
                        html += "</ul>";
                        if (skuListLength > 5) {
                            html += '<div class="more"><a href="' + EJS.CartUrl + '">查看购物车所有商品&gt;&gt; </a></div>';
                        }
                        html += '<div class="count">' +
                            '共 <span>' + data.data.totalNumber + '</span> ' +
                            '件商品 &nbsp; 共计： <span>¥ ' + data.data.totalPrice + '</span>' +
                            '</div>' +
                            '<div class="btns"><a class="e-btn btn-default btn-sm" href="' + EJS.CartUrl + '">去购物车结算</a></div>';

                        if (!parent.running) {

                            if (animation) {
                                parent.cartBox.append(html).find('.cart-product-list').show();
                                parent.cartBox.find('.ajax-loading').remove();
                            } else {
                                parent.cartBox.find('.ajax-loading').remove();
                                parent.cartBox.html(html).show().find('.cart-product-list').show();
                            }

                        }

                        parent.cartNumberBox.text(data.data.totalNumber).show();
                    } else {
                        parent.cartBox.find('.ajax-loading').remove();
                        parent.cartBox.append('<div class="not-product"><span class="icon icon-cart"></span>' +
                            '您的购物车是空的哦~<!--<br><a href="#">去看看有什么值得购买 &gt;</a></div>-->').show();
                        parent.cartNumberBox.text(0).hide();
                    }

                } else {

                    if (!parent.running) {
                        parent.cartBox.find('.ajax-loading').remove();
                        parent.cartBox.html('<div class="not-product"><span class="icon icon-cart"></span>' +
                            '<p class="msg">' + data.msg + '</p></div>').show();
                    }

                }

            },
            error: function () {
                parent.cartBox.find('.ajax-loading').remove();
                parent.cartBox.html('<p class="msg">请求出错</p>').show();
            }
        });
    },
    hide: function () {
        this.cartId.removeClass('open');
        this.cartBox.hide();
        this.running = true;
    },
    bindRemoveEvent: function () {
        var parent = this;
        this.cartId.on('click', '.cart_del', function (e) {
            e.preventDefault();
            parent.removeItem($(this));
        });
    },
    removeItem: function (obj) {

        if (typeof obj === 'undefined') {
            return;
        }

        var parent = this,
            url = obj.attr('href'),
            totalPrice,
            totalNumber;

        $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            success: function (data) {
                if (data.success) {

                    totalPrice = data.data.totalPrice;
                    totalNumber = data.data.totalNumber;

                    if (data.data.totalPrice === '0.00') {
                        parent.cartBox.html('<div class="not-product"><span class="icon icon-cart"></span>' +
                            '您的购物车是空的哦~</div>');
                        parent.cartNumberBox.text(0).hide();
                    } else {
                        parent.running = true;
                        obj.parents('li').hide();
                        parent.show(false);
                    }
                } else {
                    parent.cartBox.html('<div class="not-product"><span class="icon icon-cart"></span>' +
                        '<p class="msg">' + data.msg + '</p></div>');
                }
            },
            error: function () {
                parent.cartBox.html('<div class="not-product"><span class="icon icon-cart"></span>' +
                    '<p class="msg">请求出错</p></div>');
            }
        });

    }
};
Ejs.Cart.getNumber = function () {
    var oCartNumber = $('.minicart').eq(0).find('.cart-total');
    if (!!oCartNumber === false) {
        return;
    }
    $.ajax({
        type: "get",
        cache: false,
        url: EJS.JsonCart,
        dataType: 'json',
        success: function (result) {
            var rightBar = $('#fixed-sidebar');

            if (result.success) {
                if (result.data.totalNumber > 0) {
                    oCartNumber.show().text(result.data.totalNumber);
                    rightBar.length && rightBar.find('.cart-number').text(result.data.totalNumber);
                } else {
                    oCartNumber.hide().text('0');
                    rightBar.length && rightBar.find('.cart-number').text(0);
                }
            } else {
                oCartNumber.hide().text(0);
                rightBar.length && rightBar.find('.cart-number').text(0);
            }
        }
    });
};


// 显示咨询框
Ejs.showAskWindow = function () {
    var _width = 600;
    var _height = 440;
    var _top = parseInt((parseInt($(window).height()) - _height) / 2);
    var _left = parseInt((parseInt($(window).width()) - _width) / 2);
    _top = _top > 0 ? _top : 0;
    _left = _left > 0 ? _left : 0;
    var _config = 'height=' + _height + ',width=' + _width + ',top=' + _top + ',left=' + _left + ',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no';
    window.open(EJS.HomeUrl + '/chat', '在线客服' + Math.floor(Math.random() * 1000), _config);
};

// 底部交互
Ejs.footer = function () {
    var _help = $(".footer_help");
    if (_help.length < 1) {
        return
    }
    _help.eq(0).find("dl").each(function (i) {
        $(this).addClass("fh_item_" + i);
    });
};

// 固定顶部
Ejs.topFixed = function () {

    if ($("#product_details").length > 0) {
        return;
    }
    var fixed = function () {
        var nav = $("#common_nav"),
            category = $("#common_category");
        if (parseInt($(window).height()) < 700) {
            nav.removeClass("fixed");
            category.removeClass("fixed");
            return;
        }
        if ($(window).scrollTop() > 137) {
            nav.addClass("fixed");
            category.addClass("fixed");
        } else {
            nav.removeClass("fixed");
            category.removeClass("fixed");
        }
    };

    fixed();
    $(window).scroll(function () {
        fixed();
    });
};

// 立即购买套餐组合
Ejs.submitBuyCombinationForm = function () {
    $("#buyCombinationForm").submit();
};

$(function () {
    Ejs.nav();
    //Ejs.category();
    //Ejs.categoryHighlight();
    //Ejs.topFixed();
    setTimeout(function () {
        Ejs.userInfo();
        Ejs.Cart.getNumber();

        if (Ejs.rightLayerDisplay) {
            $LAB.script(EJS.StaticDomain + '/js/module/fixedSidebar.js').wait(function(){
                Ejs.fixedSidebar();
            });
        }

    }, 600);

    Ejs.myEjs();
    new Ejs.Cart();
    Ejs.search();
    Ejs.footer();

    $('img.lazy').lazyload({ effect: 'fadeIn', threshold: 300 });
});

// 商品SKU选择
Ejs.Sku = {};

Ejs.Sku.Model = function (skuMap) {

    //保存最后的组合结果信息
    var SKUResult = {};

    //获得对象的key
    function getObjKeys(obj) {
        if (obj !== Object(obj)) throw new TypeError('Invalid object');
        var keys = [];
        for (var key in obj)
            if (Object.prototype.hasOwnProperty.call(obj, key))
                keys[keys.length] = key;
        return keys;
    }

    //把组合的key放入结果集SKUResult
    function add2SKUResult(key, sku) {
        if (SKUResult[key]) {//SKU信息key属性·
            SKUResult[key].stock += sku.stock;
            SKUResult[key].sellPrice.push(sku.sellPrice);
        } else {
            SKUResult[key] = {
                stock: sku.stock,
                sellPrice: [sku.sellPrice]
            };
        }
    }

    //初始化得到结果集
    function initSKU() {
        var skuKeys = getObjKeys(skuMap);
        for (i = 0; i < skuKeys.length; i++) {
            var skuKey = skuKeys[i];//一条SKU信息key
            var sku = skuMap[skuKey];    //一条SKU信息value
            var skuKeyAttrs = skuKey.split(","); //SKU信息key属性值数组
            var len = skuKeyAttrs.length;

            combineSKU(skuKeyAttrs, skuKeyAttrs, len, sku, 1);
        }
    }

    function combineSKU(sourceArr, keysArr, len, sku, cicleIndex) {
        var arr = copyArr(sourceArr);
        if (arr.length < 1) return;
        if (cicleIndex > 6) return;
        var tempArr = [];

        for (var i = 0; i < arr.length; i++) {
            var subArr = arr[i].split(",");
            var index = indexArr(keysArr, subArr[subArr.length - 1]);

            if (cicleIndex == 1) add2SKUResult(arr[i], sku);

            for (var j = index + 1; j < len; j++) {
                var newEle = arr[i] + "," + keysArr[j];
                tempArr.push(newEle);

                add2SKUResult(newEle, sku);
            }
        }
        cicleIndex++;
        combineSKU(tempArr, keysArr, len, sku, cicleIndex);
    }

    function copyArr(arr) {
        var temp = [];
        temp = arr.join("-").split("-");
        return temp;
    }

    function indexArr(arr, str) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == str) return i;
        }
    }

    initSKU();
    //返回数据
    return SKUResult;
};

Ejs.Sku.View = function (options) {

    var priceEle = $(options.priceEle);
    var skuTextEle = (!options.skuTextEle ? null : $(options.skuTextEle));
    var amountEle = $(options.amountEle);
    var integralEle = $(options.integralEle);
    var descEle = $(options.descEle); //2014-07-29新增描述
    var amountInputEle = $(options.amountInputEle);
    var addToCartBtn = $(options.addToCartBtn);
    var addToCartUrl = EJS.AddSkuToCart;
    var _isSKU = !!(options.hasSkuMap);

    var _selectedIds = [];
    var _stock = 0;
    var _limit = 1;

    var _price = 0;
    var _marketing = false;
    var _marketingPrice = 0;
    var _marketPrice = 0;
    var _num = 1;
    var _skuImgList = null;

    //有多个sku的情况
    if (!!options.hasSkuMap) {
        var isDirectBuy = options.isDirectBuy;
        var buyCallback = options.buyCallback;
        var SKUResult = options.hasSkuMap.model;
        var skuMap = options.hasSkuMap.skuMapData;
        var skuMapEle = $(options.hasSkuMap.skuMapEle);
        var _skuAttrNum = $(">dl", skuMapEle).length;
        var _skuEles = $('.sku', skuMapEle);
        attachSkuEvent();
        _skuImgList = createSKUImage();
    }

    //生成sku图片列表
    function createSKUImage() {
        var thumeImg = $("#photo-thumb>ul>li>a"),
            imgList = {};
        thumeImg.each(function () {
            var that = $(this);
            var src = that.children('img').attr('src');
            if (src) {
                imgList[src] = that;
            }
        });
        return imgList;
    }

    //当有sku组合时，绑定事件
    function attachSkuEvent() {

        //skumap初始化及事件绑定
        _skuEles.each(function () {
            var self = $(this);
            var attr_id = self.attr('data');
            if (self.siblings().length == 0) {
                self.addClass("current");
                veritySkuBtn(self);
            }

            if (!SKUResult[attr_id] || SKUResult[attr_id]['stock'] == 0) {
                self.addClass("null");

            }
        }).click(function () {
            if ($(this).hasClass("null") || $(this).hasClass("current")) return false;
            var self = $(this);
            self.toggleClass('current').siblings().removeClass('current');
            if (priceEle.parent()[0].id == "popProductPrice") {
                amountInputEle.val(1);
            }
            veritySkuBtn(self);

            return false;
        });

        //判断其它按钮否则可选
        function veritySkuBtn(self) {
            self = self || null;
            var selectedObjs = $('.current', skuMapEle);

            if (selectedObjs.length) {
                //获得组合key价格
                _selectedIds = [];
                selectedObjs.each(function () {
                    _selectedIds.push($(this).attr('data'));
                });
                _selectedIds.sort(function (value1, value2) {
                    return parseInt(value1) - parseInt(value2);
                });
                var len = _selectedIds.length;
                var prices = SKUResult[_selectedIds.join(',')].sellPrice;
                var maxPrice = Math.max.apply(Math, prices);
                var minPrice = Math.min.apply(Math, prices);
                var currentSku = skuMap[_selectedIds.join(',')];

                if (len == _skuAttrNum) {
                    _price = currentSku.sellPrice;
                    //要显示价格区别时_productPrice.text(maxPrice > minPrice ? minPrice + "-" + maxPrice : maxPrice);
                    priceEle.html(currentSku.sellPrice.replace(/(^\d+)/, '<span>$1</span>'));
                    if ($("#marketPrice").length) {
                        $("#marketPrice").html(currentSku.marketPrice.replace(/(^\d+)/, '<span>$1</span>'));
                    }
                    if (integralEle.length) {
                        integralEle.text(currentSku.skuIntegral);
                    }
                    if (descEle.length) {//2017-7-29新增描述
                        descEle.text(currentSku.skuDesc);
                    }
                    if (currentSku.marketing) {
                        if ($("#marketingPrice").length) {
                            $("#marketingPrice").html(currentSku.marketingPrice.replace(/(^\d+)/, '<span>$1</span>'));
                        }
                        priceEle.text(currentSku.money);
                    }

                    if(skuTextEle && currentSku.skuText){
                        skuTextEle.text(currentSku.skuText);
                    }

                    if (currentSku.skuImageList && currentSku.skuImageList.length > 0) {
                        var bigImgUrl = currentSku.skuImageList[0],
                            smallImgUrl = bigImgUrl.replace(/(\.(?:jpg|jpeg|png|gif))$/, '_10$1'),
                            selectImg = _skuImgList ? _skuImgList[smallImgUrl] : undefined;
                        if (selectImg) {
                            selectImg.trigger('click');
                        }

                    }
                    skuMapEle.removeClass('no_finished');
                }

                //用已选中的节点验证待测试节点 underTestObjs

                _skuEles.not(selectedObjs).not(self).each(function () {
                    var siblingsSelectedObj = $(this).siblings('.current');
                    var testAttrIds = [];//从选中节点中去掉选中的兄弟节点
                    if (siblingsSelectedObj.length) {
                        var siblingsSelectedObjId = siblingsSelectedObj.attr('data');
                        for (var i = 0; i < len; i++) {
                            (_selectedIds[i] != siblingsSelectedObjId) && testAttrIds.push(_selectedIds[i]);
                        }
                    } else {
                        testAttrIds = _selectedIds.concat();

                    }
                    testAttrIds = testAttrIds.concat($(this).attr('data'));
                    testAttrIds.sort(function (value1, value2) {
                        return parseInt(value1) - parseInt(value2);
                    });
                    if (!SKUResult[testAttrIds.join(',')] || SKUResult[testAttrIds.join(',')]['stock'] == 0) {
                        $(this).removeClass('current').addClass("null");
                    } else {
                        $(this).removeClass("null");
                    }
                });

            } else {
                //设置默认价格
                //_productPrice.text('--');
                //设置属性状态
                _selectedIds = [];
                _skuEles.each(function () {
                    SKUResult[$(this).attr('data')] ? $(this).removeClass('null') : $(this).removeClass('current');
                });
            }
        }

        function initSelect() {
            if (selectSku.stock > 0) {
                _selectedIds = selectSku.pvList;
                readSkuDom(selectSku);
            } else {
                for (var sku in skuMap) {
                    if (skuMap[sku].stock > 0) {
                        _selectedIds = skuMap[sku].pvList;
                        (skuMap[sku]);
                        break;
                    }
                }
            }

            function readSkuDom(sku) {
                priceEle.html(sku.sellPrice.replace(/(^\d+)/, '<span>$1</span>'));
                for (var i = 0; i < _skuAttrNum; i++) {
                    skuMapEle.find(".sku[data=" + sku.pvList[i] + "]").addClass("current");
                }
                veritySkuBtn();
            }

        }

        if (!(typeof selectSku == 'undefined') && !!skuMap) {
            initSelect();
        }

    }

    //购买
    addToCartBtn.click(function (e) {
        //e.preventDefault();
        if (_isSKU) {
            if (!verifySku()) return false;
            $(this).attr("href", addToCartUrl + "?skuId=" + skuMap[_selectedIds.join(',')]["id"] + "&number=" + amountInputEle.val());
        } else {
            $(this).attr("href", addToCartUrl + "?skuId=" + defaultSku["id"] + "&number=" + amountInputEle.val());
        }

        //start ajax
        var _url = $(this).attr("href");
        $.ajax({
            type: "GET",
            url: _url,
            async: false,
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data["success"]) {
                    if ((typeof isDirectBuy == 'undefined') || isDirectBuy) {
                        window.location.href = data["data"]["cartUrl"];
                    } else {

                        if (typeof buyCallback != 'undefined') {
                            buyCallback();
                        }

                        new Ejs.Dialog({
                            title: "添加到购物车",
                            type: Ejs.Dialog.type.RIGHT,
                            info: "物品已经成功添加到购物车",
                            isConfirm: false,
                            isModal: true,
                            afterClose: function (v) {
                                Ejs.Cart.getNumber();
                            },
                            buttons: [
                                {
                                    buttonText: "继续购物",
                                    buttonClass: "button_a",
                                    onClick: function () {

                                    }
                                },
                                {
                                    buttonText: "去结算",
                                    buttonClass: "button_a",
                                    onClick: function () {
                                        window.location.href = data["data"]["cartUrl"];
                                    }
                                }
                            ]
                        });
                    }
                } else {
                    var _buttons = [];
                    if (data["data"]["errorType"] == "exceedMaxNum") {
                        _buttons.push({
                            buttonText: "去结算",
                            buttonClass: "button_a",
                            onClick: function () {
                                window.location.href = EJS.CartUrl;
                            }
                        });
                    } else if (data["data"]["errorType"] == "systemError") {
                        _buttons.push({
                            buttonText: "去首页逛逛",
                            buttonClass: "com_button_green_d",
                            onClick: function () {
                                window.location.href = EJS.HomeUrl;
                            }
                        });
                    }
                    new Ejs.Dialog({
                        title: "提示信息",
                        type: Ejs.Dialog.type.ERROR,
                        info: data["msg"],
                        isConfirm: false,
                        buttons: _buttons,
                        isModal: true
                    });
                }
            }
        });
        //end ajax

        return false;
    });

    // 商品摘要 - 设置商品购买数量
    //_limit = 999;
    //console.log(_limit);

    amountEle.click(function (event) {
        if (_isSKU) {
            if (!verifySku()) return false;
            _limit = skuMap[_selectedIds.join(',')]["limit"];
            _stock = skuMap[_selectedIds.join(',')]["stock"];
        } else {
            _limit = defaultSku["limit"];
            _stock = defaultSku["stock"];
        }

        var _targetParentClassName = event.target.parentNode.className;


        if (_targetParentClassName == "btn-add") {
            _limit = _limit > _stock ? _stock : _limit;
            _num = parseInt(amountInputEle.val()) + 1;
            if (_num > _limit) {
                _num = _limit;
                Ejs.tip(amountInputEle, "limit_tip", "超出此商品能购买的最大数量", 30);
            }
            countPrice(_num);
            amountInputEle.val(_num);

        } else if (_targetParentClassName == "btn-sub") {

            _limit = _limit > _stock ? _stock : _limit;
            _num = parseInt(amountInputEle.val()) - 1;
            _num = _num < 1 ? 1 : _num;
            countPrice(_num);
            amountInputEle.val(_num);

        }

    });

    function countPrice(num) {
        if (priceEle.parent()[0].id == "popProductPrice") {
            priceEle.find("span").text(+_price * num);
        }
    };

    amountInputEle.blur(function () {
        var reg = /^[0-9]+$/;
        if (!reg.test(amountInputEle.val())) {
            amountInputEle.val(1);
        }
        if (amountInputEle.val() > _limit) {
            amountInputEle.val(_limit);
            Ejs.tip(amountInputEle, "limit_tip", "超出此商品能购买的最大数量", 30);
        }
        if (amountInputEle.val() < 1 && _limit > 0) {
            amountInputEle.val(1);
            Ejs.tip(amountInputEle, "limit_tip", "商品购买数量不能小于1", 30);
        }
        countPrice(+amountInputEle.val());
    });

    //验证商品属性选择数量是否正确
    function verifySku() {
        var selectAttrNum = _selectedIds.length || 0,
            information = selectAttrNum === 0 ? "未选择商品属性" : "您还有" + (_skuAttrNum - selectAttrNum) + "个商品属性未选择";

        if (selectAttrNum < _skuAttrNum) {

            skuMapEle.addClass('no_finished');
            if (!skuMapEle.find('a.close_btn').length) {
                var closeBtn = $('<a class="close_btn" href="javascript:;">关闭</a>').appendTo(skuMapEle);
                closeBtn.bind('click', function () {
                    skuMapEle.removeClass('no_finished');
                })
            }

            /*
             new Ejs.Dialog({
             title:information,
             info:"请先选择商品属性，再加入到购物车",
             type:Ejs.Dialog.type.ERROR,
             buttons:[
             {
             buttonText:"去选择",
             buttonClass:"button_a",
             onClick:function () {

             }
             }
             ]
             });
             */

            return false;
        }
        return true;
    }

};


// 加入收藏夹
$('.e-toolbar .btn-favorite').click(function (e) {
    e.preventDefault();
    var a = "http://www.boobee.me/",
        b = "\u6613\u5c45\u5c1a-\u54c1\u8d28\u5c45\u5bb6\u7528\u54c1\u7f51\u8d2d\u5e73\u53f0";
    document.all ? window.external.AddFavorite(a, b) : window.sidebar && window.sidebar.addPanel ? window.sidebar.addPanel(b, a, "") : alert("\u6d4f\u89c8\u5668\u4e0d\u652f\u6301\uff0c\u60a8\u53ef\u4ee5\u4f7f\u7528Ctrl+D");
});


// 信息提示
Ejs.dialogAlert = function (msg, type, buttons) {

    var _buttons = [
        {
            buttonText: '关闭窗口',
            buttonClass: 'e-btn btn-default',
            buttonStyle: {},
            onClick: function (v) {

            }
        }
    ];

    new Ejs.Dialog({
        title: '提示',
        outClass: 'dialog-2014',
        border: '1px solid #ddd;',
        width: 320,
        height: 150,
        backgroundColor: '#ffffff',
        type: type || Ejs.Dialog.type.ERROR,
        info: msg || '服务器发生错误',
        buttons: buttons || _buttons
    });
};

Ejs.dialogAlertXl = function (msg, type, buttons) {

    var _buttons = [
            {
                buttonText: '关闭窗口',
                buttonClass: 'e-btn btn-default',
                buttonStyle: {},
                onClick: function (v) {

                }
            }
        ],
        outClass = 'dialog-2014';

    if (buttons === null) {
        outClass += ' dialog-2014-pay';
    }

    new Ejs.Dialog({
        title: '提示',
        outClass: outClass,
        border: '1px solid #ddd;',
        backgroundColor: '#ffffff',
        type: type || Ejs.Dialog.type.ERROR,
        info: msg || '服务器发生错误',
        infoStyle: {
            height: '100px'
        },
        buttons: buttons || _buttons
    });
};

// 确认对话框
Ejs.dialogConfirm = function (e, info, callback) {
    new Ejs.Dialog({
        type: Ejs.Dialog.type.WARN,
        title: '提示',
        isModal: true,
        outClass: 'dialog-2014',
        opacity: 0,
        border: '1px solid #ddd;',
        backgroundColor: '#ffffff',
        info: info,
        isConfirm: true,
        eventObj: e,
        afterClose: function (v) {
            if (v) {
                callback();
            }
        }
    });
};

// 登录弹出框
Ejs.UserWindows = null;
Ejs.UserWindow = function (options) {
    var url;

    if(options.actionType){
        url = EJS.ToWindowLogin + '?backUrl=&actionType=' + options.actionType;
    }else{
        url = EJS.ToWindowLogin + '?backUrl=' + options.backUrl || window.location.href;
    }

    var content = '<iframe src="' + url + '" width="420" height="640" frameborder="0" scrolling="no"></iframe>';
    Ejs.UserWindows = new Ejs.Dialog({
        width: '420',
        height: '640',
        outClass: 'dialog-login',
        border: '1px solid #ddd;',
        backgroundColor: '#ffffff',
        info: content
    });
};

//  滚动加载(到内容进入浏览器视区再加载)
Ejs.ScrollLoad = function (opts) {
    this.options = {
        wrap: '',
        delay: 50,
        loadOnce: true,
        onsight: function () {

        }
    };
    this.opts = $.extend({}, this.options, opts);
    this.wrapElem = $(this.opts.wrap);
    this.isLoaded = false;
    this.init();
};

Ejs.ScrollLoad.prototype = {
    constructor: Ejs.ScrollLoad,
    init: function () {
        this.opts.id = 'scrollLoad-' + new Date().getTime();
        this.opts.insight = false;
        this.opts.wrapHeight = this.wrapElem.height();
        this.resetOffset();
        this.bindEvent();
    },
    bindEvent: function () {
        var that = this;
        $(window).bind('scroll.' + that.opts.id, function () {
            if (that.checkInSight()) {
                that.opts.insight = true;
                that.opts.onsight(that, that);
                if (that.opts.loadOnce || that.isLoaded) {
                    setTimeout(function () {
                        that.unBindEvent();
                    }, that.opts.delay);
                }
            } else {
                that.opts.insight = false;
            }
        });
    },
    resetOffset: function () {
        var opts = this.opts;
        opts.offsetTop = this.wrapElem.offset().top;
    },
    checkInSight: function () {
        var opts = this.opts,
            docScrollTop = $(window).scrollTop(),
            winHeight = $(window).height();

        return opts.offsetTop < docScrollTop + winHeight && opts.offsetTop + opts.wrapHeight > docScrollTop;
    },
    unBindEvent: function () {
        var opts = this.opts;
        $(window).unbind('scroll.' + opts.id);
    }
};
