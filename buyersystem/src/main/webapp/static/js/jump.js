(function (window, $) {

    var backUrl = $('#backUrl').val(),
        actionType = $('#actionType').val();

    switch (actionType) {
    case 'doClose':  // 关闭父页面
        window.parent.window.close();
        break;
    case 'doRefresh': // 刷新父页面
        window.parent.window.location.reload();
        break;
    case 'updateUserInfo': // 更新用户信息
        window.parent.window.Ejs.userInfo();
        window.parent.window.Ejs.UserWindows.remove();
        break;
    case 'consultative':  // 提交咨询
        window.parent.window.Ejs.userInfo();
        window.parent.window.goodsDetails.consults.submit();
        window.parent.window.Ejs.UserWindows.remove();
        break;
    case 'combination':  // 购买组合套餐
        window.parent.window.Ejs.submitBuyCombinationForm();
        break;
    case 'kacoininfo'://积分详情
        window.parent.window.Ejs.kacoinInfo();
        window.parent.window.Ejs.userInfo();
        window.parent.window.Ejs.UserWindows.remove();
        break;
    default:
        window.parent.window.location.href = backUrl;

    }

    $('#jumpTo').click(function (e) {
        e.preventDefault();
        if (window.parent.window) {
            window.parent.window.location.href = backUrl;
        } else {
            location.href = backUrl;
        }
    });


}(window, jQuery));
