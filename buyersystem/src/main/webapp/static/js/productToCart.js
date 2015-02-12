// 配件加入购物车 弹出层
var followDialog = function (ele, title, content) {
    var _box = $("<div></div>"),
        _title = $("<h3></h3>"),
        _content = $("<div></div>"),
        _button = $("<div></div>");
    var _amount = '<div class="amount_box">'
        + '<dl class="amount clearfix" id="pop_amount">'
        + ' <dt>购买数量：</dt>'
        + ' <dd>'
        + '     <a href="javascript:void(0);" class="btn_sub"><span>减</span></a>'
        + '     <div class="number_wrap">'
        + '         <input type="text" name="buy_number" id="popBuyNumber"  class="text" value="1" />'
        + '     </div>'
        + '     <a href="javascript:void(0);" class="btn_add"><span>加</span></a>'
        + '     <div class="price" id="popProductPrice"></div>'
        + ' </dd>'
        + '</dl>'
        + '</div>';

    _box.attr("id","quickSelectProduct");
    _title.addClass("title").html(title).appendTo(_box);
    _content.addClass("content").html("<div class='attribute_box' id='attributeBox'>" + content + "</div>" + _amount).appendTo(_box);
    _button.addClass("button").html("<a href='#' class='common_btn' id='popAddToCart'><span>确定</span></a> ").appendTo(_box);

    var _this = this;
    _this._layer = new com.layer({
        trigger:ele,
        content:_box,
        loaded:false,
        cache:true,
        isModal:true,
        shadeColor:"#000",
        opacity:0.3,
        outClass:"pop_select_product",
        contentClass:'content_wrapper',
        closeClass:"close",
        closeText:'<a class="fix_ie6" href="#"></a>',
        style:{
            position:"absolute",
            backgroundColor:"transparent",
            border:"0",
            overflow:"visible",
            width:408,
            height:224
        },
        triggerType:com.layer.triggerType.click,
        positionType:com.layer.positionType.margin,
        showAction:com.layer.showAction.fade,
        hideAction:com.layer.hideAction.fade,
        actionSpeed:400,
        closeTimeout:800,
        isBgClose:true,
        offset:{
            x:-400,
            y:0
        },
        onshowing:function () { },
        onclosing:function () {
            $("#" + _this._layer.getId()).remove();
        }
    });
    _this.remove = function(){
        this._layer.hide();
        $('#' + _this._layer.getId()).remove();
    }
};

