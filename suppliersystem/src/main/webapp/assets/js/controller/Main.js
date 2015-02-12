/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 主控制器
 */

Ext.define('Supplier.controller.Main', {//定义类
    extend: 'Ext.app.Controller',//一定要继承Controller
    //添加views，让控制器找到
    views: ['MainMenu', 'OrderManage', 'Statement', 'OrderList', 'Inventory', 'ExamineGood', 'Logistics', 'ModifyPassword'],
    stores: ['OrderManage', 'GoodList', 'Statement', 'OrderList', 'Inventory', 'Storage'],
    models: ['OrderManage', 'GoodList', 'Statement', 'OrderList', 'Inventory'],
    //自动调用此方法
    init: function () {
        this.control({
            //订单管理页

            '#mainMenu #menuSelectWrap': {
                click: function () {
                    if (window.isMainAccount) {
                        Ext.getCmp('menuUserLog').show();
                        Ext.getCmp('menuUserList').show();
                    }
                }
            },

            //功能菜单切换
            "#mainMenu #menuSelect": {
                click: function (menu, item) {
                    Ext.getCmp('mainViewPort').removeAll();
                    Ext.getCmp('mainViewPort').add(Ext.widget(item.itemId));
                    if (item.itemId == "orderManage") {
                        this.reloadGridData();
                    }
                }
            },

            //验货
            "#mainMenu #examineGood": {
                click: function () {
                    Ext.widget('examineGood').show();
                }
            },

            // 修改密码
            "#mainMenu #modifyPassword": {
                click: this.openModifyPasswordWin
            },

            // 保存修改密码表单
            '#modifyPassword #saveBtn': {
                click: this.savePassword
            },

            // 重置修改密码表单
            '#modifyPassword #resetBtn': {
                click: this.resetForm
            },

            // 退出登录
            "#mainMenu #logout": {
                click: this.logout
            },

            //订单四种状态切换
            "#orderListSearch button[belong=mainBtn]": {
                click: function (button) {
                    this.changeMainBtnState(button);
                }
            },

            //订单刷新
            "#orderListGrid #refresh": {
                click: function () {
                    this.reloadGridData();
                }
            },

            //订单搜索
            "#orderListSearch #searchBtn": {
                click: function (button) {
                    this.reloadGridData();
                }
            },

            //搜索重置
            "#orderListSearch #resetBtn": {
                click: function (button) {
                    var orderState = this.getOrderState();
                    button.up('form').getForm().reset();
                    this.setOrderState(orderState);
                    this.reloadGridData();
                }
            },

            //订单表格
            "#orderListGrid": {
                //订单选中切换
                selectionchange: function (sm, records) {
                    var orderItem = Ext.getCmp('orderItem');
                    if (records[0]) {
                        this.changeSubBtn(this.getOrderState(), {disabled: false});

                        if (Ext.getCmp('orderListGrid').down('#isOrderItemShow').pressed == true) {
                            var activeTabItem = orderItem.getActiveTab().items.items[0];
                            var url = activeTabItem.getStore().getProxy().url;
                            sourceUrl = url.match(/^\/\w+/)[0];
                            var extra = orderItem.getActiveTab().itemId === "order" ? "/items" : '';
                            activeTabItem.getStore().getProxy().url = sourceUrl + ("/" + records[0].get('id') + extra);
                            activeTabItem.getStore().load();
                            orderItem.show();
                        }

                    } else {
                        this.changeSubBtn(this.getOrderState(), {disabled: true});
                        orderItem.hide();
                    }
                },
                //订单物流编号编辑
                edit: function (editor, e) {
                    var root = this;
                    Ext.Ajax.request({
                        url: '/orders/' + e.record.get('id'),
                        params: {
                            waybillNumber: e.value
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText);
                            if (data.success) {
                                root.tipMsg('成功', '物流编号修改成功');
                            } else {
                                Ext.Msg.show({
                                    title: '错误',
                                    msg: data.msg,
                                    buttons: Ext.Msg.YES,
                                    icon: Ext.Msg.WARNING
                                });
                            }

                        },
                        failure: function () {
                            Ext.Msg.show({
                                title: '错误',
                                msg: '服务器错误，请重新提交!',
                                buttons: Ext.Msg.YES,
                                icon: Ext.Msg.WARNING
                            });
                        }
                    });
                },
                //判断是否可以编辑
                beforeedit: function () {
                    if (this.getOrderState() != "Confirm") return false;
                }
            },

            //订单详情tab栏改变
            "#orderItem": {
                tabchange: function (tab, newItem, oldItem) {
                    var extra = newItem.itemId === "order" ? "/items" : '';
                    var url = newItem.items.items[0].getStore().getProxy().url;
                    sourceUrl = url.match(/^\/\w+/)[0];
                    newItem.items.items[0].getStore().getProxy().url = sourceUrl + ("/" + Ext.getCmp('orderListGrid').getSelectionModel().getSelection()[0].get('id') + extra);
                    newItem.items.items[0].getStore().load();
                }
            },

            //订单toolbar事件
            //订单汇总按钮
            "#orderListGrid #orderSummary": {
                click: function () {
                    var url = '/orders/summary_report/excel?orderIds=' + this.getGridSelType('id').join(',');
                    window.open(url);
                }
            },

            //打印物流单
            "#orderListGrid #printPreview": {
                click: this.printPreview
            },

            //打印发货单
            "#orderListGrid #printInvoice": {
                click: this.printInvoice
            },

            //批量生成物流单号
            "#orderListGrid #generateSN": {
                click: function () {
                    var root = this,
                        number = Ext.getCmp('batch_exp_num').getValue(),
                        arr = [],
                        autoAdd = 5,
                        selItems = this.getGridSel();
                    Ext.each(selItems, function (item, index, items) {
                        if (arr[0] && item.get('deliveryTypePY') != arr[0]) {
                            Ext.Msg.alert('警告', '选中订单的配送方式必须相同');
                            arr = null;
                            return false;
                        } else {
                            arr.push(item.get('deliveryTypePY'));
                        }
                    });

                    if (!arr) return false;

                    //法一、后台处理自增
                    Ext.Ajax.request({
                        url: '/orders',
                        method: 'POST',
                        params: {
                            deliveryType: arr[0],
                            orderIds: this.getGridSelType('id').join(','),
                            waybillNumber: number
                        },
                        success: function (response) {
                            var resouceData = Ext.decode(response.responseText);
                            if (resouceData.success) {
                                root.tipMsg('成功', '物流编号修改成功');
                                Ext.each(selItems, function (item, index, items) {
                                    item.set('waybillNumber', resouceData.data.list[index]);
                                });
                            } else {
                                Ext.Msg.show({
                                    title: '错误',
                                    msg: resouceData.msg,
                                    buttons: Ext.Msg.YES,
                                    icon: Ext.Msg.WARNING
                                });
                            }


                        },
                        failure: function () {
                            Ext.Msg.show({
                                title: '错误',
                                msg: '服务器错误2，请重新提交!',
                                buttons: Ext.Msg.YES,
                                icon: Ext.Msg.WARNING
                            });
                        }
                    });
                }
            },

            //确认打印
            "#orderListGrid #printLogistics": {
                click: function () {
                    var root = this;
                    Ext.Msg.show({
                        title: '修改状态?',
                        msg: '您确定要修改选中订单状态吗?',
                        buttons: Ext.Msg.YESNO,
                        icon: Ext.Msg.QUESTION,
                        fn: function (flag) {
                            flag == "yes" && doPrint();
                        }
                    });
                    function doPrint() {
                        var selItems = root.getGridSel(),
                            arr = [];

                        Ext.each(selItems, function (item, index, items) {
                            if (!item.get('waybillNumber') || item.get('waybillNumber') == "" || item.get('waybillNumber') == "null") {
                                Ext.Msg.alert('警告', '选中订单中有数据还没有加物流编号');
                                arr = null;
                                return false;
                            }
                            arr.push(item.get('id'));
                        });
                        if (!arr) return;

                        Ext.Ajax.request({
                            url: '/orders/status',
                            method: 'POST',
                            params: {
                                orderIds: arr.join(','),
                                status: 3
                            },
                            success: function (response) {
                                var resouceData = Ext.decode(response.responseText);
                                if (resouceData.success) {
                                    root.reloadGridData(true);
                                }
                            }
                        });
                    }
                }

            },

            //返回上一级1
            "#orderListGrid #goBack_1": {
                click: function () {
                    var root = this;
                    Ext.Ajax.request({
                        url: '/orders/status',
                        method: 'POST',
                        params: {
                            orderIds: this.getGridSelType('id').join(','),
                            status: 0
                        },
                        success: function (response) {
                            root.reloadGridData(Ext.decode(response.responseText).success);
                        }
                    });
                }
            },

            //批量验货
            "#orderListGrid #batchInspection": {
                click: function () {
                    var root = this;
                    Ext.Msg.show({
                        title: '修改状态?',
                        msg: '您确定要修改选中订单状态吗?',
                        buttons: Ext.Msg.YESNO,
                        icon: Ext.Msg.QUESTION,
                        fn: function (flag) {
                            flag == "yes" && doCheck();
                        }
                    });

                    function doCheck() {
                        Ext.Ajax.request({
                            url: '/orders/status',
                            method: 'POST',
                            params: {
                                orderIds: root.getGridSelType('id').join(','),
                                status: 2
                            },
                            success: function (response) {
                                root.reloadGridData(Ext.decode(response.responseText).success);
                            }
                        });
                    }

                }
            },

            //返回上一级2
            "#orderListGrid #goBack_2": {
                click: function () {
                    var root = this;
                    Ext.Ajax.request({
                        url: '/orders/status',
                        method: 'POST',
                        params: {
                            orderIds: this.getGridSelType('id').join(','),
                            status: 0
                        },
                        success: function (response) {
                            root.reloadGridData(Ext.decode(response.responseText).success);
                        }
                    });
                }
            },

            //确认发货
            "#orderListGrid #confirmationDelivery": {
                click: function () {
                    var root = this;
                    Ext.Msg.show({
                        title: '确定发货?',
                        msg: '确定发货后,此状态将不可修改?',
                        buttons: Ext.Msg.YESNO,
                        icon: Ext.Msg.QUESTION,
                        fn: function (flag) {
                            flag == "yes" && doSend();
                        }
                    });

                    function doSend() {
                        Ext.Ajax.request({
                            url: '/orders/status',
                            params: {
                                orderIds: root.getGridSelType('id').join(','),
                                status: 1
                            },
                            success: function (response) {
                                root.reloadGridData(Ext.decode(response.responseText).success);
                            }
                        });
                    }


                }
            },

            //导出表格
            "#orderListGrid #orderExport": {
                click: function () {
                    var searchFormVal = Ext.getCmp('orderListSearch').getValues(),
                        day = new Date(),
                        dayFormat = day.getFullYear()+'-'+day.getMonth()+'-'+day.getDate();
                    /*Ext.Ajax.request({
                        url: '/orders/excel-'+dayFormat+'.XLS',
                        params: searchFormVal
                    });*/
                    function parseParam (obj){
                        var str = '';
                         for (var key in obj){
                             if ( obj.hasOwnProperty(key)){
                                str += '&'+key+'='+encodeURIComponent(obj[key]);
                             }
                         }
                        str = str.substr(1);
                        return str;
                    }
                    window.open('/orders/excel-'+dayFormat+'.xls?'+parseParam(searchFormVal));
                }
            },

            //验货页
            //验单
            "#examineGood #orderSearch": {
                click: function (button) {
                    var win = button.up("#examineGood"),
                        orderGrid = button.up("#order"),
                        goodGrid = win.down("#good"),
                        msgTip = orderGrid.down('#displayMsg'),
                        OrderArr = [],
                        waybillNumber = button.up("#order").down("textfield[name=order_num]").getValue(),
                        examineGoodArr = (window.examineGoodArr = []);

                    waybillNumber = waybillNumber.trim();
                    if (waybillNumber && waybillNumber.length > 0) {
                        Ext.Ajax.request({
                            url: '/orders/inspection',
                            params: {
                                waybillNumber: waybillNumber
                            },
                            success: function (response) {
                                var responseData = Ext.JSON.decode(response.responseText),
                                    data = responseData.data;
                                if (data && data.list && responseData.success && data.list.length > 0) {

                                    for (var i = 0, j = data.list.length; i < j; i++) {
                                        OrderArr.push(data.list[i]['id']);
                                    }

                                    goodGrid.down("#orderNum").setValue(OrderArr.join(','));

                                    for (var i = 0, j = data.itemList.length; i < j; i++) {
                                        examineGoodArr.push(data.itemList[i]['barCode']);
                                    }

                                    msgTip.hide();
                                    orderGrid.getStore().removeAll();
                                    goodGrid.show();
                                    orderGrid.getStore().add(data.list);
                                    goodGrid.getStore().removeAll();
                                    goodGrid.getStore().add(data.itemList);

                                } else {
                                    orderGrid.getStore().removeAll();
                                    goodGrid.getStore().removeAll();
                                    goodGrid.hide();
                                    msgTip.show();
                                }
                            },
                            failure: function () {
                                //console.log('failure');
                            }
                        })
                    }
                }
            },

            //验货
            "#examineGood #goodSearch": {
                click: function (button) {
                    var goodGrid = button.up("#good"),
                        goodGridStore = goodGrid.getStore(),
                        goodNum = goodGrid.down("#goodNum").getValue(),
                        trueMsg = goodGrid.down("#trueMsg"),
                        falseMsg = goodGrid.down("#falseMsg"),
                        goodArr = window.examineGoodArr;

                    goodNum = goodNum.trim();
                    if (goodNum) {
                        var index = goodGridStore.find('barCode', goodNum),
                            flag = Ext.Array.contains(goodArr, goodNum) || Ext.Array.contains(goodArr, goodNum.toUpperCase());
                        if (index != -1 && flag) {
                            trueMsg.show();
                            falseMsg.hide();
                            var currentNode = goodGrid.getView().getNode(index);
                            var currentElement = Ext.get(currentNode);

                            currentElement.animate({
                                duration: 1000,
                                keyframes: {
                                    40: {
                                        backgroundColor: "#669900"
                                    },
                                    60: {
                                        opacity: 0.8
                                    }
                                }
                            });

                            Ext.Array.remove(goodArr, goodNum);
                            Ext.Array.remove(goodArr, goodNum.toUpperCase());
                            if (goodArr.length === 0) {
                                Ext.Ajax.request({
                                    url: '/orders/status',
                                    params: {
                                        orderIds: goodGrid.down('#orderNum').getValue(),
                                        status: 2
                                    },
                                    success: function (response) {
                                        var data = Ext.decode(response.responseText);
                                        if (data.success) {
                                            Ext.Function.defer(function () {
                                                var nodes = goodGrid.getView().getNodes();
                                                for (var i = 0, j = nodes.length; i < j; i++) {
                                                    if (i === j - 1) {
                                                        Ext.get(nodes[i]).animate({
                                                            duration: 1000 * (j - i),
                                                            to: {
                                                                opacity: 0
                                                            },
                                                            listeners: {
                                                                afteranimate: function () {
                                                                    goodGrid.hide();
                                                                    goodGrid.up("window").down("#order").getStore().removeAll();
                                                                    goodAmount = [];
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        button.up('#examineGood').down('#order').getStore().removeAll();
                                                        Ext.get(nodes[i]).animate({
                                                            duration: 1000 * (j - i),
                                                            to: {
                                                                opacity: 0
                                                            }
                                                        });
                                                    }

                                                }
                                            }, 1500);
                                            Ext.Function.defer(function () {
                                                Ext.Msg.show({
                                                    title: '提示',
                                                    msg: '选中物流单号已验货成功',
                                                    buttons: Ext.MessageBox.YES,
                                                    fn: function () {
                                                        //root.reloadGridData(true);
                                                    }
                                                });
                                            }, 2500);
                                        }
                                    }
                                });
                            }
                        } else if (index != -1 && !flag) {
                            trueMsg.hide();
                            falseMsg.setValue('<span style="color:#E47113">该商品已验证通过!</span>');
                            falseMsg.show();
                        } else {
                            trueMsg.hide();
                            falseMsg.setValue('<span style="color:#E47113">未扫描到该商品!</span>');
                            falseMsg.show();
                        }
                    }
                }
            },

            //验货完成
            "#examineGood": {
                close: function () {
                    if (Ext.getCmp("orderManage")) {
                        this.reloadGridData();
                    }
                }
            },

            //报表、历史订单、库存通用页面
            "#comContainer #searchBtn": {
                click: function () {
                    var wrap = Ext.getCmp('comContainer');
                    wrap.down("#grid").getStore().reload({
                        params: wrap.down("#search").getValues()
                    })
                }
            },
            "#comContainer #export": {
                click: function () {
                    //console.log('导出');
                }
            },
            "#comContainer #resetBtn": {
                click: function (button) {
                    var wrap = Ext.getCmp("comContainer");
                    wrap.down("#search").getForm().reset();
                    wrap.down("#grid").getStore().load();
                }
            }
        });
    },


    //一级菜单及连动相关按钮状态切换
    changeMainBtnState: function (button) {
        var id = button.getItemId(),
            orderGridToolBar = Ext.getCmp('orderListSearch').getDockedItems('toolbar[dock="top"]')[0];

        //切换一级菜单状态
        Ext.each(orderGridToolBar.items.items, function (item, index, items) {
            flag = (item === button);
            item.setDisabled(flag);
        });

        //对应改变form中四个一级菜单影像的状态
        this.setOrderState(id);

        //对应切换二级菜单的状态
        this.changeSubBtn(id);

        //刷新订单grid数据
        this.reloadGridData();

    },

    //二级菜单切换:belong:一级菜单id，state(obj):是否为改变状态
    changeSubBtn: function (belong, state) {
        var toolBar = Ext.getCmp('orderListGrid').getDockedItems('toolbar[dock="top"]')[0],
            buttons = toolBar.items.items;
        if (state == undefined) {
            Ext.each(buttons, function (item, buttons, index) {
                if (!item.belong) {
                    item.show();
                    item.setDisabled(false);
                } else if (item.belong == belong) {
                    item.show();
                    item.setDisabled(!item.initShow);
                } else {
                    item.hide();
                }
            });
        } else {
            var flag = state.disabled;
            Ext.each(buttons, function (item, buttons, index) {
                //flag = item.initShow ? true  flag;
                //if (item.itemId == 'orderExport') console.log(item.initShow);
                item.belong == belong && !item.initShow && item.setDisabled(flag);
            });
        }

    },

    //获取当前订单管理的四种状态（待处理-已发货-待发货-物流状态）
    getOrderState: function () {
        return Ext.getCmp('order_state').getValue();
    },

    //设置当前订单管理的四种状态 （待处理-已发货-待发货-物流状态）
    setOrderState: function (value) {
        var orderState = Ext.getCmp('order_state');
        orderState.getStore().find('field1', value) == -1 || orderState.select(value);
    },

    //获取订单管理grid选中项
    getGridSel: function () {
        return Ext.getCmp('orderListGrid').getSelectionModel().getSelection();
    },

    //获取订单管理grid选中项的某一列的数组
    getGridSelType: function (type) {
        var sels = this.getGridSel(),
            arr = [];
        Ext.each(sels, function (sel, sels, index) {
            sel && arr.push(sel.get(type));
        });
        return arr;
    },

    //取消订单管理gird选中项
    removeGridSel: function () {
        Ext.getCmp('orderListGrid').getSelectionModel().clearSelections();
        Ext.getCmp('orderItem').hide();
    },

    //订单管理数据重载:flag:是否刷新，asSearchForm：是否基于搜索表单来重载，resetSel：是否取消选中项
    reloadGridData: function (flag, asSearchForm, resetSel) {
        var flag = flag || true,
            asSearchForm = asSearchForm || true,
            resetSel = resetSel || true;
        if (flag) {
            var orderGrid = Ext.getCmp('orderListGrid');
            resetSel && this.removeGridSel();
            params = asSearchForm ? Ext.getCmp('orderListSearch').getValues() : '';
            orderGrid.getStore().reload({
                params: params
            });
        }
        /*var orderGrid = Ext.getCmp('orderListGrid');
         this.removeGridSel();
         orderGrid.getStore().reload({
         params: Ext.getCmp('orderListSearch').getValues()
         });*/
    },

    // 打印物流单
    printPreview: function () {
        var parent = this,
            selectedOrder = this.getGridSel(),
            id = [],
            isValidate = true,
            firstDelivery = '',
            delivery = '',
            designCode = '',
            printHtml;

        Ext.each(selectedOrder, function (name, index) {
            delivery = selectedOrder[index].data.deliveryTypePY;
            if (index === 0) {
                firstDelivery = delivery;
            } else {
                if (firstDelivery !== delivery) {
                    isValidate = false;
                    return false;
                }
            }
            id.push(selectedOrder[index].data.id);
        });
        id = id.join();

        if (!isValidate) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择相同的配送方式',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        parent.getLogisticsDeign(delivery, function (data) { // 获取物流单设计代码
            designCode = data;
            if (designCode === 'error') {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '所选的配送方式单面设计未找到。',
                    buttons: Ext.MessageBox.OK,
                    icon: 'x-message-box-error'
                });
                return;
            }
            parent.getOrderPrintInfo(id, function (logisticsList) { // 获取物流信息列表
                var dataList = logisticsList.data.orderPrintInfoList;
                if (dataList.length > 0) {
                    LODOP = getLodop(document.getElementById('LODOP1'), document.getElementById('LODOP_EM1'));
                    LODOP.PRINT_INIT("打印物流单");
                    LODOP.PRINT_INITA(0, 0, 1000, 600, "初始化打印控件");
                    for (var i = 0; i < dataList.length; i++) {
                        LODOP.NewPage();
                        printHtml = parent.replaceAllHtml(designCode, dataList[i]);
                        eval(printHtml);
                    }
                    LODOP.SET_PRINT_STYLE("FontSize", 14);
                    LODOP.SET_PRINT_PAGESIZE(0, "2100", "1480", "CreateCustomPage");
                    LODOP.SET_PRINT_MODE("AUTO_CLOSE_PREWINDOW", 1);
                    LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW", 1); // 在打印预览时内含背景图（当然实际打印时不输出背景图）
                    LODOP.PREVIEW();
                }
            });
        });

    },

    /// 获取到物流单设计
    getLogisticsDeign: function (delivery, callback) {
        var url = '/logistics/print_html/';
        Ext.Ajax.request({
            url: url + delivery,
            method: 'GET',
            success: function (response) {
                if (typeof callback === 'function') {
                    callback(response.responseText);
                }
            }
        });
    },

    // 获取物流信息列表
    getOrderPrintInfo: function (id, callback) {
        var url = '/logistics/print_info';
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {
                orderIds: id
            },
            success: function (response) {
                var data = Ext.JSON.decode(response.responseText);
                if (typeof callback === 'function') {
                    callback(data);
                }
            }
        });
    },

    // 替换打印配置
    replaceAllHtml: function (html, data) {
        html = html.substr(html.indexOf(";") + 1, html.length);
        if (html.indexOf("收货人联系方式") > 0) {
            html = html.replace(new RegExp("收货人联系方式", "g"), data.telephone);
        }
        if (html.indexOf("发货人联系方式") > 0) {
            html = html.replace(new RegExp("发货人联系方式", "g"), data.consignorTelephone);
        }
        if (html.indexOf("发货人单位名称") > 0) {
            html = html.replace(new RegExp("发货人单位名称", "g"), data.company);
        }
        if (html.indexOf("邮编") > 0) {
            html = html.replace(new RegExp("邮编", "g"), data.zipCode);
        }
        if (html.indexOf("发货地址") > 0) {
            html = html.replace(new RegExp("发货地址", "g"), data.consignorAddress);
        }
        if (html.indexOf("收货人地址") > 0) {
            html = html.replace(new RegExp("收货人地址", "g"), data.address);
        }
        if (html.indexOf("收货人") > 0) {
            html = html.replace(new RegExp("收货人", "g"), data.consignee);
        }
        if (html.indexOf("发货人") > 0) {
            html = html.replace(new RegExp("发货人", "g"), data.consignor);
        }
        if (html.indexOf("发货时间") > 0) {
            var _date = data.deliveryTime.split(' '),
                _YMD = _date[0].split('-'),
                _time = _date[1].split(':');

            html = html.replace(new RegExp("发货时间", "g"), _YMD[0] + '    ' + _YMD[1] + '    ' + _YMD[2] + '    ' + _time[0]);
        }
        return html;
    },

    // 打印发货单
    printInvoice: function () {
        var parent = this,
            orderPrintInfoList,
            orderDetailList,
            url = '/orders/delivery_info',
            ids = this.getGridSelType('id');

        if (ids.length < 1) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '请先选择订单',
                buttons: Ext.MessageBox.OK,
                icon: 'x-message-box-error'
            });
            return;
        }

        LODOP = getLodop(document.getElementById('LODOP1'), document.getElementById('LODOP_EM1'));
        LODOP.PRINT_INIT("打印发货单");

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {
                orderIds: ids.join(',')
            },
            success: function (response) {
                var data = Ext.JSON.decode(response.responseText),
                    style;
                if (data.success) {
                    style = '<style type="text/css">' + parent.invoiceStyle() + '</style>';
                    orderPrintInfoList = data.data.orderPrintInfoList;
                    orderDetailList = data.data.orderDetailList;
                    for (var i = 0; i < orderPrintInfoList.length; i++) {
                        parent.invoiceHtml(orderPrintInfoList[i], orderDetailList[i], orderDetailList[i].platformOrderItemList, style, 0);
                    }
                    LODOP.SET_PRINT_PAGESIZE(0, "2100", "1480", "CreateCustomPage");
                    LODOP.SET_PRINT_MODE("AUTO_CLOSE_PREWINDOW", 1);//打印后自动关闭预览窗口
                    LODOP.PREVIEW();
                } else {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: data.msg,
                        buttons: Ext.MessageBox.OK,
                        icon: 'x-message-box-error'
                    });
                }
            }
        });
    },

    // 发货单样式
    invoiceStyle: function () {
        return 'html, body, a, img, p, ul, li, div, h1, h2, h3 { margin: 0; padding: 0; border: 0; }' +
            'body { font-family: simsun, SimHei, "microsoft yahei"; color: #333; font-size: 14px; }' +
            '.clear{border:0;clear:both;display:block;float:none;font-size:0;margin:0;padding:0;overflow:hidden;visibility:hidden;width:0;height:0}' +
            '.print_wrapper { width: 97%;font-size: 14px;margin:0 0 0 25px;position:relative;height:520px;}' +
            '.clearfix:after{content:".";clear:both;display:block;height:0;overflow:hidden;visibility:hidden;}' +
            '.clearfix{ * zoom:1}' +
            '.print_wrapper .buyer { padding: 88px 10px 10px 0;line-height:22px;font-size: 14px; }' +
            '.print_wrapper .express { padding: 112px 0 10px; text-align: left;line-height:22px;font-size: 14px; }' +
            '.print_wrapper .table table { border-width: 1px 0 0 1px; border-color: #999; border-style: solid; font-size: 14px; }' +
            '.print_wrapper .table td,' +
            '.print_wrapper .table th { border-width: 0 1px 1px 0; border-color: #999; border-style: solid; text-align: center; line-height: 24px;font-size: 14px; }' +
            '.print_wrapper .order_count{height:26px;line-height:26px;margin:6px 0 0;font-size: 14px;}' +
            '.print_wrapper .amount{float:right;}' +
            '.print_wrapper .seller{position:absolute;width:90%;left:0;bottom:40px;}' +
            '.print_wrapper .seller p{line-height:22px;font-size: 14px;}'
    },

    // 发货单HTML
    invoiceHtml: function (orderPrintInfo, order, orderProducts, strBodyStyle, status) {
        var customerServiceRemark = "",
            userRemark = "";
        if (order.customerServiceRemark != null) {
            customerServiceRemark = order.customerServiceRemark;
        }
        if (order.userRemark != null) {
            userRemark = order.userRemark;
        }
        var index = 0;
        if (orderProducts.length > 3 * (status + 1)) {
            index = 3 * (status + 1);
        } else {
            index = orderProducts.length;
        }
        var deliveryType = order.deliveryType;
        if (deliveryType == "ems") {
            deliveryType = "EMS";
        } else if (deliveryType == "shunfeng") {
            deliveryType = "顺丰";
        } else if (deliveryType == "yunda") {
            deliveryType = "韵达";
        } else if (deliveryType == "zhongtong") {
            deliveryType = "中通";
        } else if (deliveryType == "zhaijisong") {
            deliveryType = "宅急送";
        } else if (deliveryType == "yuantong") {
            deliveryType = "圆通";
        } else if (deliveryType == "shentong") {
            deliveryType = "申通";
        } else if (deliveryType == "kuaijiesudi") {
            deliveryType = "快捷";
        } else if (deliveryType == "quanritongkuaidi") {
            deliveryType = "全日通";
        } else if (deliveryType == "huitongkuaidi") {
            deliveryType = "汇通";
        }
        var time = order.payDate;
        var html = '<div class="print_wrapper">';
        html += '<div class="header">';
        html += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
        html += '<tr>';
        html += '<td valign="bottom"><div class="buyer">';
        html += '<p>收 货 人：' + order.name + '</p>';
        html += '<p>联系方式：' + order.mobile + '</p>';
        html += '<p class="clearfix"><span style="float: left;height: 22px;">收货地址：</span><span style="float: left;width: 400px">' + order.province + order.location + '</span></p>';
        html += '<p>买家留言：' + userRemark + '</p>';
        html += '</div></td>';
        html += '<td valign="top" style="width:30%;"><div class="express">';
        html += '<p>快递公司：' + deliveryType + '</p>';
        html += '<p>订购日期：' + time.substr(0, time.length - 3) + '</p>';
        html += '<p>订  单 号：' + order.orderNo + '</p>';
        html += '</div></td>';
        html += '</tr>';
        html += '</table>';
        html += '</div>';
        html += '<div class="content">';
        html += '<div class="table">';
        html += '<table border="0" cellspacing="0" cellpadding="0" width="100%">';
        html += '<tr>';
        html += ' <th width="7%">序号</th>';
        html += ' <th width="7%">退货</th>';
        html += '<th>商品编号</th>';
        html += ' <th>商品名称</th>';
        html += ' <th width="13%">单价（元）</th>';
        html += '<th width="7%">数量</th>';
        html += ' <th width="13%">金额（元）</th>';
        html += '</tr>';
        var j = 0;
        var totalPrice = 0;
        for (var i = status * 3; i < index; i++) {
            var skuPrice = orderProducts[i].unitPrice * orderProducts[i].shipmentNum;
            totalPrice += skuPrice;
            html += '<tr>';
            html += '<td>' + j + 1 + '</td>';
            html += '<td>[&nbsp;&nbsp;]</td>';
            html += '<td><div style="word-break:break-all;word-wrap:break-word;width:160px;">' + orderProducts[i].itemNo + '</div></td>';
            html += '<td style="padding: 0 5px 0 18px; text-align: left;">' + orderProducts[i].productName + ' ' + orderProducts[i].skuAttribute + '</td>';
            html += '<td>¥：' + orderProducts[i].unitPrice + '</td>';
            html += '<td>' + orderProducts[i].shipmentNum + '</td>';
            html += '<td>¥：' + skuPrice + '</td>';
            html += '</tr>';
        }
        html += '</table>';
        html += '</div>';

        html += '<div class="clear"></div>';
        html += '<div class="order_count">';
        html += '<div class="amount">合计：¥' + totalPrice + '元 </div>';
        html += '</div>';
        html += '<div class="seller">';
        html += '<p>发 货  人： ' + orderPrintInfo.consignor + '</p>';
        html += '<p>发货地址： ' + orderPrintInfo.consignorAddress + '</p>';
        html += '<p>卖家留言： ' + customerServiceRemark + '</p>';
        html += '</div>';
        html += '</div>';
        html += '</div>';
        html += '</body>';
        html += '</html>';
        var strFormHtml = strBodyStyle + "<body>" + html + "</body>";
        LODOP.NewPage();
        LODOP.ADD_PRINT_HTM("01mm", 0.1, "RightMargin:0.1cm", "BottomMargin:0.1mm", "<body leftmargin=0 topmargin=0>" + strFormHtml + "</body>");
        LODOP.ADD_PRINT_BARCODE(60, 516, 230, 47, "128Auto", order.waybillNumber);
        if (orderProducts.length > 3 * (status + 1)) {
            status = status + 1;
            InvoiceHtml(orderPrintInfo, order, orderProducts, strBodyStyle, status);
        }
    },

    // 打开修改密码窗口
    openModifyPasswordWin: function () {
        Ext.widget('modifyPassword').show();
    },

    // 保存修改密码表单
    savePassword: function (button) {
        var form = button.up('form').getForm(),
            url = '/account/changePassword',
            formData = form.getValues();

        if (form.isValid()) {
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                params: formData,
                success: function (response) {
                    var data = Ext.JSON.decode(response.responseText);
                    if (data.success) {
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '密码修改成功',
                            buttons: Ext.MessageBox.OK,
                            icon: 'x-message-box-info'
                        });
                        form.reset();
                        Ext.getCmp('modifyPassword').hide();
                    } else {
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: data.msg,
                            buttons: Ext.MessageBox.OK,
                            icon: 'x-message-box-error'
                        });
                    }
                }
            });
        }
    },

    // 重置表单
    resetForm: function (button) {
        button.up('form').getForm().reset();
    },

    // 退出登录
    logout: function () {
        document.location.href = '/logout'
    },

    //  tip message
    tipMsg: function (title, format) {
        var msgCt;
        if (!msgCt) {
            msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
        }
        var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
        var m = Ext.DomHelper.append(msgCt, '<div class="msg"><h3>' + title + '</h3><p>' + s + '</p></div>', true);
        m.hide();
        m.slideIn('t').ghost("t", { delay: 1000, remove: true});
    }

    //表格是否可编辑 (不生效，暂不知道原因)
    /*isGridEditAble: function () {
     var cellEdit = Ext.getCmp('orderListGrid').getPlugin("cellEdit");
     //(this.getOrderState() == "Confirm") ? cellEdit.editing=true  : cellEdit.editing=false;
     console.log(cellEdit);
     cellEdit.disable( );
     }*/

});