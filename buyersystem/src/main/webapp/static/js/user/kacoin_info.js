$(function(){

    //点击登录查看
    $('.kacoin_infot .change .letter a.login').click(function(){
        new Ejs.UserWindow({
            actionType: 'doRefresh'
    //      backUrl: window.location.href
        });
    })

   var btntoCart = $('.cashIt'),//立即兑换按钮
       // kacoinGiftId = $('.kac_price span:eq(0)'),//需要兑换的积分
        btns_tips,//提示层
        tipsHtml,//弹出层代码
        tipsBox = $("<div></div>");
    tipsHtml =  '<span class="ico"></span>'
                + '<span class="close">X</span>'
                + '<div><strong>您的积分不足兑换本商品</strong>'
                + '您可以<a href="#">直接购买本商品</a></div>';
    tipsBox.attr('class','btns_tips').html(tipsHtml).insertAfter('.cashIt');
    btns_tips = $('.btns_tips');
    btns_tips.find('a').each(function(){
        $(this).attr("href",$(this).parents('.btns_tips').prev('.cashIt').attr('data-goods-url'));
    });
    //当前选中的商品
    var __g_cur_product_sku_id = 0;

    //立即兑换
    btntoCart.on('click', function (event) {
        var self = $(this);
        __g_cur_product_sku_id = self.attr("data-productskuid");
        event.preventDefault();
        Ejs.UserStatus.isLogin(function () {
            $.ajax({
                type: 'get',
                url: self.attr('data-checkcanjoin'),
                dataType: 'json',
                success: function (response) {
                    var count = response.data.totalPoint;
                    if (response.success) {
                        window.location.href = self.attr('href') + "?buyNumber=" + $('.text-number').val()
                    }else{
                        var reasonType = response.data.reasonType,
                            reasonMsg = {
                                "NoEnoughKaMoney": "您的积分不足兑换本商品",
                                "JoinTooManyTime" : "您参加的次数过多",
                                "OtherReason" : "其它原因"
                            };
                        btns_tips.hide();
                        $('.btns_tips strong').html(reasonMsg[reasonType]);
                        self.next('div.btns_tips').show();
                    }
                }
            });
        }, function () {
            new Ejs.UserWindow({
                actionType: 'kacoininfo'
            });
        });
    });
    //关闭弹出层
    btns_tips.on('click','span.close',function(){
        btns_tips.fadeOut();
    });
    //回调事件
    Ejs.kacoinInfo = function(){
        $.ajax({
            type: 'get',
            url: $('.cashIt[data-productskuid = ' + __g_cur_product_sku_id + ']').attr('data-checkcanjoin'),
            dataType: 'json',
            success: function (response) {
                var count = response.data.totalPoint;
                $('.kacoin_name div strong').text( response.data.userName);
                $('.kacoin_name span').text(count)
                if (response.success) {
                    window.location.href = $('.cashIt[data-productskuid = ' + __g_cur_product_sku_id + ']').attr('href')
                }else{
                    var reasonType = response.data.reasonType,
                        reasonMsg = {
                            "NoEnoughKaMoney": "您的积分不足兑换本商品",
                            "JoinTooManyTime" : "您参加的次数过多",
                            "OtherReason" : "其它原因"
                        };
                    btns_tips.hide();
                    $('.btns_tips strong').html(reasonMsg[reasonType]);
                    $('.cashIt[data-productskuid = ' + __g_cur_product_sku_id + ']').next('div.btns_tips').show();
                }
            }
        });
    }



    //登陆后，判断是否积分数足够兑换
    Ejs.UserStatus.isLogin(function () {
        $.ajax({
            type: 'get',
            url: $('.cashIt[data-productskuid = ' + __g_cur_product_sku_id + ']').attr('data-checkcanjoin'),
            dataType: 'json',
            success: function (response) {
                var count = response.data.totalPoint;
                if (response.success) {
                    var reasonType = response.data.reasonType,
                        reasonMsg = {
                            "NoEnoughKaMoney": "您的积分不足兑换本商品",
                            "JoinTooManyTime" : "您参加的次数过多",
                            "OtherReason" : "其它原因"
                        };
                    $('.btns_tips strong').html(reasonMsg[reasonType]);
                    $('.change').find('.btns_tips').fadeIn();
                }
            }
        });
    });



    function updateNumber(ele) {
        var numberInput = ele.parent('.amount').find('.text-number'),
            val = parseInt(numberInput.val(), 0),
            limit = parseInt(numberInput.attr('limit'), 0),
            price = parseInt(numberInput.attr('data-price'), 0),
            kacoinAll = parseFloat($('.kacoinAlllength').html()),//2014-8-7喀币商城详细页
            productPrice = parseFloat($('#kacoinGift').html()),
            Integralp = parseInt(kacoinAll/productPrice),
            kacoinGift = $('#kacoinGift').attr('data-allkacoin');



        function setTotalPrice(val) {
           // val = val.toFixed(2);
            ele.parents('.change').find('.kagift').text(val)
        }


        return {
            add: function () {
                val += 1;

                if (val > limit) {
                    val = limit;
                    Ejs.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                }

                if(val > Integralp){
                    if(Integralp < 1){
                        Integralp = 1;
                    }
                    val = Integralp;
                    Ejs.tip(numberInput, 'limit-tip', '您的积分不够啦~', 30);
                }

                numberInput.val(val);
                setTotalPrice(val * price);
            },
            remove: function () {
                val -= 1;

                if (val < 1) {
                    val = 1;
                }

                numberInput.val(val);
                setTotalPrice(val * price);
            },
            checkValue: function () {

                if (!/^[1-9]*\d*$/g.test(val)) {
                    val = 1;
                }

                if (val > limit) {
                    val = limit;
                    Ejs.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                }
                if(val > Integralp){
                    if(Integralp < 1){
                        Integralp = 1;
                    }
                    val = Integralp;
                    Ejs.tip(numberInput, 'limit-tip', '您的积分不够啦~', 30);
                }
                numberInput.val(val);
                setTotalPrice(val * price);
            }
        };

    }


    // 增加数量事件
    $('.change').on('click', '.btn-add', function () {
        updateNumber($(this)).add();
    });

    // 减小数量事件
    $('.change').on('click', '.btn-sub', function () {
        updateNumber($(this)).remove();
    });

    // 手动输入数量验证
    $('.change').on('blur', '.text-number', function () {
        updateNumber($(this)).checkValue();
    });
});