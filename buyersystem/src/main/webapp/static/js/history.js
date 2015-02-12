/**
 * 浏览历史
 * */

function getHistory(historyBox, id) {

    if (typeof historyBox === "undefined") {
        return;
    }

    if (typeof EJS === "undefined") {
        return;
    }

    var url = EJS.BrowsingHistory;
    if (typeof id !== "undefined") {
        url += "?productId=" + id;
    }
    $.ajax({
        type: "GET",
        cache: false,
        url: url,
        dataType: "JSON",
        success: function (data) {

            if (data.success) {

                var historyArr = data.data.history,
                    historyLength = historyArr.length,
                    i,
                    temp = "",
                    className = "";

                if (historyLength > 0) {
                    for (i = 0; i < historyLength; i++) {

                        if (i === (historyLength - 1)) {
                            className = ' class="last"';
                        }
                        temp += '<li' + className + '>' +
                            '   <div class="pic"><a href="' + historyArr[i].url + '"><img src="' + historyArr[i].picture + '" width="68" height="60" alt="" /></a></div>' +
                            '   <div class="name"><a href="' + historyArr[i].url + '">' + historyArr[i].product + '</a></div>' +
                            '   <div class="price">￥' + historyArr[i].price + '</div>' +
                            '</li>';
                    }
                    historyBox.html(temp);
                    historyBox.parents(".browse_history").show();
                } else {
                    historyBox.parents(".browse_history").hide();
                }

            }

        }
    });
}
