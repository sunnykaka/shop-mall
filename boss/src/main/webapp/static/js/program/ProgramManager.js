Desktop.ProgramWindow = Ext.extend(Ext.app.Module, {
    id:'Program-win',
    title:'设置终端首页图片',
    getData : function (type) {
        var obj;
        var value;
        if (window.ActiveXObject) {
            obj = new ActiveXObject('Microsoft.XMLHTTP');
        } else if (window.XMLHttpRequest) {
            obj = new XMLHttpRequest();
        }
        var url = '/program/query?type='+type;
        obj.open('GET', url, false);
        obj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        obj.send(null);
        var resp = Ext.decode(obj.responseText);
        return resp;
    },
    getFields : function (arr,anchor,fieldPrefix,namePrefix) {
        var size = arr.length;
        var fields = [];
        for (var i = 0; i < size; i++) {
            var field = new Ext.form.TextField({
                anchor : anchor,
                fieldLabel : fieldPrefix+(i+1),
                name : namePrefix+(i+1),
                allowBlank : true,
                value : arr[i]
            });
            fields.push(field);
        }
        return fields;
    },
    getHiddenIdFields : function (arr,prefix) {
        var size = arr.length;
        var fields = [];
        for (var i = 0; i < size; i++) {
            var field = new Ext.form.Hidden({
                name : prefix + (i+1),
                value : arr[i]
            });
            fields.push(field);
        }
        return fields;
    },
    submitFunction : function (form,size,type) {
        var submitForm = form.getForm();
        var s_imgUrl = [];
        var s_pro = [];
        var s_id = [];
        for(var i = 0; i < size; i++){
            var url = submitForm.findField("imgUrl"+(i+1)).getValue();
            s_imgUrl.push(url);
            
            var pro = submitForm.findField("productId"+(i+1)).getValue();
            s_pro.push(pro);
            
            var key = submitForm.findField("id"+(i+1)).getValue();
            s_id.push(key);
        }
        submitForm.setValues({
            ids : s_id,
            productId : s_pro,
            imgUrl : s_imgUrl
        });
        
        if(submitForm.isValid){
            submitForm.submit({
                url : '/program/update?type='+type,
                method : "POST",
                success:function (form, action) {
                    var resp = Ext.decode(action.response.responseText);
                    if(resp.success){
                        Ext.Msg.alert('成功', '修改成功');
                    } else {
                        Ext.Msg.alert('错误', result.msg);
                    }
                },
                failure :function(form, action) {
                    Ext.Msg.alert('错误', '失败！');
                }
            });
        }
    },
    createHotUploadForm : function (title,type) {
        var resp = this.getData(type);
        var submitAction = this.submitFunction;

        var urls = resp.data.object.imgUrl;
        var pros = resp.data.object.productId;
        var ids = resp.data.object.ids;

        var _length = urls.length;
        
        var url_items = this.getFields(urls,'90%','图片url','imgUrl');
        var pro_items = this.getFields(pros,'50%','商品ID','productId');
        var id_items = this.getHiddenIdFields(ids,'id');

        var form = new Ext.FormPanel({
            id : 'hot-form',
            region : 'north',
            autoScroll : true,
            height : document.body.clientHeight * 0.50,
            frame : true,
            border : true,
            title : title,
            items:[{
                    layout:'form',
                    border:false,
                    buttonAlign:'center',
                    items :[{
                        layout:'column',
                        border:false,
                        labelWidth:55,
                        items:[{
                                layout:'form',
                                columnWidth:0.5,
                                items:url_items
                            },{
                                layout:'form',
                                columnWidth:0.5,
                                items:pro_items
                            },id_items,{
                                xtype : "hidden",
                                name : "ids"
                            },{
                                xtype : "hidden",
                                name : "productId"
                            },{
                                xtype : "hidden",
                                name : "imgUrl"
                            }
                        ]
                    }]
                }
            ],
            buttons : [{
                text : '添加',
                handler : function () {
                    _length ++ ;
                    var sF = Ext.getCmp('hot-form');
                    var imgField = new Ext.form.TextField({
                        anchor : '90%',
                        fieldLabel : '图片url' + _length,
                        name : 'imgUrl' + _length,
                        allowBlank : true,
                        value : ''
                    });
                    var prodField = new Ext.form.TextField({
                        anchor : '50%',
                        fieldLabel : '商品ID' + _length,
                        name : 'productId' + _length,
                        allowBlank : true,
                        value : ''
                    });
                    var idField = new Ext.form.Hidden({
                        name : 'id' + _length,
                        value : 'newData'
                    });
                    sF.add({
                        layout : 'column',
                        border : false,
                        labelWidth : 55,
                        items : [{
                            layout : 'form',
                            columnWidth : 0.5,
                            items : [imgField]
                        },{
                            layout : 'form',
                            columnWidth : 0.5,
                            items : [prodField]
                        },{
                            items :[idField]
                        }]
                    });
                    sF.doLayout(true);
                }
            },{
                text : '确定',
                handler : function(){
                	submitAction(form,_length,type);
                }
            }]
        });
       return form;
    },
    createUploadForm:function (title,type) {
    	var resp = this.getData(type);
        var submitAction = this.submitFunction;

        var urls = resp.data.object.imgUrl;
        var pros = resp.data.object.productId;
        var ids = resp.data.object.ids;
        var _length = urls.length;
    	
    	var url_items = this.getFields(urls,'90%','图片url','imgUrl');
    	var pro_items = this.getFields(pros,'50%','商品ID','productId');
    	var id_items = this.getHiddenIdFields(ids,'id');
    	
    	var form = new Ext.FormPanel({
            region : 'north',
            height : document.body.clientHeight * 0.50,
            frame : true,
	        border : true,
            title : title,
            items:[{
                    layout:'form',
                    border:false,
                    buttonAlign:'center',
                    items :[{
                        layout:'column',
                        border:false,
                        labelWidth:55,
                        items:[{
                                layout:'form',
                                columnWidth:0.5,
                                items:url_items
                            },{
                            	layout:'form',
                                columnWidth:0.5,
                                items:pro_items
                            },id_items,{
                            	xtype : "hidden",
                            	name : "ids"
                            },{
                            	xtype : "hidden",
                            	name : "productId"
                            },{
                            	xtype : "hidden",
                            	name : "imgUrl"
                            }
                        ]
                    }]
                }
            ],
            buttons : [{
                text : '确定',
             	handler : function(){
             		submitAction(form,_length,type);
             	}
            }]
        });
       return form;
    },
    
    createSkinResourcePanel : function (){
	     var styleTab = new Ext.TabPanel({
            deferredRender: false,
            region:'center',
            items: [this.createUploadForm("头部","head"),this.createUploadForm("猜你喜欢","like"),this.createHotUploadForm("热销商品","hot")]
        });
        styleTab.setActiveTab(0);
	    return styleTab;
    },
    
    createWindow:function () {
        var tabPanel = this.createSkinResourcePanel();
        return this.app.getDesktop().createWindow({
            id:this.id,
            title:this.title,
            border:false,
            height:document.body.clientHeight * 0.60,
            width : 700,
            layout:'form',
            items:[tabPanel]
        });
    }

});
