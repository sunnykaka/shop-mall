var LODOP; //声明为全局变量
/**
 * 动态添加属性内容   打印维护， 只允许添加属性，不允许修改属性值
 * @param item
 * @constructor
 */
function Moditify(item) {
    LODOP = getLodop(document.getElementById('LODOP2'), document.getElementById('LODOP_EM2'));
    if ((!LODOP.GET_VALUE("ItemIsAdded", item.name)) && (item.checked)) {
        LODOP.ADD_PRINT_TEXTA(item.name, item.value, 32, 175, 30, $(item).attr("val"));
    } else {
        LODOP.SET_PRINT_STYLEA(item.name, 'Deleted', !item.checked);
    }
}

function CreatePage() {
    LODOP = getLodop(document.getElementById('LODOP2'), document.getElementById('LODOP_EM2'));
    LODOP.PRINT_INIT("初始化打印控件");
    var html = queryLogisticsPrintHtmlById();
    eval(html);
}

/**
 * 显示打印控件
 * @constructor
 */
function DisplaySetup() {
    CreatePage();
    LODOP.SET_SHOW_MODE("SETUP_IN_BROWSE", 1);
    LODOP.SET_SHOW_MODE("MESSAGE_NOSET_PROPERTY", '不能设置属性，请用打印设计(本提示可修改)！');
    LODOP.PRINT_SETUP();
}

/**
 * 获得打印控件程序代码
 */
function getProgram() {
    LODOP = getLodop(document.getElementById('LODOP2'), document.getElementById('LODOP_EM2'));
    document.getElementById("printHtml").value = LODOP.GET_VALUE("ProgramCodes", 0);
    $("#orderPrintSubmit").submit();
}

function queryOrderPrintInfo() {
    var dataList;
    var _idsArr = pageHander.checkSelected("#bindData");
    $.ajax({
        type:"POST",
        url:$("#orderPrintUrl").val(),
        data:{
            orderIds:_idsArr.join(",")
        },
        dataType:'json',
        async:false,
        success:function (resData) {
            if (resData['success']) {
                dataList = resData['data']['orderPrintInfoList'];
            } else {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">' + resData['msg'] + '</div> '
                });
            }
        }
    });
    return dataList;
}

function replaceAllHtml(html, data) {
    html = html.substr(html.indexOf(";") + 1, html.length);
    if (html.indexOf("收货人联系方式") > 0) {
        html = html.replace(new RegExp("收货人联系方式", "g"), data['telephone']);
    }
    if (html.indexOf("发货人联系方式") > 0) {
        html = html.replace(new RegExp("发货人联系方式", "g"), data.consignorTelephone);
    }
    if (html.indexOf("发货人单位名称") > 0) {
        html = html.replace(new RegExp("发货人单位名称", "g"), data.company);
    }
    if (html.indexOf("邮编") > 0) {
        html = html.replace(new RegExp("邮编", "g"), data.zipCode);
    }
    if (html.indexOf("发货地址") > 0) {
        html = html.replace(new RegExp("发货地址", "g"), data.consignorAddress);
    }
    if (html.indexOf("收货人地址") > 0) {
        html = html.replace(new RegExp("收货人地址", "g"), data.address);
    }
    if (html.indexOf("收货人") > 0) {
        html = html.replace(new RegExp("收货人", "g"), data.consignee);
    }
    if (html.indexOf("发货人") > 0) {
        html = html.replace(new RegExp("发货人", "g"), data.consignor);
    }
    return html;
}

function queryLogisticsPrintHtmlById() {
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
        $.ajax({
            type:'POST',
            url:resUrl + index,
            dataType:'text',
            async:false,
            success:function (resData) {
                document.getElementById("printHtml").value = resData;
                document.getElementById("logisticsInfoId").value = index;
                if (!!resData) {
                    html = resData;
                }
            }
        });
    }
    return html;
}

/**
 * 获取默认的物流公司打印信息
 * @return {*}
 */
function queryDefaultLogisticsPrintHtmlById() {
    var html = false;
    var resUrl = $("#expResUrlByName").val();
    var dataIds = $("input[name=dataId]"),
        expName = "",
        isExpName = true;
    if (dataIds.length > 0) {
         dataIds.each(function (i) {
             if(!isExpName){return}
             if (!!$(this).attr("checked")) {
                 if (expName == "") {
                     expName =  $(this).attr("defaultdelivery");
                     isExpName = true;
                 }else if(expName !="" ){
                     if(expName != $(this).attr("defaultdelivery")){
                        isExpName = false;
                     }
                 }
             }
        })
    }
    if (!isExpName) {
        new Ejs.Dialog({
            opacity: 0,
            title: "操作提示！",
            info: '<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请筛选相同的配送方式！</div> '
        });
        return;
    }else{
        $.ajax({
            type:'POST',
            url:resUrl+ expName,
            dataType:'text',
            async:false,
            success:function (resData) {
                if (!!resData) {
                    html = resData;
                }
            }
        });
    }
    return html;
}
/**
 * 打印预览
 */
