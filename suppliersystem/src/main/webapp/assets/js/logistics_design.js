/**
 * User: Hualei Du
 * Date: 13-9-26
 * Time: 下午2:53
 */
var LODOP;
$(function () {
    "use strict";

    // 加载物流公司列表
    function loadLogisticsList() {
        var list = $('#logistics_list');
        $.ajax({
            type: 'GET',
            url: '/logisticDesign/company/list',
            cache: false,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    var dataList = data.data.list,
                        dataListLength = dataList.length,
                        i,
                        html = '';

                    if (dataListLength > 0) {
                        for (i = 0; i < dataListLength; i++) {
                            html += '<li data-id="' + dataList[i].id + '" data-increase="' + dataList[i].law + '">' +
                                '<span>' + dataList[i].deliveryTypeName + '</span>' +
                                '<div class="tools">' +
                                '<a href="#" class="modify_btn" title="修改"><img src="/assets/images/logistics/modify.png" alt="modify"></a>' +
                                '<a href="#" class="delete_btn" title="删除"><img src="/assets/images/logistics/delete.png" alt="delete"></a>' +
                                '<a href="#" class="design_btn" title="物流单面设计"><img src="/assets/images/logistics/design.png" alt="design"></a>' +
                                '</div>' +
                                '</li>';
                        }
                        list.html(html);
                    } else {
                        list.html('<li>列表为空</li>');
                    }
                }
            }
        });
    }

    // 高亮列表当前操作的项
    function highlightItem(item) {
        item.addClass('current').siblings('li').removeClass('current');
    }

    // 获取设计控件
    function getDesignWidget() {
        return getLodop(document.getElementById('LODOP2'), document.getElementById('LODOP_EM2'));
    }

    // 展开添加物流表单
    function showAddLogistics() {
        $('#add_logistics_box').show();
    }

    // 隐藏添加物流表单
    function hideAddLogistics() {
        $('#add_logistics_box').hide();
    }

    // 展开修改物流表单
    function showModifyLogistics(self) {
        var box = $('#modify_logistics_box'),
            name = self.find('span').eq(0).text(),
            id = self.attr('data-id'),
            increase = self.attr('data-increase'),
            modifyIncrease = $('#modify_exp_num'),
            modifyId = $('#modify_id');

        box.find('.info').remove();
        box.find('.success').remove();
        box.find('dd').eq(0).text(name);
        modifyIncrease.val(increase);
        modifyId.val(id);
        box.show();
    }

    // 隐藏修改物流表单
    function hideModifyLogistics() {
        $('#modify_logistics_box').hide();
    }

    // 添加物流
    function addLogistics() {
        var form = $('#add_logistics_form'),
            button = form.find('.button'),
            formInfo = button.next('.success');

        form.find('.info').text('');
        formInfo.text('');

        form.ajaxForm({
            beforeSubmit: validateAddLogisticsForm,
            dataType: 'json',
            success: function (data) {
                if (formInfo.length < 1) {
                    button.after('<span class="success"></span>');
                    formInfo = button.next('.success');
                }
                if (data.success) {
                    form.find('.info').text('');
                    form.find('.success').text('');
                    form.resetForm();
                    formInfo.text('添加成功');
                    loadLogisticsList();
                } else {
                    formInfo.text(data.msg);
                }
            }
        });
    }

    // 修改物流
    function modifyLogistics() {
        var form = $('#modify_logistics_form'),
            button = form.find('.button'),
            formInfo = button.next('.success');

        form.find('.info').text('');
        formInfo.text('');

        form.ajaxForm({
            beforeSubmit: validateModifyLogisticsForm,
            dataType: 'json',
            success: function (data) {
                if (formInfo.length < 1) {
                    button.after('<span class="success"></span>');
                    formInfo = button.next('.success');
                }
                if (data.success) {
                    form.find('.info').text('');
                    form.find('.success').text('');
                    formInfo.removeClass('info');
                    formInfo.text('修改成功');
                    loadLogisticsList();
                } else {
                    formInfo.addClass('info');
                    formInfo.text(data.msg);
                }
            }
        });
    }

    // 保存设计好的物流面单
    function saveDesign() {
        LODOP = getDesignWidget();
        $('#printHtml').val(LODOP.GET_VALUE('ProgramCodes', 0));

        var form = $('#design_form'),
            url = form.attr('action'),
            formData = form.serialize();

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    alert('保存成功');
                }
            }
        });

    }

    // 验证添加物流表单
    function validateAddLogisticsForm() {

        var company = $('#deliveryType'),
            companyVal = company.val(),
            file = $('#uploadFile'),
            fileVal = file.val(),
            number = $('#add_exp_num'),
            numberVal = number.val(),
            info;

        info = company.next('.info');
        if (companyVal == 0) {
            if (info.length < 1) {
                company.parents('dd').append('<span class="info">请选择公司</span>');
            } else {
                info.html('请选择公司');
            }
            return false;
        }

        info && info.remove();

        info = file.parents('dd').find('.info');
        if (fileVal === '') {
            if (info.length < 1) {
                file.parents('dd').append('<span class="info">请选择要上传的物流面单照片</span>');
            } else {
                info.html('请选择要上传的物流面单照片');
            }
            return false;
        }
        info && info.remove();

        info = number.next('.info');
        if (numberVal === '' || !(/^[1-9]\d?$/.test(numberVal))) {
            if (info.length < 1) {
                number.parents('dd').append('<span class="info">递增数输入有误</span>');
            } else {
                info.html('递增数输入有误');
            }
            return false;
        }
        info && info.remove();

        return true;
    }

    // 验证修改物流表单
    function validateModifyLogisticsForm() {
        var number = $('#modify_exp_num'),
            numberVal = number.val(),
            info,
            formInfo = $('#modify_logistics_form').find('.button');

        info = number.next('.info');
        if (numberVal === '' || !(/^[1-9]\d?$/.test(numberVal))) {
            if (info.length < 1) {
                number.parents('dd').append('<span class="info">递增数输入有误</span>');
            } else {
                info.html('递增数输入有误');
            }

            return false;
        }
        info && info.remove();

        return true;
    }

    // 删除一个物流公司
    function deleteLogistics(self) {
        var claaName = self.attr('class'),
            id = self.attr('data-id');

        $.ajax({
            type: 'POST',
            url: '/logisticDesign/delete/' + id,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    self.hide(400, function () {
                        self.remove();
                        if (claaName === 'current') {
                            showAddLogistics();
                            hideDesignWidget();
                            hideModifyLogistics();
                        }
                    });
                }
            }
        });
    }

    // 获取物流单面设计配置
    function getDesignConfig(id) {
        var html = false;
        $.ajax({
            type: 'post',
            url: '/logisticDesign/company/' + id,
            dataType: 'json',
            cache: false,
            async: false,
            success: function (data) {
                if (data.success) {
                    html = data.data.obj;
                }
            }
        });
        return html;
    }

    // 展现物流单设计控件
    function showDesignWidget(self) {
        var id = self.attr('data-id'),
            name = self.find('span').eq(0).text(),
            designConfig,
            designBox = $('#design_box');

        if (!id) {
            return;
        }

        designBox.find('h3').eq(0).text('设计物流单 - ' + name);
        designBox.show();
        designBox.find('.tools').find('input[type=checkbox]').attr('checked', false);

        $('#logisticsInfoId').val(id);
        if (typeof LODOP === "object") {
            LODOP.SET_PRINT_STYLEA('All', 'Deleted', true);
        }
        LODOP = getDesignWidget();
        LODOP.PRINT_INIT('初始化打印控件');
        designConfig = getDesignConfig(id);
        if (designConfig === false) {
            alert('出错了!');
            return;
        }
        eval(designConfig);
        LODOP.SET_SHOW_MODE("DESIGN_IN_BROWSE", 1);
        LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW", 1); //注："BKIMG_IN_PREVIEW"-预览包含背景图 "BKIMG_IN_FIRSTPAGE"- 仅首页包含背景图
        LODOP.PRINT_DESIGN();
    }

    // 隐藏物流单设计控件
    function hideDesignWidget() {
        $('#design_box').hide();
    }

    // 修改物流单设计控件
    function modifyDesign(item) {
        LODOP = getDesignWidget();
        var inputName = item.attr('name'),
            inputVal = item.attr('val'),
            inputValue = item.val(),
            inputChecked = item.get(0).checked;

        if ((!LODOP.GET_VALUE("ItemIsAdded", inputName)) && (inputChecked == true)) {
            LODOP.ADD_PRINT_TEXTA(inputName, inputValue, 32, 175, 30, inputVal);
        } else {
            LODOP.SET_PRINT_STYLEA(inputName, 'Deleted', inputChecked != true);
        }
    }

    // 加载物流公司列表
    loadLogisticsList();

    // 注册事件
    (function () {
        var list = $('#logistics_list');

        // 展开修改物流公司表单
        list.on('click', '.modify_btn', function (E) {
            E.preventDefault();
            var li = $(this).parents('li');
            highlightItem(li);
            showModifyLogistics(li);
            hideAddLogistics();
            hideDesignWidget();
        });

        // 删除一个物流公司
        list.on('click', '.delete_btn', function (E) {
            E.preventDefault();
            var li = $(this).parents('li');
            if (confirm('确定要删除吗？')) {
                deleteLogistics($(this).parents('li'));
            }
        });

        // 展开设计物流面单
        list.on('click', '.design_btn', function (E) {
            E.preventDefault();
            var li = $(this).parents('li');
            highlightItem(li);
            showDesignWidget(li);
            hideAddLogistics();
            hideModifyLogistics();
        });

        // 设计物流面单操作
        $('#design_box').on('click', 'input', function () {
            modifyDesign($(this));
        });

        // 保存设计好的物流面单代码
        $('#save_btn').on('click', function () {
            saveDesign();
        });

        // 删除选定的设计控件
        $('#delete_btn').on('click', function () {
            LODOP.SET_PRINT_STYLEA('Selected', 'Deleted', true);
        });

        // 展开添加物流表单
        $('#add_logistics').on('click', function (E) {
            E.preventDefault();
            showAddLogistics();
            hideDesignWidget();
            hideModifyLogistics();
        });

        // 添加物流公司
        $('#add_submit_btn').on('click', function () {
            addLogistics();
        });

        // 修改物流公司
        $('#modify_submit_btn').on('click', function () {
            modifyLogistics();
        });

        $('#uploadFile').on('change', function () {
            var file = $(this),
                fileVal = file.val(),
                info = file.parents('dd').find('.info'),
                path = file.parents('dd').find('.success');

            if (fileVal === '') {
                if (info.length < 1) {
                    file.parents('dd').append('<span class="info">请选择要上传的物流面单照片</span>');
                } else {
                    info.html('请选择要上传的物流面单照片');
                }
            } else {
                info && info.remove();
                if (path.length < 1) {
                    file.parents('.input-file').after('<span class="success">' + fileVal + '</span>');
                } else {
                    path.html(fileVal);
                }
            }
        });

        $('#modify_uploadFile').on('change', function () {
            var file = $(this),
                fileVal = file.val(),
                path = file.parents('dd').find('.success');

            if (fileVal !== '') {
                if (path.length < 1) {
                    file.parents('.input-file').after('<span class="success">' + fileVal + '</span>');
                } else {
                    path.html(fileVal);
                }
            } else {
                path && path.text('');
            }
        });

    }());

});
