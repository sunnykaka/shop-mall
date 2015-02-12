/**
 * 商品对比模块
 * Created by HuaLei.Du on 2014/5/12.
 */

var goodsCompare = (function () {
    var btnEle = $('.btn-compare'),
        cookieToggle = 'compareToggle',
        cookieCompare = 'compareData';

    // 获取对比模块元素
    function getCompareEle() {
        return $('.compare-fixed');
    }

    // 获取对比数据
    function getCompareData() {
        return $.cookie(cookieCompare);
    }

    // 得到已加入对比商品的id
    function getIds() {
        var cookieData = getCompareData(),
            jsonData,
            listArr,
            listArrCount,
            i,
            ids = [];

        if (cookieData != null) {
            jsonData = jQuery.parseJSON(cookieData) || {};
            listArr = jsonData.list || [];
            listArrCount = listArr.length;
            for (i = 0; i < listArrCount; i++) {
                ids.push(listArr[i].id);
            }
        }

        return ids;
    }

    // 在商品列表高亮已经选中的商品
    function highlightSelected() {
        var ids = getIds();

        if (ids.length < 1) {
            btnEle.removeClass('selected');
            return;
        }

        btnEle.each(function () {
            var pid = $(this).attr('pid');

            if ($.inArray(pid, ids) >= 0) {
                $(this).addClass('selected');
            } else {
                $(this).removeClass('selected');
            }

        });
    }

    // IE6不支持Fixed的特殊处理
    function fixedIE6() {
        var height = 110,
            compareEle = getCompareEle(),
            top;

        if (compareEle) {

            if (compareEle.find('.up').length) {
                height = 4;
            }

            top = document.documentElement.clientHeight + document.documentElement.scrollTop - height;
            compareEle.css('top', top + 'px');
        }
    }

    // 创建浮动框HTML
    function createHTML() {
        var cookieData = getCompareData(),
            jsonData,
            listArr,
            listArrCount,
            compareHTML = '',
            listHTML = '',
            btnsHTML,
            disabled = '',
            toggle = 'down',
            toggleTitle = '收起',
            i;

        if ($.cookie(cookieToggle) === 'up') {
            toggle = 'up';
            toggleTitle = '展开';
        }

        if (cookieData != null) {
            jsonData = jQuery.parseJSON(cookieData) || {};
            listArr = jsonData.list || [];
            listArrCount = listArr.length;

            if (listArrCount > 0) {
                listHTML = getListHTML();

                if (listArrCount < 2) {
                    disabled = ' disabled';
                }

                btnsHTML = '<div class="btns pull-right">' +
                    '   <a target="_blank" href="#" class="e-btn btn-default btn-sm ' + disabled + '">开始对比</a><br>' +
                    '   <a href="javascript:;" class="removeAll">[清空商品]</a>' +
                    '</div>';

                if (listHTML !== null) {
                    listHTML = '<ul class="goodsList pull-left">' + listHTML + '</ul>';
                    compareHTML = '<div class="compare-fixed"><div class="e-wrapper ' + toggle + '"><div class="body">' +
                        '<div class="title">商品对比</div><div class="operate">' + toggleTitle + '<i></i></div>' +
                        listHTML + btnsHTML +
                        '</div></div></div>';
                    $('body').append(compareHTML);
                    if (_isIE6) {
                        fixedIE6();
                    }
                }

            }
        }
    }

    // 获取商品列表的HTML
    function getListHTML() {
        var cookieData = getCompareData(),
            jsonData,
            listArr,
            listArrCount,
            listHTML = '',
            i,
            j;

        if (cookieData != null) {

            jsonData = jQuery.parseJSON(cookieData) || {};
            listArr = jsonData.list || [];
            listArrCount = listArr.length;

            for (i = 0; i < listArrCount; i++) {
                listHTML += '<li class="item">' +
                    '<div class="pic"><img width="80" height="80" src="' + listArr[i].img + '" alt=""/></div>' +
                    '<div class="aside">' +
                    '   <div class="name"><a href="' + EJS.ProductDetailBase + '/' + listArr[i].id + '">' + listArr[i].name + '</a></div>' +
                    '   <div class="price">¥ ' + listArr[i].price + '</div>' +
                    '   <a href="javascript:;" class="remove" pid="' + listArr[i].id + '">[删除]</a>' +
                    '</div>' +
                    '</li>';
            }

            if (i < 4) {
                for (j = i; j < 4; j++) {
                    listHTML += '<li class="item-empty">' +
                        '<div class="pic">' + (j + 1) + '</div>' +
                        '<div class="aside">你还可以继续添加</div>' +
                        '</li>';
                }
            }

        } else {

            for (i = 0; i < 4; i++) {
                listHTML += '<li class="item-empty">' +
                    '<div class="pic">' + (i + 1) + '</div>' +
                    '<div class="aside">你还可以继续添加</div>' +
                    '</li>';
            }

        }
        return listHTML;
    }

    // 显示对比商品清单
    function showList() {
        var wrap = getCompareEle().find('.e-wrapper'),
            operate = wrap.find('.operate');

        wrap.removeClass('up').addClass('down');
        operate.html('收起<i></i>');
        $.cookie(cookieToggle, 'down', { expires: 1, path: '/' });

        if (_isIE6) {
            fixedIE6();
        }

    }

    // 隐藏对比商品清单
    function hideList() {
        var wrap = getCompareEle().find('.e-wrapper'),
            operate = wrap.find('.operate');

        wrap.removeClass('down').addClass('up');
        operate.html('展开<i></i>');
        $.cookie(cookieToggle, 'up', { expires: 1, path: '/' });

        if (_isIE6) {
            fixedIE6();
        }

    }

    // 重新加载对比商品清单
    function reloadList() {
        var cookieData = getCompareData(),
            jsonData,
            listArr,
            btn = getCompareEle().find('.e-btn');

        if (cookieData != null) {
            jsonData = jQuery.parseJSON(cookieData) || {};
            listArr = jsonData.list || [];

            if (listArr.length > 1) {
                btn.removeClass('disabled');
            } else {
                btn.addClass('disabled');
            }

        } else {
            btn.addClass('disabled');
        }

        getCompareEle().find('.goodsList').html(getListHTML());
        showList();
    }

    // 加入对比
    function addItem(ele) {
        var cookieData = getCompareData(),
            data,
            eleData = {
                pid: ele.attr('pid'),
                pname: ele.attr('pname'),
                price: ele.attr('price'),
                img: ele.attr('img')
            },
            jsonData,
            listArr,
            listHTML,
            btn = getCompareEle().find('.e-btn');

        data = '{"id":"' + eleData.pid + '","img":"' + eleData.img + '","price":"' + eleData.price + '","name":"' +
            $.trim(eleData.pname) + '"}';

        if (!cookieData) {
            jsonData = jQuery.parseJSON('{"list":[' + data + ']}');
        } else {
            jsonData = jQuery.parseJSON(cookieData);
            listArr = jsonData.list;

            // 是否超出数量
            if (listArr.length >= 4) {
                ele.removeClass('selected');
                alert('对不起，最多可以添加4个商品');
                return;
            }

            if ($.inArray(eleData.pid, getIds()) >= 0) {
                alert('“ ' + eleData.pname + ' ” 已经加入过了');
                return;
            }

            data = jQuery.parseJSON(data);
            jsonData.list.push(data);

            if (listArr.length > 1) {
                btn.removeClass('disabled');
            } else {
                btn.addClass('disabled');
            }
        }

        $.cookie(cookieCompare, JSON.stringify(jsonData), { expires: 1, path: '/' });

        if (getCompareEle().length) {
            listHTML = getListHTML();
            getCompareEle().find('.goodsList').html(listHTML);
            showList();
        } else {
            createHTML();
            showList();
        }
    }

    // 移除对比
    function removeItem(pid, ele) {
        var cookieData = getCompareData(),
            jsonData = jQuery.parseJSON(cookieData) || {},
            listArr = jsonData.list || [],
            listArrCount = listArr.length,
            i;

        if (listArrCount < 1) {
            return;
        }

        for (i = 0; i < listArrCount; i++) {
            if (listArr[i].id == pid) {
                listArr.splice(i, 1); // 移除
                if (listArr.length > 0) {
                    $.cookie(cookieCompare, JSON.stringify(jsonData), { expires: 1, path: '/' });
                } else {
                    $.cookie(cookieCompare, null, {  path: '/' });
                }
                break;
            }
        }

        if (ele) {
            ele.parents('li').remove();
        }

    }

    // 移除全部对比
    function removeAll() {
        $.cookie(cookieCompare, null, {  path: '/' });
    }

    // 绑定事件
    function bindEvent() {
        var body = $('body');

        // 绑定加入对比
        btnEle.on('click', function () {
            var self = $(this);

            if (self.hasClass('selected')) {
                self.removeClass('selected');
                removeItem(self.attr('pid'));
                reloadList();
                highlightSelected();
            } else {
                self.addClass('selected');
                addItem(self);
            }

        });

        // 确认对比
        body.on('click', '.compare-fixed .e-btn', function (e) {
            var ids = getIds();

            if (ids.length > 1) {
                $(this).attr('href', EJS.ProductCompare + '?ids=' + ids.join());
            } else {
                e.preventDefault();
            }

        });

        // 展开或收起对比清单
        body.on('click', '.compare-fixed .operate', function () {
            var self = $(this),
                wrap = self.parents('.e-wrapper');

            if (wrap.hasClass('down')) {
                hideList();
            } else {
                showList();
            }

        });

        // 移除全部
        body.on('click', '.compare-fixed .removeAll', function () {
            removeAll();
            reloadList();
            highlightSelected();
        });

        // 移除单个商品
        body.on('click', '.compare-fixed .remove', function () {
            var self = $(this),
                pid = self.attr('pid');

            removeItem(pid, self);
            reloadList();
            highlightSelected();
        });
    }

    // 初始化
    function init() {
        bindEvent();
        createHTML();
        highlightSelected();

        if (_isIE6) {
            fixedIE6();
            $(window).scroll(function () {
                fixedIE6();
            });
        }
    }

    // 返回一个暴露的公有对象
    return {
        init: init,
        getIds: getIds,
        removeItem: removeItem
    };

}());