function myPREVIEW() {
    var html = queryDefaultLogisticsPrintHtmlById();
    if (html == "" || html == false) {
        new Ejs.Dialog({
            opacity:0,
            title:"操作提示！",
            info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">请对物流面单进行设计再打印</div> '
        });
        return;     //提示信息
    }
    var dataList = queryOrderPrintInfo();
    if (dataList.length <= 0) {
        new Ejs.Dialog({
            opacity:0,
            title:"操作提示！",
            info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">订单打印信息获取失败</div> '
        });
        return;
    }
    LODOP = getLodop(document.getElementById('LODOP1'), document.getElementById('LODOP_EM1'));
    LODOP.PRINT_INIT("打印物流单");
    for (var i = 0; i < dataList.length; i++) {
        LODOP.NewPage();
        var print_html = replaceAllHtml(html, dataList[i]);
        eval(print_html);
    }
    LODOP.SET_PRINT_STYLE("FontSize", 14);
    LODOP.SET_PRINT_PAGESIZE(0, "2100", "1480", "CreateCustomPage");
    LODOP.SET_PRINT_MODE("AUTO_CLOSE_PREWINDOW", 1);//打印后自动关闭预览窗口
    LODOP.PREVIEW();
}


/**
 * 打印发货单预览
 * @param ids
 */
function printInvoice(ids) {
    LODOP = getLodop(document.getElementById('LODOP1'), document.getElementById('LODOP_EM1'));
    LODOP.PRINT_INIT("打印发货单");
    var numErr = [], errNum = 0;
    var orderPrintInfoList;
    var orderDetailList;
    var url = $("#invoicePrint").val();
    $.ajax({
        type:'POST',
        url:url,
        data:{
            orderIds:ids.join(',')
        },
        dataType:'json',
        async:false,
        success:function (resData) {
            errNum += 1;
            if (resData['success']) {
                orderPrintInfoList = resData['data']['orderPrintInfoList'];
                orderDetailList = resData['data']['orderDetailList'];
            } else {
                new Ejs.Dialog({
                    opacity:0,
                    title:"操作提示！",
                    info:'<div class="info_form" style="padding-top: 25px; padding-bottom:25px;">' + resData['msg'] + '</div> '
                });
            }
        }
    });
    var strBodyStyle = '<style type="text/css">' + InvoiceStyle() + '</style>';
    for (var i = 0; i < orderPrintInfoList.length; i++) {
        InvoiceHtml(orderPrintInfoList[i], orderDetailList[i], orderDetailList[i].platformOrderItemList, strBodyStyle, 0);
    }
    LODOP.SET_PRINT_PAGESIZE(0, "2100", "1480", "CreateCustomPage");
    LODOP.SET_PRINT_MODE("AUTO_CLOSE_PREWINDOW", 1);//打印后自动关闭预览窗口
    LODOP.PREVIEW();
}

function InvoiceStyle() {
    return 'html, body, a, img, p, ul, li, div, h1, h2, h3 { margin: 0; padding: 0; border: 0; }' +
        'body { font-family: simsun, SimHei, "microsoft yahei"; color: #333; font-size: 14px; }' +
        '.clear{border:0;clear:both;display:block;float:none;font-size:0;margin:0;padding:0;overflow:hidden;visibility:hidden;width:0;height:0}' +
        '.print_wrapper { width: 97%;font-size: 14px;margin:0 0 0 25px;position:relative;height:520px;}' +
        '.clearfix:after{content:".";clear:both;display:block;height:0;overflow:hidden;visibility:hidden;}' +
        '.clearfix{ * zoom:1}' +
        '.print_wrapper .buyer { padding: 88px 10px 10px 0;line-height:22px;font-size: 14px; }' +
        '.print_wrapper .express { padding: 112px 0 10px; text-align: left;line-height:22px;font-size: 14px; }' +
        '.print_wrapper .table table { border-width: 1px 0 0 1px; border-color: #999; border-style: solid; font-size: 14px; }' +
        '.print_wrapper .table td,' +
        '.print_wrapper .table th { border-width: 0 1px 1px 0; border-color: #999; border-style: solid; text-align: center; line-height: 24px;font-size: 14px; }' +
        '.print_wrapper .order_count{height:26px;line-height:26px;margin:6px 0 0;font-size: 14px;}' +
        '.print_wrapper .amount{float:right;}' +
        '.print_wrapper .seller{position:absolute;width:90%;left:0;bottom:40px;}' +
        '.print_wrapper .seller p{line-height:22px;font-size: 14px;}'
}

