/**
 * 抽奖
 * User: Hualei Du
 * Date: 13-10-24
 */

// 抽奖游戏
function Lottery() {
    this.selected = 0;
    this.box = $('#lottery_content');
    this.items = this.box.find('.prize_item'); // 得到所有奖品
    this.countItem = this.items.length; // 奖品总个数
    this.index = 1; // 当前高亮位置
    this.runIndex = 1; // 移动了多少次
    this.cycle = 1; // 移动圈数
    this.endCycle = 4; // 总圈数
    this.countRun = 0; // 共需要移动多少次
    this.wait = 0; // 等待抽奖次数(抽奖过程中点击抽奖按钮会被添加到等待，抽奖完成后会提示是否继续)
    this.callback = function () {
        //alert('恭喜您抽中了' + this.selected);
    };
    this.running = null;
}

Lottery.prototype = {
    // 初始化
    init: function () {
        if (this.selected > this.countItem) {
            return;
        }
        this.countRun = this.getCountRun();
        if (!this.running) {
            this.play();
        } else {
            this.wait += 1;
        }
    },
    // 得到总共要移动多少次
    getCountRun: function () {
        return this.countItem * this.endCycle + this.selected;
    },
    // 获取运行速度
    getSpeed: function () {
        // 如果不是前3个也不是后3个返回30
        if (this.runIndex > 3 && this.runIndex < (this.countRun - 3)) {
            return 30;
        }
        return 200;
    },
    // 动画
    play: function () {
        var that = this;

        (function () {
            that.box.find('.active').remove();
            that.items.eq(that.index - 1).append('<div class="active"></div>');

            // 到达目的地停止运行, 重新设置默认值
            if (that.cycle === that.endCycle + 1 && that.index === that.selected) {
                that.runIndex = that.selected;
                that.index = that.selected;
                that.cycle = 1;
                that.running = null;
                setTimeout(that.callback, 300);
                return;
            }

            that.index += 1;
            that.runIndex += 1;
            that.cycle = Math.ceil(that.runIndex / that.countItem);

            // 到达最后一项，从头开始
            if (that.index > that.countItem) {
                that.index = 1;
            }

            that.running = setTimeout(arguments.callee, that.getSpeed());
        }());
    }
};

