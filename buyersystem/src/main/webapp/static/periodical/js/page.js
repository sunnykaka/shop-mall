var Page = (function () {

    var $container = $('#container'),
        $bookBlock = $('#bb-bookblock'),
        $items = $bookBlock.children(),
        itemsCount = $items.length,
        current = 0,
        bb = $('#bb-bookblock').bookblock({
            speed: 800,
            perspective: 2000,
            shadowSides: 0.8,
            shadowFlip: 0.4,
            onEndFlip: function (old, page, isLimit) {

                current = page;
                // update TOC current
                updateTOC();
                // updateNavigation
                updateNavigation(isLimit);
                // initialize jScrollPane on the content div for the new item
                setJSP('init');
                // destroy jScrollPane on the content div for the old item
                setJSP('destroy', old);

            }
        }),
        $navNext = $('#bb-nav-next'),
        $navPrev = $('#bb-nav-prev').hide(),
        //$maxBtnNext = $('#btn_next'),
        //$maxBtnPrev = $('#btn_prev'),
        $menuItems = $container.find('ul.menu-toc > li'),
        $tblcontents = $('#tblcontents'),
        transEndEventNames = {
            'WebkitTransition': 'webkitTransitionEnd',
            'MozTransition': 'transitionend',
            'OTransition': 'oTransitionEnd',
            'msTransition': 'MSTransitionEnd',
            'transition': 'transitionend'
        },
        transEndEventName = transEndEventNames[Modernizr.prefixed('transition')],
        supportTransitions = Modernizr.csstransitions;

    function init() {

        // initialize jScrollPane on the content div of the first item
        setJSP('init');
        initEvents();

    }

    function lazyImage(id) {

        var load = function (n) {
            var src,
                img,
                original;

            if (n < 0) {
                n = 0;
            }

            if (n < 10) {
                n = '0' + n;
            }

            img = $('#item' + n).find('img');
            src = img.attr('src');
            if (src === '') {
                original = img.attr('data-original');
                img.attr('src', original);
            }
        };

        load(id);
        load(id + 1);
        load(id + 2);
        load(id - 1);
        load(id - 2);
    }

    function initEvents() {

        // add navigation events
        $navNext.on('click', function () {
            lazyImage(current);
            bb.next();
            return false;
        });

        $navPrev.on('click', function () {
            lazyImage(current);
            bb.prev();
            return false;
        });

        /*$maxBtnNext.on('click', function () {
            lazyImage(current);
            bb.next();
            return false;
        });

        $maxBtnPrev.on('click', function () {
            lazyImage(current);
            bb.prev();
            return false;
        });*/

        // add swipe events
        $items.on({
            'swipeleft': function (event) {
                if ($container.data('opened')) {
                    return false;
                }
                bb.next();
                return false;
            },
            'swiperight': function (event) {
                if ($container.data('opened')) {
                    return false;
                }
                bb.prev();
                return false;
            }
        });

        // show table of contents
        $tblcontents.on('click', toggleTOC);

        // click a menu item
        $menuItems.on('click', function () {
            var $el = $(this),
                idx = $el.index(),
                jump = function () {
                    bb.jump(idx + 1);
                };

            lazyImage(idx);

            current !== idx ? closeTOC(jump) : closeTOC();

            return false;

        });

        // reinit jScrollPane on window resize
        $(window).on('debouncedresize', function () {
            // reinitialise jScrollPane on the content div
            setJSP('reinit');
        });

    }

    function setJSP(action, idx) {

        var idx = idx === undefined ? current : idx,
            $content = $items.eq(idx).children('div.content'),
            apiJSP = $content.data('jsp');

        lazyImage(current);

        if (action === 'init' && apiJSP === undefined) {
            $content.jScrollPane({verticalGutter: 0, hideFocus: true, showArrows: true });
        } else if (action === 'reinit' && apiJSP !== undefined) {
            apiJSP.reinitialise();
        } else if (action === 'destroy' && apiJSP !== undefined) {
            apiJSP.destroy();
        }

    }

    function updateTOC() {
        $menuItems.removeClass('menu-toc-current').eq(current).addClass('menu-toc-current');
    }

    function updateNavigation(isLastPage) {

        if (current === 0) {
            $navNext.show();
            $navPrev.hide();
            //$maxBtnNext.show();
            //$maxBtnPrev.hide();
        } else if (isLastPage) {
            $navNext.hide();
            $navPrev.show();
            //$maxBtnNext.hide();
            //$maxBtnPrev.show();
        } else {
            $navNext.show();
            $navPrev.show();
            //$maxBtnNext.show();
            //$maxBtnPrev.show();
        }

    }

    function toggleTOC() {
        var opened = $container.data('opened');
        opened ? closeTOC() : openTOC();
    }

    function openTOC() {
        $navNext.hide();
        $navPrev.hide();
        $container.addClass('slideRight').data('opened', true);
    }

    function closeTOC(callback) {

        updateNavigation(current === itemsCount - 1);
        $container.removeClass('slideRight').data('opened', false);
        if (callback) {
            if (supportTransitions) {
                $container.on(transEndEventName, function () {
                    $(this).off(transEndEventName);
                    callback.call();
                });
            } else {
                callback.call();
            }
        }

    }

    return { init: init };

})();