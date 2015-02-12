/**
 * 订单管理
 * User: Hualei Du
 * Date: 13-9-23
 * Time: 下午1:27
 */

$LAB.script(EJS.StaticDomain + '/js/selectArea.js')
    .script(EJS.StaticDomain + '/js/address.js').wait(function () {
        var orderAddress = new Ejs.AddressManager();
        orderAddress.page = 'order';
        orderAddress.initialise();
    });

Ejs.OrderManager = function () {
    this.valueCollection = {};
    this.couponList = EJS.HomeUrl + '/order/coupon/list';
};

Ejs.OrderManager.prototype = {
    initialise: function () {
        this.payEvent();
        this.couponEvent();
        this.integralEvent();
        this.invoiceEvent();
        this.submitOrder();
    },
    // 提示信息
    tips: function (obj, className, msg) {
        if (obj.find('.' + className).length < 1) {
            obj.append('<span class="info"></span>');
        }
        if (!!msg == false) {
            obj.find('.' + className).eq(0).hide();
            return;
        }
        obj.find('.' + className).eq(0).show().html(msg);
    },
    // 转换为数字并保留两位小数
    changeTwoDecimal: function (x) {
        var f_x = parseFloat(x);
        if (isNaN(f_x)) {
            return 0;
        }
        f_x = Math.round(f_x * 100) / 100;
        var s_x = f_x.toString();
        var pos_decimal = s_x.indexOf('.');
        if (pos_decimal < 0) {
            pos_decimal = s_x.length;
            s_x += '.';
        }
        while (s_x.length <= pos_decimal + 2) {
            s_x += '0';
        }
        return s_x;
    },
    // 支付方式
    payEvent: function () {
        var parent = this,
            playTypes = $('#pay ul>li');

        parent.valueCollection.payBank = playTypes.eq(0).attr('paybank');
        playTypes.each(function () {
            var self = $(this);
            self.click(function () {
                playTypes.removeClass('cur');
                self.addClass('cur');
                parent.valueCollection.payBank = self.attr('paybank');
            })
                .hover(function () {
                    self.addClass('hover');
                }, function () {
                    self.removeClass('hover');
                });

        });
    },
    isPlaceholder: function () {
        var input = document.createElement('input');
        return 'placeholder' in input;
    },
    // 现金券
    couponEvent: function () {
        var parent = this,
            total = $('#totalPrice1'), // 总金额
            actualPrice = $('#totalPrice2'), // 实付的金额
            couponBox = $('.promo_card'),
            couponBtn = couponBox.find('.use_promo_card'),
            input = couponBox.find('.text'),
            submitBtn = couponBox.find('.submit'),
            couponListBox = $('#select_coupon'),
            couponListBtn = $('#select_coupon_btn'),
            inputTip = '请输入或选择现金券';

        if (couponBox.length < 1) {
            return;
        }

        if (parent.isPlaceholder()) {
            input.val('');
            input.attr('placeholder', inputTip);
        } else {
            input.val(inputTip);
            input.focus(function () {
                var self = $(this);
                if (self.val() === inputTip) {
                    self.val('');
                }
            });
        }

        $('#useCoupon').val('false');
        $('#code').val('');
        $('#integral').val(0);

        // 显示隐藏现金券
        function toggleCouponBox(self) {
            if (self.hasClass('use_promo_card_down')) {
                self.removeClass('use_promo_card_down');
                self.next('.promo_card_box').stop().slideUp(100).find('.form').hide();
            } else {
                self.addClass('use_promo_card_down');
                self.next('.promo_card_box').stop().slideDown(300).find('.form').show();
            }
        }

        // 显示隐藏现金券事件
        couponBtn.click(function () {
            toggleCouponBox($(this));
        });

        input.focus(function () {
            var self = $(this);
            if (self.val() === '请输入或选择现金券') {
                self.val('');
            }
        });

        // 展示现金券列表
        function showCouponList() {
            var html = '';
            $.ajax({
                type: 'GET',
                url: parent.couponList,
                dataType: 'json',
                success: function (data) {
                    html = '<h5>请选择要使用的现金券<span class="close">X</span></h5>';
                    if (data.success) {
                        var list = data.data.couponList,
                            i;

                        html += '<ul>';
                        if (list.length < 1) {
                            html += '<li style="text-align:center">无可用现金券或没有达到使用条件</li>';
                        } else {
                            for (i = 0; i < list.length; i++) {
                                html += '<li><label data-value="' + list[i].code + '">' + list[i].code +
                                    '<span>(￥' + list[i].money + ')</span></label></li>';
                            }
                        }
                        html += '<li class="close"><a href="javascript:void(0);">不使用现金券</a></li></ul>';
                    } else {
                        html += '<ul><li style="text-align:center">无可用现金券或没有达到使用条件</li>' +
                            '<li class="close"><a href="javascript:void(0);">不使用现金券</a></li></ul>';
                    }
                    couponListBox.html(html).stop().show(600);
                },
                error: function () {
                    html = '<h5>出错了<span class="close">X</span></h5>';
                    html += '<ul><li>服务器出错</li></ul>';
                    couponListBox.html(html).stop().show(600);
                }
            });
        }

        // 关闭现金券列表
        function hideCouponList() {
            couponListBox.stop().hide(200);
        }

        couponListBtn.click(function () {
            showCouponList();
        });

        // 选择现金券事件
        couponListBox.on('click', '.close,label', function (e) {
            var self = $(this);
            if (self[0].nodeName.toLowerCase() === 'label') {
                couponListBox.find('label').removeClass('cur');
                self.addClass('cur');
                input.val(self.attr('data-value'));
            }
            hideCouponList();
        });

        // 验证现金券
        function validateCoupon() {
            var inputVal = $.trim(input.val());
            if (inputVal === '请输入或选择现金券' || inputVal.length < 1) {
                parent.tips(couponBox.find('.form'), 'info', '请输入现金券号码');
            } else {
                if (inputVal.length < 6) {
                    parent.tips(couponBox.find('.form'), 'info', '现金券号码不正确');
                    return false;
                }
                if (!/^[A-Za-z0-9]+$/.test(inputVal)) {
                    parent.tips(couponBox.find('.form'), 'info', '现金券号码格式不对');
                    return false;
                }
                var isCode = false;
                $('.selected_code').each(function () {
                    if (parseInt($(this).text()) == inputVal) {
                        isCode = true;
                    }
                });
                if (isCode) {
                    parent.tips(couponBox.find('.form'), 'info', '现金券已使用');
                    return false;
                }
                return true;
            }
            return false;
        }

        // 使用现金券
        function useCoupon() {
            var inputVal = $.trim(input.val()),
                url = EJS.HomeUrl + '/order/integral/check',
                integralVal = $('#integral').val();

            if (!integralVal) {
                integralVal = 0;
            }

            $.ajax({
                type: 'GET',
                url: url,
                async: false,
                data: {
                    code: inputVal,
                    integral: integralVal
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        parent.tips(couponBox.find('.form'), 'info', false);
                        couponBtn.hide();
                        couponBox.find('.form').hide();
                        couponBox.find('.selected').show();
                        couponBox.find('.selected').find('.promo_card_text')
                            .html('已使用现金券<span class="selected_code">' + inputVal + '</span>');
                        $('#useCoupon').val('true');
                        $('#code').val(inputVal);
                        $('#couponPrice').text(data.data.couponPrice);
                        $('.promo_card_price').eq(0).text('¥-' + data.data.couponPrice);
                        actualPrice.text(data.data.orderTotalPrice);
                    } else {
                        parent.tips(couponBox.find('.form'), 'info', data.msg);
                    }
                }
            });
        }

        // 使用现金券事件
        submitBtn.click(function () {
            if (actualPrice.text() === '0.00') {
                parent.tips(couponBox.find('.form'), 'info', '应付总金额为0.00元,无需再使用现金卷');
                return;
            }
            if (validateCoupon()) {
                useCoupon();
            }
        });

        // 取消现金券
        function cancelCoupon() {
            couponBtn.show().removeClass('use_promo_card_down');
            couponBox.find('.selected').find('.promo_card_text').html('');
            couponBox.find('.promo_card_box').stop().hide().find('.selected').hide();
            $('#useCoupon').val('false');
            $('#code').val('');

            var couponPrice = $('#couponPrice'),
                countPrice = parent.changeTwoDecimal(parseFloat(total.text()) - parseFloat($('#integralPrice').text()));

            if (parseFloat(countPrice) < 0) {
                countPrice = '0.00';
            }

            actualPrice.text(countPrice);
            couponPrice.text('0.00');
            couponBox.find('.promo_card_price').text('');
        }

        // 取消现金券事件
        couponBox.on('click', '.cancel', function () {
            cancelCoupon();
        });

    },
    // 积分
    integralEvent: function () {
        var parent = this,
            total = $('#totalPrice1'), // 总金额
            actualPrice = $('#totalPrice2'), // 实付的金额
            integralBox = $('.integral'),
            integralBtn = $('.use_integral'),
            input = integralBox.find('.text'),
            submitBtn = integralBox.find('.submit'),
            inputTip = '100积分可抵1元';

        if (integralBox.length < 1) {
            return;
        }

        if (parent.isPlaceholder()) {
            input.attr('placeholder', inputTip);
        } else {
            input.val(inputTip);
            input.focus(function () {
                var self = $(this);
                if (self.val() === inputTip) {
                    self.val('');
                }
            });
        }

        // 显示隐藏积分
        function toggleIntegralBox(self) {
            if (self.hasClass('use_integral_down')) {
                self.removeClass('use_integral_down');
                self.next('.integral_box').stop().slideUp(100).find('.form').hide();
            } else {
                self.addClass('use_integral_down');
                self.next('.integral_box').stop().slideDown(300).find('.form').show();
            }
        }

        // 显示隐藏积分事件
        integralBtn.click(function () {
            var self = $(this);
            toggleIntegralBox(self);
        });

        // 验证积分
        function validateIntegral() {
            var inputVal = $.trim(input.val()),
                maxVal = parseInt(input.attr('max-value'));

            if (inputVal.length < 1 || inputVal === inputTip) {
                parent.tips(integralBox.find('.form'), 'info', '请输入要使用的积分');
                return false;
            }
            if (!/^[1-9][0-9]*$/.test(inputVal)) {
                parent.tips(integralBox.find('.form'), 'info', '积分输入错误');
                return false;
            }
            if (parseInt(inputVal) > maxVal) {
                parent.tips(integralBox.find('.form'), 'info', '本次最多可用' + maxVal + '积分');
                return false;
            }
            parent.tips(integralBox.find('.form'), 'info');
            return true;
        }

        // 使用积分
        function useIntegral() {
            var inputVal = $.trim(input.val()),
                codeVal = $('#code').val(),
                url = EJS.HomeUrl + '/order/integral/check';

            if (!!codeVal == false) {
                codeVal = '';
            }

            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    integral: inputVal,
                    code: codeVal
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        var jsonIntegralPrice = data.data.integralPrice,
                            jsonOrderTotalPrice = data.data.orderTotalPrice;
                        integralBtn.hide();
                        integralBox.find('.form').hide();
                        integralBox.find('.selected').show();
                        integralBox.find('.selected').find('.integral_text').html('已使用<span class="selected_code">' +
                            inputVal + '</span>积分');
                        integralBox.find('.integral_price').eq(0).text('¥-' + jsonIntegralPrice);
                        $('#integralPrice').text(jsonIntegralPrice);
                        $('#integral').val(inputVal);
                        actualPrice.text(jsonOrderTotalPrice);
                    } else {
                        parent.tips(integralBox.find('.form'), 'info', data.msg);
                    }
                }
            });

        }

        // 使用积分事件
        submitBtn.click(function () {
            if (actualPrice.text() === '0.00') {
                parent.tips(integralBox.find('.form'), 'info', '应付总金额为0.00元,无需再使用积分');
                return;
            }

            if (validateIntegral()) {
                useIntegral();
            }

        });

        // 取消积分
        function cancelIntegral() {
            integralBtn.show().removeClass('use_integral_down');
            integralBox.find('.selected').find('.integral_text').html('');
            integralBox.find('.integral_box').stop().hide().find('.selected').hide();
            var integralPrice = $('#integralPrice'),
                countPrice = parent.changeTwoDecimal(parseFloat(total.text()) - parseFloat($('#couponPrice').text()));

            if (parseFloat(countPrice) < 0) {
                countPrice = '0.00';
            }

            actualPrice.text(countPrice);
            integralPrice.text('0.00');
            integralBox.find('.integral_price').text('');
            $('#integral').val(0);
        }

        // 取消积分事件
        integralBox.on('click', '.cancel', function () {
            cancelIntegral();
        });

    },
    // 发票信息
    invoiceEvent: function () {
        var parent = this,
            invoiceInfo = $('#invoiceInfo'),
            invoiceRows = invoiceInfo.children('li'),
            invoiceTitleRow = invoiceRows.eq(2),
            invoiceCompany = $('#invoiceCompany'),
            invoiceCompanyBaseUrl = invoiceCompany.attr('baseUrl');

        invoiceRows.eq(0).siblings('li').hide();

        // 选择发票
        function selectInvoice(self) {
            self.addClass('cur').siblings('span').removeClass('cur');
            var isInvoiceElement = $('#isInvoice');
            if (self.attr('invoice') === 'false') {
                isInvoiceElement.val('false');
                invoiceRows.eq(0).siblings('li').hide();
                return;
            }
            isInvoiceElement.val('true');
            invoiceRows.eq(0).siblings('li').show();
            parent.valueCollection.isInvoice = $('#isInvoice').val();
            parent.valueCollection.invoiceType = invoiceRows.eq(1).find('span').eq(0).attr("invoiceType");
            parent.valueCollection.invoiceTitle = invoiceRows.eq(2).find('span').eq(0).attr("invoiceTitle");
            parent.valueCollection.invoiceContent = invoiceRows.eq(3).find('span').eq(0).attr("invoiceContent");
        }

        // 选择发票事件
        invoiceRows.eq(0).on('click', 'span', function () {
            var self = $(this);
            selectInvoice(self);
        });

        // 获取发票单位列表
        function getCompanyList() {
            return invoiceCompany.children('li');
        }

        // 选择发票抬头
        function selectInvoiceTitle(self) {
            var invoiceTitleVal = self.attr('invoicetitle');
            self.addClass('cur').siblings('span').removeClass('cur');
            parent.valueCollection.invoiceTitle = invoiceTitleVal;
            if (invoiceTitleVal === 'individual') {
                invoiceTitleRow.find('.invoice_wrapper').slideUp();
            } else {
                invoiceTitleRow.find('.invoice_wrapper').slideDown(300, function () {
                    var list = getCompanyList(),
                        firstSpan;

                    if (list.length < 1) {
                        $('#addInvoiceCompany').click();
                    } else {
                        firstSpan = list.eq(0).find('span');
                        firstSpan.addClass('cur');
                        parent.valueCollection.companyName = firstSpan.attr('companyname');
                    }
                });
            }
        }

        // 选择发票抬头事件
        invoiceTitleRow.on('click', 'span[invoicetitle]', function () {
            selectInvoiceTitle($(this));
        });

        // 选中发票单位
        function selectInvoiceCompany(self) {
            var row = self.parent();
            row.parent().find("span").removeClass("cur");
            row.find("span").eq(0).addClass("cur");
            parent.valueCollection.companyName = self.attr("companyname");
        }

        // 选中发票单位事件
        invoiceCompany.on('click', 'span', function () {
            selectInvoiceCompany($(this));
        });

        // 添加发票单位
        function addInvoiceCompany() {
            var box = $("#invoiceCompany"),
                list = getCompanyList();
            newRow = '<li class="clearfix"><input type="text" style="display:block;" value=""/>' +
                '<a href="javascript:;" style="display:block;" class="invoice_company_comfirm btn">确定</a>' +
                '<span style="display: none;"><b></b></span>' +
                '<a href="javascript:;" class="com_reuse_color1 invoice_company_replair btn" style="display:none;">修改</a>' +
                '<a href="javascript:;" class="invoice_company_delete btn" style="display:none;">删除</a></li>';

            box.append(newRow);
            list.last().find('input').focus();
            if (list.length >= 3) {
                $("#addInvoiceCompany").parent().hide();
            }
        }

        // 修改发票单位事件
        invoiceTitleRow.on('click', '#addInvoiceCompany', function (E) {
            E.preventDefault();
            addInvoiceCompany();
        });

        // 修改发票单位
        function modifyInvoiceCompany(self) {
            var row = self.parent('li'),
                list = getCompanyList();
            self.hide();
            row.find('span').hide();
            row.find('a.invoice_company_delete').hide();
            row.find('input').show().focus();
            row.find('a.invoice_company_comfirm').show();

            list.each(function () {
                var self = $(this);
                if (!self.find("input").attr("data-id")) {
                    self.remove();
                    if (getCompanyList().length < 3) {
                        $("#addInvoiceCompany").parent().show();
                    }
                }
            });
        }

        // 修改发票单位事件
        invoiceTitleRow.on('click', '.invoice_company_replair', function (E) {
            E.preventDefault();
            modifyInvoiceCompany($(this));
        });

        // 验证发票单位
        function validateInvoiceCompany(input) {
            if (!$.trim(input.val())) {
                Ejs.tip(input, 'cILL', '请输入单位名称', 1, 200, 1800);
                input.focus();
                return false;
            }
            if ((/<[^>]+>/g).test(input.val())) {
                Ejs.tip(input, "cILL", "单位名称里含有非法字符,请修改！", 1, 200, 1800);
                input.focus();
                return false;
            }
            return true;
        }

        // 保存发票单位
        function saveInvoiceCompany(self) {
            var row = self.parent('li'),
                url = invoiceCompanyBaseUrl,
                input = row.find('input'),
                parameter = {};

            if (validateInvoiceCompany(input)) {
                if (input.attr('data-id')) {
                    url += 'update';
                    parameter.id = input.attr('data-id');
                } else {
                    url += 'add';
                }
                parameter.companyName = input.val();
                $.ajax({
                    type: 'POST',
                    url: url,
                    async: false,
                    data: parameter,
                    dataType: 'JSON',
                    success: function (data) {
                        if (data.success) {
                            row.find('.com_reuse_color1').css('display', 'block');
                            row.find('span').attr('companyName', input.val()).show();
                            row.find('span>b').text(input.val());
                            row.find('input').attr('data-id', data.data.dataId).hide();
                            row.find('.invoice_company_comfirm').hide();
                            row.find('.invoice_company_delete').show();
                            row.find('span').click();
                            var list = getCompanyList();
                            if (list.length === 1) {
                                list.eq(0).find('span').addClass('cur');
                                parent.valueCollection.companyName = parameter.companyName;
                            } else if (list.length >= 3) {
                                $('#addInvoiceCompany').hide();
                            }
                        } else {
                            new Ejs.Dialog({
                                title: '提示信息',
                                type: Ejs.Dialog.type.ERROR,
                                info: data.msg
                            });
                        }
                    }
                });
            }
        }

        // 保存发票单位事件
        invoiceTitleRow.on('click', '.invoice_company_comfirm', function (E) {
            E.preventDefault();
            saveInvoiceCompany($(this));
        });

        // 删除发票单位
        function deleteInvoiceCompany(self) {
            var row = self.parent('li'),
                url = invoiceCompanyBaseUrl,
                input = row.find('input'),
                parameter = {},
                id = input.attr('data-id');

            if (id) {
                url += 'delete';
                parameter.invoiceCompanyId = id;
                $.ajax({
                    type: 'POST',
                    url: url,
                    async: false,
                    data: parameter,
                    dataType: 'JSON',
                    success: function (data) {
                        if (data.success) {
                            var newCompanyBtn = $('#addInvoiceCompany');
                            newCompanyBtn.parent('p').slideDown();
                            row.hide(200, function () {
                                row.remove();
                                var list = getCompanyList();
                                if (list.length === 0) {
                                    newCompanyBtn.click();
                                    $('#invoiceCompany input').eq(0).focus();
                                    parent.valueCollection.companyName = '';
                                } else {
                                    if (row.find('span').hasClass('cur')) {
                                        list.eq(0).find('span').addClass('cur');
                                        parent.valueCollection.companyName = list.eq(0).find('span').attr("companyname");
                                    }
                                }
                            });
                        } else {
                            new Ejs.Dialog({
                                title: '提示信息',
                                type: Ejs.Dialog.type.ERROR,
                                info: data.msg
                            });
                        }
                    }
                });
            }
        }

        // 删除发票单位事件
        invoiceTitleRow.on('click', '.invoice_company_delete', function (E) {
            E.preventDefault();
            deleteInvoiceCompany($(this));
        });

        // 选择发票内容
        function selectInvoiceContent(self) {
            self.addClass('cur').siblings('span').removeClass('cur');
            parent.valueCollection.invoiceContent = self.attr('invoiceContent');
        }

        // 选择发票内容事件
        invoiceRows.eq(3).on('click', 'span', function () {
            var self = $(this);
            selectInvoiceContent(self);
        });

    },
    // 验证订单表单
    validateOrderForm: function () {
        // 收货地址
        if (!$('#addressId').val()) {
            if ($('#address_list>li').length > 0) {
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

        var sku = $('.row>.cell>.skuId'),
            skuId = sku.eq(0).attr('skuId'),
            number = sku.eq(0).attr('number'),
            formData,
            submitInfo = $('#fromCart');

        formData = {
            payBank: this.valueCollection.payBank || 'Alipay',
            invoice: this.valueCollection.isInvoice || 'false',
            invoiceType: this.valueCollection.invoiceType || '普通发票',
            invoiceTitle: this.valueCollection.invoiceTitle || 'individual',
            invoiceContent: this.valueCollection.invoiceContent || '办公用品',
            companyName: this.valueCollection.companyName
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

        // 是否为直接购买
        if (submitInfo.val() === 'false') {
            formData.skuId = skuId;
            formData.number = number;
            formData.totalPrice = $('#totalPrice2').text();
            formData.source = 'detail';
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
        $('#number').val(formData.number);
        $('#companyName').val(formData.companyName);
        $('#totalPrice').val(formData.totalPrice);
        $('#source').val(formData.source);
        $('#messageInfo').val($('#remark').val());
        return true;
    },
    // 提交订单
    submitOrder: function () {
        var parent = this;
        $('#submitLink').on('click', function (E) {
            E.preventDefault();
            if (parent.validateOrderForm()) {
                $("#orderSubmit").attr("action", $('#fromCart').attr("submitUrl")).submit();
            }
        });
    }
};

(function () {
    var orderManager = new Ejs.OrderManager();
    orderManager.initialise();
}());

