/**
 * 退货申请
 * Created by HuaLei.Du on 2014/6/25.
 */

$(function () {

    var uploadLimit = 3,
        uploadFileBtn = $('#uploadFile');

    $LAB.script(EJS.StaticDomain + '/js/uploadify/jquery.uploadify.js')
        .wait(function () {
            uploadFileBtn.uploadify({
                'swf': '/static/uploadify/uploadify.swf',
                'uploader': '/my/toBack/uploadBackGoodPictures',
                //'uploader': '/uploadify/uploadify.php',
                'buttonText': '选择图片',
                'queueSizeLimit': 3,
                'uploadLimit': uploadLimit,
                'width': 80,
                'checkExisting': false,
                'fileSizeLimit': '1024KB',
                'fileTypeDesc': 'Image Files',
                'fileTypeExts': '*.gif; *.jpg; *.png',
                'removeCompleted': false, // 是否自动将已完成任务从队列中删除
                'itemTemplate': '<div id="${fileID}" class="uploadify-queue-item">' +
                    '<a href="javascript:void(0);" target="_blank" class="link-image">等待上传</a>' +
                    '<span class="cancel">删除</span>' +
                    '</div>',
                'onUploadSuccess': function (file, data, response) {
                    var itemEle = $('#' + file.id),
                        imgUrl,
                        dataArr = data.split('|');

                    if (dataArr.length === 2) {
                        imgUrl = dataArr[1];
                        itemEle.find('.link-image').attr('href', imgUrl);
                        itemEle.find('.link-image').html('<img src="' + imgUrl + '" alt="" width="100" height="100">');
                        itemEle.append('<input type="hidden" name="uploadFilesURL" value="' + data + '">');
                    }

                },
                'onUploadError': function (file, errorCode, errorMsg, errorString) {
                    //alert('文件 “' + file.name + '” 无法上传: ' + errorString);
                    var itemEle = $('#' + file.id);
                    itemEle.find('.link-image').text('上传失败');
                },
                'onDialogClose': false,
                'onSelectError': function (file, errorCode, errorMsg) {
                    var queueData = this.queueData;

                    switch (errorCode) {
                        case -100:
                            queueData.errorMsg = '选定的文件数量超出限制';
                            break;
                        case -110:
                            queueData.errorMsg = '文件 “' + file.name + '” 大小超出限制' + this.settings.fileSizeLimit;
                            break;
                        case -120:
                            queueData.errorMsg = '文件 “' + file.name + '” 没有大小';
                            break;
                        case -130:
                            queueData.errorMsg = '文件 “' + file.name + '” 类型不匹配，只允许上传图片格式的文件(gif,jpg,png)';
                            break;
                        default:
                            break;
                    }
                }
            });
        });

    $('.upload_let').on('click', '.cancel', function () {
        uploadFileBtn.uploadify('cancel', $(this).parents('.uploadify-queue-item').attr('id'));
        uploadLimit += 1;
        uploadFileBtn.uploadify('settings', 'uploadLimit', uploadLimit);
    });

    $('#submitBackItemForm').submit(function () {

        var self = $(this),
            backDiscription = $('#backDiscription'),
            backDiscriptionVal = backDiscription.val();


        if (backDiscriptionVal === '') {
            alert('退货原因详细描述不能为空');
            backDiscription.focus();
            return false;
        }

        if (backDiscriptionVal.length < 5 || backDiscriptionVal.length > 50) {
            alert('退货原因详细描述应在5-50个字符范围内');
            backDiscription.focus();
            return false;
        }

        return true;

    });
});