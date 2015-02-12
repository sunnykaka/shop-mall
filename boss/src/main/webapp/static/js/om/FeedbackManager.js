/**
 *  信息反馈的管理JS
 */

Desktop.FeedbackWindow = Ext.extend(Ext.app.Module, {

    id: 'Feedback-win',

    title: '信息反馈',

    createFeedBackGrid: function () {

        var feedbackStore = getStore("feedback/list", Ext.Model.FeedBack);

        var feedbackGrid = new Ext.grid.GridPanel({
            store: feedbackStore,
            region: 'north',
            autoScroll: true,
            loadMask: true,
            height: 500,
            columns: [
                { header: 'ID', sortable: true, dataIndex: 'id'},
                { header: '反馈类型', sortable: true, dataIndex: 'type'},
                { header: '联系方式', sortable: true, dataIndex: 'information'}
            ],

            sm: new Ext.grid.RowSelectionModel({
                singleSelect: true
            }),
            viewConfig: {
                forceFit: true
            },

            tbar: [
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        feedbackGrid.getStore().reload();
                    }
                }

            ],

            bbar: new Ext.PagingToolbar({
                pageSize: 31,
                store: feedbackStore,
                displayInfo: true
            })
        });


        var tpl = new Ext.XTemplate(
            '<div class="view-text">{content}</div>',
            '<tpl if="this.isImg(fileType) == false && !Ext.isEmpty(fileType)">',
            '<a href="feedback/downloadFile/{id}">下载附件</a>',
            '</tpl>',
            '<tpl if="this.isImg(fileType) == true">',
            '<img src="feedback/img/{id}"/>',
            '</tpl>',

            {
                isImg: function (type) {
                    return (type == "image/jpeg" || type == "image/png" || type == "image/bmp" || type == "image/gif");
                }
            }
        );


        feedbackGrid.getSelectionModel().on('rowselect', function (sm, rowIdx, r) {
            var detailPanel = Ext.getCmp('detailPanel');
            tpl.overwrite(detailPanel.body, r.data, true).slideIn('l', {stopFx: true, duration: .2});
            detailPanel.show();
            Ext.getCmp('Feedback-win').setHeight(document.body.clientHeight*0.85);
            Ext.getCmp('Feedback-win').doLayout();
        });

        return feedbackGrid;

    },


    createDetailPanel: function () {

        return {
            id: 'detailPanel',
            region: 'center',
            autoScroll: true,
            hidden:true,
            bodyStyle: {
                background: '#ffffff'
            }

        }

    },

    createWindow: function () {
        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            pageX:document.body.clientWidth*0.3,
            pageY:height*0.1,
            width: 800,
            height: height * 0.54,
            title: this.title,
            layout: 'border',
            items: [this.createFeedBackGrid(), this.createDetailPanel()]
        });
    }
});