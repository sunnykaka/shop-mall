//这个函数负责布局桌面上的图标
(function () {

    extDesk = window.extDesk || {};

    extDesk.defaultOptions = {
        iconsContainerTag: "x-shortcuts",
        iconWidth: 87,
        iconHeight: 'auto',
        iconMinHeight: 87,
        displayModal: 'V'//'H'
    };

    extDesk.core = function (options) {
        this._opt = options || {};
        this.initialise();
    }

    extDesk.core.prototype = {
        setOptions: function (_options) {
            Ext.extend(extDesk.defaultOptions, _options || {});
        },
        initialise: function () {
            //this.setOptions(this._opt);
            this.setIconContainerHeight();
            this.reSetIcons();
            this.regWindowReSizeEvent();
        },
        reSetIcons: function () {
            var _eleParent = Ext.fly(extDesk.defaultOptions.iconsContainerTag);
            _eleParent.setStyle({position: 'relative'});

            if (extDesk.defaultOptions.displayModal == "H") {
                Ext.each(_eleParent.select('dt').elements, function (ele) {
                    Ext.fly(ele).setStyle({
                        clear: 'none',
                        float: 'left',
                        width: extDesk.defaultOptions.iconWidth + 'px',
                        height: extDesk.defaultOptions.iconHeight + 'px',
                        overflow: 'hidden'
                    });
                });
            } else if (extDesk.defaultOptions.displayModal == "V") {
                var _h = 0,
                    _v = 0,
                    i = 0;
                Ext.each(_eleParent.select('dt').elements, function (val, i, a) {
                    var _this = Ext.fly(val);
                    if ((_v + _this.getHeight()) > _eleParent.parent().getHeight()) {
                        _v = 0;
                        _h += _this.getWidth() + parseInt(_this.getStyle('margin-left').replace('px', ''));
                    }
                    _this.setStyle({
                        left: _h + 'px',
                        top: _v + 'px',
                        float: 'none',
                        position: 'absolute',
                        'min-height': extDesk.defaultOptions.iconMinHeight + 'px',
                        width: extDesk.defaultOptions.iconWidth + 'px',
                        height: extDesk.defaultOptions.iconHeight + 'px'
                    });
                    _v += _this.getHeight() + parseInt(_this.getStyle('margin-top').replace('px', ''));
                });
            }
        },
        setIconContainerHeight: function () {
            var _height = Ext.fly('x-shortcuts').parent().getHeight();
            Ext.fly(extDesk.defaultOptions.iconsContainerTag).setHeight(_height - 20);
        },
        regWindowReSizeEvent: function () {
            var _self = this;
            Ext.fly(window).on('resize',
                (function (e) {
                    _self.setIconContainerHeight.apply(_self);
                    _self.reSetIcons.apply(_self);
                }));
        }
    }

})();

Ext.app.App = function (cfg) {
    Ext.apply(this, cfg);
    this.addEvents({
        'ready': true,
        'beforeunload': true
    });

    Ext.onReady(this.initApp, this);

    Ext.onReady(function () {
        new extDesk.core({});
    });

};


/**
 * App维护一个桌面，桌面上有图表，任务栏，任务栏上有开始菜单
 */
Ext.extend(Ext.app.App, Ext.util.Observable, {
    isReady: false,
    startMenu: null,
    modules: null,

    getStartConfig: function () {

    },

    initApp: function () {

        this.startConfig = this.startConfig || this.getStartConfig();

        this.desktop = new Ext.Desktop(this);

        this.launcher = this.desktop.taskbar.startMenu;

        this.modules = this.getModules();

        if (this.modules) {
            this.initModules(this.modules);
        }

        this.init();

        Ext.EventManager.on(window, 'beforeunload', this.onUnload, this);
        this.fireEvent('ready', this);
        this.isReady = true;
    },

    getModules: Ext.emptyFn,

    init: Ext.emptyFn,

    initModules: function (ms) {
        for (var i = 0, len = ms.length; i < len; i++) {
            var m = ms[i];
            this.launcher.add(m.launcher);
            m.app = this;
        }
    },

    getModule: function (name) {
        var ms = this.modules;
        if (ms) {
            for (var i = 0, len = ms.length; i < len; i++) {
                if (ms[i].id == name || ms[i].appType == name) {
                    return ms[i];
                }
            }
        }
    },

    onReady: function (fn, scope) {
        if (!this.isReady) {
            this.on('ready', fn, scope);
        } else {
            fn.call(scope, this);
        }
    },

    getDesktop: function () {
        return this.desktop;
    },

    onUnload: function (e) {
        if (this.fireEvent('beforeunload', this) === false) {
            e.stopEvent();
        }
    }
});