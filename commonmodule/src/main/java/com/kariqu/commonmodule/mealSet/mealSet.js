(function () {
    $LAB.script(function () {
            if (typeof $.fn.jqzoom === 'undefined') {
                return EJS.StaticDomain + '/js/jquery.jqzoom.js';
            } else {
                return null;
            }
        })
        .script(function () {
            if (typeof $.fn.jcarousel === 'undefined') {
                return EJS.StaticDomain + '/js/jquery.jcarousel.js';
            } else {
                return null;
            }
        })
        .script(EJS.StaticDomain + '/js/order_pop.js')
        .wait()
        .script(EJS.StaticDomain + '/js/product_combination_sku.js');
}());