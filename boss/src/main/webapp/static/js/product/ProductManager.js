/**
 * 实现商品管理的js
 */

Ext.Loader.load(['/static/js/product/ProductModel.js']);

//创建商品发布窗口
var createProductPublishWindow = function (node, e) {

    var treeMenu = new Ext.menu.Menu({
        items: [
            {
                text: '发布商品',
                handler: function () {
                    var cid = node.id;
                    var form = new Ext.FormPanel({
                        baseCls: 'x-plain',
                        labelWidth: 80,
                        autoScroll:true,
                        frame: true,
                        url: 'product/new',
                        width: 500,
                        defaults: {
                            width: 350
                        }
                    });
                    form.add({
                        xtype: 'hidden',
                        name: 'categoryId',
                        value: cid
                    });

                    var win = new Ext.Window({
                        width: 500,
                        layout: 'fit',
                        plain: true,
                        buttonAlign: 'center',
                        bodyStyle: 'padding:5px;',
                        buttons: [
                            {
                                text: '保存',
                                handler: function (button) {
                                    button.setDisabled(true);
                                    commitForm(form, function () {
                                        Ext.Msg.alert("成功", "成功发布商品");
                                        productGrid.getStore().reload();
                                        win.close();
                                    });
                                    button.setDisabled(false);
                                }
                            },
                            {
                                text: '取消',
                                handler: function () {
                                    win.close();
                                }
                            }
                        ]
                    });

                    win.setTitle('在类目' + node.text + '下发布商品');

                    var callback = function () {

                        form.add({
                            fieldLabel: '商品编号',
                            name: 'productCode',
                            xtype: 'textfield',
                            regex: /^\S+$/,
                            allowBlank: false
                        });

                        form.add({
                            fieldLabel: '商品名',
                            name: 'name',
                            xtype: 'textarea',
                            maxLength: 100,
                            allowBlank: false
                        });
                        form.add({
                            fieldLabel: '推荐理由',
                            name: 'description',
                            xtype: 'textarea',
                            maxLength: 100
                        });

                        form.add({
                            xtype: 'combo',
                            hiddenName: 'storeStrategy',
                            fieldLabel: '库存策略',
                            mode: 'local',
                            editable: false,
                            triggerAction: 'all',
                            store: new Ext.data.ArrayStore({
                                fields: [ 'type', 'value' ],
                                data: [
                                    [ '拍下减库存', 'NormalStrategy' ],
                                    [ '付款成功减库存', 'PayStrategy' ]
                                ]
                            }),
                            displayField: 'type',
                            valueField: 'value',
                            value: 'NormalStrategy',
                            allowBlank: false
                        });

                        win.add(form);
                        var height=0;
                        form.items.each(function(item,index,length){
                            if(item.items!=undefined){
                                /*判断多选框的个数 输出了多少行*/
                                height=height+Math.ceil(item.items.length/5);
                            }else{
                                /*出去隐藏控件*/
                                if(item.xtype!='hidden'){
                                    height=height+1;
                                }

                            }
                        });
                        height=height*22+84+(height)*8;
                        if(height>document.body.clientHeight*0.8){
                            form.setHeight(document.body.clientHeight*0.8) ;
                            form.doLayout();
                        }
                        win.show();
                    };

                    buildDynamicProductForm(form, cid, callback);

                }
            }
        ]
    });

    treeMenu.showAt(e.getXY());
};

