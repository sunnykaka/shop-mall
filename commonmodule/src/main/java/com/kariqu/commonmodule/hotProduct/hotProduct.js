(function ($) {
    $LAB.script(function () {
        if (typeof $.fn.jcarousel === 'undefined') {
            return EJS.StaticDomain + '/js/jquery.jcarousel.js';
        }
        return null;
    }).wait(function () {
        var carousel = $('.mod-hot-recommend .e-carousel');
        carousel.each(function () {
            var self = $(this);

            new Ejs.ScrollLoad({
                wrap: self,
                onsight: function (o) {
                    o.wrapElem.find('img').each(function () {
                        $(this).attr('src', $(this).attr('data-original'));
                    });
                }
            });

            if (self.find('li').length > 7) {
                self.jcarousel({
                    vertical: false,
                    scroll: 7
                });
            }
        });
    });
}(jQuery));