function InvoiceHtml(orderPrintInfo, order, orderProducts, strBodyStyle, status) {
    var customerServiceRemark="";
    var userRemark="";
    if(order.customerServiceRemark!=null){
        customerServiceRemark=order.customerServiceRemark;
    }
    if(order.userRemark!=null){
        userRemark=order.userRemark;
    }
    var index = 0;
    if (orderProducts.length > 3 * (status + 1)) {
        index = 3 * (status + 1);
    } else {
        index = orderProducts.length;
    }
    var deliveryType = order.deliveryType;
    if (deliveryType == "ems") {
        deliveryType = "EMS";
    }
    if (deliveryType == "shunfeng") {
        deliveryType = "顺丰";
    }
    if (deliveryType == "yunda") {
        deliveryType = "韵达";
    }
    if (deliveryType == "zhongtong") {
        deliveryType = "中通";
    }
    if (deliveryType == "zhaijisong") {
        deliveryType = "宅急送";
    }
    if (deliveryType == "yuantong") {
        deliveryType = "圆通";
    }
    if (deliveryType == "shentong") {
        deliveryType = "申通";
    }
    if (deliveryType == "kuaijiesudi") {
        deliveryType = "快捷";
    }
    if (deliveryType == "quanritongkuaidi") {
        deliveryType = "全日通";
    }
    if (deliveryType == "huitongkuaidi") {
        deliveryType = "汇通";
    }
    var baseUrl = document.getElementById("baseUrl").value;
    var time = order.payDate;
    var html = '<div class="print_wrapper">';
    html += '<div class="header">';
    html += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
    html += '<tr>';
    html += '<td valign="bottom"><div class="buyer">';
    html += '<p>收 货 人：' + order.name + '</p>';
    html += '<p>联系方式：' + order.mobile + '</p>';
    html += '<p class="clearfix"><span style="float: left;height: 22px;">收货地址：</span><span style="float: left;width: 400px">' + order.province+order.location + '</span></p>';
    html += '<p>买家留言：'+userRemark+'</p>';
    html += '</div></td>';
    html += '<td valign="top" style="width:30%;"><div class="express">';
    html += '<p>快递公司：' + deliveryType+ '</p>';
    html += '<p>订购日期：' + time.substr(0, time.length - 3) + '</p>';
    html += '<p>订  单 号：' + order.orderNo + '</p>';
    html += '</div></td>';
    html += '</tr>';
    html += '</table>';
    html += '</div>';
    html += '<div class="content">';
    html += '<div class="table">';
    html += '<table border="0" cellspacing="0" cellpadding="0" width="100%">';
    html += '<tr>';
    html += ' <th width="8%">序号</th>';
    html += ' <th width="8%">退货</th>';
    html += '<th width="20%">商品编号</th>';
    html += ' <th width="30%">商品名称</th>';
    html += ' <th width="15%">单价（元）</th>';
    html += '<th width="8%">数量</th>';
    html += ' <th width="15%">金额（元）</th>';
    html += '</tr>';
    var j = 0;
    var totalPrice=0;
    for (var i = status * 3; i < index; i++) {
        var skuPrice=orderProducts[i].unitPrice * orderProducts[i].shipmentNum;
        totalPrice+=skuPrice;
        html += '<tr>';
        html += '<td>' + j + 1 + '</td>';
        html += '<td>[&nbsp;&nbsp;]</td>';
        html += '<td>' + orderProducts[i].itemNo + '</td>';
        html += '<td style="padding: 0 5px 0 18px; text-align: left;">' + orderProducts[i].productName + ' ' + orderProducts[i].skuAttribute + '</td>';
        html += '<td>¥：' + orderProducts[i].unitPrice + '</td>';
        html += '<td>' + orderProducts[i].shipmentNum + '</td>';
        html += '<td>¥：' +skuPrice+ '</td>';
        html += '</tr>';
    }
    html += '</table>';
    html += '</div>';

    html += '<div class="clear"></div>';
    html += '<div class="order_count">';
    html += '<div class="amount">合计：¥' +totalPrice + '元 </div>';
    html += '</div>';
    html += '<div class="seller">';
    html += '<p>发 货  人： '+ orderPrintInfo.consignor +'</p>';
    html += '<p>发货地址： '+ orderPrintInfo.consignorAddress +'</p>';
    html += '<p>卖家留言： '+customerServiceRemark+'</p>';
    html += '</div>';
    html += '</div>';
    html += '</div>';
    html += '</body>';
    html += '</html>';
    var strFormHtml = strBodyStyle + "<body>" + html + "</body>";
    LODOP.NewPage();
    LODOP.ADD_PRINT_HTM("01mm", 0.1, "RightMargin:0.1cm", "BottomMargin:0.1mm", "<body leftmargin=0 topmargin=0>" + strFormHtml + "</body>");
    LODOP.ADD_PRINT_BARCODE(60, 516, 230, 47, "128Auto", order.waybillNumber);
    if (orderProducts.length > 3 * (status + 1)) {
        status = status + 1;
        InvoiceHtml(orderPrintInfo, order, orderProducts, strBodyStyle, status);
    }

}
function SaveAsFile(status) {
    LODOP = getLodop(document.getElementById('LODOP1'), document.getElementById('LODOP_EM1'));
    LODOP.PRINT_INIT("导出Excel");
    if (status == 1) {
        LODOP.ADD_PRINT_TABLE(100, 20, 500, 80, document.getElementById("orderDetails").innerHTML);
    } else if (status == 2) {
        LODOP.ADD_PRINT_TABLE(100, 20, 1400, 80, document.getElementById("stockSku").innerHTML);
    } else {
        LODOP.ADD_PRINT_TABLE(100, 20, 1500, 80, document.getElementById("commodityStatistics").innerHTML);
    }
    LODOP.SET_SAVE_MODE("Orientation", 2); //Excel文件的页面设置：横向打印   1-纵向,2-横向;
    LODOP.SET_SAVE_MODE("PaperSize", 9);  //Excel文件的页面设置：纸张大小   9-对应A4
    LODOP.SET_SAVE_MODE("Zoom", 90);       //Excel文件的页面设置：缩放比例
    LODOP.SET_SAVE_MODE("CenterHorizontally", true);//Excel文件的页面设置：页面水平居中
    LODOP.SET_SAVE_MODE("CenterVertically", true); //Excel文件的页面设置：页面垂直居中
//		LODOP.SET_SAVE_MODE("QUICK_SAVE",true);//快速生成（无表格样式,数据量较大时或许用到）
    if (status == 1) {
        LODOP.SAVE_TO_FILE("订单明细.xls");
    } else if (status == 2) {
        LODOP.SAVE_TO_FILE("库存信息.xls");
    } else {
        LODOP.SAVE_TO_FILE("商品统计.xls");
    }
}