//创建商品表单的动态字段
var buildDynamicProductForm = function (form, cid, callback) {

    //先请求品牌属性的pid
    Ext.Ajax.request({
        url: 'product/brand/property/id',
        success: function (response, options) {
            var jsonObject = Ext.util.JSON.decode(response.responseText);
            if (jsonObject.success == false) {
                Ext.Msg.alert('错误', jsonObject.msg);
                return;
            }
            var brandPid = jsonObject.data.brandPid;

            var customerStore = new Ext.data.Store({
                reader: new Ext.data.JsonReader({
                    fields: [ 'name', 'id'],
                    root: 'data.customers'
                }),
                proxy: new Ext.data.HttpProxy({
                    url: 'product/customer/list',
                    method: 'GET'
                })
            });

            customerStore.load();

            var brandStore = new Ext.data.Store({
                reader: new Ext.data.JsonReader({
                    fields: [ 'name', 'id'],
                    root: 'data.brands'
                }),
                proxy: new Ext.data.HttpProxy({
                    url: 'product/customer/brand/list',
                    method: 'GET'
                })
            });

            //将商家和品牌放到前面
            var customerCombo = {
                xtype: 'combo',
                hiddenName: 'customerId',
                fieldLabel: '选择商家',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: customerStore,
                displayField: 'name',
                valueField: 'id',
                allowBlank: false,
                listeners: {
                    select: function (combo, record, index) {
                        doAjax('product/customer/store/number/' + combo.getValue(), function (obj) {
                            brandCombo.reset();
                            var StackNumber = obj.data.storeNumber;
                            if (StackNumber > 0) {
                                brandStore.load({
                                    params: {
                                        customerId: combo.getValue()
                                    }
                                });
                            } else {
                                Ext.Msg.alert('警告', '此商家还没添加库位,请到商家管理添加！');
                            }
                        });
                    }
                }
            };

            var brandCombo = new Ext.form.ComboBox({
                hiddenName: brandPid,
                fieldLabel: '选择品牌',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '请选择',
                store: brandStore,
                displayField: 'name',
                valueField: 'id',
                allowBlank: false
            });

            form.add(customerCombo);
            form.add(brandCombo);

            //加载所有类目属性，数据类似如下格式
            //{"rows":[{"name":"价格区间","multiValue":false,"cid":5,"pid":6},{"name":"品牌","multiValue":false,"cid":5,"pid":3},{"name":"茶盘形状","multiValue":true,"cid":5,"pid":7},{"name":"茶盘材质","multiValue":true,"cid":5,"pid":5},{"name":"颜色","multiValue":true,"cid":5,"pid":25},{"name":"xxxxx","multiValue":true,"cid":5,"pid":28},{"name":"eeee","multiValue":false,"cid":5,"pid":29}]}
            var store = new Ext.data.Store({
                reader: new Ext.data.JsonReader({
                    fields: [ 'name', 'cid', 'pid', 'multiValue'],
                    root: 'data.cps'
                }),
                proxy: new Ext.data.HttpProxy({
                    url: 'product/category/property/list?type=all&categoryId=' + cid,
                    method: 'GET'
                })
            });

            //把这个类目下的多值的属性和值读过来，是一个map,key是cid-pid,value是一个数组，这个map用来构建checkbox处理多值属性
            //类似如下数据
            //{"5-5":[{"name":"塑料","cid":5,"pid":5,"vid":5},{"name":"金属","cid":5,"pid":5,"vid":4}],"5-7":[{"name":"圆形","cid":5,"pid":7,"vid":133},{"name":"方形","cid":5,"pid":7,"vid":134}],"5-28":[{"name":"ddd","cid":5,"pid":28,"vid":142},{"name":"ggggg","cid":5,"pid":28,"vid":143}],"5-25":[{"name":"红色","cid":5,"pid":25,"vid":135},{"name":"绿色","cid":5,"pid":25,"vid":136}]}
            var multiMap;
            Ext.Ajax.request({
                url: 'product/category/property/value/multi/list?type=all&categoryId=' + cid,
                success: function (response, options) {
                    multiMap = Ext.util.JSON.decode(response.responseText);
                    store.load({callback: function (r) {
                        //遍历所有类目属性，一个个的构建表单元素
                        for (var i = 0; i < r.length; i++) {

                            var cp = r[i];

                            var multiValue = cp.get('multiValue');

                            //属性里过滤掉品牌
                            if (cp.get('name') != '品牌') {
                                if (multiValue) {  //如果是多值类目属性，构建checkbox
                                    var cpvs = multiMap[cid + '-' + cp.get("pid")]; //去预先读取的值映射里拿到多值
                                    var items = [];
                                    Ext.each(cpvs, function (cpv) {
                                        items.push({boxLabel: cpv.name, name: cpv.pid, inputValue: cpv.vid, labelAlign: 'left'});
                                    });
                                    if (items.length == 0) {
                                        Ext.Msg.alert("错误", "该类目发现有属性没有设定值，请先在类目中心设值");
                                    }
                                    var group = {
                                        xtype: 'checkboxgroup',
                                        columns: 5,
                                        fieldLabel: cp.get('name'),
                                        items: items,
                                        allowBlank: false
                                    };

                                    form.add(group);

                                } else {//如果不是多值属性，则构建一个combx，这样值是用户点击之后触发加载
                                    var field = {
                                        xtype: 'combo',
                                        hiddenName: cp.get('pid'),
                                        fieldLabel: cp.get('name'),
                                        mode: 'remote',
                                        editable: false,
                                        triggerAction: 'all',
                                        emptyText: '请选择',
                                        store: new Ext.data.Store({
                                            reader: new Ext.data.JsonReader({
                                                fields: [ 'name', 'cid', 'pid', 'vid'],
                                                root: 'data.result',
                                                idProperty: 'name'
                                            }),
                                            proxy: new Ext.data.HttpProxy({
                                                url: 'product/category/property/value/list/' + cid + '/' + cp.get('pid'),
                                                method: 'GET'
                                            })
                                        }),
                                        displayField: 'name',
                                        valueField: 'vid',
                                        allowBlank: false
                                    };
                                    form.add(field);
                                }
                            }

                        }
                        callback();//表单构建完毕之后回调
                    }});

                },
                failure: function () {
                    Ext.Msg.alert("错误", "读取数据失败");
                }
            });

        }
    });
};

