/**
 * 确认订单页面
 * Created by HuaLei.Du on 2014/6/13.
 */


$LAB.script(EJS.StaticDomain + '/js/selectArea.js')
    .script(EJS.StaticDomain + '/js/address.js').wait(function () {
        var orderAddress = new Ejs.AddressManager();
        orderAddress.page = 'order';
        orderAddress.initialise();
    });

(function ($, Ejs) {
    "use strict";

    var valueCollection = {}, // 存放订单信息
        totalPriceEle = $('#total-price'),
        totalPrice = parseInt(totalPriceEle.text(), 0);

    (function () {
        var cartSteps = $('.cart-steps');
        cartSteps.find('li').eq(0).addClass('strong');
        cartSteps.find('li').eq(1).addClass('strong');
        cartSteps.find('.step').addClass('step-2');
    }());

    // 支付方式
    function payment() {
        var box = $('.payment-box'),
            payTypes = box.find('li');

        valueCollection.payBank = payTypes.eq(0).attr('paybank');
        payTypes.on('click', function () {
            var self = $(this);

            if (self.hasClass('more-payBank')) {
                self.empty();
                box.find('.more-list').show();
                return;
            }
            payTypes.removeClass('current');
            self.addClass('current');
            valueCollection.payBank = self.attr('paybank');

        });
    }

    // 模拟radio元素
    function radioEvent(ele) {
        var radioCheckedClass = 'form-radio-checked',
            radioName = ele.attr('name');

        if (radioName) {
            $('.form-radio[name=' + radioName + ']').removeClass(radioCheckedClass);
            ele.addClass(radioCheckedClass);
            return true;
        }
        return false;
    }

    // 发票信息
    function invoice() {
        var box = $('.invoice-box'),
            baseurl;

        valueCollection.isInvoice = 'false';

        // 选择抬头单位
        function selectCompany(ele) {
            ele.parent().addClass('current').siblings('li').removeClass('current');
            valueCollection.companyName = ele.attr('val');
        }

        function getCompanyList() {
            return box.find('li');
        }

        // 验证单位
        function validateCompany(input) {
            if (!$.trim(input.val())) {
                Ejs.tip(input, 'cILL', '请输入单位名称', 34, 0, 1800);
                input.focus();
                return false;
            }
            if ((/<[^>]+>/g).test(input.val())) {
                Ejs.tip(input, "cILL", "单位名称里含有非法字符,请修改！", 34, 0, 1800);
                input.focus();
                return false;
            }
            return true;
        }

        // 保存抬头单位
        function saveCompany(ele) {
            var parent = ele.parent('li'),
                input = parent.find('input'),
                type = 'add',
                company = {};

            if (baseurl === undefined) {
                baseurl = box.attr('baseurl');
            }

            if (validateCompany(input)) { // 输入内容是否合法

                if (input.attr('data-id')) {
                    type = 'update';
                    company.id = input.attr('data-id');
                }

                company.companyName = input.val();
                $.ajax({
                    type: 'POST',
                    url: baseurl + type,
                    async: false,
                    data: company,
                    dataType: 'JSON',
                    success: function (result) {

                        var list;
                        if (result.success) {
                            parent.find('input').attr('data-id', result.data.dataId).hide();
                            parent.find('.btn-save').hide();
                            parent.find('.company-name').find('span').text(company.companyName);
                            parent.find('.company-name').attr('val', company.companyName).show().click();
                            parent.find('.btn-modify').show();
                            parent.find('.btn-delete').show();

                            list = getCompanyList();

                            if (list.length === 1) {
                                list.eq(0).find('span').addClass('current');
                                valueCollection.companyName = company.companyName;
                            } else if (list.length >= 3) {
                                box.find('.btn-add').parent().hide();
                            }

                        } else {
                            Ejs.dialogAlert(result.msg);
                        }

                    }
                });

            }
        }

        // 删除抬头单位
        function deleteCompany(ele) {

            var parent = ele.parent('li'),
                input = parent.find('input'),
                type = 'delete',
                id = input.attr('data-id'),
                company = {};

            if (baseurl === undefined) {
                baseurl = box.attr('baseurl');
            }

            if (id) {
                company.invoiceCompanyId = id;

                $.ajax({
                    type: 'POST',
                    url: baseurl + type,
                    async: false,
                    data: company,
                    dataType: 'JSON',
                    success: function (result) {
                        var list,
                            btnAdd = box.find('.btn-add');

                        btnAdd.parent().show();

                        if (result.success) {
                            list = getCompanyList();

                            if (list.length === 0) {
                                btnAdd.click();
                            } else {

                                if (parent.hasClass('current')) {
                                    list.eq(0).find('.company-name').click();
                                }

                            }

                            parent.remove();
                            box.find('.btn-add').parent().show();
                        } else {
                            new Ejs.Dialog({
                                title: '提示信息',
                                type: Ejs.Dialog.type.ERROR,
                                info: result.msg
                            });
                        }
                    }
                });
            }
        }

        // 添加抬头单位
        function addCompany(ele) {
            var companyBox = box.find('.company-list ul'),
                html = '<li class="clearfix">' +
                    '<input type="text" class="form-text" value="">' +
                    '<a class="e-btn btn-default btn-save" href="javascript:;">确定</a>' +
                    '<span class="company-name" style="display:none;"><i></i><span></span></span>' +
                    '<a class="e-btn btn-grey btn-xs btn-modify" href="javascript:;" style="display:none;">修改</a>' +
                    '<a class="e-btn btn-grey btn-xs btn-delete" href="javascript:;" style="display:none;">删除</a>' +
                    '</li>',
                list;

            companyBox.append(html);
            list = getCompanyList();
            list.last().find('input').focus();

            if (list.length >= 3) {
                ele.parent().hide();
            }

        }

        // 编辑抬头单位
        function modifyCompany(ele) {

            var parent = ele.parent();
            parent.find('input').show();
            parent.find('.btn-save').show();
            parent.find('.company-name').hide();
            parent.find('.btn-modify').hide();
            parent.find('.btn-delete').hide();

        }

        // 绑定事件(选择，添加，删除，修改)
        (function () {

            // 选择是否需要发票
            box.on('click', '.form-radio[name=invoice]', function () {
                var self = $(this),
                    val;

                if (radioEvent(self)) {
                    val = self.attr('val');
                    valueCollection.isInvoice = val;

                    if (val === 'true') {
                        box.find('.invoice-title').show();
                        box.find('.invoice-content').show();

                        if (!valueCollection.invoiceTitle) {
                            valueCollection.invoiceTitle = 'individual';
                        }

                        if (!valueCollection.invoiceContent) {
                            valueCollection.invoiceContent = '明细';
                        }

                    } else {
                        box.find('.invoice-title').hide();
                        box.find('.invoice-content').hide();
                        box.find('.company-list').hide();
                    }

                }

            });

            // 选择发票抬头
            box.on('click', '.form-radio[name=invoiceTitle]', function () {
                var self = $(this),
                    val,
                    list,
                    companyCount;

                if (radioEvent(self)) {
                    val = self.attr('val');
                    valueCollection.invoiceTitle = val;
                    list = box.find('.company-list');
                    companyCount = list.find('li').length;

                    if (val === 'company') {
                        list.slideDown(200);

                        if (companyCount > 0 && list.find('.current').length < 1) {
                            list.find('li').eq(0).find('.company-name').click();
                        }

                    } else {
                        list.slideUp(100);
                    }

                }

            });

            // 发票内容
            box.on('click', '.form-radio[name=invoiceContent]', function () {
                var self = $(this);

                if (radioEvent(self)) {
                    valueCollection.invoiceContent = self.attr('val');
                }

            });

            // 选择抬头单位
            box.on('click', '.company-name', function () {
                selectCompany($(this));
            });

            // 添加抬头单位
            box.on('click', '.btn-add', function () {
                addCompany($(this));
            });

            // 保存抬头单位
            box.on('click', '.btn-save', function () {
                saveCompany($(this));
            });

            // 修改抬头单位
            box.on('click', '.btn-modify', function () {
                modifyCompany($(this));
            });

            // 删除抬头单位
            box.on('click', '.btn-delete', function () {
                deleteCompany($(this));
            });

        }());
    }

    // 积分
    function integral() {
        var box = $('.integral'),
            form = box.find('.form'),
            selected = box.find('.selected');

        // 使用积分
        function useIntegral() {
            var input = box.find('input[name=integral]'),
                val = input.val(),
                maxVal = input.attr('max-value'),
                url = box.attr('data-url');

            if (val.length < 1) {
                Ejs.tip(input, 'cILL', '请输入要使用的积分', 34, 0, 1800);
                return;
            }

            if (!/^[0-9]+(\.[0-9]{1,2})?$/.test(val)) {
                Ejs.tip(input, 'cILL', '积分输入错误', 34, 0, 1800);
                return;
            }

            if (Number(val) > Number(maxVal)) {
                Ejs.tip(input, 'cILL', '本次最多可用 ' + maxVal + ' 积分', 34, 0, 1800);
                return;
            }

            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    integral: val,
                    code: valueCollection.coupon || ''
                },
                dataType: 'json',
                success: function (result) {

                    if (result.success) {
                        valueCollection.integral = val;
                        form.hide();
                        selected.show().find('.integral-text').html('已使用 <strong>' + val + '</strong> 积分');
                        $('#integral-price').text(result.data.integralPrice);
                        totalPriceEle.text(result.data.orderTotalPrice);
                    } else {
                        Ejs.tip(input, 'cILL', result.msg, 34, 0, 1800);
                    }

                }
            });
        }

        // 取消积分
        function cancelIntegral() {
            var couponPriceEle = $('#coupon-price'),
                couponPriceText = couponPriceEle.text(),
                couponPrice,
                lastPrice;

            form.show();
            selected.hide();
            valueCollection.integral = null;
            $('#integral-price').text('0.00');

            if (couponPriceText === '0.00') {
                totalPriceEle.text(totalPrice);
                return;
            }

            couponPrice = parseInt(couponPriceText, 0);
            lastPrice = totalPrice - couponPrice;
            totalPriceEle.text(lastPrice.toFixed(2));

        }

        // 使用积分
        box.on('click', '.btn-save', function () {
            useIntegral();
        });

        // 取消积分
        box.on('click', '.btn-cancel', function () {
            cancelIntegral();
        });

        // 展开或收起积分
        box.on('click', 'h4', function () {
            var self = $(this),
                couponBox = $('.integral-box');

            if (self.hasClass('open')) {
                self.removeClass('open');
                couponBox.hide();
            } else {
                self.addClass('open');
                couponBox.show();
            }
        });
    }

    // 现金券
    function coupon() {
        var box = $('.coupon'),
            form = box.find('.form'),
            selected = box.find('.selected'),
            couponInput = box.find('input[name=coupon]'),
            url = box.attr('data-url');

        function validateCoupon(input) {
            var inputVal = $.trim(input.val());

            if (inputVal === '请输入或选择现金券' || inputVal.length < 1) {
                Ejs.tip(input, 'cILL', '请输入现金券号码', 34, 0, 1800);
            } else {

                if (inputVal.length < 6) {
                    Ejs.tip(input, 'cILL', '现金券号码不正确', 34, 0, 1800);
                    return false;
                }

                if (!/^[A-Za-z0-9]+$/.test(inputVal)) {
                    Ejs.tip(input, 'cILL', '现金券号码格式不对', 34, 0, 1800);
                    return false;
                }

                return true;
            }
        }

        function doAction(codeVal, type) {
            $.ajax({
                type: 'GET',
                url: url,
                async: false,
                data: {
                    code: codeVal,
                    integral: valueCollection.integral || 0
                },
                dataType: 'json',
                success: function (result) {
                    if (result.success) {
                        $('#useCoupon').val('true');
                        valueCollection.coupon = codeVal;
                        $('#coupon-price').text(result.data.couponPrice);
                        totalPriceEle.text(result.data.orderTotalPrice);

                        if (type && type === 'form') { // 如果是手动输入的优惠券
                            form.hide();
                            selected.show().find('.coupon-text').text('使用优惠码，共优惠' + result.data.couponPrice + '元');
                            selected.find('.form-radio').click();
                        }

                    } else {
                        Ejs.tip(couponInput, 'cILL', result.msg, 34, 0, 1800);
                    }
                }
            });
        }

        // 取消现金券
        function cancelCoupon() {
            var integralPriceEle = $('#integral-price'),
                integralPriceText = integralPriceEle.text(),
                integralPrice,
                lastPrice;

            valueCollection.coupon = null;
            $('#coupon-price').text('0.00');
            $('#useCoupon').val('false');

            if (integralPriceText === '0.00') {
                totalPriceEle.text(totalPrice.toFixed(2));
                return;
            }

            integralPrice = parseInt(integralPriceText, 0);
            lastPrice = totalPrice - integralPrice;
            totalPriceEle.text(lastPrice.toFixed(2));
        }

        // 选择优惠券
        box.on('click', '.form-radio', function () {
            var self = $(this),
                val = self.attr('val');

            if (radioEvent(self) && val) {
                doAction(val);
                couponInput[0].disabled = true;
            } else {
                if (!self.hasClass('coupon-text')) {
                    couponInput[0].disabled = false;
                    cancelCoupon();
                }
            }

        });

        // 手动输入优惠券
        box.on('click', '.btn-save', function () {
            var input = box.find('input[name=coupon]'),
                val;

            if (validateCoupon(input)) {
                val = input.val();
                doAction(val, 'form');
            }

        });


        // 取消优惠券
        box.on('click', '.btn-cancel', function () {

            box.find('input[name=coupon]').val('')[0].disabled = true;
            form.show();
            selected.hide();
            cancelCoupon();

        });

        // 展开或收起现金券
        box.on('click', 'h4', function () {
            var self = $(this),
                couponBox = $('.coupon-box');

            if (self.hasClass('open')) {
                self.removeClass('open');
                couponBox.hide();
            } else {
                self.addClass('open');
                couponBox.show();
            }
        });

    }

    // 验证订单表单
    function validateOrderForm() {
        var formData;
        // 收货地址
        if (!$('#addressId').val()) {

            if ($('#address_list').children('li').length.length > 0) {
                new Ejs.Dialog({
                    title: '收货地址为空',
                    type: Ejs.Dialog.type.ERROR,
                    info: '收货地址不能为空！',
                    buttons: [
                        {
                            buttonText: '返回选择',
                            onClick: function () {
                                $('html, body').animate({
                                    scrollTop: $('#addressPosition').offset().top
                                }, 400);
                            }
                        }
                    ]
                });
            } else {
                new Ejs.Dialog({
                    title: '提示信息',
                    type: Ejs.Dialog.type.ERROR,
                    info: '请添加一个新的收货地址！',
                    buttons: [
                        {
                            buttonText: '返回添加',
                            onClick: function () {
                                $('html, body').animate({
                                    scrollTop: $('#addressPosition').offset().top
                                }, 400);
                            }
                        }
                    ]
                });
            }
            return false;
        }

        formData = {
            payBank: valueCollection.payBank || 'Alipay',
            invoice: valueCollection.isInvoice || 'false',
            invoiceType: valueCollection.invoiceType || '普通发票',
            invoiceTitle: valueCollection.invoiceTitle || 'individual',
            invoiceContent: valueCollection.invoiceContent || '办公用品',
            companyName: valueCollection.companyName
        };

        // 发票
        if (formData.invoice === 'true' && formData.invoiceTitle === 'company') {
            if (!formData.companyName) {
                new Ejs.Dialog({
                    title: '发票单位名称为空',
                    type: Ejs.Dialog.type.ERROR,
                    info: '发票类型为单位时，单位名称不能为空！',
                    buttons: [
                        {
                            buttonText: '返回修改',
                            onClick: function () {
                                $('html, body').animate({
                                    scrollTop: $('#invoicePosition').offset().top
                                }, 200);
                            }
                        }
                    ]
                });
                return false;
            }
        }

        // 是否是从购物车下单， 如果不是则是从积分商城过来
        if ($('#fromCart').val() === 'false') {
            formData.source = 'currency';
            formData.skuId = $('#skuTempId').val();
        } else {
            formData.source = 'cart';
        }

        $('#payBank').val(formData.payBank);
        $('#isInvoice').val(formData.invoice);

        if (formData.invoice === 'true') {
            $('#invoiceType').val(formData.invoiceType);
            $('#invoiceTitle').val(formData.invoiceTitle);
            $('#invoiceContent').val(formData.invoiceContent);
        }

        $('#skuId').val(formData.skuId);
        //$('#number').val(formData.number);
        $('#companyName').val(formData.companyName);
        $('#totalPrice').val(formData.totalPrice);
        $('#source').val(formData.source);
        $('#messageInfo').val($('#remark').val());
        $('#code').val(valueCollection.coupon);
        $('#integral').val(valueCollection.integral);

        return true;

    }

    // 提交订单
    $('#submitLink').on('click', function (E) {
        E.preventDefault();
        if (validateOrderForm()) {
            $(this).attr('disabled', true);
            $('#orderSubmit').attr('action', $('#fromCart').attr('submitUrl')).submit();
        }
    });

    payment();
    invoice();
    integral();
    coupon();

}(jQuery, Ejs));
