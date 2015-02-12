//商品咨询
$("#problemList").find("li").bind("click", function (e) {
    if (e.target.className !== "look_reply") return;
    var thisLi = $(this), lookReply = $(e.target), obj = {};
    if (thisLi.hasClass("current")) {
        obj.name = " ";
        obj.txt = "查看回复";
    }
    else {
        obj.name = "current";
        obj.txt = "收起回复";
    }
    thisLi[0].className = obj.name;
    lookReply.text(obj.txt);
});

//分页
var _pageNum = $("#PageNumber");
var _regNum = /^\d+$/;
_pageNum.blur(function () {
    if (!_regNum.test(_pageNum.val())) {
        _pageNum.val(1);
    }
    if (parseInt(_pageNum.val()) > parseInt(_pageNum.attr("val"))) {
        _pageNum.val(_pageNum.attr("val"));
    }
});