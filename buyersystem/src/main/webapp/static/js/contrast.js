$(function () {
    /*
     *删除对比内容
     */
    function removeCompare(pid) {
        if (!pid && typeof pid != "number") {
            return false;
        }
        // 从cookie中移除
        var domJSON = $.cookie('contrastData');
        if (domJSON != null) {
            domJSON = jQuery.parseJSON($.cookie('contrastData'));
            var domList = domJSON["list"];
            for (var ii = 0; ii < domList.length; ii++) {
                if (domList[ii]['id'] == pid) {
                    domList.splice(ii, 1);
                    if (domList.length > 0) {
                        $.cookie('contrastData', JSON.stringify(domJSON), { path:'/' });
                    } else {
                        $.cookie('contrastData', null, {  path:'/' });
                    }
                }
            }
            Ejs.contrasts.contrastShow();
        }
    }

    /*
     *商品对比弹出框
     */
    Ejs.ContrastWindows = null;
    Ejs.ContrastWindow = function (options) {
        this.options = options || {};
        this.showBar = null;
        this.initialise();
    };

    Ejs.ContrastWindow.defaultOptions = {
        width:800,
        height:"auto",
        src:"",
        beforeShow:function () {

        },
        beforeClose:function () {

        },
        ids:''
    };

    Ejs.ContrastWindow.prototype = {

        initialise:function () {
            this.setOptions(this.options);
            this.popWindow();
            this.evenEsc();
        },
        setOptions:function (opt) {
            $.extend(true, Ejs.ContrastWindow.defaultOptions, opt || {});
        },
        popWindow:function () {
            var _root = this;
            var _close = '<span class="close_Contrast" style="">关闭</span>';
            var _src = EJS.ProductCompare + '?ids=' + Ejs.ContrastWindow.defaultOptions.ids;
            if (_src == "") {
                return false
            }
            //alert(_src);
            _root.showBar = new com.layer({
                isModal:true,
                title:'',
                content:'',
                src:_src,
                closeText:_close,
                closeStyle:{
                    float:'right',
                    position:'absolute',
                    top:'0',
                    right:'13px',
                    width:26,
                    height:26,
                    'line-height':'26px',
                    'text-align':'center',
                    'cursor':'pointer'
                },
                loaded:false,
                cache:false,
                opacity:0.2,
                style:{
                    position:"absolute",
                    backgroundColor:"#f0f0f0",
                    border:"0px solid #d0d0d0",
                    overflow:"hidden",
                    width:Ejs.ContrastWindow.defaultOptions.width,
                    height:Ejs.ContrastWindow.defaultOptions.height
                },
                positionType:com.layer.positionType.center,
                isBgClose:true,
                onshowing:function () {
                    Ejs.ContrastWindow.defaultOptions.beforeShow();
                },
                onclosing:function () {
                    Ejs.ContrastWindow.defaultOptions.beforeClose();
                },
                offset:{
                    y:-120
                }
            });

            _root.regCloseEvent();
            _root.showBar.show();
            $("#com_layer_bg_shade").unbind();
            $("#com_layer_bg_shade").bind('click', function () {
                _root.remove();
            });
        },
        remove:function () {
            this.showBar.hide();
            $('#' + this.showBar.getId()).remove();
        },
        regCloseEvent:function () {
            var _root = this;
            $("#" + _root.showBar.getCloseId()).unbind().click(function (e) {
                e.preventDefault();
                _root.remove();
            });
            //alert(_root.showBar.getId());
        },
        evenEsc:function () {
            var _root = this;
            $(document).unbind('keydown');
            $(document).bind("keydown", function (event) {
                if (event.keyCode == 27) {
                    _root.remove();
                }
            });
        },
        reSize:function (size) {
            var _root = this;
            var _size = size || {
                width:_root.showBar.defaultOptions.width,
                height:_root.showBar.defaultOptions.height
            };
            _root.showBar.setWidth(_size.width);
            _root.showBar.setHeight(_size.height);
        }
    };
    Ejs.contrasts = null;
    Ejs.contrast = function () {
        this.init();
        this.contrastShow();
        this.removeAll();
        this.LiveBtn();
    };

    Ejs.contrast.prototype = {
        init:function () {
            var _this = this;
            $('.join_contrast').each(function () {
                var _this2 = $(this);
                _this2.live('click', function (E) {
                    E.preventDefault();
                    var _PNAME = $(this).attr("pname"),
                        _PID = $(this).attr("pid"),
                        _CID = $(this).attr("cid"),
                        _URL = $(this).attr("url"),
                        _IMG = $(this).attr("img");
                    var contrastData = $.cookie('contrastData');
                    var _Data = "";

                    if (_PID != "") {
                        if (contrastData == null) {
                            _Data = '{"cid":"' + _CID + '","list":[{"id":"' + _PID + '","img":"' + _IMG + '","url":"' + _URL + '","name":"' + $.trim(_PNAME) + '"}]}';
                            $.cookie('contrastData', _Data, { path:'/' });
                        } else {
                            var JsonData = jQuery.parseJSON($.cookie('contrastData'));
                            var List = JsonData["list"];
                            // 处理数据
                            _Data = '{\"id\":\"' + _PID + '\",\"img\":\"' + _IMG + '\",\"url\":\"' + _URL + '\",\"name\":\"' + $.trim(_PNAME) + '\"}';
                            _Data = jQuery.parseJSON(_Data);

                            // 检查是否已经选择了商品
                            for (var i = 0; i < List.length; i++) {
                                if (List[i]["id"] == _PID) {
                                    _this2.addClass("join_exist")
                                    return false;
                                }
                            }

                            // 判断是否超过3个
                            if (List.length >= 3) {
                                var _moreList = '<ul class="contrast_more_list clearfix">';
                                for (var j = 0; j < List.length; j++) {
                                    if (j == List.length - 1) {
                                        _moreList += '<li class="last"><img src="';
                                    } else {
                                        _moreList += '<li><img src="';
                                    }
                                    _moreList += List[j]['img'];
                                    _moreList += '"/>';
                                    _moreList += '<a class="fix_ie6" href="#" pid="';
                                    _moreList += List[j]['id'];
                                    _moreList += '">';
                                    _moreList += '</a>';
                                    _moreList += '</li>';
                                }
                                _moreList += '</ul>';

                                var _theLast = '<li class="last" style="display:none; width: 0;"><img src="';
                                _theLast += _IMG;
                                _theLast += '"/>';
                                _theLast += '<a class="fix_ie6" href="#" pid="';
                                _theLast += _PID;
                                _theLast += '"></a></li>';
                                var _ids = '';
                                new Ejs.Dialog({
                                    title:"对不起，您最多可以添加3个商品进行对比,请先删除一个商品！",
                                    titleStyle:{
                                        paddingTop:6,
                                        fontWeight:"normal",
                                        width:280,
                                        paddingLeft:8,
                                        textAlign:"left"
                                    },
                                    type:Ejs.Dialog.type.WARN,
                                    info:_moreList,
                                    buttons:[
                                        {
                                            buttonText:"查看对比",
                                            onClick:function () {
                                                Ejs.ContrastWindows = new Ejs.ContrastWindow({
                                                    ids:_ids
                                                });
                                            },
                                            buttonStyle:{
                                                display:"none"
                                            }
                                        },
                                        {
                                            buttonText:"清空所有商品",
                                            onClick:function () {
                                                $.cookie('contrastData', null, {  path:'/' });
                                                $("#contrast_fixed").html(" ").hide();
                                            },
                                            buttonStyle:{
                                                marginRight:0
                                            }
                                        }
                                    ],
                                    beforeShow:function () {
                                        var _listGoods = $("ul.contrast_more_list>li");
                                        _listGoods.each(function (k) {
                                            var _li = $(this);
                                            _li.find("a").live("click", function (evt) {
                                                evt.preventDefault();
                                                var _r_id = $(this).attr("pid");
                                                _listGoods.removeClass("last").parent().find("a").hide();
                                                _li.animate({
                                                    width:0
                                                }, 600, function () {
                                                    _li.remove();
                                                    _listGoods.parents(".content_wrapper").find(".button_wrapper").find("input").eq(0).fadeIn().css({display:"inline"});
                                                    var _listGoods2 = $("ul.contrast_more_list>li>a");
                                                    $(_listGoods2).each(function (i) {
                                                        _ids += $(this).attr('pid') + ',';
                                                    });
                                                });
                                                $(_theLast).appendTo(_li.parent()).css({display:"inline"}).animate({width:65}, 600);
                                                _listGoods.parent().find("a").hide();
                                                removeCompare(_r_id);
                                                var JsonData2 = jQuery.parseJSON($.cookie('contrastData'));
                                                JsonData2["list"].push(_Data);
                                                $.cookie('contrastData', JSON.stringify(JsonData2), { path:'/' });
                                                _this.contrastShow();
                                            });
                                        });
                                    }
                                });
                                return false;
                            }
                            JsonData["list"].push(_Data);
                            $.cookie('contrastData', JSON.stringify(JsonData), { path:'/' });
                        }
                        _this.contrastShow();
                    }
                    return false;

                })
            });
        },
        getTop:function () {
            return document.documentElement && document.documentElement.scrollTop || document.body.scrollTop;
        },
        contrastShow:function () { //载入对比的商品
            var _this = this;
            if ($.browser.msie && $.browser.version == "6.0") {
                $("#contrast_fixed").css("top", 206 + _this.getTop() + 'px');
                $(window).scroll(function () {
                    $("#contrast_fixed").css("top", 206 + _this.getTop() + 'px');
                });
            }
            if ($("#contrast_fixed").length < 1) {
                $("body").append('<div class="contrast_fixed" id="contrast_fixed"></div>');
            }
            var domJSON = $.cookie('contrastData');
            if (domJSON == null) {
                $("#contrast_fixed").hide();
                return false;
            }
            domJSON = jQuery.parseJSON(domJSON);
            var domList = domJSON["list"];
            if (domList.length < 1) {
                $("#contrast_fixed").hide();
                return false;
            }

            var _HTML = '<div class="contrast_wrapper">';
            _HTML += '<h3><span id="contrast_removeAll" class="removeAll">清空</span>商品对比</h3>';
            _HTML += '';
            _HTML += '';
            _HTML += "<ul>";
            for (var i = 0; i < domList.length; i++) {
                _HTML += '<li>';
                _HTML += '<div class="pic"><img src="' + domList[i]["img"] + '" alt="" /></div>';
                _HTML += '<div class="title">';
                _HTML += '<a target="_blank" href="' + domList[i]["url"] + '">' + domList[i]["name"] + '</a>';
                _HTML += '</div>';
                _HTML += '<a href="#" class="contrast_remove" pid="' + domList[i]["id"] + '">移除</a>';
                _HTML += '</li>';
            }
            _HTML += "</ul>";
            _HTML += '<div class="btn_bar">';
            if (domList.length > 1) {
                _HTML += '<input type="button" class="btn_w" value=" " id="all_contrast">';
            }
            _HTML += '<a href="#" id="slideContrastUp" class="slideUp">收起</a>';
            _HTML += '</div></div>';
            _HTML += '<div class="contrast_fixed_thumb"><a id="contrast_fixed_thumb" href="#"></a><span id="total_contrast">';
            _HTML += domList.length;
            _HTML += '</span></div>';

            $("#contrast_fixed").show();
            $("#contrast_fixed").html(_HTML);
            $("#slideContrastUp").click(function (e) {
                e.preventDefault();
                $("#contrast_fixed>.contrast_wrapper").hide();
                $("#contrast_fixed>.contrast_fixed_thumb").show();
            });
            $("#contrast_fixed_thumb").click(function (e) {
                e.preventDefault();
                $("#contrast_fixed>.contrast_fixed_thumb").hide();
                $("#contrast_fixed>.contrast_wrapper").show();
            });

            $('.contrast_remove').each(function () {
                $(this).unbind();
                $(this).bind('click', function (E) {
                    E.preventDefault();
                    var _id = $(this).attr('pid');
                    for (var ii = 0; ii < domList.length; ii++) {
                        if (domList[ii]['id'] == _id) {
                            domList.splice(ii, 1); // 移除
                            if (domList.length > 0) {
                                $.cookie('contrastData', JSON.stringify(domJSON), { path:'/' });
                            } else {
                                $.cookie('contrastData', null, {  path:'/' });
                            }
                            _this.contrastShow();
                        }
                    }

                });
            });
        },
        removeAll:function () { //移除全部对比的商品
            $("#contrast_removeAll").live('click', function () {
                $.cookie('contrastData', null, {  path:'/' });
                $("#contrast_fixed").html(" ").hide();
            });
        },
        LiveBtn:function () { //绑定确认对比按钮
            var _this = this;
            $("#all_contrast").live('click', function (E) {
                E.preventDefault();
                var _url = EJS.HomeUrl;
                if (!_url) {
                    return false
                }
                var cookieTxt = $.cookie('contrastData');
                if (cookieTxt == null) {
                    new Ejs.Dialog({
                        title:"提示信息",
                        type:Ejs.Dialog.type.ERROR,
                        info:'对不起，您还没有选择对比的商品！'
                    });
                    $("#contrast_fixed").html(" ").hide();
                    return false;
                }
                cookieTxt = jQuery.parseJSON(cookieTxt);
                var domList = cookieTxt["list"];
                var _ids = '';
                $(domList).each(function (i) {
                    _ids += this['id'] + ',';
                });
                if (domList.length < 2) {
                    new Ejs.Dialog({
                        title:"提示信息",
                        type:Ejs.Dialog.type.WARN,
                        info:'对不起，最少要选择两个商品才能对比！'
                    });
                    _this.contrastShow();
                    return false;
                } else {
                    _this.contrastShow();
                }
                Ejs.ContrastWindows = new Ejs.ContrastWindow({
                    ids:_ids
                });
            });
        }
    };

    Ejs.contrasts = new Ejs.contrast();

});

$(document).ready(function () {
    /*
     *加入对比效果
     */
    $(".product_list li>.box").each(function (i) {
        var _this = $(this);
        _this.hover(function (e) {
            _this.find(".join").fadeIn();
        }, function (e) {
            _this.find(".join").fadeOut();
            _this.find(".join>span").removeClass("join_exist");
        });
    });
});