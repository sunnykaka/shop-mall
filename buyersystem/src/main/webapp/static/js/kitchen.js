//专题页-kitchen
$(function(){


EJS.kvScroll = {
		wrap: null,//jQuery('.kv-wrap')
		switchEle: null,//jQuery('.kv-wrap .kvs')
		unit: null,//jQuery('.kv-wrap .kv')
		current: 1,//[1 - ]
		unitWidth : 1000,
		leftMaskWidth : 0,
		isAutoPlay : null,
		jHtml : null,
		changePicItem : function(){
			var len=this.unit.length;
			var index=this.current;
			if (index>len){
				index=index-len;
			}
			$(".kv-list").stop().animate({"top":-179*(index-1)});
		},
		getWrap: function (ele, childEleClass, grandsonEle) {
			ele = typeof ele === 'string' ? jQuery(ele) : ele;
			this.wrap = ele;
			this.jHtml = this.wrap.html();
			this.switchEle = ele.find(childEleClass);
			this.unit = ele.find(grandsonEle);
			var kvImg=this.unit.find("img");
			var kvItemImg=$(".kv-list img");
			kvImg.each(function(){
				if ($(this).attr("src").length<4){
					$(this).attr("src",$(this).attr("data-original"));
				}
			});
			kvItemImg.each(function(){
				if ($(this).attr("src").length<4){
					$(this).attr("src",$(this).attr("data-original"));
				}
			});
			var winWidth = jQuery(window).width(), uWidth = this.unitWidth;
			winWidth = jQuery.browser.msie ? winWidth : winWidth;
			//alert(this.jHtml);
			if(winWidth > 1000) {
				ele.width(winWidth);
			} else {
				ele.width(1000);
			}
			ele.css('left', -1 * (parseInt((winWidth - uWidth)/2, 10)));
			this.switchEle.css('left', -1 * (uWidth - parseInt((winWidth - uWidth)/2, 10)));
			this.leftMaskWidth = Math.abs(this.wrap.position().left);
			if(winWidth > 1000) {
				if(this.wrap.width() % 2 === 1){
					this.wrap.css('left', -1 * (this.leftMaskWidth + 1));
				}
				if(jQuery.browser.msie && (jQuery.browser.version == "6.0") && !jQuery.support.style){
					this.wrap.parent().css('left', this.leftMaskWidth + 1);
				}
			} else {
				this.wrap.css('left', 0);
			}
		},
		createControl: function () {
			var html = [
					'<div class="prev"><div class="mask"></div><a  class="p ie6png"></a></div>',
					'<div class="next"><div class="mask"></div><a  class="n ie6png"></a></div>'
			].join('');
			if(this.wrap[0]){
				this.wrap.append(html);
				var prev = this.wrap.children('.prev'), next = this.wrap.children('.next'), wrapLeft = this.leftMaskWidth;
				prev.width(wrapLeft+1);
				next.width(wrapLeft+1);	
			}
			var cloneEles = this.unit.clone();
			cloneEles.addClass('clone');
			this.switchEle.append(cloneEles);
			var ele = this.switchEle, uWidth = this.unitWidth, curPos = ele.position().left, kv = ele.children('li'), kvLength = kv.length;
			ele.css('left', this.leftMaskWidth - (kvLength / 2) * (uWidth));
			this.current = kvLength/2 +1 ;
		},
		switchTo: function (direction) {//>0,左/perv, <0,右/next
			if(this.switchEle.is(":animated")){return false;}
			var prev = this.wrap.find('.prev .p'), next = this.wrap.find('.next .n');			
			var ele = this.switchEle, uWidth = this.unitWidth, curPos = ele.position().left, kv = ele.children('li'), kvLength = kv.length, that = this, current = this.current;
			if(typeof direction !== 'number' || direction === 0 || kvLength <=3 )
			{
				return false;
			}			
			prev.unbind('click');
			next.unbind('click');
			jQuery(document).unbind('keydown');
			
			if(that.current <= 2 || that.current >= kvLength-1){
				if(that.current <= 2){
					ele.css('left', that.leftMaskWidth - (kvLength/2 + 1) * (uWidth));
					curPos = ele.position().left;
					that.current = kvLength/2 + 2;
				} else {
					ele.css('left', that.leftMaskWidth - (kvLength/2 - 2)*uWidth);
					curPos = ele.position().left;
					that.current = kvLength/2 - 1;
				}
			}//无缝
			
			var moveOther = function () {
				var flashBox = null, flash = null, fSrc = '', html;
				if(!ele.is(":animated")){
					if(direction > 0){
						that.current--;
						that.changePicItem();
						ele.animate({left: '+=1000'},500, function () {
							that._bind();
						});
					} else {
						that.current++;
						that.changePicItem();
						ele.animate({left: '-=1000'},500, function () {
							that._bind();
						});
					}
					flashBox = jQuery('.kv').eq(that.current-1);
					if(flashBox.hasClass('flash-wrap')){
						flash = flashBox.children();
						if(flash.attr('src')){
							fSrc = flash.attr('src');
							flash.attr('src', fSrc);
						} else {
							if(flash.attr('data')){
								fSrc = flash.attr('data');
								flash.attr('data', fSrc);
							} else {
								html = flashBox.html();
								flashBox.html(html);
							}
						}
					}
				} else {
					setTimeout(moveOther, 1000);
				}
			}
			moveOther();
		},
		autoPlay: function () {
			var that = this;
			this.isAutoPlay = setTimeout(function(){
				clearTimeout(that.isAutoPlay);
				var current = that.current+1 >= that.unit.length/2-1 ? that.unit.length-1 : that.current+1;
				that.switchTo(-1);
				that.autoPlay();
			},6000);
		},
		resetAutoPlay: function () {
			clearTimeout(this.isAutoPlay);
			if(this.unit.length >= 3) {
				this.autoPlay();
			}
		},
		_bind: function () {
			var prev = this.wrap.find('.prev .p'), next = this.wrap.find('.next .n'), that = this;
			prev.bind('click', function () {
				that.switchTo(1);
				that.resetAutoPlay();
				return false;
			});
			next.bind('click', function () {
				that.switchTo(-1);
				that.resetAutoPlay();
				return false;
			});
			jQuery(document).keydown(function(e){
				switch(e.keyCode) {
					case 37: that.switchTo(1);that.resetAutoPlay();
						break;
					case 39: that.switchTo(-1);that.resetAutoPlay();
						break;
				}
			});
		},
		_unbind: function () {
			var prev = this.wrap.find('.prev .p'), next = this.wrap.find('.next .n'), that = this;
			prev.unbind('click');
			next.unbind('click');
			jQuery(document).unbind('keydown');
		},
		clear: function () {
			this._unbind();
			clearTimeout(this.isAutoPlay);
			this.wrap.find('.prev').remove();
			this.wrap.find('.next').remove();
			this.switchEle.css('left', '0');
			this.wrap.css('left', '0');
		},
		checkNumDoSomething: function () {
			if(this.unit.length < 3){
				this._unbind();
				this.unit.css('visibility', 'hidden');
				this.unit.children().remove();
				clearTimeout(this.isAutoPlay);
			}
		},
		ini: function () {
			this.getWrap('.kv-wrap', '.kvs', 'li.kv');
			this.createControl();
			this._bind();
			this.autoPlay();
		}
	};	
  
EJS.kvScroll.ini();

if (!_isIE6){
	$(".r1 .img_text").hover(function(){
		$(this).addClass("act");
	},function(){
		$(this).removeClass("act");
	});
}

$(".slide").each(function (i) {
	$(this).jQSlide({
		list: true,
		interval: 6000,
		animation: "slide",
		btnNext: $(this).find(".slide_btn_next"),
		btnPrev: $(this).find(".slide_btn_prev")
	});
	$(this).find(".slide_list").find("li:last").addClass("last");
}); 

(function(){

	var rightNavBox=document.createElement("div");
	rightNavBox.id="oil_line";
	rightNavBox.className="oil_line";
	$(".special_kitchen").append(rightNavBox);
	var rightNav=$(".right .slide_nav");
	
	function oilReflash(){
	
		var winWidth=$("body").width();
		var rightSpace=parseInt((winWidth-1000)/2);
		if (rightSpace>(190+90)){
			rightNavBox.style.display="block";
			rightNav.css("display","block");
			rightNavBox.style.right=rightSpace-190-87+"px";
			rightNavBox.style.width="190px";
			//alert(rightNavBox.offsetWidth);
			rightNav.css("right","-142px");
		}else  if (rightSpace<92) {
			rightNavBox.style.display="none";
			rightNav.css("display","none");
		}else{
			rightNavBox.style.display="block";
			rightNav.css("display","block");
			if (rightSpace>(50+92)){
				rightNavBox.style.right=0;
				rightNavBox.style.width=(rightSpace-87)+"px";
				rightNav.css("right","-142px");
			}else if (rightSpace>92 && rightSpace<(50+92)){
				rightNav.css("right","-100px");
				rightNavBox.style.right=0;
				rightNavBox.style.width=(rightSpace-47)+"px";
			}
		}
	}
	
	oilReflash();
	
	$(window).resize(function(){
		oilReflash();
	});
	
})();


})