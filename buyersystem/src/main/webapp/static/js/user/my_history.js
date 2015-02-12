$(function(){
    //删除浏览记录
    var history_1 = $('#history_1'),
        history_2 = $('#history_2'),
        history_3 = $('#history_3'),
        history_4 = $('#history_4'),
        history_5 = $('#history_5'),
        history_listL = $('#history_list li'),
        h_module = $('.n_his_right').children('.h_module'),
        n_his_listL = $('.n_his_list').children('li');
    $(".h_module").on('click', '.del', function () {
        var self = $(this),
            url = '/delHistory?id=' + self.attr('data-id');


        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {
                if (response.success) {

                       if(self.parents('#history_1').find('li').length == 1){
                           self.parents('ul').remove();
                           history_1.find('.r_his_content').append('<div class="his_no">您今天没有浏览记录哦~</div>')
                       }else if(self.parents('#history_2').find('li').length == 1){
                           self.parents('ul').remove();
                           history_2.find('.r_his_content').append('<div class="his_no">您昨天没有浏览记录哦~</div>')
                       }else if(self.parents('#history_3').find('li').length == 1){
                           self.parents('ul').remove();
                           history_3.find('.r_his_content').append('<div class="his_no">您前天没有浏览记录哦~</div>')
                       }
                       else if(self.parents('#history_4').find('li').length == 1){
                           self.parents('ul').remove();
                           history_4.find('.r_his_content').append('<div class="his_no">您一周内没有浏览记录哦~</div>')
                       }
                       else if(self.parents('#history_5').find('li').length == 1){
                           self.parents('ul').remove();
                           history_5.find('.r_his_content').append('<div class="his_no">您一月内没有浏览记录哦~</div>')
                       }else{
                        self.parents('li').remove();
                     }
                    if($('.n_his_right').find('ul').length == 0){
                        $('.tips_his').remove();
                        $('.del_allclear').removeClass('del_all').addClass('del_allno');
                        $('.n_his_right').html('<div class="hide"><a href="/"> 逛逛时尚品质居家驿站&gt;&gt;</a>您还没有浏览过任何商品哦~</div>');
                    }
                } else {
                    alert(response.msg);
                }

            },
            error: function () {
                alert('服务器发生错误');
            }
        });

    });

    //左边菜单del所有记录
    $('.del_all').click(function(){
        $('.tips_his').fadeIn();
    });
    $('.del_allclear').mouseover(function(){
        if($(this).hasClass('del_allno')){
            $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您还没有浏览记录哦</p></div>')
        }
    }).mouseout(function(){
        $('.tips').remove();
    })

    $('.tips_his h1 span.close,.tips_con .tips_btnNo').click(function(){
        $(this).parents('.tips_his').hide();
    });
    $(".tips_btnOk").on('click',  function () {
        var self = $(this),
            url = '/my/delAllHistory'
        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {
                if (response.success) {
                    $('.h_module').slideUp(200);
                    $('.tips_his').remove();
                    $('.del_allclear').removeClass('del_all').addClass('del_allno');
                    $('.n_his_right').html('<div class="hide"><a href="/"> 逛逛时尚品质居家驿站&gt;&gt;</a>您还没有浏览过任何商品哦~</div>');
                } else {
                    alert(response.msg);
                }

            },
            error: function () {
                alert('服务器发生错误');
            }
        });

    });

    //浏览记录左边菜单
    history_listL.mouseover(function(){
        var _this = $(this).index();
        if($('.n_his_right').children('div.h_module').eq(_this).find('li').length === 0){
            if(_this == 4){
                $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您一月内没有浏览记录哦</p></div>');
            }else if(_this == 3){
                $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您一周内没有浏览记录哦</p></div>');
            }else if(_this == 2){
                $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您前天没有浏览记录哦</p></div>');
            }else if(_this == 1){
                $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您昨天没有浏览记录哦</p></div>');
            }else  if(_this == 0){
                $(this).append('<div class="tips" style="display: block;"><span class="ico"></span><p>您今天还没有浏览记录哦</p></div>');
            }
        }
    }).mouseout(function(){
        $('.tips').remove();
    }).click(function(e){
        e.preventDefault();
        var a = history_listL.find('a');
        if(!$(this).hasClass('nocurr')){
            $(this).addClass('current').siblings().removeClass('current');
        };
        target=$(this).find('a').attr("href");
        var targetOffset=$(target).offset().top;
        $('html,body').animate({scrollTop:targetOffset-40});
    });

    $(window).scroll(function(){
        if($(document).scrollTop() >= 195){
            $('.n_his_fixed').css({position:"fixed"}).animate({top:'0'})
        }else{
            $('.n_his_fixed').css({position:"relative"}).animate({top:'0'})
        };
        if($('.n_his_right').find('ul').length > 0){
            target= history_listL.find('a').attr("href");
            var targetOffset=$(target).offset().top;
            var his1_top = h_module.eq(0).offset().top-40,
                his2_top = h_module.eq(1).offset().top-40,
                his3_top = h_module.eq(2).offset().top-40,
                his4_top = h_module.eq(3).offset().top-40,
                his5_top = h_module.eq(4).offset().top-40,
                scroH = $(this).scrollTop()//滚动条位置;
            if(scroH >= his5_top){
                if(!n_his_listL.eq(4).hasClass('nocurr')){
                    setcurr(4)
                }
            }else if(scroH >= his4_top){
                if(!n_his_listL.eq(3).hasClass('nocurr')){
                    setcurr(3)
                }
            }else if(scroH >= his3_top){
                if(!n_his_listL.eq(2).hasClass('nocurr')){
                    setcurr(2)
                }
            }else if(scroH >= his2_top){
                if(!n_his_listL.eq(1).hasClass('nocurr')){
                    setcurr(1)
                }
            }else if(scroH >= his1_top){
                if(!n_his_listL.eq(0).hasClass('nocurr')){
                    setcurr(0)
                }
            }
        }
    });

    function setcurr(n){
        history_listL.removeClass('current');
        history_listL.eq(n).addClass("current");
    }
})