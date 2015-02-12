Picture = function (productId) {


    var store = new Ext.data.JsonStore({
        url: 'product/images/' + productId,
        root: 'data.pictureList',
        fields: ['id', 'name', 'pictureUrl', 'productId', 'mainPic', 'minorPic']
    });
    store.load();


    var tpl = new Ext.XTemplate(
        '<tpl for=".">',
        '<div class="thumb-wrap" id="{name}">',
        '<div class="thumb"><img src="{pictureUrl}" title="{name}" ></div>',
        '<span class="x-editable">{shortName}</span></div>',
        '</tpl>',
        '<div class="x-clear"></div>'
    );

    var picUploadForm = new Ext.FormPanel({
        items: [
            {
                xtype: 'hidden',
                name: 'productId',
                value: productId
            },
            new Ext.ux.form.FileUploadField({
                name: 'uploadFile',
                fieldLabel: '选择图片',
                allowBlank: false,
                buttonText: '浏览'
            }),
            {
                xtype: 'combo',
                hiddenName: 'mainPic',
                fieldLabel: '是否主图',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: new Ext.data.ArrayStore({
                    fields: [
                        'valueField',
                        'displayText'
                    ],
                    data: [
                        ["true", '是'],
                        ["false", '否']
                    ]
                }),
                displayField: 'displayText',
                valueField: 'valueField',
                value: 'false',
                allowBlank: false
            }
        ],
        border: false,
        fileUpload: true,
        bodyStyle: 'background:transparent;padding:10px;',
        defaults: {
            anchor: '100%'
        }
    });

    var dataView = new Ext.DataView({
        id:'productImgSpaceId-view',
        store: store,
        tpl: tpl,
        autoHeight: true,
        multiSelect: true,
        overClass: 'x-view-over',
        itemSelector: 'div.thumb-wrap',
        emptyText: '该商品没有图片',
        prepareData: function (data) {
            data.shortName = Ext.util.Format.ellipsis(data.name, 13);
            if (data.mainPic) {
                data.shortName = "<div style='color:#3ecd2d'>" + data.shortName + "</div>"
            }
            if (data.minorPic) {
                data.shortName = "<div style='color:#0033FF'>" + data.shortName + "</div>"
            }
            data.pictureUrl = data.pictureUrl + "?version=" + (new Date()).getTime();
            return data;
        }

    });

    var PictureUpload = function (button) {
        commitForm(picUploadForm, function () {
            win.hide();
            picUploadForm.getForm().reset();
            store.load();
        }, 'product/images/upload', {
            waitTitle: '进度',
            // 动作发生期间显示的文本信息
            waitMsg: '正在上传，请等待..'
        })
    }

    var win = new Ext.Window({autoHeight: true,
        width: 500,
        resizable: false,
        plain: true,
        modal: true,
        closeAction: 'hide',
        autoScroll: true,
        title: '本地上传图片(jpg,gif,png,jpeg)',
        keys: [
            {
                key: Ext.EventObject.ENTER,
                fn: PictureUpload //执行的方法
            }
        ],
        buttons: [
            {
                text: '提交',
                handler: PictureUpload
            }
        ],

        items: picUploadForm });


    Picture.superclass.constructor.call(this, {
        id: 'images-view',
        width: 535,
        autoHeight: true,
        layout: 'fit',
        title: '图片管理',
        tbar: [
            {
                text: '删除图片',
                iconCls: 'remove',
                handler: function () {
                    var ids = [];
                    if (dataView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要删除的图片");
                        return;
                    }
                    Ext.each(dataView.getSelectedRecords(), function (r) {
                        ids.push(r.get('id'));
                    });

                    doAjax('product/images/delete', function () {
                        store.load();
                    }, {
                        ids: ids
                    }, "确定删除这些图片吗？");
                }
            },
            '-',
            {
                text: '本地上传',
                iconCls: 'add',
                handler: function () {
                    var win = new Ext.Window({
                        id: 'batch-upload-img',
                        title: '多文件上传',
                        width: 500,
                        height: 500,
                        resizable: false,
                        layout: 'fit',
                        items: [
                            {
                                sourceWindowId:'productImgSpaceId-view',
                                xtype: 'uploadpanel',
                                uploadUrl: window.BossHome + '/product/images/upload?productId=' + productId,
                                filePostName: 'uploadFile', // 这里很重要，默认值为'fileData',这里匹配action中的setMyUpload属性
                                flashUrl: window.BossHome + '/static/js/product/js/swfupload.swf',
                                fileSize: '500 MB',
                                height: 400,
                                border: false,
                                fileTypes: '*.*', // 在这里限制文件类型:'*.jpg,*.png,*.gif'
                                fileTypesDescription: '所有文件'
                            }
                        ],
                        bbar: ['仅支持图片文件']

                    });
                    win.show(this.id);
                }
            } ,
            '-',
            {
                text: '设置主图',
                iconCls: 'run',
                handler: function () {
                    if (dataView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要设置主图的图片");
                        return;
                    }
                    if (dataView.getSelectedRecords().length > 1) {
                        Ext.Msg.alert("信息", "主图只能为一张");
                        return;
                    }
                    if (dataView.getSelectedRecords()[0].get('mainPic')) {
                        Ext.Msg.alert("信息", "该图片已经是主图");
                        return;
                    }

                    doAjax('product/images/setMainPic', function () {
                        store.load();
                    }, {
                        id: dataView.getSelectedRecords()[0].get('id')
                    }, "你确定要设置这张图片为主图吗？");

                }
            },
            '-',
            {
                text: '设置副图',
                iconCls: 'run',
                handler: function () {
                    if (dataView.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要设置副图的图片");
                        return;
                    }
                    if (dataView.getSelectedRecords().length > 1) {
                        Ext.Msg.alert("信息", "副图只能为一张");
                        return;
                    }
                    if (dataView.getSelectedRecords()[0].get('mainPic')) {
                        Ext.Msg.alert("信息", "主图不能设置为副图");
                        return;
                    }
                    if (dataView.getSelectedRecords()[0].get('minorPic')) {
                        Ext.Msg.alert("信息", "该图片已经是副图");
                        return;
                    }

                    doAjax('product/images/setMinorPic', function () {
                        store.load();
                    }, {
                        id: dataView.getSelectedRecords()[0].get('id')
                    }, "你确定要设置这张图片为副图吗？");

                }
            },
            '-',
            {

                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    store.load();
                }
            } ,
            '-'
        ],
        items: dataView

    });
}


Ext.extend(Picture, Ext.Panel);