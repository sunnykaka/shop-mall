$(function () {
    //logistics_box
    var _type = $("#deliveryType").val();
    var _postId = $("#postId").val();
    var _form = $("#form").val();
    var _to = $("#to").val();


    if (_type == "" || _postId == "") {
        return false;
    }

    var ajaxR = null;
    //window.clearTimeout(ajaxR);
    var ajaxRequestNumber = 0;
    var HTML = "";
    var logisticsBox = $("#logistics_box");

    if($("#logisticsLoading").length < 1){
        logisticsBox.append('<div class="ajaxLoading" id="logisticsLoading"></div>');
    }
    var ajaxLoading = $("#logisticsLoading");

    function getData() {
        ajaxRequestNumber += 1;
        if (ajaxRequestNumber >= 3) { // 超时了
            ajaxLoading.hide();
            HTML = "<p class='logistics_error'>暂无物流信息, 请稍后再试！</p>";
            logisticsBox.html(HTML);
            window.clearTimeout(ajaxR);
            return false;
        }
        var dataUrl = EJS.HomeUrl + "/trade/logistics/message?type=" + _type + "&postId=" + _postId + "&form=" + _form + "&to=" + encodeURIComponent(_to);
        if (ajaxRequestNumber > 1) {
            dataUrl = EJS.HomeUrl + "/trade/logistics/getData?num=" + _postId;
        }
        $.ajax({
            type:"GET",
            url:dataUrl,
            dataType:"text",
            success:function (data) {
                if (data == "false") { // 失败了
                    ajaxLoading.hide();
                    HTML = "<p class='logistics_error'>物流信息加载失败，请稍后再试！</p>";
                    logisticsBox.html(HTML);
                    window.clearTimeout(ajaxR);
                    return false
                } else if (data == "true") { // 成功了
                    ajaxR = setTimeout(getData, 500);
                    return false
                } else if (data == "" || data == "undefined"){
                    ajaxR = setTimeout(getData, 500);
                    return false
                } else { // 客户已签收
                    var D = eval("(" + data + ")");
                    HTML = '<div class="shopcart_step3_order_info_title clearfix">'
                        + '<div class="cell col1"><h4>处理时间</h4></div>'
                        + '<div class="cell col2"><h4>处理信息</h4></div>'
                        + '</div>';
                    HTML += '<div class="com">';
                    HTML += '<ul class="line clearfix">';
                    if (D["lastResult"]["status"] == "200") {
                        var logisticsList = D["lastResult"]["data"];
                        if (logisticsList.length > 0) {
                            $.each(logisticsList, function (i) {
                                HTML += '<li><a href="###">';
                                HTML += '<b class="cell col1">' + logisticsList[i].time + '</b>';
                                HTML += '<b class="cell col2">' + logisticsList[i].context + '</b>';
                                HTML += '</a></li>';
                            });
                        } else {
                            HTML += '<li>暂时数据</li>';
                        }
                    } else {
                        HTML += '<li>暂时数据</li>';
                    }
                    HTML += '</ul>';
                    HTML += '</div>';
                    ajaxLoading.hide();
                    logisticsBox.html(HTML);
                    window.clearTimeout(ajaxR);
                    return false
                }
            }
        });
    }
    getData();
});