//商品修改属性函数
var productPropertyUpdate = function (type, product, commitUrl) {
    var cid = product.get('categoryId');
    var getPvUrl = 'product/category/product/cpv/map?type=' + type + '&productId=' + product.get('id');
    var getCpUrl = 'product/category/property/list?type=' + type + '&categoryId=' + cid;
    var getMultiPvUrl = 'product/category/property/value/multi/list?type=' + type + '&categoryId=' + cid;

    var productCPVMap;
    //加载商品的每个属性和它的值的id，key是pid,value是vid数组
    //类似格式：{"3":[131],"5":[5,4],"6":[129],"7":[133,134],"25":[135,136],"29":[144],"28":[142,143]}

    doAjax(getPvUrl, function (obj) {
        productCPVMap = obj.data.map
    });


    var comboxHolder = {}; //combox容器


    var store = new Ext.data.Store({//加载所有类目属性
        reader: new Ext.data.JsonReader({
            fields: [ 'name', 'cid', 'pid', 'multiValue'],
            root: 'data.cps'
        }),
        proxy: new Ext.data.HttpProxy({
            url: getCpUrl,
            method: 'GET'
        })
    });

    var multiMap;

    Ext.Ajax.request({
        url: getMultiPvUrl, //加载这个类目下的所有多值
        success: function (response, options) {
            multiMap = Ext.util.JSON.decode(response.responseText);
            store.load({callback: function (r) {
                if (r.length == 0) {
                    Ext.Msg.alert("信息", "没有属性可修改");
                    return;
                }
                for (var i = 0; i < r.length; i++) {
                    var cp = r[i];
                    var multiValue = cp.get('multiValue');
                    if (cp.get('name') != '品牌') {
                        //如果是多值属性，则要判断哪些值是在数据库中的，要让checkbox为选定状态
                        if (multiValue) {
                            var cpvs = multiMap[cid + '-' + cp.get("pid")];//拿到这个属性的所有cpv
                            var items = [];
                            var checkedItems = productCPVMap[cp.get("pid")]; //拿到这个商品的某个属性的值
                            if (checkedItems == undefined) {
                                //每个属性肯定是有值的，如果没有的话可能发生类目新加了属性
                                Ext.each(cpvs, function (cpv) {
                                    items.push({boxLabel: cpv.name, name: cpv.pid, inputValue: cpv.vid, labelAlign: 'left'});
                                });
                            } else {
                                Ext.each(cpvs, function (cpv) {
                                    if (checkedItems.indexOf(cpv.vid) == -1) { //判断商品中是否有这个vid，如果没有表示没有选择
                                        items.push({boxLabel: cpv.name, name: cpv.pid, inputValue: cpv.vid, labelAlign: 'left'});
                                    } else {
                                        items.push({boxLabel: cpv.name, name: cpv.pid, inputValue: cpv.vid, checked: true, labelAlign: 'left'});
                                    }
                                });
                            }
                            var group = {
                                xtype: 'checkboxgroup',
                                columns: 3,
                                fieldLabel: cp.get('name'),
                                items: items,
                                allowBlank: false
                            };
                            propertyForm.add(group);

                        } else {
                            //构建comobx，但是要绑定读取到值
                            var store = new Ext.data.Store({
                                autoLoad: true,
                                reader: new Ext.data.JsonReader({
                                    fields: [ 'name', 'cid', 'pid', 'vid'],
                                    root: 'data.result',
                                    idProperty: 'name'
                                }),
                                proxy: new Ext.data.HttpProxy({
                                    url: 'product/category/property/value/list/' + cid + '/' + cp.get('pid'),
                                    method: 'GET'
                                }),
                                listeners: {
                                    load: function (store) { //加载完毕设置combox的值，表单中显示的就是数据库中的值
                                        comboxHolder[store.cp.get('pid')].setValue(productCPVMap[store.cp.get('pid')][0]);
                                    }
                                }
                            });
                            store.cp = cp; //记住这次store关联的类目属性
                            var field = new Ext.form.ComboBox({
                                hiddenName: cp.get('pid'),
                                fieldLabel: cp.get('name'),
                                mode: 'local',
                                editable: false,
                                triggerAction: 'all',
                                store: store,
                                displayField: 'name',
                                valueField: 'vid',
                                allowBlank: false
                            });
                            comboxHolder[cp.get('pid')] = field; //放入容器
                            propertyForm.add(field);
                        }
                    }
                }
                productEditWin.show();

            }});
        },
        failure: function () {
            Ext.Msg.alert("读取数据失败");
        }
    });

    propertyForm = new Ext.FormPanel({
        baseCls: 'x-plain',
        labelWidth: 80,
        frame: true,
        width: 500,
        defaults: {
            width: 350
        }
    });

    propertyForm.add({
        xtype: 'hidden',
        name: 'id',
        value: product.get('id')
    });

    propertyForm.add({
        xtype: 'hidden',
        name: 'categoryId',
        value: product.get('categoryId')
    });


    var productEditWin = new Ext.Window({
        width: 500,
        layout: 'fit',
        plain: true,
        buttonAlign: 'center',
        bodyStyle: 'padding:5px;',
        buttons: [
            {
                text: '更新',
                handler: function (button) {
                    button.setDisabled(true);
                    commitForm(propertyForm, function () {
                        Ext.Msg.alert("成功", "修改成功");
                        productGrid.getStore().reload();
                        productEditWin.close();
                    }, commitUrl);
                    button.setDisabled(false);
                }
            }
        ],
        items: [
            propertyForm
        ]

    });
};


