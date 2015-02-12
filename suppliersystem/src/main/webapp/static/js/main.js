
$(document).ready(function(){
	//主页实体类
	mainHander=window.mainHander||{};

	mainHander.instance=null;

	mainHander.core=function(options){
		this.option=options||{};
		this.defaultOptions={
			statorTag:'',
			sliderTag:'',
			sliderWidth:20,
			menuBarTag:'',
			leftSideTag:'',
			contentTag:'',
			isFixed:false,
			iFrameTag:'',
			iFrameSrc:'',
			noteTipTag:''
		};
		$.extend(true,this.defaultOptions,this.option);
		this.initialise();
	};

	mainHander.core.prototype={
		initialise:function(){
			this.defaultOptions.iFrameSrc=$(this.defaultOptions.iFrameTag).attr("src");
			this.statorEvent();
			this.menuEvent();
			this.sliderEvent();
			this.noteTipEvent();
		},
		statorEvent:function(){
			var self=this;
			$(self.defaultOptions.statorTag).click(function(){
				var _this=$(this);
				if(_this.hasClass('fixed')){
					self.defaultOptions.isFixed=false;
					_this.removeClass('fixed');
				}else{
					self.defaultOptions.isFixed=true;
					_this.addClass('fixed');
				}
			});
		},
		menuEvent:function(){
			//菜单效果
			var self=this;
			var ele=$(self.defaultOptions.menuBarTag).find("ul>li>ul>li>a");
			ele.each(function(i){
				var _this=$(this);
				_this.click(function(e){
					e.preventDefault();
					self.resetSrc(_this.attr("href"));
					ele.removeClass('cur');
					_this.addClass('cur');

				});
			});
		},
		activeClickEvent:function(i){
			if(typeof i==='number'){
				var ele=$(this.defaultOptions.menuBarTag).find("ul>li>ul>li>a");
				if(i<=ele.length){
					ele.eq(i-1).click();
				}
			}
		},
		noteTipEvent:function(){
			$(this.defaultOptions.noteTipTag).hover(function(e){
				$(this).find(".tips").stop(true,true).fadeIn(320);
			},function(e){
				$(this).find(".tips").stop(true,true).fadeOut(200);
			});
		},
		sliderEvent:function(){
			var self=this;
			var leftSide=$(self.defaultOptions.leftSideTag);
			var content=$(self.defaultOptions.contentTag);
			var slider=$(self.defaultOptions.sliderTag);
			var menuBar=$(self.defaultOptions.menuBarTag);

			leftSide.hover(function(){

			},function(){
				if(!self.defaultOptions.isFixed){
					menuBar.animate({
						width:0
					},500,function(){
						menuBar.hide();
						slider.show();
					});
					content.animate({
						paddingLeft:self.defaultOptions.sliderWidth+10
					},500);
				}
			});

			slider.click(function(e){
				e.preventDefault();
				slider.hide();
				menuBar.width(262).show();
				content.css({
					paddingLeft:270
				});
			});

		},
		resetIFrame:function(size){
			if(typeof size!='object'){
				return false;
			}
			$(this.defaultOptions.iFrameTag).css(size);
		},
		resizeEvent:function(){
			var self=this;
			$(window).resize(function(){
				self.resetSrc();
			});
		},
		resetSrc:function(src){
			this.defaultOptions.iFrameSrc=(!!src)?src:this.defaultOptions.iFrameSrc;
			$(this.defaultOptions.iFrameTag).attr("src",this.defaultOptions.iFrameSrc);
		}
	};

	//实例化
	mainHander.instance=new mainHander.core({
		statorTag:"#stator",
		sliderTag:"#slider",
		menuBarTag:".menu_wrapper",
		leftSideTag:".main_wrapper>.left_side",
		contentTag:".main_wrapper>.content_wrapper",
		isFixed:true,
		iFrameTag:"#mainFrame",
		noteTipTag:"#note_tips"
	});

});
