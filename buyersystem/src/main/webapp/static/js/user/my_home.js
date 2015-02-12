$(function () {
    $('.remind div').find('a').click(function (e) {
        e.preventDefault();
        var url = $(this).attr('href'),
            input = $(this).parent().find('input'),
            phoneMsgBox = $('#phoneNumber_error'),
            emailMsgBox = $('#email_error');
        if (phoneMsgBox[0].style.display == 'inline' || emailMsgBox[0].style.display == 'inline') {
            return;
        }
        var d = e.target.id == "addEmail"?"email=" + input.val():"phone=" + input.val();
        if ($(this).parent().find('input').hasClass('rem_in')) {
            $.ajax({
                url: url,
                type: 'POST',
                dataType: "json",
                data: d,
                success: function (data) {
                    if (data["success"]) {
                        if (e.target.id == "addEmail") {
                            e.target.style.display = 'none';
                            showBox("cor", emailMsgBox, data.msg);
                        }
                        else {
                            showBox("cor", phoneMsgBox, data.msg);
                        }
                        input.removeClass('rem_in').attr("readonly", true);
                    }
                    else {
                        
                        e.target.id == "addEmail" ? showBox("", emailMsgBox, data.msg) : showBox("", phoneMsgBox, data.msg);
                    }
                }
            });
            function showBox(classN, obj, text) {
                obj.addClass(classN).text(text).show(function () {
                    setTimeout(function () {
                        obj.hide().removeClass(classN);
                    }, 2000);
                });
            };
        } else {
            input.addClass('rem_in').attr("readonly", false);
        }
    });
   
    //我的关注
    (function () {
        var box_concern = $('.concern_box .concern');
        var i = 0, width = 158 + 30;
        var len = box_concern.find('li').length;
        $('.uc_order .prev').click(function () {
            if (i > 0) {
                i--;
                concern();
            }
        });
        $('.uc_order .next').click(function () {
            if (i >= 0 && i < len - 5) {
                i++;
                concern();
            }
        });
        function concern() {
            box_concern.animate({ left: -width * i });
        };
    })();

    //浏览记录删除
    $("#historyList").on('click', '.remove', function () {
        var self = $(this),
            url = '/delHistory?id=' + self.attr('data-id');

        $.ajax({
            type: 'GET',
            cache: false,
            url: url,
            dataType: 'JSON',
            success: function (response) {
                if (response.success) {
                    self.parents('li').slideUp(200);
                } else {
                    alert(response.msg);
                }

            },
            error: function () {
                alert('服务器发生错误');
            }
        });

    });

});