// 绑定抽奖事件
(function ($) {
    var lottery = null,
        lotteryBtn = $('#lottery_btn'),
        rotaryId = 1;

    function dialog(content, btnText, callback) {
        new Ejs.Dialog({
            title: ' ',
            type: Ejs.Dialog.type.RIGHT,
            info: content,
            outClass: 'com_lottery_window',
            border: '1px solid #f3ae00',
            width: 360,
            height: 200,
            isModal: true,
            backgroundColor: '#fff',
            buttons: [
                {
                    buttonText: btnText,
                    buttonClass: 'button_a',
                    buttonStyle: {
                        marginRight: 0
                    },
                    onClick: function () {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    }
                }
            ]
        });
    }

    function AddressWindow(info, lotteryId) {
        this.info = info;
        this.lotteryId = lotteryId;
        this.layer = null;
    }

    AddressWindow.prototype = {
        init: function () {
            var closeText = '<span class="close"></span>',
                formHtml = this.createForm(),
                width = 400,
                height = 280,
                that = this;

            that.layer = new com.layer({
                isModal: true,
                contentClass: 'content_wrapper',
                content: formHtml,
                closeText: closeText,
                loaded: false,
                cache: false,
                opacity: 0.2,
                hideAction: com.layer.hideAction.fade,
                actionSpeed: 200,
                style: {
                    position: "absolute",
                    backgroundColor: "#fff",
                    border: "1px solid #f3ae00",
                    overflow: "visible",
                    width: width,
                    height: height,
                    borderRadius: 4
                },
                positionType: com.layer.positionType.center,
                isBgClose: false,
                onshowing: function () {
                    var form = $('#prizeForm', '#' + this.getId());
                    that.saveForm(form);
                },
                onclosing: function () {
                },
                outClass: 'com_lottery_form'
            });
            that.layer.show();
            $('#' + that.layer.getCloseId()).off().on('click', function () {
                if (confirm('您还没有填写收货信息，确定要关闭窗口吗？')) {
                    that.layer.hide();
                }
            });
        },
        createForm: function () {
            return '<div class="prize_form">' +
                '<div class="title_wrapper">&nbsp;</div>' +
                '<div class="info_wrapper">' +
                '   <div class="info">恭喜您抽中了“' + this.info + '”，请填写收货信息</div>' +
                '</div>' +
                '<form action="/rotary/lottery/send" method="post" id="prizeForm">' +
                '<input type="hidden" name="lotteryId" value="' + this.lotteryId + '">' +
                '<div class="content">' +
                '<ul>' +
                '   <li><label>联系人：</label> <input type="text" class="text" name="consigneeName"/><span>*</span></li>' +
                '   <li><label>联系电话：</label> <input type="text" class="text" name="consigneePhone"/><span>*</span></li>' +
                '   <li><label>收货地址：</label> <input type="text" class="text text_address" name="consigneeAddress"/><span>*</span></li>' +
                '</ul>' +
                '<div class="btn_wrapper">' +
                '   <button type="submit" class="button_a">提交表单</button>' +
                '   <span class="error"></span>' +
                '</div>' +
                '</div>' +
                '</form>' +
                '</div>';
        },
        saveForm: function (form) {
            var that = this;

            form.submit(function (event) {
                event.preventDefault();

                var inputName = form.find('input[name=consigneeName]'),
                    inputMobile = form.find('input[name=consigneePhone]'),
                    inputLocation = form.find('input[name=consigneeAddress]'),
                    error = form.find('.error');

                error.text('');

                if (inputName.val().length < 1) {
                    error.text('联系人不能为空');
                    inputName.focus();
                    return;
                }

                if (inputMobile.val().length < 1) {
                    error.text('联系电话不能为空');
                    inputMobile.focus();
                    return;
                }

                if (inputLocation.val().length < 1) {
                    error.text('收货地址不能为空');
                    inputLocation.focus();
                    return;
                }

                error.text('');
                form.find('button').eq(0).text('正在处理')[0].setAttribute('type', 'button');

                $.ajax({
                    type: "POST",
                    cache: false,
                    url: form.attr('action'),
                    data: form.serialize(),
                    dataType: 'json',
                    success: function (res) {
                        if (res.success) {
                            that.hide();
                            dialog('<strong>提交成功,我们会尽快把货送到您手里</strong><p>如有其它疑问请联系客服。客服热线：400-9933 1784</p>', '关闭窗口');
                        } else {
                            form.find('button').eq(0).text('提交表单')[0].setAttribute('type', 'submit');
                        }
                    }
                });

            });
        },
        hide: function () {
            this.layer.hide();
        }
    };

    function doLottery(res) {
        var meed = res.data.meed,
            rotaryMeed = meed.rotaryMeed,
            integralCount = meed.integralCount,
            lotteryId = meed.lotteryId,
            description = rotaryMeed.description,
            meedType = rotaryMeed.meedType,
            meedIndex = rotaryMeed.meedIndex;

        if (!lottery) {
            lottery = new Lottery();
        }
        lottery.selected = meedIndex;
        lottery.callback = function () {
            if (meedType === 'Coupon' || meedType === 'Integral') {
                // 抽中积分或现金券
                dialog('<strong>恭喜您，抽中了 ' + description + '</strong><p>今日忌加班，忌省钱，宜享乐，宜抽奖，就像我</p>', '继续抽奖', function () {
                    lotteryBtn.click();
                });
            } else if (meedType === 'Product') {
                // 抽中实物
                var addressWindow = new AddressWindow(description, lotteryId);
                addressWindow.init();
            } else {
                // 没有中奖
                dialog('<strong>很遗憾，这次没能中奖</strong>', '继续努力');
            }
            $('#integral_count').text(integralCount);
        };
        lottery.init();
    }

    lotteryBtn.on('click', function () {
        // 正在抽奖
        if (lottery && lottery.running) {
            return;
        }
        // 发送ajax获取中奖信息
        $.ajax({
            type: "GET",
            cache: false,
            url: '/rotary/lottery?rotaryId=' + rotaryId,
            dataType: 'json',
            success: function (res) {
                if (res.success) {
                    doLottery(res);
                } else {
                    dialog('<p>' + res.msg + '</p>', '关闭');
                }
            },
            error: function () {
                dialog('<strong>获取不到抽奖信息!</strong>', '关闭');
            }
        });
    });

}(jQuery));

// 会员中奖榜
(function ($) {

    var box = $('#lottery_log_list'),
        top = 0,
        count = box.children('li').length,
        running = null,
        maxTop;

    if (count > 1) {
        maxTop = (count - 1) * 80;
    } else {
        return;
    }

    function next() {
        top += 80;
        if (top > maxTop) {
            box.stop().animate({top: -top}, 400, function () {
                top = 0;
                box.stop().css('top', '30px');
                box.stop().animate({top: top}, 60);
            });
        } else {
            box.stop().animate({top: -top}, 400);
        }
    }

    running = setInterval(next, 3000);

    box.hover(function () {
        clearInterval(running);
    }, function () {
        running && clearInterval(running);
        running = setInterval(next, 3000);
    });

}(jQuery));
