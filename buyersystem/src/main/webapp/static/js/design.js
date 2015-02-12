/**
 * Created by IntelliJ IDEA.
 * User: computer2
 * Date: 12-5-7
 * Time: 下午12:42
 * To change this template use File | Settings | File Templates.
 */
/*import jquery.js file*/

var Ejs = window.Ejs || {};



Ejs.layer=function(target){
    this.target=target;
    this.popEditWindow=null;
    this.init();
}

Ejs.layer.prototype = {
    init:function () {
        var _self=this;
        if($(_self.target).length===0){
            return false;
        }
        _self.attachShader(_self.target);
        _self.attachAddBar();
        _self.attachElementsEvent();
        if(!_self.isBodyModule()){
            _self.attachAddBarEvent();
        }
        $(".e_region").each(function(i){
            _self.reSetRegion(this);
        });
    },
    attachShader:function(obj){
        var _length=$(obj).length;
        var _self=this;
        $(obj).each(function(i){
            var _this=$(this),
                _isEditable=_self.isEditable(this),
                _isDeleteable=_self.isDeleteable(this),
				_moduleName = _this.attr('data-module-name'),
                _htmlEdit, _htmlDelete, _htmlUp, _htmlDown,_htmlModuleName;

            var _e_wcss=$('<div class="e_wcss"></div>'),
                _wrap= $('<div class="wrap"></div>');

            if(_moduleName){
                _htmlModuleName = '<a href="javascript:void(0);"><span>' + _moduleName + '</span></a>';
                _wrap.append(_htmlModuleName);
            }
			
            if (_isEditable) {
                _htmlEdit = '<a href="#" class="edit"><span>编辑</span></a>';
                _wrap.append(_htmlEdit);
            }

            if(_self.isRegion()){
                _htmlUp = '<a href="#" class="move_up"><span>上移</span></a>';
                _wrap.append(_htmlUp);

                _htmlDown = '<a href="#" class="move_down"><span>下移</span></a>';
                _wrap.append(_htmlDown);

                if (_isDeleteable) {
                    _htmlDelete = '<a href="#" class="delete"><span>删除</span></a>';
                    _wrap.append(_htmlDelete);
                }
            }

	        var _wrapperHeight=_this.outerHeight();

            _e_wcss.css({
                height:_wrapperHeight,
                "margin-top":(-_wrapperHeight) + "px"
            });

            _wrap.css({
                height:_wrapperHeight-4
            });

            _e_wcss.append(_wrap);

            _this.append(_e_wcss);
        });
    },
    attachAddBar:function(){
        var dataNames = {
            headRegion: "添加头部新模块",
            bodyRegion: "添加中部新模块",
            footRegion: '添加底部新模块'
        }
        $(".e_region").each(function(i){
            var dataText = dataNames[$(this).attr("data-name")] || "添加新模块";
            var _e_addbar=$('<div class="e_addbar">'+dataText+'</div>');
            if($(this).find(".e_addbar").length==0){
                $(this).append(_e_addbar);
            }
        });
    },
    attachElementsEvent:function(){
        var _root=this;
        $(this.target).each(function(i){
            var _self=$(this);
            //显示装修层
            _self.live("mouseover",function(){
                _self.find(".e_wcss").show();
            });

            //隐藏装修层
            _self.live("mouseout",function(){
                _self.find(".e_wcss").hide();
            });

            //编辑按扭
            _self.find(".edit").live("click",function(e){
                e.preventDefault();
                $.get(_root.getDataEditUrl(_self),function(_data){
                    var _editPop=_root.popWindow("编辑内容",_data,function(){
                        $("#submitEdit").submit(function(e){
                            //console.log("HSKLSL");
                            e.preventDefault();

                            if(window.designEditor){
                                window.designEditor.sync();
                            }

                            $.ajax({
                                data:$("#submitEdit").serialize(),
                                type:"POST",
                                cache:false,
                                url:$("#submitEdit").attr("action"),
								dataType:'text',
                                beforeSend: function(){

                                },
                                error: function(requestX) {

                                },
                                success: function(_data2) {
                                    var _e = eval("(" + _data2 + ")");
                                    if (_e["success"]) {
                                        $.get(_e["data"]["previewUrl"], function (_data3) {
                                            var replaceConten=$(_data3);
                                            _self.replaceWith(replaceConten);
                                            _root.attachShader(replaceConten);
                                            _root.dieAllElementsEvent();
                                            _root.attachElementsEvent();
                                            _editPop.hide();
                                            $("#"+_editPop.getId()).remove();
	                                        _root.reSetRegion(replaceConten.parent());
                                        });
                                    } else {
                                        alert(_e["msg"]);
                                    }
                                }
                            });
                            return false;
                        });
                    });
                },"text");
            });

            //删除按扭
            _self.find(".delete").live("click",function(e){
                e.preventDefault();

                new Ejs.Dialog({
                    type: Ejs.Dialog.type.WARN,
                    title: "确定删除吗？<br>删除后将不能恢复",
                    isModal: true,
                    opacity: 0,
                    titleStyle: {
                        fontWeight: "normal",
                        paddingLeft: 5,
                        paddingTop: 15
                    },
                    titleWrapperStyle: {
                        paddingTop: 20
                    },
                    info: "",
                    infoStyle: {
                        height: 10,
                        overflow: "hidden"
                    },
                    isConfirm: true,
                    eventObj: e,
                    afterClose: function (v) {
                        if (v) {
                            _root.deleteLayer(_self);
                        }
                    }
                });

            });

            //上移按扭
            _self.find(".move_up").live("click",function(e){
                e.preventDefault();
                if(_self.prev().length>0){
                    if(_root.moveLayer(_self,"up")){
                        _self.after(_self.prev());
                        _root.reSetRegion(_self.parent());
                    }
                }
            });

            //下移按扭
            _self.find(".move_down").live("click",function(e){
                e.preventDefault();
                if(_self.next().length>0){
                    if(_root.moveLayer(_self,"down")){
                        _self.before(_self.next());
                        _root.reSetRegion(_self.parent());
                    }
                }
            });
        });
    },
    attachAddBarEvent:function(){
        var _root=this;
        //添加模块
        //if(_root.isRegion()){
        $(".e_addbar").each(function (i) {
            var _this_add_bar = $(this);
            _this_add_bar.live("click", function (e) {
                var _url = _root.getDataUrl(_root.getRegion(_this_add_bar));
                $.get(_url, function (data2) {
                    _root.popEditWindow=_root.popWindow("添加新模块", data2, function () {
                        $(".dialog_panel .btn_ok").each(function (ik) {
                            var _btn_this = $(this);
                            _btn_this.live("click", function (es) {
                                _root.getDataUrl(_btn_this);
                                $.ajax({
                                    type:"POST",
                                    async:false,
                                    url:_root.getDataUrl(_btn_this),
									dataType:'text',
                                    data:{
                                        prototypeId:_root.getDataPrototypeId(_btn_this),
                                        regionName:_root.getDataRegionName(_btn_this),
                                        pageId:_root.getDataPageId()
                                    },
                                    success:function (data3) {
                                        //console.log(data3);
                                        var _e = eval("(" + data3 + ")");
                                        if (_e["success"]) {
                                            $.get(_e["data"]["previewUrl"], function (data4) {
                                                var _aadContent = $(data4);
                                                _this_add_bar.before(_aadContent);
                                                _root.attachShader(_aadContent);
                                                _root.dieAllElementsEvent();
                                                _root.attachElementsEvent();
                                                _root.reSetRegion(_this_add_bar.parent());
                                            });
                                        } else {
                                            alert(_e["msg"]);
                                        }
                                    }
                                });
                            });
                        });
                    });
                });
            });

            /*
             $("#Hpop").html("");
             $(".dialog_nav li").each(function(is){
             var _cthis=$(this);
             console.log(_cthis);
             _cthis.click(function(e){
             console.log(e);
             $(".dialog_nav li").removeClass("cur");
             _cthis.addClass("cur");
             $(".dialog_panel ul").hide();
             $(".dialog_panel ul").eq(is).show();
             });
             });*/
        });
    },
    dieAllElementsEvent:function(){
        $(this.target).each(function(i){
            $(this).die();
            $(this).find(".edit").die();
            $(this).find(".delete").die();
            $(this).find(".move_up").die();
            $(this).find(".move_down").die();
            $(this).find(".e_addbar").die();
        });
    },
    deleteLayer:function(obj){
        var _self=this;
        $.post(_self.getDataUrl(obj)+"delete",
            {
                moduleInstanceId:_self.getDataId(obj),
                pageId:_self.getDataPageId(),
                regionName:_self.getDataName($(obj).parent())
            },function(data){
                var _e=eval("("+data+")");
                if(_e["success"]){
                    var _parent=$(obj).parent();
                    $(obj).remove();
                    _self.reSetRegion(_parent);
                }else{
                    alert(_e["msg"]);
                }
            });
    },
    disableMoveUp:function(obj){
        $(obj).find(".move_up").removeClass("move_up").addClass("no_move_up") ;
    },
    disableMoveDown:function(obj){
        $(obj).find(".move_down").removeClass("move_down").addClass("no_move_down");
    },
    enableMoveUp:function(obj){
        $(obj).find(".no_move_up").removeClass("no_move_up").addClass("move_up");
    },
    enableMoveDown:function(obj){
        $(obj).find(".no_move_down").removeClass("no_move_down").addClass("move_down");
    },
    popWindow:function(_title,_htmlContents,_func){
        var _root=this;
        var _htmlContent = '<div class="pop_edit">';
        _htmlContent +=_htmlContents;
        _htmlContent +='</div>';

        var _htmlTitle = '<div class="pop_edit_title">';
        _htmlTitle += '<span class="pop_close" style="">关闭</span>';
        _htmlTitle+=_title;
        _htmlTitle+='</div>';

        var _height=365;
        var popEdit = new com.layer({
            content:_htmlContent,
            src:"",
            loaded:false,
            cache:false,
            title:_htmlTitle,
            closeText:"",
            isModal:false,
            shadeColor:"#000000",
            positionType:com.layer.positionType.center,
            opacity:0.0,
            style:{
                position:"absolute",
                backgroundColor:"#F1EFE6",
                border:"2px solid #705540",
                overflow:"hidden",
                width:750,
                height:460
            },
            onshowing:function () {
                if(typeof _func=="function"){
                    _func();
                }
            },
            onclosing:function () {

            }
        });

        popEdit.show();

        $(".pop_close").click(function (evs) {
            popEdit.hide();
            $("#"+popEdit.getId()).remove();
        });

        return popEdit;
    },
    isEditable:function(obj){
        return ($(obj).attr("data-iseditable")=="true");
    },
    isDeleteable:function(obj){
        return (this.isRegion()&&($(obj).attr("data-iseditable")=="true"));
    },
    isRegion:function(){
        return $(this.target).eq(0).parent().hasClass("e_region") ;
    },
    isModuleElement:function(obj){
        return $(obj).hasClass("e_module");
    },
    isBodyModule:function(){
        return $(this.target).parent().hasClass("wrapper");
    },
    getRegion:function(childElement){
        if($(childElement).parent().hasClass("e_region")){
            return $(childElement).parent();
        }else{
            return null;
        }
    },
    getDataId:function(obj){
        return $(obj).attr("data-id");
    },
    getShopId:function(obj){
        return $("#shopId").val();
    },
    getDataPrototypeId:function(obj){
        return $(obj).attr("data-prototypeid");
    },
    getDataEditUrl:function(obj){
        return $(obj).attr("data-url-edit");
    },
    getDataName:function(obj){
        return $(obj).attr("data-name");
    },
    getDataRegionName:function(obj){
        return $(obj).attr("data-regionName");
    },
    getDataUrl:function(obj){
        return $(obj).attr("data-url");
    },
    getDataPageId:function(){
        return $("#pageId").val();
    },
    moveLayer:function(obj,_direction){
        var _root=this;
        var _returnValue=null;
        $.ajax({type:"post",async:false,
            url:_root.getDataUrl(obj)+"move",
            data:{
                moduleInstanceId:_root.getDataId(obj),
                regionName:_root.getDataName(obj.parent()),
                pageId:_root.getDataPageId(),
                direction:_direction
            },
            success:function(data){
                var _e=eval("("+data+")");
                //console.log("1");
                if(_e["success"]){
                    _returnValue=true;
                }else{
                    alert(_e["msg"]);
                    _returnValue=false;
                }
            },
            complete:function(){
                //console.log("2");
            }
        });
        //console.log("3");
        return _returnValue;
    },
    reSetRegion:function(regionName){
        var _modules=$(regionName).find(".e_module");
        var _self=this;
        if(_modules.length==0){
            return false;
        }else if(_modules.length==1){
            _self.disableMoveDown(_modules);
            _self.disableMoveUp(_modules);
        }else if(_modules.length==2){
            _self.disableMoveUp(_modules[0]);
            _self.enableMoveDown(_modules[0]);

            _self.disableMoveDown(_modules[_modules.length-1]);
            _self.enableMoveUp(_modules[_modules.length-1]);
        }else{
            _self.enableMoveDown(_modules);
            _self.enableMoveUp(_modules);

            _self.disableMoveUp(_modules[0]);
            _self.enableMoveDown(_modules[0]);

            _self.disableMoveDown(_modules[_modules.length-1]);
            _self.enableMoveUp(_modules[_modules.length-1]);
        }
    }
}

