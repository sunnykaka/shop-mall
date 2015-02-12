$(document).ready(function () {
    //所有页面效果
    pageHander = window.pageHander || {};
    //页面的公共方法
    pageHander.resetParentHeight = function () {
        if (typeof window.parent.window.mainHander === "undefined") {
            return false;
        }
        var _instance = window.parent.window.mainHander.instance;
        if (_instance != undefined) {
            var bodyHeight = parseInt($('body').outerHeight() + 80);
            var minHeight = parseInt($(window.parent.document)[0].documentElement.clientHeight - 120);
            bodyHeight = minHeight > bodyHeight ? minHeight : bodyHeight;
            var size = {
                height:bodyHeight
            };
            _instance.resetIFrame(size);
        }
    };

    //检查每行数据表格的宽高
    pageHander.checkDataGrid = function (func) {
        var _dg = $('.dataGrid');
        _dg.each(function (i) {
            var _this = $(this);
            _this.css({
                height:'auto'
            });
            var _table = _this.find('table').eq(0);
            var _ths = _table.find("th");
            var _totalWidth = 0;
            _ths.each(function (j) {
                var __this = $(this);
                var _cls = __this.attr("class").replace("col_", "");
                _totalWidth += (+_cls);
            });

            var opt = {
                overflowX:_totalWidth > _this.width() ? 'scroll' : 'auto',
                overflowY:_table.height() > 500 ? 'auto' : 'auto'
            };
            if (opt['overflowY'] === 'scroll') {
                _this.height("auto");
            } else {
                _this.height("auto");
            }
            if (opt['overflowX'] === 'auto') {
                _table.width('99%');
            } else {
                _table.width(_totalWidth);
            }
            _this.css(opt);
            if (typeof func === "function") {
                func.call();
            }
        });
    };

    //日期效果
    pageHander.dateEvent = function () {
        $('input.date').each(function (i) {
            $(this).simpleDatepicker({
                startdate:'1/1/2010',
                x:0,
                y:28
            })
        });
    };

    //全选按扭事件
    pageHander.checkAllEvent = function (target) {
        if (!target) {
            return false;
        }
        var _checkAll = $(target).find('input[name=checkAll]').eq(0);
        var ele = $(target).find("input[name=dataId]");
        _checkAll.click(function (e) {
            ele.attr("checked", !!_checkAll.attr("checked"));
            if (!!_checkAll.attr("checked")) {
                //ele.parents("tr.table_entity").addClass("selected");
                ele.parent("td").addClass("selected");
            } else {
                //ele.parents("tr.table_entity").removeClass("selected");
                ele.parent("td").removeClass("selected");
            }
        });

        ele.each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.stopPropagation();
                if (!!_this.attr("checked")) {
                    //_this.parents("tr.table_entity").addClass("selected");
                    _this.parent("td").addClass("selected");
                } else {
                    //_this.parents("tr.table_entity").removeClass("selected");
                    _this.parent("td").removeClass("selected");
                    _checkAll.attr("checked", false);
                }
            });
        });

    };

    //检查数据列表checkBox
    pageHander.checkSelected = function (target) {
        if (!target) {
            return false;
        }
        var ele = $(target).find("input[name=dataId]");
        var _arr = [];
        ele.each(function (i) {
            var _this = $(this);
            if (!!_this.attr("checked")) {
                _arr.push(_this.val());
            }
        });
        if (_arr.length > 0) {
            return _arr;
        } else {
            new Ejs.Dialog({
                opacity:0,
                title:"操作提示！",
                info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请至少选择一个数据！</div> '
            });
            return false;
        }
    };

    //订单列表
    pageHander.orderList = function () {
        //确认发货
        $("#confirmationDelivery").click(function (e) {
            e.preventDefault();

            changeStatus(1);

        });


        //确认批量验货
        $("#batchInspection").click(function (e) {
            e.preventDefault();
            changeStatus(2);


        });

        //Tab菜单效果
        var _tabs = $('.small_tab>ul>li>a');
        _tabs.each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.preventDefault();
                _tabs.removeClass('cur');
                _this.addClass('cur');
                $('.tab_content_wrapper>.tab_content').hide();
                $('.tab_content_wrapper>.tab_content').eq(i).show();
                pageHander.checkDataGrid();
                pageHander.resetParentHeight();
            });
        });

        //加载日期效果
        pageHander.dateEvent();

        //加载全选事件
        pageHander.checkAllEvent('#bindData');

        //加载详细数据
        $("#bindData tr.table_entity").each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                $("#bindData tr.table_entity").removeClass("selected");
                _this.addClass("selected");
                $("#bindData tr.table_entity input[name=dataId]").attr('checked', false);
                _this.find('input[name=dataId]').attr('checked', true);
                var url = $("#getDetailUrl").val();
                var tabWrapper = $("#showDetails");
                var tabContentWrapper = $("#detailContent");
                tabContentWrapper.html("");
                $.ajax({
                    type:"GET",
                    url:url + '/' + _this.attr("val") + '/true',
                    dataType:'html',
                    success:function (html) {
                        tabContentWrapper.html(html);
                        $('.small_tab>ul>li>a').removeClass("cur").eq(0).addClass("cur");
                        tabWrapper.show();
                        pageHander.resetParentHeight();
                    }
                });
            });
        });
    };

    //订单打印
    pageHander.orderPrint = function () {
        var requestUrl = $("#createLogisticsPrintInfo").val();

        pageHander.checkDataGrid();
        pageHander.resetParentHeight();

        //加载全选事件
        pageHander.checkAllEvent('#bindData');

        //加载日期效果
        pageHander.dateEvent();

        //删除物流面单
        $("#deleteLogisticPrintInfo").click(function (e) {
            var html = false;
            var expEle = $("#printExpress>.express"),
                resUrl = $("#expResUrl").val();
            var res = false, index = 0;

            expEle.each(function (is) {
                var radio = $(this).find("input[name=express]");
                if (!!radio.attr("checked")) {
                    res = true;
                    index = radio.val();
                    return;
                }
            });
            if (res) {
                var resUrl = $("#deleteLogisticsPrintInfoById").val();
                if (res) {
                    $.ajax({
                        url:resUrl + '/' + index,
                        dataType:'json',
                        async:false,
                        success:function (resData) {
                            if (resData['success']) {
                                window.location.reload();
                            }
                        }
                    });
                }
            } else {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示信息！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请选择要删除的物流公司！</div>'
                });
            }
        });
        //物流单打印设计
        $("#expPrint").click(function (e) {
            e.preventDefault();
            var res = false;
            var expEle = $("#printExpress>.express");
            expEle.each(function (is) {
                if (!!$(this).find("input[name=express]").attr("checked")) {
                    res = true;
                    return false;
                }
            });
            if (res) {
                $("#dataGrid").hide();
                $("#showDetails").hide();
                $("#pageBar").parents(".fix_button_wrapper").hide();
                $("#expBox").show();
                pageHander.resetParentHeight();
                DisplaySetup();
            } else {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示信息！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请选择物流公司！</div>'
                });
            }
        });

        //返回打开页面效果
        $("#getBack").click(function (e) {
            $("#dataGrid").show();
            $("#showDetails").show();
            $("#pageBar").parents(".fix_button_wrapper").show();
            $("#expBox").hide();
        });

        //添加快递公司
        $("#addExpress").click(function (e) {
            e.preventDefault();
            var html = '<form id="addExpSubmit"  enctype="multipart/form-data"  method="POST" action="' + requestUrl + 'add"><div class="info_form clearfix"><div class="tip" id="addTip"></div> <dl>';
            html += '<dt>公司名称</dt>';
            html += '<dd><select style="width:125px;" name="name" id="deliveryName"></select></dd>';
            html += '<dt>物流面单图片</dt>';
            html += '<dd><input type="file" name="uploadFile" id="uploadFile" /></dd>';
            html += '<dt>递增</dt>';
            html += '<dd><input type="text" name="law" class="text" id="add_exp_num" style="width: 60px;" maxlength="1"></dd>';
            html += '</dl></div><div class="btn_w"><input type="submit" value="确定" class="button_a"></div></form>';

            var addExp = new Ejs.Dialog({
                opacity:0,
                title:"添加物流公司",
                info:html,
                preventClosed:true,
                eventObj:e,
                outClass:"pop_addExpressForm",
                typeStyle:{

                },
                width:360,
                height:200,
                beforeShow:function () {
                    $("#deliveryName").html($("#deliveryType").html());

                    $("#addExpSubmit").bind("submit", function (e) {
                        var addTip = $("#addTip");
                        var add_exp = $("#deliveryName");
                        var add_exp_num = $("#add_exp_num");
                        var add_uploadFile = $("#uploadFile");
                        var fileVal = add_uploadFile.val();

                        if (!$.trim(add_exp.val())) {
                            addTip.text("请选择一个物流公司！");
                            e.preventDefault();
                            return;
                        }
                        var isExp = false;
                        var expressArr = $("#printExpress").find("input[name=express]");
                        expressArr.each(function () {
                            if (isExp) {
                                return;
                            }
                            if ($(this).attr("val") == add_exp.val()) {
                                isExp = true;
                            }

                        });
                        if (isExp) {
                            addTip.text("您选择的物流公司已经添加");
                            e.preventDefault();
                            return;
                        }
                        if (!$.trim(fileVal)) {
                            addTip.text("请上传jpg格式的图片。");
                            e.preventDefault();
                            return;
                        }
                        var fileExt = fileVal.substring(fileVal.lastIndexOf(".") + 1);
                        if (!fileExt || fileExt.toLocaleLowerCase() != "jpg") {
                            addTip.text("格式不对,请上传jpg格式的图片。");
                            e.preventDefault();
                            return;
                        }

                        if (!/^[1-9]{1}$/.test(add_exp_num.val())) {
                            addTip.text("请填写1-9的递增数量！");
                            add_exp_num.focus();
                            e.preventDefault();
                        }

                    });

                },
                eventOffset:{
                    x:300,
                    y:10
                },
                buttons:false
            });

        });

        //修改快递公司
        $("#modifyExpress").click(function (e) {
            e.preventDefault();
            var expEle = $("#printExpress>.express");
            var res = false, index = 0;

            expEle.each(function (is) {
                if (!!$(this).find("input[name=express]").attr("checked")) {
                    res = true;
                    index = is;
                    return;
                }
            });
            if (res) {
                var html = '<form id="modifyExpSubmit" method="POST"><div class="info_form clearfix"><div class="tip" id="modifyTip"></div> <dl>';
                html += '<dt>公司名称</dt><input type="hidden" name="id" id="expId" value=""/>';
                html += '<dd><select style="width:125px;" name="name" id="deliveryName2"></select></dd>';
                html += '<dt>递增规律</dt>';
                html += '<dd><input type="text" name="law" class="text" id="exp_num" style="width: 60px;" maxlength="1"></dd>';
                html += '</dl></div></form>';

                var modifyExp = new Ejs.Dialog({
                    opacity:0,
                    title:"修改物流公司",
                    info:html,
                    eventObj:e,
                    beforeShow:function () {
                        $("#deliveryName2").html($("#deliveryType").html());
                        $("#deliveryName2").find("option").each(function (i) {
                            var __exp = expEle.eq(index).find("input[name=express]").eq(0);
                            if ($(this).val() === __exp.attr("val")) {
                                $(this).attr("selected", "selected");
                            } else {
                                $(this).remove();
                            }
                            $("#exp_num").val(__exp.attr("law"));
                            $("#expId").val(__exp.val());
                        });
                    },
                    eventOffset:{
                        x:160,
                        y:-0
                    },
                    preventClosed:true,
                    buttons:[
                        {
                            onClick:function () {
                                var modifyTip = $("#modifyTip");
                                var exp = $("#deliveryName2");
                                var exp_num = $("#exp_num");

                                if (!$.trim(exp.val())) {
                                    modifyTip.text("请选择物流公司！");
                                    return false;
                                }

                                if (!/^[1-9]{1}$/.test(exp_num.val())) {
                                    modifyTip.text("请填写1-9的递增数量！");
                                    exp_num.focus();
                                    return false;
                                }

                                modifyTip.text("");
                                $.ajax({
                                    type:"POST",
                                    dataType:'json',
                                    url:requestUrl + 'update',
                                    data:$("#modifyExpSubmit").serialize(),
                                    success:function (resData) {
                                        if (resData['success']) {
                                            location.href = location.href;
                                        } else {
                                            modifyTip.text(resData['msg']);

                                        }
                                    }
                                });
                            }
                        }
                    ]
                });
            } else {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示信息！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请选择您要修改的物流公司！</div>',
                    eventObj:e,
                    eventOffset:{
                        x:160,
                        y:-0
                    }
                });
            }

        });


        //批量生成物流单号
        $("#generateSN").click(function (e) {
            e.preventDefault();
            var dataIds = $("input[name=dataId]"),
                expName = "",
                isExpName = true;
            $(".tr_selected").removeClass("tr_selected");
            if (dataIds.length > 0) {
                dataIds.each(function (i) {
                    if (!isExpName) {
                        return
                    }
                    if (!!$(this).attr("checked")) {
                        $(this).parents("tr").addClass("tr_selected");
                        //console.log(i);
                        if (expName == "") {
                            expName = $(this).attr("defaultdelivery");
                        } else if (expName != "") {
                            if (expName != $(this).attr("defaultdelivery")) {
                                isExpName = false;
                            }
                        }
                    }
                })
            }

            if (expName == "") {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示信息！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请选择要打印的订单！</div>',
                    eventObj:e,
                    eventOffset:{
                        x:560,
                        y:-0
                    }
                });
                return;
            }
            if (!isExpName) {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请筛选相同的配送方式！</div> ',
                    eventObj:e,
                    eventOffset:{
                        x:560,
                        y:-0
                    }
                });
                return;
            } else {
                var _ids = pageHander.checkSelected("#bindData");
                if (!!_ids) {
                    $("#orderIds").val(_ids.join(','));
                    $("#logisticName").val(expName);
                    $("#logisticWaybillNumberText").show();
                }
            }
        });

        $("#batchWaybillNumberButton").click(function (e) {
            var batch_exp_num = $("#batch_exp_num");
            if (!$.trim(batch_exp_num.val())) {
                $("#msg").text("请填写第一个物流单号");
                batch_exp_num.focus();
                return false;
            }
            var logisticName = $("#logisticName").val();
            if(logisticName=='shunfeng'){
                if ($.trim(batch_exp_num.val()).length<11) {
                    $("#msg").text("顺丰物流单号不能少于11位");
                    batch_exp_num.focus();
                    return false;
                }
            }else if(logisticName=='ems'){
                if ($.trim(batch_exp_num.val()).length<10) {
                    $("#msg").text("EMS物流单号不能少于10位");
                    batch_exp_num.focus();
                    return false;
                }
            }
            var _ids = $("#orderIds").val();

            var _resUrl = $("#batchWaybillNumber").val();
            $.ajax({
                type:"POST",
                dataType:'json',
                url:_resUrl,
                data:{
                    orderIds:_ids,
                    deliveryType:logisticName,
                    waybillNumber:batch_exp_num.val()
                },
                success:function (resData) {
                    if (resData['success']) {
                        var waybillNumbers = resData["data"]["waybillNumbers"];
                        $(".tr_selected").each(function (_i) {
                            $(this).find(".expNum").find("span").text(waybillNumbers[_i]);
                        });
                        $("#msg").text("");
                    } else {
                        $("#msg").text(resData['msg']);
                    }
                }
            });
        });
        //打印物流单号
        $("#printLogistics").click(function (e) {
            e.preventDefault();
            var _ids = pageHander.checkSelected("#bindData");
            if (!!_ids) {
                new Ejs.Dialog({
                    isConfirm:true,
                    opacity:0,
                    isLeftHand:true,
                    title:"操作提示!",
                    info:"你确定要做此操作？操作后不可恢复！",
                    infoStyle:{
                        minHeight:50,
                        padding:"20px 5px 10px",
                        lineHeight:"22px"
                    },
                    afterClose:function (v) {
                        if (v) {
                            var _url = $("#updateStateAffirmPrintUrl").val();

                            $.ajax({
                                type:"POST",
                                url:_url,
                                dataType:'json',
                                data:{
                                    orderIds:_ids.join(',')
                                },
                                async:false,
                                success:function (resData) {
                                    if (resData['success']) {
                                        window.parent.window.mainHander.instance.activeClickEvent(1);
                                    } else {
                                        new Ejs.Dialog({
                                            opacity:0.3,
                                            title:"操作提示信息！",
                                            info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">' + resData["msg"] + '</div>'
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        //订单汇总
        $("#orderSummary").click(function (e) {
            var _ids = pageHander.checkSelected("#bindData");
            $("#printOrder").val(_ids.join(','));
            if (!!_ids) {
                $("#orderExportExcel").submit();
            }
        });

        //打印发货单
        $("#printInvoice").click(function (e) {
            e.preventDefault();
            var _ids = pageHander.checkSelected("#bindData");
            if (!!_ids) {
                printInvoice(_ids);
            }
        });

        //打印物流单
        $("#printPreview").click(function (e) {
            e.preventDefault();

            var expEle = $("#printExpress>.express");
            /*var res = false, index = 0;

             expEle.each(function (is) {
             var _checked = $(this).find("input[name=express]");
             if (! ! _checked.attr("checked")) {
             res = true;
             index = _checked.attr("val");
             return false;
             }
             });*/

            //if (res) {
            var _ids = pageHander.checkSelected("#bindData");
            if (!!_ids) {


                myPREVIEW();
            }
            /*} else {
             new Ejs.Dialog({
             opacity: 0.3,
             title: "操作提示信息！",
             info: '<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请选择一个物流公司！</div>'
             });
             }*/

        });

        //动态修改物流单号
        var expNumbers = $(".table_entity>td.expNum");
        expNumbers.each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                var expNum = _this.find('span');
                var dyn = $("<input type='text'/>");
                dyn.val(expNum.text());
                expNum.hide();
                dyn.appendTo(_this);
                dyn.focus();
                dyn.focusout(function (evt) {
                    var newNum = dyn.val();
                    var _resUrl = $("#updateWaybillNumber").val();
                    $.ajax({
                        type:"POST",
                        url:_resUrl,
                        dataType:'json',
                        data:{
                            orderId:_this.attr("val"),
                            waybillNumber:newNum
                        },
                        success:function (resData) {
                            dyn.remove();
                            if (resData['success']) {
                                expNum.text(newNum).css({display:'inline'});
                            } else {
                                expNum.css({display:'inline'});
                                new Ejs.Dialog({
                                    opacity:0.3,
                                    title:"操作提示信息！",
                                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">' + resData["msg"] + '</div>'
                                });
                            }
                        }
                    });

                });
            });
        });

        //Tab菜单效果
        var _tabs = $('.small_tab>ul>li>a');
        _tabs.each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.preventDefault();
                _tabs.removeClass('cur');
                _this.addClass('cur');
                $('.tab_content_wrapper>.tab_content').hide();
                $('.tab_content_wrapper>.tab_content').eq(i).show();
                pageHander.checkDataGrid();
                pageHander.resetParentHeight();
            });
        });

        $("#bindData tr.table_entity").each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                $("#bindData tr.table_entity").removeClass("selected");
                _this.addClass("selected");
                $("#bindData tr.table_entity input[name=dataId]").attr('checked', false);
                _this.find('input[name=dataId]').attr('checked', true);
                var url = $("#getDetailUrl").val();
                var tabWrapper = $("#showDetails");
                var tabContentWrapper = $("#detailContent");
                tabContentWrapper.html("");
                $.ajax({
                    type:"GET",
                    url:url + '/' + _this.attr("val") + '/false',
                    dataType:'html',
                    success:function (html) {
                        tabContentWrapper.html(html);
                        $('.small_tab>ul>li>a').removeClass("cur").eq(0).addClass("cur");
                        tabWrapper.show();
                        pageHander.resetParentHeight();
                    }
                });
            });
        });


    };

    //报表页面
    pageHander.goodsStatist = function () {
        //加载全选事件
        pageHander.checkAllEvent('.dataGrid>table');
        //加载日期效果
        pageHander.dateEvent();
    };

    //查看库存页面
    pageHander.stock = function () {

    };

    //验货页面
    pageHander.inspect = function () {
        //设置光标焦点
        var searchVar = $(".order_print_search>input.txt");
        searchVar.eq(searchVar.length - 1).focus();

        //扫描单号为数字
        $("form").eq(0).submit(function (e) {
            if (!$.trim(searchVar.eq(0).val())) {
                $(".error_msg1").eq(0).text("请输入物流单号！").show();
                searchVar.eq(0).focus();
                return false;
            }
            $(".error_msg1").eq(0).text("").hide();
        });

        //商品条形码
        var allSN = $("tr>td.sn");

        $("#snForm").submit(function (e) {
            e.preventDefault();
            checkSN();
        });

        /*        $(document).keydown(function (e) {
         var evt = e || window.event;
         var keyCode = evt.keyCode;
         if (keyCode == 13) {
         checkSN();
         }
         });*/

        function checkSN() {
            var text = searchVar.eq(searchVar.length - 1).val();
            if (!$.trim(text)) {
                return false;
            }
            var isAllChecked = true;
            allSN.each(function (i) {
                var _this = $(this);
                if ($.trim(_this.text()) === $.trim(text) && !_this.parent('tr').hasClass("right")) {
                    _this.parent('tr').removeClass('right').addClass('right');
                    $(".error_msg1").eq(1).hide();
                    searchVar.eq(searchVar.length - 1).val("");
                    searchVar.eq(searchVar.length - 1).focus();
                    return false;
                } else {
                    $(".error_msg1").eq(1).text("没有找到该商品！").show();
                }
                searchVar.eq(searchVar.length - 1).val("");
                searchVar.eq(searchVar.length - 1).focus();
            });


            allSN.each(function (i) {
                var _this = $(this);
                if (!_this.parent('tr').hasClass('right')) {
                    isAllChecked = false;
                }
            });
            if (isAllChecked) {
                var orderIdArr = "";
                $("input[name=orderId]").each(function (i) {
                    if (i == 0) {
                        orderIdArr = $(this).val();
                        return;
                    }
                    orderIdArr += "," + $(this).val();
                });
                $.ajax({
                    type:"POST",
                    url:$("#updateOrderState").val(),
                    dataType:'json',
                    data:{
                        "orderIds":orderIdArr
                    },
                    async:false,
                    success:function (resData) {
                        if (resData['success']) {
                            $(".correct_msg1").show();
                            setTimeout(function () {
                                window.parent.window.mainHander.instance.activeClickEvent(3);
                            }, 2500);
                        } else {
                            $(".error_msg1").text(resData['msg']).show();
                        }
                    }
                });
            } else {
                searchVar.eq(searchVar.length - 1).focus();
            }
        }

    };

    //所有页面加载
    pageHander.checkDataGrid();
    pageHander.resetParentHeight();

    //窗体改变事件
    $(window).bind("resize", function () {
        pageHander.checkDataGrid();
        pageHander.resetParentHeight();
    });


    pageHander.userCheckAllEvent = function (target) {

        if (!target) {
            return false;
        }
        var _checkAll = $(target).find('input[name=checkAll]').eq(0);
        var ele = $(target).find("input[name=dataId]");

        _checkAll.click(function (e) {
            ele.attr("checked", !!_checkAll.attr("checked"));
            if (!!_checkAll.attr("checked")) {
                ele.parents("tr").addClass("selected");
            } else {
                ele.parents("tr").removeClass("selected");
            }
        });

        ele.each(function (i) {
            var _this = $(this);
            _this.click(function (e) {
                e.stopPropagation();
                if (!!_this.attr("checked")) {
                    _this.parents("tr").addClass("selected");
                } else {
                    _this.parents("tr").removeClass("selected");
                    _checkAll.attr("checked", false);
                }
            });
        });

    };

    pageHander.getFrameHeight = function (obj) {

    };


    // 用户日志
    pageHander.userLog = function () {

        pageHander.userCheckAllEvent("#user_table");

        //删除用户操作日志
        $("#deleteSupplier").click(function (e) {
            e.preventDefault();

            var url = $("#supplierLogDelete").val(),
                data = pageHander.user.getCheckedVal($("#user_table"));

            if (!data) {
                alert("请选择要删除的项");
                return;
            }

            if (!confirm("确定要删除所选项？")) {
                return;
            }

            $.ajax({
                type:"POST",
                dataType:'json',
                url:url,
                data:"ids=" + data,
                success:function (resData) {
                    if (resData['success']) {
                        location.reload();
                    } else {
                        alert(resData['msg']);
                    }
                },
                error:function () {
                    alert("服务端出错了~~");
                }
            });

        });

    };


    pageHander.Dialog = {};

    pageHander._Dialog = function (src, options) {
        var opt = options || {};
        var defaultOptions = {
            opacity:0,
            title:"",
            html:'<iframe src="' + src + '" frameborder="0" width="360" height="0" scrolling="no" id="userFrame" name="user-frame"></iframe>'
        };
        $.extend(true, defaultOptions, opt);

        pageHander.Dialog = new Ejs.Dialog({
            opacity:defaultOptions.opacity,
            title:defaultOptions.title,
            info:defaultOptions.html,
            preventClosed:true,
            eventObj:null,
            outClass:"pop_add_user_form",
            typeStyle:{},
            width:360,
            height:160,
            beforeShow:function () {
                // TODO 表单验证
                if (typeof opt.html === "undefined") {
                    var userFrameObj = $("#userFrame");
                    userFrameObj.on("load", function () {
                        var frameHeight = ($("#userFrame").contents().height());
                        userFrameObj.attr("height", frameHeight);
                        pageHander.Dialog.reSetConfirm(frameHeight + 32);
                    });
                }
            },
            buttons:false
        });
    };

    pageHander.user = pageHander.user || {};


    pageHander.user.getCheckedVal = function (obj) {
        var checked = obj.find("input[name=dataId]:checked"),
            arr = [];
        checked.each(function () {
            arr.push($(this).val());
        });
        if (arr.length < 1) {
            return false;
        }
        arr = arr.join();
        return arr;
    };

    // 用户管理
    pageHander.userManagement = function () {
        pageHander.resetParentHeight();
        pageHander.userCheckAllEvent("#user_table");

        var tableObj = $("#user_table");

        tableObj.find(":checkbox").attr("checked", false);

        // 添加新用户
        $("#addUser").click(function (e) {
            e.preventDefault();
            var src = $("#addSupplierAccountPageJump").val();
            pageHander._Dialog(src, {
                title:"添加新用户"
            });
        });

        // 删除用户
        $("#deleteUser").click(function (e) {
            e.preventDefault();
            var url = $("#supplierAccountDelete").val(),
                data = pageHander.user.getCheckedVal(tableObj);

            if (!data) {
                alert("请选择要删除的项");
                return;
            }

            if (!confirm("确定要删除所选项？")) {
                return;
            }

            // 发送到服务器
            $.ajax({
                type:"POST",
                dataType:'json',
                url:url,
                data:"ids=" + data,
                success:function (resData) {
                    if (resData['success']) {
                        location.reload();
                    } else {
                        alert(resData['msg']);
                    }
                },
                error:function () {
                    alert("服务端出错了~~");
                }
            });
        });

        // 角色分配
        tableObj.on("dblclick", ".table_entity", function () {
            var id = $(this).find("input[name=dataId]").val();
            var src = $("#supplierRoleConfigPageJump").val();
            pageHander._Dialog(src + "/" + id, {
                title:"角色分配"
            });
        });

    };

    // 角色管理
    pageHander.roleManagement = function () {
        pageHander.resetParentHeight();
        pageHander.userCheckAllEvent("#role_table");

        var tableObj = $("#role_table");
        tableObj.find(":checkbox").attr("checked", false);

        // 添加角色
        $("#addRole").click(function (e) {
            e.preventDefault();
            var src = $("#addRolePageJump").val();
            pageHander._Dialog(src, {
                title:"添加角色"
            });
        });

        // 删除角色
        $("#deleteRole").click(function (e) {
            e.preventDefault();

            var url = $("#roleDelete").val(),
                data = pageHander.user.getCheckedVal(tableObj);

            if (!data) {
                alert("请选择要删除的项");
                return;
            }

            if (!confirm("确定要删除所选项？")) {
                return;
            }

            // 发送到服务器
            $.ajax({
                type:"POST",
                dataType:'json',
                url:url,
                data:"ids=" + data,
                success:function (resData) {
                    if (resData['success']) {
                        alert("删除成功");
                        location.reload();
                    } else {
                        alert(resData['msg']);
                    }
                },
                error:function () {
                    alert("服务端出错了~~");
                }
            });

        });

        // 更改角色信息
        tableObj.on("dblclick", ".table_entity", function () {
            var id = $(this).find("input[name=dataId]").val();
            //console.log(id);
            var src = $("#updateRolePageJump").val();
            pageHander._Dialog(src + "/" + id, {
                title:"编辑角色"
            });
        });

    };

    // 权限管理
    pageHander.competenceManagement = function () {
        pageHander.resetParentHeight();
        pageHander.userCheckAllEvent("#competence_table");

        var tableObj = $("#competence_table");
        tableObj.find(":checkbox").attr("checked", false);

        // 添加权限
        $("#addPermission").click(function (e) {
            e.preventDefault();
            var src = $("#addPermissionPageJump").val();
            pageHander._Dialog(src, {
                title:"添加权限"
            });
        });

        // 删除权限
        $("#deletePermission").click(function (e) {
            e.preventDefault();

            var url = $("#permissionDelete").val(),
                data = pageHander.user.getCheckedVal(tableObj);

            //console.log(data);
            if (!data) {
                alert("请选择要删除的项");
                return;
            }

            if (!confirm("确定要删除所选项？")) {
                return;
            }

            // 发送到服务器
            $.ajax({
                type:"POST",
                dataType:'json',
                url:url,
                data:"ids=" + data,
                success:function (resData) {
                    if (resData['success']) {
                        alert("删除成功");
                        location.reload();
                    } else {
                        alert(resData['msg']);
                    }
                },
                error:function () {
                    alert("服务端出错了~~");
                }
            });

        });

        // 更改权限信息
        tableObj.on("dblclick", ".table_entity", function () {
            var id = $(this).find("input[name=dataId]").val();
            //console.log(id);
            var src = $("#updatePermissionPageJump").val();
            pageHander._Dialog(src + "/" + id, {
                title:"编辑权限"
            });
        });

        $(".category").on("click", "h4", function () {
            var parent = $(this);
            if (parent.find("i").hasClass("hide")) {
                parent.find("i").removeClass("hide").addClass("show");
                parent.parents("table").find(".table_entity").show();
            } else {
                parent.parents("table").find(".table_entity").hide();
                parent.find("i").removeClass("show").addClass("hide");
            }
        });

    };

});