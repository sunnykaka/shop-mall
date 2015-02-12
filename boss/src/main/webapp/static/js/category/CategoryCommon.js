//设置节点选中状态
var setCheckStatus = function (node, isChk) {
    if (node.leaf) {
        return;
    } else {
        Ext.each(node.childNodes, function (eachNode) {
            eachNode.getUI().checkbox.checked = isChk;
            eachNode.attributes.checked = isChk;
            setCheckStatus(eachNode, isChk)
        });
    }
}

//设置节点有效和失效状态
var setDisableStatus = function (node, check) {
    if (node.leaf) {
        return;
    } else {
        Ext.each(node.childNodes, function (eachNode) {
            if (eachNode.getUI().checkbox) {
                eachNode.getUI().checkbox.disabled = check;
                eachNode.getUI().checkbox.checked = false;
            }
            eachNode.attributes.checked = false;
            eachNode.attributes.disabled = check;
            setDisableStatus(eachNode, check)
        });
    }

};

var setCheckAllDisableStatus= function (node, check){
    var parentNode=node.parentNode;

    if(parentNode!='[Node -1]' && parentNode.attributes.checked != undefined){
        var num=0;
        Ext.each(parentNode.childNodes, function (eachNode) {
            if (eachNode.getUI().checkbox.checked) {
                num++
            }
        });
        if(num == parentNode.childNodes.length){
            Ext.each(parentNode.childNodes, function (eachNode) {
                if (eachNode.getUI().checkbox) {
                    eachNode.getUI().checkbox.disabled = check;
                    eachNode.getUI().checkbox.checked = false;
                }
                eachNode.attributes.checked = false;
                eachNode.attributes.disabled = check;
            });
            parentNode.getUI().checkbox.checked = true;
            parentNode.attributes.checked = true;
            setCheckAllDisableStatus(parentNode, check);
        }

    }
}

//选择树的级联选择函数,父和子联动，子选择了父也选择
//在前台类目选择属性和值的时候使用
var cascadeCheck = function (node, check) {
    if (node.leaf && check) {
        var parent = node.parentNode;
        parent.getUI().checkbox.checked = true;
        parent.attributes.checked = true;
        return;
    }
    setCheckStatus(node, check);
};

//在前台类目设定筛选属性的时候使用
var attributeScreening = function (node, check) {
    if (node.leaf) {
        var parent = node.parentNode;
        parent.getUI().checkbox.checked = true;
        parent.attributes.checked = true;
        Ext.each(parent.childNodes, function (eachNode) {
            eachNode.getUI().checkbox.disabled = true;
            eachNode.getUI().checkbox.checked = false;
        });
        return;
    }
    attributeScreening(node, check);
};

/**
 * 对树节点进行级联取消
 * 在前台类目关联后台类目节点的时候使用
 * 如果选择了父亲节点，则所有儿子节点不能选
 * @param node
 * @param check
 */
var cascadeDisable = function (node, check) {
    setCheckAllDisableStatus(node, check);
    if (node.leaf) {
        return;
    } else {
        if(check){
            node.expand()
        }
        setDisableStatus(node, check);
    }
}


//当前操作的表格当前全局变量
var currentGrid;





