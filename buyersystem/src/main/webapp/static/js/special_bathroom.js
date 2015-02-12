//专题页-卫浴
$(function () {

    var carousel = $("#carousel").featureCarousel({
        autoPlay						: 	4000,
        carouselSpeed				:	300,
        smallFeatureWidth		:	.7,
        smallFeatureHeight	:	.7,
        topPadding					:	0,
        sidePadding					:	0,
        smallFeatureOffset		:	100
    });


    var bigImg = null;
    $(".floor_0 .list li").hover(function (){

        if (!$(this).find("a").attr("big-img")){
            return false;
        }

        if ($(this).siblings("li.big").length==0){
            bigImg=$('<li class="big"><img src="" width="326" height="326" /><div class="item"></div></li>').appendTo($(this).parent());
        }

        var _this = $(this).find("img");
		
		var _thisA = $(this).find("a");

        if (_thisA.attr("price").split("-")[1]==0){
            var html = '<p>'+_thisA.attr("title")+'</p><p><span class="p1">￥'+_thisA.attr("price").split("-")[0]+'</span></p>';
        }else{
            var html = '<p>'+_thisA.attr("title")+'</p><p><span class="p1">￥'+_thisA.attr("price").split("-")[0]+'</span><span class="p2">¥ '+_thisA.attr("price").split("-")[1]+'</span></p>';
        }



        bigImg.find("img").attr("src", _thisA.attr("big-img"));
        bigImg.find(".item").html(html);

        var x = $(this).position().left+168;
        var y = $(this).position().top;

        x<839 || (x=x-504);
        y<167 || (y=90);

        bigImg.css({"left" : x+"px","top" : y+"px"});
        bigImg.show();
    },function (){
        bigImg.hide();
    });
});

$(function(){

    $(".floor_1 .list li , .floor_2 .list li").hover(function (){

        var _this = $(this).find("a");
        if ($(this).children(".item").length==0){

            if (_this.attr("price").split("-")[1]==0){
                var $item = '<div class="item ie6png"><p>'+_this.attr("title")+'</p><p><span class="p1">￥'+_this.attr("price").split("-")[0]+'</span></p></div>';
            }else{
                var $item = '<div class="item ie6png"><p>'+_this.attr("title")+'</p><p><span class="p1">￥'+_this.attr("price").split("-")[0]+'</span><span class="p2">¥ '+_this.attr("price").split("-")[1]+'</span></p></div>';
            }

            $(this).append($item);
        }
        $(this).children(".item").show();
    },function (){
        $(this).children(".item").hide();
    });
});

