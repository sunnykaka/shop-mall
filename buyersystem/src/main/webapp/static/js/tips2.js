function Tips2(options) {
    var opts = $.extend({
        obj: "auto",
        content: "null",
        style: "bottom"
    }, options || {});

    this.obj = opts.obj;
    this.id = "tips_" + new Date().getTime();
    this.className = opts.style;
    this.content = opts.content;
    this.show();
}

Tips2.prototype = {
    getId: function () {
        return $("#" + this.id);
    },
    show: function () {
        this.remove();
        var offLeft = this.obj.offset().left,
            offTop = this.obj.offset().top,
            tips = $('<div><div class="tips_wrapper"><span class="at"></span>' +
                '<div class="tips_content">' + this.content + '</div>' +
                '<div class="clear"></div>' + // IE6 BUG 边框显示不完整
                '</div></div>');

        tips.attr("id", this.id);
        tips.addClass(this.className);
        tips.css({
            left: offLeft,
            top: offTop
        });
        $("body").append(tips);
    },
    remove: function () {
        var tips = $("." + this.className);
        if (tips.length > 0) {
            tips.remove();
        }
    },
    changeContent: function (html) {
        this.getId().find(".tips_content").html(html);
    }
};

