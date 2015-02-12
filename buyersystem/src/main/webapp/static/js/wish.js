var TBFixHide = true;

function timerZone(option) {
    if (typeof option != 'object') {
        return false;
    }
    var _opt = option || {};

    var daySC = 24 * 60 * 60 * 1000;
    var hourSC = 60 * 60 * 1000;
    var minuteSC = 60 * 1000;
    var secondSC = 1000;

    var defaultOptions = {
        featureDate : null,
        nowDate : null,
        targetDay : '',
        targetHour : '',
        targetMin : '',
        targetSec : ''
    };

    $.extend(true,defaultOptions,_opt);

    var limitSecCopy = defaultOptions.featureDate.getTime() - defaultOptions.nowDate.getTime();

    setInterval(function () {
        var limitSec = limitSecCopy;
        if (limitSec <= 0) {
            var _parent = $(defaultOptions.targetDay).parent("p.p2");
            _parent.siblings("h4.h4").html("<span>活动已经结束！</span>");
            _parent.html("<span>敬请等待下一期...</span>");
            return;
        }

        var day = parseInt(Math.floor(limitSec / daySC));
        $(defaultOptions.targetDay).text(day);
        limitSec = limitSec - day * daySC;

        var hour = parseInt(Math.floor(limitSec / hourSC));
        $(defaultOptions.targetHour).text(hour);
        limitSec = limitSec - hour * hourSC;

        var minute = parseInt(Math.floor(limitSec / minuteSC));
        $(defaultOptions.targetMin).text(minute);
        limitSec = limitSec - minute * minuteSC;

        var second = parseInt(Math.floor(limitSec / secondSC));
        $(defaultOptions.targetSec).text(second);

        limitSecCopy -= 1000;

    },1000);

}


$(document).ready(function () {
    //tab效果
    var tabs = $("#tabs");
    var tabsLength = tabs.find("a").length;
    var tabsId = "";
    if (tabsLength < 3) {
        tabsId = "_s2";
    }
    var newClass = ['one' + tabsId,'two' + tabsId,'three' + tabsId];
    tabs.find("a").each(function (i) {
        var _this = $(this);
        _this.click(function (e) {
            e.preventDefault();
            if ($(".hd_tab_bar").css("position") == "fixed") {
                $(document).scrollTop(63);
            }
            tabs.removeClass('one one' + tabsId + ' two' + tabsId + ' three' + tabsId);
            tabs.addClass(newClass[i]);
            var _tabContent = $(".hd_tab_content");
            _tabContent.hide();
            _tabContent.eq(i).show();
            $("img.lazy").lazyload();
        });
    });

    //var nowDate = new Date(); //服务器时间

    //var dateA = "12/31/2012 0:0:1";
    //var dateB = "12/31/2012 0:0:52";


    var product_list = $(".product_list_x2>li");
    product_list.each(function () {
        var _this = $(this);
        _this.hover(function () {
            _this.addClass("hover");
            _this.find(".share").animate({bottom : 0},480);
        },function () {
            _this.removeClass("hover");
            _this.find(".share").css({bottom : - 35});
        });
    });

    var fixed_bar = $(".hd_tab_bar");
    $(window).bind("resize scroll",function (e) {
        var scrollVal = $(document).scrollTop();
        var dom = '(document.documentElement || document.body)';
        if (scrollVal >= 63) {
            fixed_bar.addClass("hd_fixed");
            if (_isIE6) {
                fixed_bar[0].style.setExpression('top','eval(' + dom + '.scrollTop' + ') + "px"');
            }
        } else {
            fixed_bar.removeClass("hd_fixed");
            if (_isIE6) {
                fixed_bar.css({top : 'auto'});
            }
        }
    });

    var email = $("#emailAddress");
    email.blur(function (e) {
        if ($.trim(email.val()) === '') {
            email.val("Email");
        }
    }).focus(function (e) {
            if ($.trim(email.val()) === 'Email') {
                email.val("");
            }
        });

    $("#sentEmail").submit(function (e) {
        e.preventDefault();
        var regEmail = new RegExp("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
        var emailVal = $.trim(email.val());
        if (! regEmail.test(emailVal)) {
            alert("请输入正确的Email地址!");
            email.focus();
        }else{
            var emailUrl = EJS.HomeUrl+"/subscribe";
            $.ajax({
                type : "POST",
                async : false,
                url : emailUrl,
                data : {
                    email : emailVal
                },
                dataType : "json",
                success : function (Data) {
                    if (Data["success"]) {
                        alert("订阅成功!");
                    } else {
                        alert(Data["msg"]);
                    }
                }
            });
        }
    });

});
