// Tab切换
var tabs = function (targetA, targetB, curClassName,actionType,FUN) {
	this.targetA = targetA;
	this.targetB = targetB;
	this.curClassName = curClassName;
	this.actionType=actionType||"click";
	this.FUN = FUN;
	var _self=this;
	_self.init = function () {
		var _handderB = $(targetB);
		if ($(targetA).length <= 0) {
			return false;
		}
		$(targetA).each(function (_j) {
			$(this).on(_self.actionType,function (e) {
				e.preventDefault();
				$(targetA).removeClass(curClassName);
				$(targetA).eq(_j).addClass(curClassName);
				_handderB.hide();
				$(_handderB[_j]).show();
				if(typeof _self.FUN == "function"){
					_self.FUN();
				}
			});
		});
	};
	_self.init();
};

/**
 * jQuery cookie Plugin
 */

jQuery.cookie = function(name, value, options) {
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
 * jQuery TextChange Plugin
 * http://www.zurb.com/playground/jquery-text-change-custom-event
 */
(function (a){
	a.event.special.textchange = {
		setup: function (){
			a(this).data("lastValue", this.contentEditable === "true" ? a(this).html() : a(this).val());
			a(this).bind("keyup.textchange", a.event.special.textchange.handler);
			a(this).bind("cut.textchange paste.textchange input.textchange", a.event.special.textchange.delayedHandler)
		},
		teardown: function (){
			a(this).unbind(".textchange")
		},
		handler: function (){
			a.event.special.textchange.triggerIfChanged(a(this))
		},
		delayedHandler: function (){
			var c = a(this);
			setTimeout(function (){
				a.event.special.textchange.triggerIfChanged(c)
			},
			25)
		},
		triggerIfChanged: function (a){
			var b = a[0].contentEditable === "true" ? a.html() : a.val();
			b !== a.data("lastValue") && (a.trigger("textchange", [a.data("lastValue")]), a.data("lastValue", b))
		}
	};
	a.event.special.hastext = {
		setup: function (){
			a(this).bind("textchange", a.event.special.hastext.handler)
		},
		teardown: function (){
			a(this).unbind("textchange", a.event.special.hastext.handler)
		},
		handler: function (c, b){
			b === "" && b !== a(this).val() && a(this).trigger("hastext")
		}
	};
	a.event.special.notext = {
		setup: function (){
			a(this).bind("textchange", a.event.special.notext.handler)
		},
		teardown: function (){
			a(this).unbind("textchange", a.event.special.notext.handler)
		},
		handler: function (c, b){
			a(this).val() === "" && a(this).val() !== b && a(this).trigger("notext")
		}
	}
})(jQuery);



/**
 * http://www.JSON.org/json2.js
 * 2008-05-25
*/
if(!this.JSON){
	JSON = function (){

		function f(n){
			return n < 10 ? '0' + n : n;
		}

		Date.prototype.toJSON = function (key){
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

		function quote(string){
			escapeable.lastIndex = 0;
			return escapeable.test(string) ? '"' + string.replace(escapeable, function (a){
				var c = meta[a];
				if(typeof c === 'string'){
					return c;
				}
				return '\\u' + ('0000' + (+ (a.charCodeAt(0))).toString(16)).slice(-4);
			}) + '"' : '"' + string + '"';
		}

		function str(key, holder){
			var i,
			k,
			v,
			length, mind = gap,
				partial, value = holder[key];

			if(value && typeof value === 'object' && typeof value.toJSON === 'function'){
				value = value.toJSON(key);
			}

			if(typeof rep === 'function'){
				value = rep.call(holder, key, value);
			}

			switch (typeof value){
			case 'string':
				return quote(value);
			case 'number':
				return isFinite(value) ? String(value) : 'null';
			case 'boolean':
			case 'null':
				return String(value);
			case 'object':

				if(!value){
					return 'null';
				}

				gap += indent;
				partial = [];

				if(typeof value.length === 'number' && !(value.propertyIsEnumerable('length'))){

					length = value.length;
					for (i = 0; i < length; i += 1){
						partial[i] = str(i, value) || 'null';
					}

					v = partial.length === 0 ? '[]' : gap ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']' : '[' + partial.join(',') + ']';
					gap = mind;
					return v;
				}

				if(rep && typeof rep === 'object'){
					length = rep.length;
					for (i = 0; i < length; i += 1){
						k = rep[i];
						if(typeof k === 'string'){
							v = str(k, value, rep);
							if(v){
								partial.push(quote(k) + (gap ? ': ' : ':') + v);
							}
						}
					}
				}else{

					for (k in value){
						if(Object.hasOwnProperty.call(value, k)){
							v = str(k, value, rep);
							if(v){
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
			stringify: function (value, replacer, space){
				var i;
				gap = '';
				indent = '';

				if(typeof space === 'number'){
					for (i = 0; i < space; i += 1){
						indent += ' ';
					}
				}else if(typeof space === 'string'){
					indent = space;
				}

				rep = replacer;
				if(replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')){
					throw new Error('JSON.stringify');
				}

				return str('', {
					'': value
				});
			},

			parse: function (text, reviver){

				var j;
				function walk(holder, key){
					var k, v, value = holder[key];
					if(value && typeof value === 'object'){
						for (k in value){
							if(Object.hasOwnProperty.call(value, k)){
								v = walk(value, k);
								if(v !== undefined){
									value[k] = v;
								}else{
									delete value[k];
								}
							}
						}
					}
					return reviver.call(holder, key, value);
				}

				cx.lastIndex = 0;
				if(cx.test(text)){
					text = text.replace(cx, function (a){
						return '\\u' + ('0000' + (+ (a.charCodeAt(0))).toString(16)).slice(-4);
					});
				}

				if(/^[\],:{}\s]*$/.
				test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
				replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
				replace(/(?:^|:|,)(?:\s*\[)+/g, ''))){

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


/**
 * 弹出层
*/
var com = window.com || {};
com.layer = function (options, deep){
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
	hover: function (){
		var trigger = this.options.trigger;
		var wrapperId = this.getId();
		var src = this;
		var over = false;
		var curTrigger;
		var timeoutId;
		$("#" + wrapperId).hover(function (){
			clearTimeout(timeoutId);
			over = true;
		},
		function (){
			over = false;
			timeoutId = setTimeout(close, src.options.closeTimeout);
		});
		$(trigger).hover(function (){
			clearTimeout(timeoutId);
			over = true;
			if(curTrigger == this && src.alreadyShow){
				return;
			}
			src.options.positionType.call(src, this);
			src.show(this);
			curTrigger = this;
		},
		function (){
			over = false;
			timeoutId = setTimeout(close, src.options.closeTimeout);
		});

		function close(){
			if(!over){
				src.hide();
			}
		}
	},
	click: function (){
		var trigger = this.options.trigger;
		var wrapperId = this.getId();
		var src = this;
		var curTrigger;
		$(trigger).click(function (e){
			e.stopPropagation();
			if(curTrigger == this && src.alreadyShow){
				return;
			}
			src.show(this);
		});
		$("#" + wrapperId).click(function (e){
			e.stopPropagation();
		});
	}
};
com.layer.positionType = {
	center: function (){
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
		if($.ie6()){
			$("#" + this.contentBgFrameId).css(pos).css({
				width: wrapper.width(),
				height: wrapper.height()
			});
		}
	},
	margin: function (trigger){
		if(!trigger){
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
		if($.ie6()){
			$("#" + this.contentBgFrameId).css(pos).css({
				width: wrapper.outerWidth(),
				height: wrapper.outerHeight()
			});
		}
	}
};
com.layer.showAction = {
	normal: function (){
		$("#" + this.getId()).show();
	},
	fade: function (){
		$("#" + this.getId()).fadeIn(this.options.actionSpeed);
	},
	slideV: function (){
		$("#" + this.getId()).slideDown(this.options.actionSpeed);
	},
	expand: function (){
		$("#" + this.getId()).show(this.options.actionSpeed);
	}
};
com.layer.hideAction = {
	normal: function (){
		$("#" + this.getId()).hide();
	},
	fade: function (){
		$("#" + this.getId()).fadeOut(this.options.actionSpeed);
	},
	slideV: function (){
		$("#" + this.getId()).slideUp(this.options.actionSpeed);
	},
	expand: function (){
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
	onshowing: function (){},
	onclosing: function (){},
	afterShow:function(){}
};
com.layer.prototype = {
	setOptions: function (options, deep){
		if(typeof deep == "boolean" && !deep){
			$.extend(this.options, options || {});
		}
		$.extend(true, this.options, options || {});
	},
	_init: function (){
		if( !! this._hasInit){
			return;
		}
		this._createContentBgIframe();
		var opt = this.options;
		if(opt.isModal){
			this._createShade();
		}
		var wrapperDiv = $("<div>");
		wrapperDiv.attr("id", this.getId());
		wrapperDiv.css(opt.style);
		wrapperDiv.css("zIndex", $.getMaxIndex()+10);
		wrapperDiv.addClass(opt.outClass);
		if(opt.title != ""){
			var titleDiv = $("<div>");
			titleDiv.attr("id", this.getTitleId());
			titleDiv.css(opt.titleStyle);
			titleDiv.addClass(opt.titleClass);
			titleDiv.html(opt.title);
			titleDiv.appendTo(wrapperDiv);
		}
		if( !! opt.closeText){
			var src = this;
			var closeDiv = $("<div>");
			closeDiv.attr("id", this.getCloseId());
			closeDiv.css(opt.closeStyle);
			closeDiv.addClass(opt.closeClass);
			closeDiv.html(opt.closeText);
			closeDiv.click(function (e){
				e.preventDefault();
				src.hide();
			});
			closeDiv.appendTo(wrapperDiv);
		}
		var contentDiv = $("<div>");
		contentDiv.attr("id", this.getContentId());
		contentDiv.css(opt.contentStyle);
		contentDiv.addClass(opt.contentClass);
		if(this.isIframeContent()){
			var iframe = $("<iframe>");
			iframe.attr({
				frameborder: 0,
				allowtransparency: true
			});
			if(opt.loaded){
				iframe.attr("src", opt.src + (opt.cache ? "" : (/\?/.test(opt.src) ? "&_=" : "?_=") + Math.random()));
			}
			iframe.appendTo(contentDiv);
		}else{
			contentDiv.html($(opt.content));
		}
		contentDiv.appendTo(wrapperDiv);
		wrapperDiv.hide().appendTo($(document.body));
		this._resizeIframe();
		if(this.hasTrigger()){
			opt.triggerType.apply(this);
		}
		if(this.options.isModal && this.options.isBgClose){
			var src=this;
			$("#" + com.layer.__bgDivId).click(function (e){
				src.hide();
				alreadyShow = false;
			});
		}
		this._hasInit = true;
	},
	getSize: function (){
		return {
			width: this.options.style.width,
			height: this.options.style.height
		}
	},
	reload: function (){
		var iframe = this.getIframe();
		if(iframe != null){
			iframe[0].reload();
		}
	},
	setWidth: function (width){
		this.options.style.width = width;
		this.getWrapper().width(width);
		this._resizeIframe();
	},
	setHeight: function (height){
		this.options.style.height = height;
		this.getWrapper().height(height);
		this._resizeIframe();
	},
	_resizeIframe: function (){
		var iframe = this.getIframe();
		if(iframe != null){
			var size = this.getSize();
			iframe.css({
				width: size.width,
				height: size.height+( !! this.options.title ? -$("#" + this.getTitleId()).height() : 0)
			});
		}
	},
	_showContentBgFrame: function (){
		if($.ie6()){
			$("#" + this.contentBgFrameId).show();
		}
	},
	_hideContentBgFrame: function (){
		if($.ie6()){
			$("#" + this.contentBgFrameId).hide();
		}
	},
	_createContentBgIframe: function (){
		if($.ie6()){
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
	_createShade: function (){
		var __hiddenIframeId = com.layer.__bgIframeId;
		if($("#" + __hiddenIframeId).length == 0){
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
			$(window).resize(function (){
				var bgIframe = $("#" + com.layer.__bgIframeId);
				if(bgIframe.length == 0) return;
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
	_showShade: function (){
		$("#" + com.layer.__bgDivId).show();
		$("#" + com.layer.__bgIframeId).show();
	},
	_hideShade: function (){
		$("#" + com.layer.__bgDivId).hide();
		$("#" + com.layer.__bgIframeId).hide();
	},
	isIframeContent: function (){
		return !!this.options.src;
	},
	getIframe: function (){
		if(this.isIframeContent()){
			return this.getWrapper().find("iframe");
		}
		return null;
	},
	setTitle: function (html){
		this.options.title = html;
		$("#" + this.getTitleId()).html(html);
	},
	setContent: function (html){
		this.options.content = html;
		$("#" + this.getContentId()).html(html);
	},
	getId: function (){
		return this.id;
	},
	getCloseId: function (){
		return this.getId() + "_close";
	},
	getTitleId: function (){
		return this.getId() + '_title';
	},
	getContentId: function (){
		return this.getId() + "_content";
	},
	getTriggers: function (){
		if(this.hasTrigger){
			return $(this.options.trigger)
		}
	},
	getWrapper: function (){
		return $("#" + this.getId());
	},
	hasTrigger: function (){
		return !!this.options.trigger;
	},
	setSrc: function (src){
		this.options.src = src;
		this.reload();
	},
	reload: function (){
		var iframe = this.getIframe();
		if(iframe != null){
			var opt = this.options;
			iframe.attr("src", opt.src + (opt.cache ? "" : (/\?/.test(opt.src) ? "&_=" : "?_=") + Math.random()));
			this.options.loaded = true;
		}
	},
	show: function (trigger){
		if( !! trigger){
			this.lastTrigger = trigger;
		}
		if(typeof this.options.onshowing == "function") this.options.onshowing.call(this, trigger);
		if(!this.options.loaded){
			this.reload();
		}
		if(this.alreadyShow){
			return;
		}
		this.options.positionType.call(this, trigger);
		if(this.options.isModal){
			this._showShade();
		}
		this._showContentBgFrame();
		this.options.showAction.apply(this);
		this.alreadyShow = true;
		if(typeof this.options.afterShow == "function") this.options.afterShow.call(this, trigger);
	},
	hide: function (){
		if(!this.alreadyShow){
			return;
		}
		if(this.options.isModal){
			this._hideShade();
		}
		this._hideContentBgFrame();
		this.options.hideAction.apply(this);
		this.alreadyShow = false;
		if(typeof this.options.onclosing == "function"){
			this.options.onclosing.call(this);
		}
	},
	unbind: function (type){
		var trigger = this.options.trigger;
		var wrapperId = this.getId();
		$(trigger).unbind(type);
		$("#" + wrapperId).unbind(type);
	},
	abandon: function (){
		this.options.triggerType.unbind.apply(this);
		$("#" + this.getId()).remove();
	}
};
(function ($){
	$.extend({
		getMaxIndex: function (){
			var index = 0;
			$('*').each(function(i,n){
				var tem =parseInt($(n).css("z-index"));
				if(tem > 0){
					if(tem > index){
						index = tem + 1;
					}
				}
			});
			return index;
		},
		ie6: function (){
			return $.browser.msie && $.browser.version == "6.0";
		}
	});
})(jQuery);

/* 幻灯片 */
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
            if ($(this).attr("src").length < 4) {
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

/*
 * Lazy Load - jQuery plugin for lazy loading images
 *
 * Copyright (c) 2007-2013 Mika Tuupola
 *
 *  Version:  1.8.4
 *
 */
(function(a,b,c,d){var e=a(b);a.fn.lazyload=function(c){function i(){var b=0;f.each(function(){var c=a(this);if(h.skip_invisible&&!c.is(":visible"))return;if(!a.abovethetop(this,h)&&!a.leftofbegin(this,h))if(!a.belowthefold(this,h)&&!a.rightoffold(this,h))c.trigger("appear"),b=0;else if(++b>h.failure_limit)return!1})}var f=this,g,h={threshold:0,failure_limit:0,event:"scroll",effect:"show",container:b,data_attribute:"original",skip_invisible:!0,appear:null,load:null};return c&&(d!==c.failurelimit&&(c.failure_limit=c.failurelimit,delete c.failurelimit),d!==c.effectspeed&&(c.effect_speed=c.effectspeed,delete c.effectspeed),a.extend(h,c)),g=h.container===d||h.container===b?e:a(h.container),0===h.event.indexOf("scroll")&&g.bind(h.event,function(a){return i()}),this.each(function(){var b=this,c=a(b);b.loaded=!1,c.one("appear",function(){if(!this.loaded){if(h.appear){var d=f.length;h.appear.call(b,d,h)}a("<img />").bind("load",function(){c.hide().attr("src",c.data(h.data_attribute))[h.effect](h.effect_speed),b.loaded=!0;var d=a.grep(f,function(a){return!a.loaded});f=a(d);if(h.load){var e=f.length;h.load.call(b,e,h)}}).attr("src",c.data(h.data_attribute))}}),0!==h.event.indexOf("scroll")&&c.bind(h.event,function(a){b.loaded||c.trigger("appear")})}),e.bind("resize",function(a){i()}),/iphone|ipod|ipad.*os 5/gi.test(navigator.appVersion)&&e.bind("pageshow",function(b){b.originalEvent.persisted&&f.each(function(){a(this).trigger("appear")})}),a(b).load(function(){i()}),this},a.belowthefold=function(c,f){var g;return f.container===d||f.container===b?g=e.height()+e.scrollTop():g=a(f.container).offset().top+a(f.container).height(),g<=a(c).offset().top-f.threshold},a.rightoffold=function(c,f){var g;return f.container===d||f.container===b?g=e.width()+e.scrollLeft():g=a(f.container).offset().left+a(f.container).width(),g<=a(c).offset().left-f.threshold},a.abovethetop=function(c,f){var g;return f.container===d||f.container===b?g=e.scrollTop():g=a(f.container).offset().top,g>=a(c).offset().top+f.threshold+a(c).height()},a.leftofbegin=function(c,f){var g;return f.container===d||f.container===b?g=e.scrollLeft():g=a(f.container).offset().left,g>=a(c).offset().left+f.threshold+a(c).width()},a.inviewport=function(b,c){return!a.rightoffold(b,c)&&!a.leftofbegin(b,c)&&!a.belowthefold(b,c)&&!a.abovethetop(b,c)},a.extend(a.expr[":"],{"below-the-fold":function(b){return a.belowthefold(b,{threshold:0})},"above-the-top":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-screen":function(b){return a.rightoffold(b,{threshold:0})},"left-of-screen":function(b){return!a.rightoffold(b,{threshold:0})},"in-viewport":function(b){return a.inviewport(b,{threshold:0})},"above-the-fold":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-fold":function(b){return a.rightoffold(b,{threshold:0})},"left-of-fold":function(b){return!a.rightoffold(b,{threshold:0})}})})(jQuery,window,document)


/*大屏滚动插件*/

;(function ($) {

	if (typeof $.fn.kvSlide !== 'undefined') {
        return;
    }

    $.fn.kvSlide = function(options){
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

       var  current = 0,
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

        $(window).resize(function(){
        	initSlider();
        	prev.width(leftMaskWidth + 1);
            next.width(leftMaskWidth + 1);
        });

        function initSlider (){
            kvPic.find("img").each(function(){
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

        function createControl () {
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
            current = kvLength / 2 ;
        }

        function switchToReady(direction){ 
            if (direction === "left"){
                if (current<=1) {
                    current=current+(kvLength/2);
                    kvPics.css('left',leftMaskWidth-(kvLength/2+1)*uWidth);
                }
                current--;
            }else if (direction === "right"){
                if (current>=kvLength-2){
                    current = current-(kvLength/2);
                    kvPics.css('left',leftMaskWidth-(kvLength/2-2)*uWidth);
                }
                current++;
            }else{
                
            }
            switchTo();
        }

        function switchTo(){
            if (!kvPics.is(":animated")) {
                kvPics.animate({left:-current*uWidth+leftMaskWidth},opts.bufferTime);
            }
            var listIndex = current < (kvLength/2) ? current : current-(kvLength/2);
            kvList.removeClass("current");
            kvList.eq(listIndex).addClass("current");
        }

        function attachEvent(){
            var prev = kvPicWrap.find('.prev .p'),
                next = kvPicWrap.find('.next .n');
            prev.bind('click',function(){
                switchToReady("left");
                return false;
            });
            next.bind('click',function(){
                switchToReady("right");
                return false;
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

            kvList.bind('click', function(){
                var index = kvList.index($(this));
                if (index === current && index === (current-(kvLength/2))) return false;
                current = index == 0 ? (kvLength/2) : index;
                switchTo();
            })
        }

        var timer;

        if (opts.auto == true){
            timer = setInterval(autoPlay,opts.interval);
        }

        if (opts.hoverStop == true && opts.auto == true){
            kvPicWrap.hover(function(){
                clearInterval(timer);
            },function(){
                timer = setInterval(autoPlay,opts.interval);
            })
        }

        function autoPlay(){
            switchToReady('right');
        }
    }

})(jQuery);

/*end-大屏滚动插件*/
