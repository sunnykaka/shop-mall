/**
 * 表单验证
 * Created by HuaLei.Du on 14-3-5.
 */

var Ejs = window.Ejs || {};
Ejs.user = Ejs.user || {};
(function ($) {
    "use strict";
    Ejs.user.FormValidate = function (options, callback) {
        this.items = options.items;
        this.form = options.form;
        this.label = options.label || false; // 默认不对label 处理，如果为true 当焦点到文本框的时候隐藏相关联label
        this.defaultTipsPosition = options.defaultTipsPosition || 'right';
        this.addToValidate = options.addToValidate;
        this.callback = callback;
        this.formValidate = true;
        this.vtypes = {
            Account: {
                Regex: /(1[34578][0-9]{9}$)|(^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$)|(^[A-Za-z0-9_\-\\u4e00-\\u9fa5]+$)/,
                Text: '账号格式不对'
            },
            Username: {
                Regex: /^[A-Za-z0-9_\-\\u4e00-\\u9fa5]+$/,
                Text: '用户名格式不对'
            },
            Email: {
                Regex: /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/,
                Text: '邮箱格式不对'
            },
            Phone: {
                Regex: /(^0?(1[34578][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/,
                Text: '电话号码格式不对'
            },
            Mobile: {
                Regex: /^0?(1[34578][0-9]{9})$/,
                Text: '手机号码格式不对'
            }
        };
    };

    Ejs.user.FormValidate.prototype.init = function () {
        var root = this;
        root.eachValidate(true); // 绑定事件
        root.submit();
    };

    Ejs.user.FormValidate.prototype.isPlaceholder = ('placeholder' in document.createElement('input'));

    // 验证数据无误后提交
    Ejs.user.FormValidate.prototype.submit = function () {
        var root = this,
            isValidate = true;
        root.form.submit(function () {
            isValidate = root.isValidate();

            if (!isValidate) {
                return false;
            }

            if (typeof root.callback === 'function') {
                root.callback();
                return false;
            }

            return true;
        });
        return this;
    };

    Ejs.user.FormValidate.prototype.isValidate = function () {

        this.formValidate = true;
        this.eachValidate(false);

        if (!this.formValidate) {
            return false;
        }

        if (typeof this.addToValidate === 'function') { // 追加验证
            this.formValidate = this.addToValidate();

            if (!this.formValidate) {
                return false;
            }
        }

        return true;
    };

    // 遍历绑定事件或直接验证
    Ejs.user.FormValidate.prototype.eachValidate = function (isBind) {
        var root = this,
            items = this.items,
            i,
            itemEle,
            countItems;

        if (typeof items === 'object') {
            countItems = items.length;
            if (countItems > 0) {
                if (isBind) {
                    for (i = 0; i < countItems; i++) {
                        itemEle = $('#' + items[i].id);
                        itemEle.length && root.bind(items[i]);
                    }
                } else {
                    for (i = 0; i < countItems; i++) {
                        itemEle = $('#' + items[i].id);
                        itemEle.length && root.validate(itemEle, items[i]);
                    }
                }
            }
        }
    };

    // 验证是否合法
    Ejs.user.FormValidate.prototype.validate = function (ele, item) {
        var errorMsg = $('#errormsg-' + item.id),
            itemValidate = true,
            value = ele.val(),
            valueLength = value ? value.replace(/[^\x00-\xff]/g, "rr").length : 0,
            eleType = ele.attr('type'),
            isAddClass = (eleType !== 'radio' && eleType !== 'checkbox');

        //ele.attr('validate', true);

        // 是否允许为空
        if (itemValidate && !item.allowBlank) {
            if (!value || /^\s*$/.test(value) || (item.blankText && !this.isPlaceholder && value == item.blankText)) {
                errorMsg.text(item.allowBlankText || '此处不能留空。');
                itemValidate = false;
            }
        }

        // 是否小于设置的最小字符数
        if (itemValidate && item.minLength && valueLength && valueLength < item.minLength) {
            errorMsg.text('此项输入的字符数不应少于 ' + item.minLength);
            itemValidate = false;
        }

        // 是否超过设置的最大字符数
        if (itemValidate && item.maxLength && valueLength && valueLength > item.maxLength) {
            errorMsg.text('此项输入的字符数不应大于 ' + item.maxLength);
            itemValidate = false;
        }

        // 自定义验证器
        if (itemValidate && item.validator && valueLength && item.validator(value) !== true) {
            errorMsg.text(item.validator(value));
            itemValidate = false;
        }

        // 根据预定义的规则进行验证
        if (itemValidate && item.vtype && valueLength && this.vtypes[item.vtype] && !this.vtypes[item.vtype].Regex.test(value)) {
            errorMsg.text(this.vtypes[item.vtype].Text);
            itemValidate = false;
        }

        // 同步ajax验证(比如验证用户名是否存在)
        if (itemValidate && item.validatorSynchro && valueLength) {
            item.validatorSynchro(ele.val(), function (success, msg) {
                if (!success) {
                    itemValidate = false;
                    errorMsg.text(msg);
                }
            });
        }

        if (!itemValidate) {

            if (isAddClass) {
                ele.addClass('form-text-error');
            }

            this.formValidate = false;
        } else {

            if (isAddClass) {
                ele.addClass('form-text-error');
            }

            ele.removeClass('form-text-error');
            errorMsg.text('');
        }

    };


    // 给元素绑定事件
    Ejs.user.FormValidate.prototype.bind = function (item) {
        var root = this,
            ele = $('#' + item.id);

        if (item.id) {

            if (!item.allowBlank || item.minLength || item.maxLength || item.validator || item.vtype) {
                root.form.on('blur', '#' + item.id, function () {
                    root.removeTips(item);
                    root.validate($(this), item);

                    if (!root.isPlaceholder) {
                        /^\s*$/.test(ele.val()) && ele.val(item.blankText).addClass('form-text-blank');
                    }

                    if (root.label) {
                        /^\s*$/.test(ele.val()) && $('label[for="' + item.id + '"]').show();
                    }

                });
            }


            if (item.blankText) {

                if (root.isPlaceholder) {
                    ele.attr('placeholder', item.blankText);
                } else {
                    ele.val(item.blankText).addClass('form-text-blank');
                }

            }

            root.form.on('focus', '#' + item.id, function () {

                if (item.tipsText) {
                    root.tips($(this), item);
                }

                if (item.blankText && !root.isPlaceholder) {
                    ele.val() == item.blankText && ele.val('').removeClass('form-text-blank');
                }

                if (root.label) {
                    $('label[for="' + item.id + '"]').hide();
                }

            });

            if (root.label) {
                root.form.on('click', 'label[for=' + item.id + ']', function () {
                    $(this).hide();
                });
            }


        }
    };

    // 输入提示
    Ejs.user.FormValidate.prototype.tips = function (ele, item) {

        var id = item.id,
            w = ele.outerWidth(),
            h = ele.outerHeight(),
            x = ele.offset().left,
            y = ele.offset().top,
            left = w + x + 5,
            top = y,
            tip = $('#form-input-tip-' + id);


        if (tip.length < 1) {
            tip = $('<div/>').addClass('form-input-tip').attr('id', 'form-input-tip-' + id);
            $('body').append(tip);
        }

        tip.html(item.tipsText);

        if (this.defaultTipsPosition === 'left') {
            left = x - 5 - parseInt((this.defaultTipsWidth || tip.outerWidth()));
        }

        if (this.defaultTipsPosition === 'bottom') {
            left = x;
            top = y + h;
        }

        tip.css({
            left: left,
            top: top
        }).show();

    };

    // 移除输入提示
    Ejs.user.FormValidate.prototype.removeTips = function (item) {
        $('#form-input-tip-' + item.id).remove();
    };

}(jQuery));