$(function () {
    // 配件加入购物车
    $(".accessories").eq(0).find(".pic").each(function () {
        var $this = $(this);
        $this.hover(function () {
            var proToCart = $this.find(".product_to_cart");
            var proToCartUrl = proToCart.attr("href");
            var proItem = $this.parents("li");
            proToCart.show();
            proToCart.unbind();
            proToCart.bind("click", function (E) {
                E.preventDefault();
                $.ajax({
                    type:"GET",
                    url:proToCartUrl,
                    async:false,
                    dataType:'json',
                    success:function (data) {
                        if (data["success"]) {
                            if (typeof data["data"]["cartUrl"] != "undefined") { // 单SKU商品直接加入购物车
                                window.location.href = data["data"]["cartUrl"];
                               /* new Ejs.Dialog({
                                    title: "添加到购物车",
                                    type: Ejs.Dialog.type.RIGHT,
                                    info: "物品已经成功添加到购物车",
                                    isModal: true,
                                    isConfirm: false,
                                    afterClose:function () {
                                        getCartNumber();
                                    },
                                    buttons:[
                                        {
                                            buttonText:"继续购物",
                                            buttonClass:"button_a",
                                            onClick:function () {

                                            }
                                        },
                                        {
                                            buttonText:"去结算",
                                            buttonClass:"button_a",
                                            onClick:function () {
                                                window.location.href = data["data"]["cartUrl"];
                                            }
                                        }
                                    ]
                                });*/
                            } else { //多SKU需弹出选择SKU的对话框
                                if (data["data"]["canAdd"] == false) {
                                    //alert("此商品有多个SKU");
                                    var __skuMap = data["data"]["skuMap"];
                                    var __defaultSku = data["data"]["defaultSku"];
                                    //alert(__skuMap.toSource());
                                    //console.log(data);
                                    //console.log(__skuMap);
                                    var __domUrl = data["data"]["skuUrl"];
                                    $.ajax({
                                        type:"GET",
                                        url: __domUrl,
                                        async:false,
                                        dataType:'html',
                                        success:function (HTML) {
                                            var _followDialog = new followDialog(proItem, "请选择您所需要的商品属性！", HTML );
                                            //alert($("#attributeBox").html());
                                            var __stock = 1;
                                            var __limit = 1;
                                            var __buyNumber = $("#popBuyNumber");

                                            var __num = 1;
                                            var __isSKU = true;

                                            var __skuDefStock = __defaultSku["stock"];
                                            var __skuDefLimit = __defaultSku["limit"];
                                            __stock = __skuDefStock;
                                            __limit = __skuDefLimit;

                                            var __skuNumber = __defaultSku["pvList"].length;
                                            var __skuDefPvList = __defaultSku["pvList"];
                                            var __skuSel = $("#attributeBox .sel");

                                            // 设置默认SKU为选中状态
                                            (function (seltions) {
                                                if (__skuDefStock > 0 && __skuDefLimit > 0) {
                                                    __limit = __limit > __stock ? __stock : __limit;
                                                    if (__skuNumber == 0) {
                                                        return false
                                                    }
                                                    $(seltions).each(function (i) {
                                                        var _this = $(this);
                                                        var _val = __skuDefPvList[i];
                                                        _this.find("a").each(function () {
                                                            if ($(this).attr("data") == _val) {
                                                                $(this).addClass("current");
                                                            }
                                                        });
                                                    });
                                                    __reSetUrl();
                                                }
                                            })("#attributeBox .sel");

                                            // 如果默认的SKU库存小于1,从Sku列表查找有库存的商品重新设为选中状态
                                            if (__skuDefStock < 1 || __skuDefLimit < 1) {
                                                $.each(__skuMap, function (i) {
                                                    var skuMapTemp = __skuMap[i];
                                                    if (skuMapTemp["stock"] > 0 && skuMapTemp["limit"] > 0) {
                                                        __stock = skuMapTemp["stock"];
                                                        __limit = skuMapTemp["limit"];
                                                        __limit = __limit > __stock ? __stock : __limit;
                                                        var _skuArr = i.split(",");
                                                        $.each(_skuArr, function (ii, nn) {
                                                            //alert(nn);
                                                            $(".sel").eq(ii).find("a").each(function () {
                                                                if ($(this).attr("data") == nn) {
                                                                    //alert(nn);
                                                                    $(this).addClass("current").siblings("a").removeClass("current");
                                                                }
                                                            });
                                                        });
                                                        return false;
                                                    }
                                                });
                                                __reSetUrl();
                                            }

                                            // 库存为空的Sku设置为不可选
                                            // 取第1选项的所有值存为数组 ["8589944595","8589944596","8589944597"]
                                            var __skuMapArr = [];
                                            $.each(__skuMap, function (i) {
                                                //console.log(i);
                                                var __skuArrFirst = i.split(",")[0];
                                                if (__skuMapArr.length > 0) {
                                                    if (jQuery.inArray(__skuArrFirst, __skuMapArr) < 0) {
                                                        __skuMapArr.push(__skuArrFirst);
                                                    }
                                                } else {
                                                    __skuMapArr.push(__skuArrFirst);
                                                }
                                            });
                                            //console.log(__skuMapArr);

                                            // 取所有第2选项对应第2选项的库存状态存为数组 [[1,0,0],[0,1,1],[1,1,0]]
                                            var __skuStockArr = [];
                                            $.each(__skuMapArr, function (i, n) {
                                                //console.log(n);
                                                var ___stockArr = [];
                                                var _n = n;
                                                $.each(__skuMap, function (ii, nn) {
                                                    var _defaultSkuArr = ii.split(",")[0];
                                                    if (_defaultSkuArr == _n) {
                                                        var _str = (__skuMap[ii]["stock"] > 0 && __skuMap[ii]["limit"]) >0 ? 1 : 0;
                                                        ___stockArr.push(_str);
                                                    }

                                                });
                                                __skuStockArr.push(___stockArr);
                                                //console.log(__skuStockArr);
                                            });
                                            //console.log(__skuStockArr);

                                            // 渲染第1选项是否有库存

                                            __skuSel.eq(0).find("a").each(function (i) {
                                                var __s = false;
                                                $.each(__skuStockArr[i], function (ii, nn) {
                                                    if (parseInt(nn) > 0) {
                                                        __s = true;
                                                        return false;
                                                    }
                                                });
                                                if (!__s) {
                                                    $(this).addClass("null");
                                                }
                                            });

                                            // 渲染第2选项是否有库存   现阶段只支持2级
                                            function __skuStockDom(stockArr) {
                                                if (__skuNumber > 1 && __skuNumber < 3) {
                                                    if (typeof stockArr == 'undefined') {
                                                        return false
                                                    }
                                                    if (stockArr.length < 1) {
                                                        return false
                                                    }
                                                    var _ii = -1;
                                                    var _stockArr = stockArr;
                                                    __skuSel.each(function (i) {
                                                        var _this = $(this);
                                                        if (i == 0) {
                                                            $(this).find("a").each(function (ii) {
                                                                if ($(this).hasClass("current")) {
                                                                    _ii = ii;
                                                                }
                                                            });
                                                        } else if (i == 1 && _ii >= 0) {  //判断第1选项是否已选择
                                                            //console.log(_ii);
                                                            $.each(_stockArr[_ii], function (ii, n) {
	                                                            _this.find("a").eq(ii).removeClass("null");
                                                                if (n == 0) {
                                                                    _this.find("a").eq(ii).addClass("null");
                                                                    _this.find("a").eq(ii).removeClass("current");
                                                                }
                                                            });
	                                                        if(_this.find("a").length>1&&_this.find('.current').length==0){
		                                                        _this.find("a").each(function(ia){
			                                                        var _a_this=$(this);
			                                                        if(!_a_this.hasClass("null")){
				                                                        _a_this.addClass("current");
				                                                        return false;
			                                                        }
		                                                        });
	                                                        }
                                                        }
                                                    });
                                                }
                                            }

                                            // 重置加入购物车的URL
                                            //__skuStockDom(__skuStockArr);
                                            function __reSetUrl() {
                                                __isSKU = true;
                                                if (__skuSel.length > 0) {
                                                    var arrData = [];
                                                    __skuSel.each(function (i) {
                                                        if (!__isSKU) {
                                                            return;
                                                        }
                                                        var data = $(this).find(".current").attr("data");
                                                        //alert(_data);
                                                        if (typeof data == "undefined") {
                                                            __isSKU = false;
                                                            return;
                                                        }
                                                        arrData[i] = data;
                                                    });
                                                }
                                                //console.log(_arrData);
                                                if (__isSKU) {
                                                    __stock = __skuMap[arrData.join(',')]["stock"];
                                                    __limit = __skuMap[arrData.join(',')]["limit"];
                                                    __price = __skuMap[arrData.join(',')]["money"];
                                                    __limit = __limit > __stock ? __stock : __limit;
                                                    if(__buyNumber.val() > __limit){
                                                        __buyNumber.val(__limit);
                                                    }
                                                    $("#popAddToCart").attr("href", EJS.AddSkuToCart + "?skuId=" + __skuMap[arrData.join(',')]["id"] + "&number=" + __buyNumber.val());
                                                    $("#popProductPrice").html("单价：<span>￥" + __price + "</span>元");
                                                }
                                            }

                                            // 选择商品属性
                                            __skuSel.each(function (i) {
                                                var _chooseItem = $(this);
                                                _chooseItem.find("a").each(function () {
                                                    $(this).click(function (E) {
                                                        E.preventDefault();
                                                        if ($(this).hasClass("null")) {
                                                            return false;
                                                        }
                                                        if (!$(this).hasClass("current")) {
                                                            $(this).addClass("current").siblings("a").removeClass("current");
                                                        }
                                                        if (i == 0) {
                                                            __skuStockDom(__skuStockArr);
                                                        }
                                                        __reSetUrl();
                                                    });
                                                });
                                            });

                                            // 设置购买数量
                                            __buyNumber.blur(function () {
                                                __limit = __limit > __stock ? __stock : __limit;
                                                var reg = /^[0-9]+$/;
                                                if (!reg.test(__buyNumber.val())) {
                                                    __buyNumber.val(1);
                                                }
                                                if (__buyNumber.val() > __limit) {
                                                    __buyNumber.val(__limit);
													Tip(__buyNumber , "limit_tip", "超出此商品能购买的最大数量", 30);
                                                }
                                                if(__buyNumber.val() < 1 && __limit > 0){
                                                    __buyNumber.val(1);
                                                    Tip(__buyNumber , "limit_tip", "商品购买数量不能小于1", 30);
                                                }
                                            });

                                            $("#pop_amount .btn_add").click(function () {
                                                __limit = __limit > __stock ? __stock : __limit;
                                                __num = parseInt(__buyNumber.val()) + 1;
												if(__num > __limit){
													__num = __limit;
													Tip(__buyNumber , "limit_tip", "超出此商品能购买的最大数量", 30);
												}
                                                __buyNumber.val(__num);
                                            });
                                            $("#pop_amount .btn_sub").click(function () {
                                                __num = parseInt(__buyNumber.val()) - 1;
                                                __num = __num < 1 ? 1 : __num;
                                                __buyNumber.val(__num);
                                            });

                                            // 加入购物车
                                            $("#popAddToCart").bind("click",function(E){
                                                E.preventDefault();
                                                __reSetUrl();
                                                var _url = $(this).attr("href");
                                                _followDialog.remove();
                                                $.ajax({
                                                    type: "GET",
                                                    url: _url,
                                                    async: false,
                                                    dataType: 'json',
                                                    success: function (data) {
                                                        if (data["success"]) {
                                                            window.location.href = data["data"]["cartUrl"];
                                                            /*new Ejs.Dialog({
                                                                title:"添加到购物车",
                                                                type:Ejs.Dialog.type.RIGHT,
                                                                info:"物品已经成功添加到购物车",
                                                                isConfirm:false,
                                                                isModal:true,
                                                                isConfirm:false,
                                                                afterClose:function (v) {
                                                                    getCartNumber();
                                                                },
                                                                buttons:[
                                                                    {
                                                                        buttonText:"继续购物",
                                                                        buttonClass:"button_a",
                                                                        onClick:function () {
                                                                        }
                                                                    },
                                                                    {
                                                                        buttonText:"去结算",
                                                                        buttonClass:"button_a",
                                                                        onClick:function () {
                                                                            window.location.href = data["data"]["cartUrl"];
                                                                        }
                                                                    }
                                                                ]
                                                            });*/
                                                        } else {
                                                            new Ejs.Dialog({
                                                                title:"提示信息",
                                                                type:Ejs.Dialog.type.ERROR,
                                                                info:data["msg"],
                                                                isConfirm:false,
                                                                buttons:[
                                                                    {
                                                                        buttonText:"确认",
                                                                        buttonClass:"button_b",
                                                                        onClick:function () {

                                                                        }
                                                                    }
                                                                ],
                                                                isModal:true
                                                            });
                                                        }
                                                    }
                                                });
                                            });
                                        }
                                    });

                                }
                            }
                        } else { // 加入购物车失败
                            new Ejs.Dialog({
                                title:"提示信息",
                                type:Ejs.Dialog.type.ERROR,
                                info:data["msg"],
                                isConfirm:false,
                                buttons:[
                                    {
                                        buttonText:"确认",
                                        buttonClass:"button_b",
                                        onClick:function () {

                                        }
                                    }
                                ],
                                isModal:true
                            });
                        }
                    }
                });
            });
        }, function () {
            $(this).find(".product_to_cart").hide();
        });
    });
});
