(function(){

    //html = '<li class="login"><a href="' + pageLogin + '" class="t">登录</a></li>' +
      //          '<li class="register"><a href="' + EJS.UserRegister + '" class="t">注册</a></li>';
    
    var weiboPage = {};
    weiboPage.isOver = isOver;  //活动时间到，结束
    weiboPage.noStore = (currentStore == 0) ? true : false ;  //库存商品为0

    //交易记录
    weiboPage.slider = function(data,options){
        this.wrapEle = $(options.wrapEle);
        this.showNum = options.showNum || 3;    //一屏展示多少条
        this.oncePushNum = options.oncePushNum || this.showNum*3;   //一次压放多少条数据
        this.animateSpeed = options.animateSpeed || '300';  //数据显示时的动画时间
        this.distanceTime = options.distanceTime || '2000'; //相隔多少秒加入数据
        this.timer = null;
        this.data = data;   //交易记录数据
        if (this.data.length==0) return false;

        //活动结束或商品卖完了
        if (weiboPage.isOver || weiboPage.noStore){
            var oldLength = this.data.length;
            var endList = (oldLength > this.showNum) ? this.showNum: oldLength;
            this.addList(this.generateDom(0, endList));
        }else{
            this.init();
        }
        
    }
    weiboPage.slider.prototype = {
        init: function(){
            var dataLength = this.data.length;
            if (this.showNum < dataLength && dataLength < this.oncePushNum){
                this.addList(this.generateDom(0, dataLength));
                this.autoRun();
            }else if (dataLength < this.showNum){
                this.addList(this.generateDom(0, dataLength));
            }else{
                this.addList(this.generateDom(0));
                this.autoRun();
            }

        },
        autoRun: function(){
            var root = this;
            this.timer = setInterval(function(){
                root.move();
            }, this.distanceTime);
        },
        stopRun: function(){
            clearInterval(this.timer);
            return false;
        },
        getListLength: function(){
            return ($('li', this.wrapEle).length);
        },
        move: function(){
            if (weiboPage.isOver) {
                this.stopRun();
                return false;
            }
            var root = this;
            var currenList = $("li.hide", this.wrapEle).last();
            currenList.length == 0 && this.addList(this.generateDom(this.getListLength()));
            currenList.show(root.animateSpeed, function(){
                $(this).fadeTo(root.animateSpeed, 1, function(){
                    $(this).removeClass('hide');
                });
            });
        },
        addList: function(html){
            this.wrapEle.prepend(html);
        },
        generateDom: function(start, end){
            var html = '';
            var className = 'hide';
            var end = end || start+this.oncePushNum;
            if (end > this.data.length){
                end = this.data.length;
            }
            if(start == this.data.length){
                this.stopRun();
            }
            for (var i=start; i<end; i++){
                var curData = this.data[i];
                if (i+1>this.showNum || this.getListLength()>0){
                    html = '<li class="clearfix hide"><span class="name fl">'+curData["dimUserName"]+'</span><span class="order fl">订单'+curData["dimTradeOrderNo"]+'</span><span class="time fr">'+curData["formatTradeDate"]+'</span></li>' + html;
                }else{
                    html = '<li class="clearfix"><span class="name fl">'+curData["dimUserName"]+'</span><span class="order fl">订单'+curData["dimTradeOrderNo"]+'</span><span class="time fr">'+curData["formatTradeDate"]+'</span></li>' + html;
                }
            }
            return html;
        },
        getNewData: function(){
            var root = this;
            $.ajax({
                type:"GET",
                url:EJS.HomeUrl+'/weibo/buy/trade/'+weiboId,
                async:false,
                dataType:'json',
                success:function (data) {
                    root.data = data.data.tradeInfoList;
                    if (root.data.length==0) return false;
                    root.move();
                    root.autoRun();
                }
            });
        }
    }
    
    //隔时请求产品数据
    weiboPage.product = function(tradeChange){

        var pro_item = $(".pro_item", weiboPage.page);
        var price = $(".price span", pro_item);
        var trans_num = $(".trans_num span", pro_item);
        var has_buy = $(".has_buy span", pro_item);
        var store = $(".store span", pro_item);
        var add_cart = $(".add_cart .btn_addcart", pro_item);
        var bnt_disabled = $(".add_cart .bnt_disabled", pro_item);
        var pro_timer = null;

        this.init = function(){
            //若库存为0退出
            if(weiboPage.noStore) return false;

            //加入购物车
            $(".btn_addcart", weiboPage.page).click(function(){
                var _url = $(this).attr("href");
                $.ajax({
                    type:"GET",
                    url:_url,
                    async:false,
                    dataType:'json',
                    success:function (data) {
                        if (data["success"]) {
                            if ((typeof isDirectBuy == 'undefined') || isDirectBuy){
                                window.location.href = data["data"]["cartUrl"];
                            }else{

                                if (typeof buyCallback != 'undefined'){
                                    buyCallback();
                                }

                                new Ejs.Dialog({
                                    title:"添加到购物车",
                                    type:Ejs.Dialog.type.RIGHT,
                                    info:"物品已经成功添加到购物车",
                                    isConfirm:false,
                                    isModal:true,
                                    isConfirm:false,
                                    afterClose:function (v) {
                                        Ejs.cartNumber();
                                    },
                                    buttons:[
                                        {
                                            buttonText:"继续购物",
                                            buttonClass:"button_a",
                                            onClick:function () {

                                            }
                                        },
                                        {
                                            buttonText:"去结算",
                                            buttonClass:"button_a",
                                            onClick:function () {
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
                                    buttonText:"去结算",
                                    buttonClass:"button_a",
                                    onClick:function () {
                                        window.location.href = EJS.CartUrl;
                                    }
                                });
                            } else if (data["data"]["errorType"] == "systemError") {
                                _buttons.push({
                                    buttonText:"去首页逛逛",
                                    buttonClass:"com_button_green_d",
                                    onClick:function () {
                                        window.location.href = EJS.HomeUrl;
                                    }
                                });
                            }
                            new Ejs.Dialog({
                                title:"提示信息",
                                type:Ejs.Dialog.type.ERROR,
                                info:data["msg"],
                                isConfirm:false,
                                buttons:_buttons,
                                isModal:true
                            });
                        }
                    }
                });
                //end ajax
                return false;
            });
            
            //开始隔时请求
            this.autoRun();
        }
        this.autoRun = function(){
            var root = this;
            pro_timer = setInterval(function(){
                root.getProInfo();
            },5000);
        }
        this.noStoreAction = function(){
            weiboPage.noStore = true;
            add_cart.css("display","none");
            bnt_disabled.css("display","block");
            clearInterval(pro_timer);
        }
        this.isOverAction = function(){
            bnt_disabled.text('活动已结束');
            add_cart.css("display","none");
            bnt_disabled.css("display","block");
            weiboPage.isOver = true;
            clearInterval(pro_timer);
        }
        this.getProInfo = function(){
            var root = this;
            $.ajax({
                type:"GET",
                url:EJS.HomeUrl+'/weibo/buy/count/'+weiboId,
                async:false,
                dataType:'json',
                success:function (data) {
                    if (data["success"]) {
                        var proData = data["data"];
                        if (proData["currentStore"] === 0){
                            root.noStoreAction();
                        }
                        if(!!proData["isOver"]){
                            root.isOverAction();
                        }
                        if(proData["sellNumber"] > parseInt(has_buy.text())){
                            if (tradeChange) tradeChange();
                        }
                        price.text(proData["currentPrice"]);
                        trans_num.text(proData["rePostCount"]);
                        has_buy.text(proData["sellNumber"]);
                        store.text(proData["currentStore"]);
                    }
                }
            });
        }
   
    }

    //倒计时
    weiboPage.countDown = function(callback){

        var offsetTime = (endDate-nowTime)/1000;
        if (offsetTime<0) return false;
        var day = Math.floor(offsetTime/(60*60*24));
        var hour = Math.floor((offsetTime-day*60*60*24)/(60*60));
        var min = Math.floor((offsetTime-day*60*60*24-hour*60*60)/60);
        var sec = Math.floor(offsetTime-day*60*60*24-hour*60*60-min*60);

        $('#countdown_dashboard').countDown({
            targetOffset: {
                'day':      day,
                'month':    0,
                'year':     0,
                'hour':     hour,
                'min':      min,
                'sec':      sec
            },
            omitWeeks: true,
            onComplete: function() { 
                if (callback) callback();
            }
        });

    }

    $(function(){


        weiboPage.page = $(".active_weibo");

        //交易记录初始化
        var weiboTradeList = new weiboPage.slider(
            tradeInfoList,
            {
                wrapEle: '.active_notes ul',
                showNum: 8,
                oncePushNum: 16
            }
        );

        //产品信息初始化
        var weiboProductInfo = new weiboPage.product(function(){
            weiboTradeList.getNewData();
        });
        weiboProductInfo.init();

        //倒计时初始化
        $LAB
        .script(EJS.StaticDomain+'/js/jquery.lwtCountdown-1.0.js')
        .wait(function(){
            weiboPage.countDown(
                function(){
                   weiboProductInfo.isOverAction();
                   weiboTradeList.stopRun();
                }
            );
        });

        var pageLogin = EJS.ToPageLogin + "?backUrl=";
        pageLogin += encodeURIComponent(location.href);
        $('.weibo_login', weiboPage.page).html('<a href="' + pageLogin + '" class="t">登录</a>/<a href="' + EJS.UserRegister + '" class="t">注册</a>');

    });

})();