var createContextWindow = function (grid, rowIndex, e) {
    e.preventDefault();//取消默认的浏览器右键事件
    var record = grid.getStore().getAt(rowIndex);
    var online = record.get('online');
    var text = online ? '下架' : '上架';
    var items = [
        {
            text: text + '商品',
            handler: function () {
                doAjax('product/updateOnline', function () {
                    productGrid.getStore().reload();
                }, {
                    id: record.get('id'),
                    online: !online
                }, "你确定要" + text + "这件商品吗？");
            }
        },
        {
            text: '设置SEO推广信息',
            handler: function () {
                SEO(record.get('id')+'号商品',record.get('id'),'PRODUCT');
            }
        },
        {
            text: '加入家有购',
            handler: function () {
                Ext.Ajax.request({
                    url:'/product/jyg',
                    success:function (response, options) {
                        var obj = Ext.decode(response.responseText);
                        if (!obj.success) {
                            Ext.Msg.alert("失败", obj.msg);
                        } else {
                            Ext.Msg.alert("成功", "加入成功");
                        }
                    },
                    failure:function () {
                        Ext.Msg.alert("失败","加入失败");
                    },
                    params:{
                        productId:record.get('id')
                    }
                });
            }
        },
        {
            text: '修改库存策略',
            handler: function () {
                var record = grid.getStore().getAt(rowIndex);
                var strategyUpdateForm = new Ext.FormPanel({
                    baseCls: 'x-plain',
                    labelWidth: 80,
                    url: 'product/updateStrategy',
                    frame: true,
                    width: 500,
                    defaults: {
                        width: 300
                    },
                    items: [
                        {
                            xtype: 'hidden',
                            name: 'id',
                            value: record.get("id")
                        },
                        {
                            xtype: 'combo',
                            hiddenName: 'storeStrategy',
                            fieldLabel: '库存策略',
                            mode: 'local',
                            editable: false,
                            triggerAction: 'all',
                            store: new Ext.data.ArrayStore({
                                fields: [ 'type', 'value' ],
                                data: [
                                    [ '拍下减库存', 'NormalStrategy' ],
                                    [ '付款成功减库存', 'PayStrategy' ]
                                ]
                            }),
                            displayField: 'type',
                            valueField: 'value',
                            allowBlank: false
                        }
                    ]

                });

                var updateStrategy = function () {
                    commitForm(strategyUpdateForm, function () {
                        productGrid.getStore().reload();
                        updateStrategyWindow.close();
                    });
                };

                var updateStrategyWindow = new Ext.Window({
                    title: '修改' + record.get("id") + '号商品库存策略',
                    width: 500,
                    layout: 'fit',
                    plain: true,
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px;',
                    items: strategyUpdateForm,
                    //热键添加
                    keys: [
                        {
                            key: Ext.EventObject.ENTER,
                            fn: updateStrategy //执行的方法
                        }
                    ],
                    buttons: [
                        {
                            text: '保存',
                            // 单击按钮时响应的动作
                            handler: updateStrategy
                        },
                        {
                            text: '取消',
                            handler: function () {
                                updateStrategyWindow.close();
                            }
                        }
                    ]

                });

                updateStrategyWindow.show();

                strategyUpdateForm.load({
                    url: 'product/strategy/' + record.get('id'),
                    success: function (form, action) {
                        var fromValues = Ext.util.JSON.decode(action.response.responseText);
                        strategyUpdateForm.getForm().setValues(fromValues.data);
                    }
                });
            }
        },
        {
            text: '修改商家品牌',
            handler: function () {
                var product = grid.getStore().getAt(rowIndex);

                //先请求品牌属性的pid
                Ext.Ajax.request({
                    url: 'product/brand/property/id',
                    success: function (response, options) {
                        var jsonObject = Ext.util.JSON.decode(response.responseText);
                        var brandPid = jsonObject.data.brandPid;
                        var customerStore = new Ext.data.Store({
                            autoLoad: true,
                            reader: new Ext.data.JsonReader({
                                fields: [ 'name', 'id'],
                                root: 'data.customers'
                            }),
                            proxy: new Ext.data.HttpProxy({
                                url: 'product/customer/list',
                                method: 'GET'
                            }),
                            listeners: {
                                load: function (store) { //加载完毕设置combox的值，表单中显示的就是数据库中的值
                                    customerCombo.setValue(product.get('customerId'));
                                    brandStore.load({
                                        params: {
                                            customerId: product.get('customerId')
                                        },
                                        callback: function () {
                                            brandCombo.setValue(product.get('brandId'));
                                        }
                                    });
                                }
                            }
                        });

                        var brandStore = new Ext.data.Store({
                            reader: new Ext.data.JsonReader({
                                fields: [ 'name', 'id'],
                                root: 'data.brands'
                            }),
                            proxy: new Ext.data.HttpProxy({
                                url: 'product/customer/brand/list',
                                method: 'GET'
                            })
                        });

                        //将商家和品牌放到前面
                        var customerCombo = new Ext.form.ComboBox({
                            hiddenName: 'customerId',
                            fieldLabel: '选择商家',
                            mode: 'local',
                            editable: false,
                            triggerAction: 'all',
                            emptyText: '请选择',
                            store: customerStore,
                            displayField: 'name',
                            valueField: 'id',
                            allowBlank: false,
                            listeners: {
                                select: function (combo, record, index) {
                                    customerStore.removeListener("load");
                                    brandCombo.reset();
                                    brandStore.load({
                                        params: {
                                            customerId: combo.getValue()
                                        }
                                    });
                                }
                            }
                        });

                        var brandCombo = new Ext.form.ComboBox({
                            hiddenName: brandPid,
                            fieldLabel: '选择品牌',
                            mode: 'local',
                            editable: false,
                            triggerAction: 'all',
                            emptyText: '请选择',
                            store: brandStore,
                            displayField: 'name',
                            valueField: 'id',
                            allowBlank: false
                        });

                        FB([
                            {
                                xtype: 'hidden',
                                name: 'productId',
                                value: product.get('id')
                            },
                            customerCombo,
                            brandCombo
                        ], 'product/update/customer/brand', '修改商家品牌信息', false, function () {
                            productGrid.getStore().reload();
                        });
                    },
                    failure: function () {
                        Ext.Msg.alert("错误", "加载品牌属性出错");
                    }
                });

            }
        },
        {
            text: '修改关键属性',
            handler: function () {
                var product = grid.getStore().getAt(rowIndex);
                productPropertyUpdate("KEY_PROPERTY", product, "product/update/key/property");
            }
        }
    ];

    if (!online) {
        items.push({
            text: '修改销售属性',
            handler: function () {
                var product = grid.getStore().getAt(rowIndex);
                productPropertyUpdate("SELL_PROPERTY", product, "product/update/sell/property");
            }
        });
        items.push({
            text: "修改类目",
            handler: function () {
                var product = grid.getStore().getAt(rowIndex);
                var cid = product.get('categoryId');
                //展开商品所在的类目
                Ext.Ajax.request({
                    url: 'category/selectPath/' + cid,
                    success: function (response, options) {
                        var tree = new Ext.tree.TreePanel({
                            rootVisible: false,
                            lines: false,
                            dataUrl: 'category/tree',
                            autoScroll: true,
                            root: {
                                id: -1,
                                nodeType: 'async',
                                expanded: true
                            },
                            listeners: {
                                click: function (node) {
                                    if (node.isLeaf()) {
                                        if (node.id != product.get("categoryId")) {
                                            Ext.Msg.confirm("确认", "你确定修改商品的类目吗？", function (btn) {
                                                if (btn == 'yes') {
                                                    var cid = node.id;

                                                    var form = new Ext.FormPanel({
                                                        baseCls: 'x-plain',
                                                        labelWidth: 80,
                                                        url: 'product/update/category',
                                                        frame: true,
                                                        width: 500,
                                                        defaults: {
                                                            width: 350
                                                        }
                                                    });

                                                    form.add({
                                                        xtype: 'hidden',
                                                        name: 'id',
                                                        value: product.get('id')
                                                    });
                                                    form.add({
                                                        xtype: 'hidden',
                                                        name: 'categoryId',
                                                        value: cid
                                                    });

                                                    var updateCategoryWindow = new Ext.Window({
                                                        title: '重新根据新类目设置属性',
                                                        width: 500,
                                                        layout: 'fit',
                                                        plain: true,
                                                        buttonAlign: 'center',
                                                        bodyStyle: 'padding:5px;',
                                                        buttons: [
                                                            {
                                                                text: '保存',
                                                                handler: function () {
                                                                    commitForm(form, function () {
                                                                        Ext.Msg.alert("成功", "成功修改类目");
                                                                        productGrid.getStore().reload();
                                                                    })
                                                                }
                                                            }
                                                        ]
                                                    });

                                                    var callback = function () {
                                                        updateCategoryWindow.add(form);
                                                        updateCategoryWindow.show();
                                                    };

                                                    buildDynamicProductForm(form, cid, callback);

                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                        tree.selectPath(response.responseText);
                        var win = new Ext.Window({
                            title: '修改所属类目',
                            width: 600,
                            height: 500,
                            layout: 'fit',
                            items: [
                                tree
                            ]
                        });
                        win.show();
                    },
                    failure: function () {
                        Ext.Msg.alert('失败', '加载数据失败');
                    }
                });
            }

        });
    }

    var treeMenu = new Ext.menu.Menu({
        items: items
    });
    treeMenu.showAt(e.getXY());
};


Desktop.ProductWindow = Ext.extend(Ext.app.Module, {
    id: 'Product-win',

    title: '商品管理',

    //创建商品管理窗口
    createWindow: function () {

        var productSelector = buildProductSelector();

        ProductCategoryTree.on('contextmenu', function (node, e) {
            if (node.isLeaf()) {
                createProductPublishWindow(node, e);
            }
        }, this);

        productGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var product = this.getStore().getAt(rowIndex);
            productTab.removeAll(true);
            productTab.add(new Picture(product.get('id')));
            productTab.add(new Title(product));
            productTab.add(new StockPrice(product));
            productTab.add(new KindEditorHtmlDesc(product));
            productTab.add(new Recommend(product.get('id')));
            productTab.add(new AttentionInfo(product.get('id')));
            productTab.activate(0);
        });

        productGrid.on('rowcontextmenu', function (grid, rowIndex, event) {
            createContextWindow(grid, rowIndex, event);
        });

        productTab = new Ext.TabPanel({
            region: 'center',
            deferredRender: false
        });

        var height = document.body.clientHeight;

        //手动设置在西边
        productSelector.region = 'west';

        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '商品管理',
            width: 1000,
            height: height * 0.85,
            layout: 'border',
            items: [
                productSelector,
                productTab
            ]

        });
    }
});