$(document).ready(function(){
    new Ejs.layer(".wrapper>.e_module");
    new Ejs.layer(".e_region>.e_module");
});

Ejs.showLayer = function(_title,_htmlContents){
    var _htmlContent = '<div class="pop_edit">' + _htmlContents + '</div>',
        _htmlTitle = '<div class="pop_edit_title">' +
            '<span class="pop_close" style="">关闭</span>' + _title + '</div>';

    var popEdit = new com.layer({
        content:_htmlContent,
        src:"",
        loaded:false,
        cache:false,
        title:_htmlTitle,
        closeText:"",
        isModal:false,
        shadeColor:"#000000",
        positionType:com.layer.positionType.center,
        opacity:0.0,
        style:{
            position:"absolute",
            backgroundColor:"#F1EFE6",
            border:"2px solid #705540",
            overflow:"hidden",
            width:400,
            height:130
        },
        onshowing:function () {},
        onclosing:function () {}
    });

    popEdit.show();

    $(".pop_close").click(function (evs) {
        popEdit.hide();
        $("#" + popEdit.getId()).remove();
    });

    return popEdit;
}

function uploadConfiguration() {
    var iFrameSrc = $("#uploadFileJump").val(),
        htmlContent = '<div style="padding: 10px 0 0 40px">' +
            '<iframe frameborder="0" scrolling="no" width="320" height="80" src="' + iFrameSrc + '"></iframe>' +
            '</div>',
        showUploadWindow = Ejs.showLayer('上传配置', htmlContent);
}