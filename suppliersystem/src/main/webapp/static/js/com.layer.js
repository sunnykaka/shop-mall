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
		wrapperDiv.css("zIndex", $.getMaxIndex());
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

var Ejs = window.Ejs || {};

/**
 * 网站公用弹出框对话框
 */

Ejs.Dialog = function (options) {
	this.options = options || {};
	this.retuenVal = false;
	this.defaultOptions = {
		buttons:[],
		type:Ejs.Dialog.type.INFO,
		typeStyle:{

		},
		title:"",
		info:"",
		infoStyle:{

		},
        outClass:"com_pop_window",
        width:253,
        height:161,
		isModal:true,
		isConfirm:false,
		isLeftHand:false,
		isBgClose:true,
		opacity:0.3,
		eventObj:null,
		eventOffset:{
			x:0,
			y:0
		},
		afterClose:function (value) {

		},
		beforeShow:function () {

		},
		afterShow:function(){

		},
		preventClosed:false
	};
	this.initialise();
};

Ejs.Dialog.type = {
	WARN:"warn",
	RIGHT:"right",
	ERROR:"error",
	INFO:"info"
};

Ejs.Dialog.prototype = {
	initialise:function () {
		$.extend(true, this.defaultOptions, this.options);
		this.generateDialog();
	},
	generateDialog:function () {
		var _root = this;
		_root._dialog = new com.layer({
			positionType:com.layer.positionType.center,
			isModal:_root.defaultOptions.isModal,
			opacity:_root.defaultOptions.opacity,
			isBgClose:_root.defaultOptions.isBgClose,
			outClass:_root.defaultOptions.outClass,
            width:_root.defaultOptions.width,
            height:_root.defaultOptions.height,
			titleClass:"pop_title",
			title:_root.defaultOptions.title,
			closeClass:"close",
			closeText:'<a class="fix_ie6" href="#"></a>',
			contentClass:'content_wrapper',
			content:'',
			onshowing:function () {
				_root.defaultOptions.beforeShow.call(_root);

			},
			afterShow:function(){
				if(_root.defaultOptions.eventObj!=null){
					_root._dialog.getWrapper().css({
						left:_root.defaultOptions.eventObj.pageX-210+_root.defaultOptions.eventOffset.x,
						top:_root.defaultOptions.eventObj.pageY+100+_root.defaultOptions.eventOffset.y
					});
				}
				_root.defaultOptions.afterShow.call(_root);
			},
			onclosing:function () {
				$("#" + _root._dialog.getId()).remove();
				_root.defaultOptions.afterClose.call(_root, _root.retuenVal);
			},
			style:{
				width:_root.defaultOptions.width,
				height:_root.defaultOptions.height,
				border:"0",
				backgroundColor:"transparent",
				overflow:"visible"
			}
		});

		_root.generateHTML().appendTo($("#" + _root._dialog.getContentId()));
		_root._dialog.show();
	},
	generateHTML:function () {
		var _root = this;
		var html = $("<div></div>");
		var info = $("<div></div>");
		var button = $("<div></div>");

		var _type = $("<div></div>");
		_type.addClass(this.defaultOptions.type);
		_type.css(this.defaultOptions.typeStyle);

		info.addClass("info_wrapper");
		info.css(this.defaultOptions.infoStyle);
		$("<div></div>").addClass("info").html(this.defaultOptions.info).appendTo(info);
		info.appendTo(html);

		button.addClass("button_wrapper");

		if (this.defaultOptions.isConfirm) {
			var button_yes = $("<input type='button' class='button_b' value='是'/> ");
			var button_no = $("<input type='button' class='button_b' value='否'/> ");

			if(this.defaultOptions.isLeftHand){
				button_no.css({
					marginRight:0
				});
				button_yes.appendTo(button);
				button_no.appendTo(button);
			}else{
				button_yes.css({
					marginRight:0
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
					marginRight:0
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
	generateButton:function (button, ele) {
		var _root = this;
		var cloneButton = {
			buttonText:"确定",
			buttonClass:"button_a",
			buttonStyle:{

			},
			onClick:function (v) {

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
			if(!_root.defaultOptions.preventClosed){
				_root._dialog.hide();
			}
			cloneButton.onClick.call(this);
		});
	},
	getContent:function(){
		return $("#"+this._dialog.getContentId());
	},
	getClose:function(){
		return $("#"+this._dialog.getCloseId());
	},
	close:function(){
		this._dialog.hide();
	},
    reSetConfirm:function(h){
        var _root = this;
        _root._dialog.setHeight(h);
        _root._dialog.getWrapper().css({
            marginTop: -(h / 2)
        });
    }
};