/**
 * 分页
 * @param value
 */
function changePageSize(value) {
    document.getElementById("limit").value = value;
    $("#pageNo").val(0);
    var form = document.getElementById("orderForm");
    form.submit();
}
function forwardPage(type) {
    document.getElementById("forwardType").value = type;
    var form = document.getElementById("orderForm");
    form.submit();
}

function pageJump(pageIndex, totalPage) {
    var limit = document.getElementById("limit").value;
    var pageJumpPageNo = document.getElementById("PageJumpPageNo").value;
    if(isNaN(pageJumpPageNo)){
        return;
    }
    if (parseInt(pageJumpPageNo)>parseInt(totalPage)) {
        return;
    }
    if (pageIndex == 0) {
        document.getElementById("pageNo").value =pageJumpPageNo;
    } else {
        document.getElementById("pageNo").value = pageIndex;
    }
    var form = document.getElementById("orderForm");
    form.submit();
}
function resetQuery() {
    var dataType = $("#dataType option");
    var delType = $("#deliveryType option");
    var storage = $("#storageId option");
    var sortValue = $("#sortValue option");
    var sortMode = $("#sortMode option");
    var queryOption = $("#queryOption option");

    queryOption.attr("selected", false);
    dataType.attr("selected", false);
    delType.attr("selected", false);
    storage.attr("selected", false);
    sortValue.attr("selected", false);
    sortMode.attr("selected", false);

    queryOption.first().attr("selected", "selected");
    dataType.first().attr("selected", "selected");
    delType.first().attr("selected", "selected");
    storage.first().attr("selected", "selected");
    sortValue.first().attr("selected", "selected");
    sortMode.first().attr("selected", "selected");

    $("#startDate").val("");
    $("#queryValue").val("");
    $("#endDate").val("");
    $("#orderNo").val("");
    $("#orderNo2").val("");
}


