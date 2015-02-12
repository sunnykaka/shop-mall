/**
 * 表单构建
 */

var SEO = function (titleText,seoObjectId,seoType) {
    Ext.Ajax.request({
        url:'seo/query?seoObjectId='+seoObjectId+'&seoType='+seoType,
        success:function (response, options) {
            var jsonObject = Ext.util.JSON.decode(response.responseText);
            if (jsonObject.success == false) {
                Ext.Msg.alert('错误', jsonObject.msg);
                return;
            }
            FB([
                {
                    name:'id',
                    value:jsonObject.data.id,
                    xtype:'hidden'
                },
                {
                    name:'seoObjectId',
                    value:seoObjectId,
                    xtype:'hidden'
                },
                {
                    name:'seoType',
                    value:seoType,
                    xtype:'hidden'
                },
                {
                    fieldLabel:'标题',
                    name:'title',
                    value:jsonObject.data.title,
                    xtype:'textfield',
                    maxLength:60
                },
                {
                    fieldLabel:'描述',
                    name:'description',
                    value:jsonObject.data.description,
                    xtype:'textfield',
                    maxLength:120
                },
                {
                    fieldLabel:'关键字',
                    name:'keywords',
                    value:jsonObject.data.keywords,
                    xtype:'textfield',
                    maxLength:60
                }
            ], 'seo/edit', '设置' + titleText + '的SEO推广信息', false, function () {
            });
        },
        failure:function () {
            Ext.Msg.alert("错误", "读取数据失败");
        }});
};
