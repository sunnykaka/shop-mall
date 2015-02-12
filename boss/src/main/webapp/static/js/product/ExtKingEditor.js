Ext.namespace("KE");
KE.app = (function() {
    return {
        init : function (initParam){
            setTimeout(function(){
                KindEditor.create('#' + initParam.renderTo, initParam);
            }, ((!initParam.delayTime || initParam.delayTime) <= 0 ? 5 : initParam.delayTime));
        },

        getEditor : function(renderTO) {
            var editors = KindEditor.instances;
            for(var i = 0; i < editors.length; i++){
                if(editors[i].renderTo && editors[i].renderTo === renderTO){
                    return editors[i];
                }
            }
        }
    };
})();