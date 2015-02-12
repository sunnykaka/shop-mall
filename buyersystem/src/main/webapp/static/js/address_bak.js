/**
 * 收货地址管理
 * User: Hualei Du
 * Date: 13-9-16
 * Time: 下午2:33
 */
var Ejs = window.Ejs || {};

Ejs.AddressManager = function () {
    "use strict";
    this.page = 'address'; // 默认 'address' 可选 'order'
    this.list = $('#address_list');
    this.newAddress = $('#new_address>.new');
    this.newAddressFormId = '#add_my_new_address';
    this.newAddressBtn = $('#add_new_address');
};

Ejs.AddressManager.prototype = {
    initialise: function () {
        "use strict";
        this.regListItemEvent();
        this.regAddEvent();
    },
    // 注册地址列表item事件(修改，删除，选中, 展开全部地址)
    regListItemEvent: function () {
        "use strict";
        var parent = this,
            list = parent.list;

        // 修改地址事件
        list.delegate('.modify', 'click', function (E) {
            E.preventDefault();
            var listItem = $(this).parents('li'),
                form = listItem.find('form');

            if (form.length > 0) {
                parent.hideModify(form);
            } else {
                parent.showModify(listItem);
                parent.hideAdd();
            }
        });

        // 删除地址事件
        list.delegate('.delete', 'click', function (E) {
            E.preventDefault();
            var self = $(this),
                item = self.parents('li'),
                id = item.attr('data-id');
            new Ejs.Dialog({
                type: Ejs.Dialog.type.WARN,
                title: '您确定要删除这条收货地址吗？',
                isModal: true,
                opacity: 0,
                isLeftHand: false,
                titleStyle: {
                    fontWeight: 'normal',
                    paddingLeft: 0,
                    paddingTop: 5,
                    width: 200,
                    float: 'right',
                    textAlign: 'left'
                },
                titleWrapperStyle: {
                    paddingTop: 20
                },
                info: '',
                infoStyle: {
                    height: 10,
                    overflow: 'hidden'
                },
                isConfirm: true,
                eventObj: E,
                afterClose: function (v) {
                    if (v) {
                        parent.deleteAddressItem(item, id);
                    }
                }
            });
        });


        if (this.page === 'address') {

            // 显示设置默认地址按钮事件
            list.on('mouseenter', 'li', function () {
                var self = $(this);
                if (self.find('.default').length < 1) {
                    if (self.find('.setDefault').length < 1) {
                        self.find('.modify').after('<a href="#" class="fr setDefault">设为默认地址</a>');
                    } else {
                        self.find('.setDefault').show();
                    }
                }
            });

            // 隐藏设置默认地址按钮事件
            list.on('mouseleave', 'li', function () {
                var self = $(this);
                self.find('.setDefault').hide();
            });

            // 设置默认地址事件
            list.on('click', '.setDefault', function (E) {
                E.preventDefault();
                var item = $(this).parents('li'),
                    id = item.attr('data-id');
                parent.setDefaultAddress(item, id);
            });
        } else {
            // 选中地址事件
            list.on('click', 'p>span', function (e) {
                list.find('>li').removeClass('current');
                var li = $(this).parents('li');
                li.addClass('current');
                $("#addressId").val(li.attr("data-id"));
            });

            // 展示全部地址事件
            $("#show_all_address").click(function (e) {
                e.preventDefault();
                var self = $(this);
                parent.showAllAddress(self);
            });

        }
    },
    // 展开修改模块
    showModify: function (listItem) {
        "use strict";
        var parent = this,
            id = listItem.attr('data-id');

        $.ajax({
            type: 'GET',
            url: EJS.AddressBaseUrl + id,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    var addressData = data.data.address,
                        areaArr,
                        form,
                        formId = 'modify_address_' + id,
                        html = '<div class="new">' +
                            '<i class="arrow-up"></i>' +
                            '<form id="' + formId + '">' +
                            '   <ul>' +
                            '       <li class="area">' +
                            '           <label><b>*</b>选择地区：</label>' +
                            '           <select name="province" class="select"></select>' +
                            '           <select name="city" class="select"></select>' +
                            '           <select name="districts" class="select"></select>' +
                            '       </li>' +
                            '       <li>' +
                            '           <label><b>*</b>街道地址：</label>' +
                            '           <input type="text" name="location" class="text"  style="width:372px;" value="' + addressData.location + '">' +
                            '           </li>' +
                            '       <li>' +
                            '           <label><b>*</b>邮政编码：</label>' +
                            '           <input type="text" name="zipCode" class="text" value="' + addressData.zipCode + '">' +
                            '       </li>' +
                            '       <li>' +
                            '           <label><b>*</b>收 货 人：</label>' +
                            '           <input type="text" name="name" class="text" value="' + addressData.name + '">' +
                            '       </li>' +
                            '       <li>' +
                            '           <label><b>*</b>联系电话：</label>' +
                            '           <input type="text" name="mobile" class="text" value="' + addressData.mobile + '">' +
                            '       </li>' +
                            '       <li class="btn_w">' +
                            '           <button type="submit" class="e-btn btn-default">确认收货地址</button>' +
                            '           <span class="add_address_error"></span>' +
                            '       </li>' +
                            '   </ul>' +
                            '   <input type="hidden" name="id" value="' + addressData.id + '" />' +
                            '</form">' +
                            '</div">';
                    listItem.append(html);
                    areaArr = addressData.province.split(",");
                    form = $('#' + formId);
                    form.find('.area').selectArea({
                        province: areaArr[0] || null,
                        city: areaArr[1] || null,
                        districts: areaArr[2] || null
                    });
                    
                    listItem.find('.new').stop().slideDown(400, function () {
                        parent.regFormItemsEvent('update', '#' + formId);
                    });

                    listItem.siblings('li').find('.new').slideUp(200, function () {
                        $(this).remove();
                    });
                }
            }
        });

    },
    // 隐藏修改模块
    hideModify: function (form) {
        "use strict";
        var box = form.parent('.new');
        box.stop().slideUp(200, function () {
            form.remove(); // IE下不知道为何，移除了box，form竟然还在
            box.remove();
        });
    },
    // 删除一个地址
    deleteAddressItem: function (item, id) {
        "use strict";
        var parent = this;
        $.ajax({
            type: 'POST',
            url: EJS.AddressBaseUrl + 'delete',
            data: {
                id: id
            },
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    item.hide(400, function () {
                        item.remove();
                        if (parent.list.find('li').length < 1) {
                            var html = '<li id="haveNoAddress" style="text-align: center">' +
                                '<br>' +
                                '<span style="color: #808080">您还没有添加收货地址！</span> <p></p>' +
                                '</li>';
                            parent.list.html(html);
                        }
                    });
                }
            }
        });
    },
    // 展开全部地址
    showAllAddress: function (self) {
        "use strict";
        var list = this.list;
        $.ajax({
            type: "GET",
            async: false,
            url: EJS.AddressBaseUrl + 'rest',
            success: function (data) {
                list.append(data);
                self.remove();
            }
        });
    },
    // 设置为默认地址
    setDefaultAddress: function (item, id) {
        "use strict";
        $.ajax({
            type: "POST",
            url: EJS.AddressBaseUrl + 'setDefault',
            data: {
                id: id
            },
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    item.parent().find('li').removeClass("cur");
                    item.parent().find('.default').remove();
                    item.addClass("cur");
                    item.find('.modify').after('<a class="default">默认地址</a>');
                    item.find('.setDefault').remove();
                }
            }
        });
    },
    // 获取表单items
    getFormItems: function (formId) {
        "use strict";
        var form = $(formId);
        return {
            element: form,
            name: form.find("input[name='name']"),
            location: form.find("input[name='location']"),
            zipCode: form.find("input[name='zipCode']"),
            mobile: form.find("input[name='mobile']"),
            province: form.find('select[name=province]'),
            city: form.find('select[name=city]'),
            districts: form.find('select[name=districts]'),
            message: $('<span style="color: #f00;margin:0 0 0 10px"></span>')
        };
    },
    // 注册表单item事件(blur,submit)
    regFormItemsEvent: function (type, formId) {
        "use strict";
        var parent = this,
            formItems = this.getFormItems(formId),
            form = formItems.element,
            name = formItems.name,
            location = formItems.location,
            zipCode = formItems.zipCode,
            mobile = formItems.mobile,
            message = formItems.message,
            regexZipCode = /^([0-9]{6})$/,
            regexMobile = /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/;

        name.blur(function () {
            if (!name.val() || name.val().length < 2) {
                name.siblings("span").remove();
                message.text("请输入收件人的姓名,不能少于2个字");
                name.after(message.clone());
            } else {
                name.siblings("span").remove();
            }
        });

        location.blur(function () {
            if (!location.val()) {
                location.siblings("span").remove();
                message.text("请输入收件人的详细地址");
                location.after(message.clone());
            } else {
                location.siblings("span").remove();
            }
        });

        zipCode.blur(function () {
            if (!zipCode.val() || !regexZipCode.test(zipCode.val())) {
                zipCode.siblings("span").remove();
                message.text("请输入六位数字的邮政编码");
                zipCode.after(message.clone());
            } else {
                zipCode.siblings("span").remove();
            }
        });

        mobile.blur(function () {
            if (!mobile.val() || !regexMobile.test(mobile.val())) {
                mobile.siblings("span").remove();
                message.text("请输入正确的电话/手机号码");
                mobile.after(message.clone());
            } else {
                mobile.siblings("span").remove();
            }
        });

        form.on('click', 'button[type=submit]', function (E) {
            E.preventDefault();
            parent.saveForm(type, formId);
        });

    },
    // 验证表单
    validateForm: function (formId) {
        "use strict";
        var formItems = this.getFormItems(formId),
            name = formItems.name,
            location = formItems.location,
            zipCode = formItems.zipCode,
            mobile = formItems.mobile,
            province = formItems.province,
            city = formItems.city,
            districts = formItems.districts,
            message = formItems.message,
            regexZip = /^([0-9]{6})$/,
            regexMobile = /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/;


        if (province.val() === '0') {
            province.siblings("span").remove();
            message.text("请选择省");
            districts.after(message.clone());
            return false;
        }
        if (city.val() === '0') {
            city.siblings("span").remove();
            message.text("请选择市");
            districts.after(message.clone());
            return false;
        }

        if (districts.find("option").length > 1) {
            if (districts.val() === '0') {
                districts.siblings("span").remove();
                message.text("请选择区县");
                districts.after(message.clone());
                return false;
            }
        } else {
            districts.find("option").eq(0).attr("value", "");
        }
        districts.siblings("span").remove();

        if (!location.val()) {
            location.siblings("span").remove();
            message.text("请输入收件人的详细地址");
            location.after(message.clone());
            location.focus();
            return false;
        }
        location.siblings("span").remove();

        if (!zipCode.val() || !regexZip.test(zipCode.val())) {
            zipCode.siblings("span").remove();
            message.text("请输入六位数字的邮政编码");
            zipCode.after(message.clone());
            zipCode.focus();
            return false;
        }
        zipCode.siblings("span").remove();

        if (!name.val() || name.val().length < 2) {
            name.siblings("span").remove();
            message.text("请输入收件人的姓名,不能少于2个字");
            name.after(message.clone());
            name.focus();
            return false;
        }
        name.siblings("span").remove();

        if (!mobile.val() || !regexMobile.test(mobile.val())) {
            mobile.siblings("span").remove();
            message.text("请输入正确的电话/手机号码");
            mobile.after(message.clone());
            mobile.focus();
            return false;
        }
        mobile.siblings("span").remove();

        return true;
    },
    // 保存表单到数据库
    saveForm: function (type, formId) {
        "use strict";
        var parent = this,
            form = $(formId),
            listItem = form.parents('li'),
            formData = form.serialize(),
            url = EJS.AddressBaseUrl + type;

        if (!parent.validateForm(formId)) {
            return;
        }

        $.ajax({
            type: "POST",
            async: false,
            url: url,
            data: formData,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    if (type === 'update') {
                        parent.addToList(form, type, data, listItem);
                        parent.hideModify(form);
                    } else if (type === 'add') {
                        parent.addToList(form, type, data);
                        parent.hideAdd();
                    }
                } else {
                    form.find('.add_address_error').text(data.msg);
                }
            }
        });

    },
    // 追加到列表
    addToList: function (form, type, formData, listItem) {
        "use strict";
        if (typeof formData !== "object") {
            return;
        }

        var parent = this,
            list = eval('(' + formData.data.address + ')'),
            addressId = list.id,
            html = '',
            firstLi;

        html += '<a href="javascript:void(0);" class="fr delete">删除</a>\n' +
            '<a href="javascript:void(0);" class="fr modify">修改</a>\n' +
            '<span class="col1">' + list.name + '</span>\n' +
            '<span class="col2">' + list.province.replace(/\,/g, "") + list.location + '</span>\n' +
            '<span class="col3">' + list.zipCode + '</span>\n' +
            '<span class="col4">' + list.mobile + '</span>';

        if (type === 'update') {
            listItem.find('p').html(html);

            if (parent.page === 'order') {
                listItem.find('p').find('span').eq(0).click();
            }
        } else if (type === 'add') {
            html = '<li data-id="' + addressId + '"><p class="clearfix">' + html + '</p></li>';
            // 如果添加的是第一条，将删除没有记录的提示
            firstLi = parent.list.find('li').eq(0);
            if (firstLi.attr('id') === 'haveNoAddress') {
                firstLi.slideUp(200, function () {
                    firstLi.remove();
                });
            }
            parent.list.append(html);

            if (parent.page === 'order') {
                parent.list.children('li').last().find('span').eq(0).click();
            }
        }

    },
    // 注册展开添加新地址事件
    regAddEvent: function () {
        "use strict";
        var parent = this,
            list = parent.list.find('li');
        this.newAddressBtn.on('click', function (E) {
            E.preventDefault();
            var self = $(this);

            if (parent.newAddress.css('display') !== 'block') {
                self.addClass('open');
                parent.showAdd();
            } else {
                self.removeClass('open');
                parent.hideAdd();
            }
        });

        if (list.length < 1 || (list.length === 1 && list.eq(0).attr('id') === 'haveNoAddress')) {
            this.newAddressBtn.click();
        }
    },
    // 展开添加模块
    showAdd: function () {
        "use strict";
        var parent = this,
            formId = parent.newAddressFormId,
            form = $(formId);

        form.find('ul').children('li').eq(0).addClass('area');

        this.list.children('li').find('.new').slideUp(200, function () {
            $(this).remove();
        });

        this.newAddress.stop().slideDown('400', function () {
            form.find('.area').selectArea();
            parent.regFormItemsEvent('add', formId);
        });
    },
    // 隐藏添加模块
    hideAdd: function () {
        "use strict";
        var formId = this.newAddressFormId,
            form = $(formId);
        form[0].reset();
        form.find('ul').children('li:not([class!=btn_w])').find('span').remove(); // 移除错误提示
        this.newAddress.stop().slideUp(200);
    }
};

function userAddress() {
    $LAB.script(EJS.StaticDomain + '/js/selectArea.js').wait(function () {
        var addressManager = new Ejs.AddressManager();
        addressManager.initialise();
    });
}

