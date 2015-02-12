$(function(){

    $('.show1 .kacoin_sign').hover(function(){
        $(this).next('.tips').fadeIn();
    },function(){
        $(this).next('.tips').fadeOut();
    });
    //签到
    $('.sign').on('click','a.kacoin_sign',function(){


        var self = $(this),
            url = self.attr('data-url'),
            dataUserSign = self.attr('data-userSign');
//$('.kacoin_sign').click(function(){
//  $('#arrive').show();
//})
        Ejs.UserStatus.isLogin(function () {
            $.ajax({
                type:'GET',
                cache:false,
                url:url,
                dataType:'JSON',
                success: function (data) {
                    if (data.success) {
                        var data =  data.data;
                        //    {"data":{"point":10,"totalPoint":10,"signInCount":1},"success":true,"msg":""}

                        self.parent().hide();
                        $('.sign_ok').show();
                        var  kacoinBox = $("<div></div>");


                        var _amount  = '<div class="kacoin_done">' +
                            '<span class="close" ></span>' +
 
                            '<div class="done"> 签到成功，您已连续签到<span>'+ data.signInCount +'</span>天，再接再厉。</div>'+
                            '<div class="tomorrow">明天签到可获得<span >'+ data.tomorrowPoint   +'</span>积分，连续签到<em>'+ data.activityDays     +'</em>天可获得<span>'+ data.activityTotalPoint  +'</span>积分。</div>'+
                            '<div class="allkb">您的积分总计<strong>'+  data.totalPoint +'</strong>个，可兑换<strong>'+  data.totalChangeMoney   +'</strong>元。</div>'+
                           '<a href="#" class="now_sign">立即兑换</a>'+
 
                            '<dl class="recommend">'+
                            '<dt><a href="kacoin_info.php"><img src="assets/stylesimg/user/kacoin_done.jpg" width="500" height="100"> </a> </dt>'+
                            '<dd>'+
                            '<div>'+
                            '<span class="how"><i>100</i><em>积分</em></span>'+
                            '<span class="price">¥ 39.00</span>'+
                            '<a href="" class="now_sign">立即兑换</a></div>'+
                            '<h6><a href="#">礼德正宗头采特级胎菊甘菊</a> </h6>'+
                            '</dd></dl></div>';

                        kacoinBox.attr("id","arrive").html(_amount).appendTo('.mod_kacoin_sign');
                        var img_src = $('.kacoin_img').find('img').attr('data-smallimgurl'),
                            price = $('.kacoin_done .recommend'),
                            h3A = $('.kacoin_intro_letter h3 a'),
                            h3A_href = h3A.attr("href"),
                            dataTime = $('.kacoin_done .done span').html();
                        $('.how i').text($('.kacoin_introduce .kac_price div span').html());
                        price.find('.price').text($('.kacoin_introduce .kac_price em').html());
                        price.find('h6 a').attr("href",h3A_href).text(h3A.html());
                        price.find('dt img').attr("src",img_src);
                        $('.kacoin_name span').html(data.totalPoint);
                        $('.sign_ok .kacoin_kabi span').html("签到成功，您已连续签到" + dataTime + "天");
                        $('.kacoin_done a.now_sign').attr('href',h3A.attr('href'));
                        $('.recommend dt a').attr('href',$('.kacoin_img a').attr('href'))
                        //关闭签到弹出层
                        $('.close,.now_sign').click(function(){
                            $('#arrive').fadeOut().remove();
                        })

                    }else{
                        alert(data.msg);
                    }
                },
                error:function(){
                    alert('服务器发生错误')
                }


            })
        },function(){
            new Ejs.UserWindow({
                actionType: 'doRefresh',
                backUrl: window.location.href
            });
        })
    });

    $('.kacoin_name a').click(function(){
        new Ejs.UserWindow({
            actionType: 'doRefresh',
            backUrl: window.location.href
        });
    });
    //AQ什么事积分
    var kacoin_what = $('.kacoin_what'),
        ask = kacoin_what.children('.Ask'),
        tabs = $('.tabs'),
        kacoin_AQ_Box = $('.kacoin_AQ_Box'),
        kacoin_AQ = kacoin_AQ_Box.children('.kacoin_AQ'),
        close = $('.close');

    kacoin_AQ.eq(0).show();
    ask.on('click',function(){
        var idx = $(this).index();
        kacoin_AQ_Box.fadeIn();
        kacoin_AQ.hide();
        kacoin_AQ.eq(idx).show();
        tabs.children('li').removeClass('current');
        tabs.children('li').eq(idx).addClass('current');
    });

    tabs.children('li').on('click',function(){
        var idx = $(this).index();
        tabs.children('li').removeClass('current');
        $(this).addClass('current');
        kacoin_AQ.hide();
        kacoin_AQ.eq(idx).show();
    });

    close.on('click',function(){
        kacoin_AQ_Box.fadeOut();
    })



})