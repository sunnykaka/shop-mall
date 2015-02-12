Space = function (node) {

    var imageStore = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({
            url: 'spacePicture/queryPictureBySpaceId',
            failure: function (response, opts) {
                if (response.status == 403) {
                    Ext.Msg.alert('错误', '没有设置访问权限');
                    return;
                }
                Ext.Msg.alert('错误', '服务器出错');
            }
        }),
        reader: new Ext.data.JsonReader({
            totalProperty: 'data.totalCount',
            root: 'data.result'
        }, Ext.data.Record.create([
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'pictureName',
                type: 'string'
            },
            {
                name: 'pictureUrl',
                type: 'string'
            }

        ]))
    });
    imageStore.baseParams = {
        spaceId: node.id
    };

    imageStore.load({
        params: {
            start: 0,
            limit: 24,
            spaceId: node.id
        }
    });

    var tpl = new Ext.XTemplate(
        '<tpl for=".">',
        '<div class="thumb-wrap" id="{pictureName}">',
        '<div class="thumb"><img src="{pictureUrl}" title="{pictureName}"></div>',
        '<span class="x-editable">{shortName}</span></div>',
        '</tpl>',
        '<div class="x-clear"></div>'
    );

    var view = new Ext.DataView({
        id: 'spaceImage_view',
        store: imageStore,
        tpl: tpl,
        autoHeight: true,
        multiSelect: true,
        loadingText: '加载进行中....',
        itemSelector: 'div.thumb-wrap',
        plugins: new Ext.DataView.DragSelector({dragSafe: true}),
        emptyText: '该空间没有图片',
        prepareData: function (data) {
            data.shortName = Ext.util.Format.ellipsis(data.pictureName, 15);
            return data;
        }

    });

    Space.superclass.constructor.call(this, {
        id: 'images',
        frame: true,
        title: node.text + '的空间图片',
        autoScroll: true,
        bbar: new Ext.PagingToolbar({
            pageSize: 24,
            store: imageStore,
            displayInfo: true
        }),
        tbar: [
            {
                text: '删除图片',
                iconCls: 'remove',
                handler: function () {
                    var ids = [];
                    if (view.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要删除的图片");
                        return;
                    }
                    Ext.each(view.getSelectedRecords(), function (r) {
                        ids.push(r.get('id'));
                    });

                    doAjax('spacePicture/deleteSpacePicture', function () {
                        view.getStore().reload();
                    }, {ids: ids}, "你确定删除这些图片吗？");

                }
            },
            '-',
            {
                text: '本地上传',
                iconCls: 'add',
                handler: function () {
                    var win = new Ext.Window({
                        id: 'spaceImage_win',
                        title: '多文件上传',
                        width: 500,
                        height: 500,
                        resizable: false,
                        layout: 'fit',
                        items: [
                            {
                                sourceWindowId:'spaceImage_view',
                                xtype: 'uploadpanel',
                                uploadUrl: window.BossHome + '/spacePicture/createSpacePicture/' + node.id,
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
            new Ext.ux.CopyButton({
                text: '复制图片链接',
                iconCls: 'run',
                getValue: function () {
                    if (view.getSelectedRecords().length == 0) {
                        Ext.Msg.alert("信息", "请选择要复制的图片链接");
                        return;
                    }
                    var pictureUrls = [];
                    Ext.each(view.getSelectedRecords(), function (r) {
                        pictureUrls.push(r.get('pictureUrl'));
                    });
                    return pictureUrls[0];
                }
            })
        ],
        border: false,
        items: view
    });

}

Ext.extend(Space, Ext.Panel);