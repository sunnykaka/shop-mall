/**
 * 短信发送管理JS
 */
Ext.Loader.load([/*'/static/js/sms/SmsCharacter.js', */'/static/js/sms/SmsMould.js', '/static/js/sms/SmsPreview.js', '/static/js/sms/SmsSendHosiery.js']);

Desktop.SmsWindow = Ext.extend(Ext.app.Module, {

    id:'Sms-win',

    title:'短信发送管理',
    createSmsForm: function() {

        /*var smsCharacterStore = new Ext.data.Store({
            reader:new Ext.data.JsonReader({
                fields:[ 'value', 'name' ],
                root:'data.result'
            }),
            proxy:new Ext.data.HttpProxy({
                url:'sms/smsCharacter/listComboBox',
                method:'GET'
            })
        });
        smsCharacterStore.load();

        var smsCharacterCombo = new Ext.ux.form.LovCombo({
            id:'smsCharacterCombo',
            hideOnSelect:false,
            hiddenName:'smsCharacter',
            fieldLabel:'选择字符',
            store:smsCharacterStore,
            triggerAction:'all',
            mode:'local',
            valueField:'value',
            value:'mobile',
            displayField:'name',
            showSelectAll   : true,
            resizable       : true,
            allowBlank:false

        });*/

        var UserInfoFile = new Ext.ux.form.FileUploadField({
            name:'uploadFile',
            fieldLabel:'用户信息',
            allowBlank:false,
            buttonText:'请选择Excel文件'
        });

        var smsMouldStore = new Ext.data.Store({
            reader:new Ext.data.JsonReader({
                fields:[ 'id', 'description' ],
                root:'data.result'
            }),
            proxy:new Ext.data.HttpProxy({
                url:'sms/smsMould/listComboBox',
                method:'GET'
            })
        });
        smsMouldStore.load();
        var smsMouldComboBox = new Ext.form.ComboBox({
            id:'smsMouldComboBox',
            store:smsMouldStore,
            triggerAction:'all',
            mode:'local',
            fieldLabel:'选择模板',
            valueField:'id',
            displayField:'description',
            listeners:{
                select:function (combo, record, index) {
                    doAjax('sms/smsMould/content/' + combo.getValue(), function (obj) {
                        var content = obj.data.content;
                        form.getForm().findField('mouldContent').setValue(content);
                    });
                }
            }
        });

        var form = new Ext.FormPanel({
            baseCls:'x-plain',
            region:'north',
            bodyStyle:'padding:10px 10px 50px 10px',
            labelWidth:80,
            buttonAlign:'center',
            height:document.body.clientHeight * 0.30,
            defaults:{
                anchor:'100%',
                xtype:'textfield'
            },
            fileUpload:true,
            items:[
                // smsCharacterCombo,
                UserInfoFile,
                smsMouldComboBox,
                {
                    fieldLabel:'模板内容',
                    name:'mouldContent',
                    height:200,
                    allowBlank:false,
                    xtype:'textarea'
                }
            ],
            buttons:[
                {
                    text:'提交',
                    handler:function () {
                        commitForm(form, function (action, obj) {
                            Ext.Msg.alert('成功', '成功发送信息');
                        }, '/sms/send')
                    }
                } ,
                {
                    text:'预览',
                    handler:function () {
                        commitForm(form, function (action, obj) {
                            var smsSendList = action.result.data.result;
                            var smsTab = Ext.getCmp('smsTab');
                            smsTab.removeAll();
                            smsTab.add(new SmsMould());
                            //smsTab.add(new SmsCharacter());
                            smsTab.add(new SmsSendHosiery());
                            smsTab.add(new SmsPreview(smsSendList));
                            //smsTab.activate(3);
                            smsTab.activate(2);

                        }, '/sms/send/preview')
                    }
                },
                {
                    text:'重置',
                    handler:function () {
                        form.getForm().reset();
                    }
                }
            ]
        });
        return form;
    },

    createSmsGrid:function () {
        var smsTab = new Ext.TabPanel({
            id:'smsTab',
            region:'center',
            deferredRender:false,
            items:[new SmsMould(), /*new SmsCharacter(),*/ new SmsSendHosiery()]
        });
        smsTab.activate(0);
        return smsTab;
    },
    createWindow:function () {
        return this.app.getDesktop().createWindow({
            id:this.id,
            title:this.title,
            layout:'border',
            border:false,
            autoScroll:true,
            height:document.body.clientHeight * 0.60,
            width:750,
            items:[this.createSmsForm(), this.createSmsGrid()]
        });
    }

});
