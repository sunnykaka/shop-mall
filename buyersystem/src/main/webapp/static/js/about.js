(function (win) {
    function About() { return new About.prototype.init(); };
    About.prototype = {
        init: function () {
            this.nav = $("#aboutNav");
            this.lis = this.nav.find("li");
            this.len = this.lis.length - 1;
            this.top = this.nav.offset().top;
            this.offsetTop = [];
            this.navFix();
        },
        navFix: function () {
            var _this = this;
            $(win).bind("scroll", function () {
                var scrolls = $(win).scrollTop();
                scrolls >= _this.top ? _this.nav.addClass("fix") : _this.nav.removeClass("fix");
                for (var n = 0; n < _this.len; n++) {
                    if (scrolls >= _this.offsetTop[n] - 100) {
                        _this.lis.eq(n).addClass("current").siblings().removeClass("current");
                        continue;
                    }
                };
            });
        },
        intro: function () {
            var _this = this;
            if (!win.XMLHttpRequest) return;
            for (var i = 0; i < _this.len; i++) {
                _this.offsetTop[i] = $(_this.lis.eq(i).find("a").attr("href")).offset().top;
            };
            _this.nav.find("li").click(function (e) {
                //e.preventDefault();
                var self = $(this);
                if (self.index() == _this.len) return;
                setTimeout(function () {
                    self.addClass("current").siblings().removeClass("current");
                }, 500);
                animate(_this.offsetTop[self.index()]);
                return false;
            });
            function animate(top) {               
                $("body,html").stop().animate({ "scrollTop": top - 100 });
            };
            win.location.hash && animate($(win.location.hash).offset().top);
        }
    };
    About.prototype.init.prototype = About.prototype;
    win.About = About;
})(window);