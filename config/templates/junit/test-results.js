jQuery.fn.extend({
    scrollToMe: function () {
        var x = jQuery(this).offset().top - 200;
        jQuery('html,body').animate({scrollTop: x}, 500);
    },
    pointer: function () {
        jQuery(this).hover(function() {
            $(this).css('cursor', 'pointer')
        }, function() {
            $(this).css('cursor', 'normal')
        })
    },
    toggleText: function(one, two) {
        jQuery(this).text(jQuery(this).text() == one ? two : one);
    },
    collapseList: function(recursive) {
        if ($(this).is('ol')) {
            var list = $(this);
            if (recursive) {
                list.find('li > ol').each(function() {
                    $(this).collapseList();
                });
            }

            list.children('li').slice(1, -1).hide();
            list.prev().find('.toggle-one').text('+');
            list.findSuccesses().each(function() {
                list.css('background-color', '#99CC99');
            });
            list.findErrors().each(function() {
                list.css('background-color', '#FF9999');
            });
        }
    },
    expandList: function(recursive) {
        if ($(this).is('ol')) {
            var list = $(this);
            if (recursive) {
                list.find('li > ol').each(function() {
                    $(this).expandList();
                });
            }

            list.children('li').filter(':not(.aop-report)').slice(1, -1).show();
            list.prev().find('.toggle-one').text('-');
            list.css('background-color', 'inherit');
        }
    },
    isListExpanded: function() {
        if ($(this).is('ol')) {
            if ($(this).children('li').slice(1, -1).is("li:visible")) {
                return true;
            }
        }

        return false;
    },
    toggleList: function(recursive) {
        if ($(this).is('ol')) {
            if ($(this).isListExpanded()) {
                $(this).collapseList(recursive);
            } else {
                $(this).expandList(recursive);
            }
        }
    },
    findErrors: function() {
        return $(this)
            .find('.message-container[class~="ERROR"]')
            .add('.message-container[class~="WARN"]');
    },
    findSuccesses: function() {
        return $(this).find('.message-container[class~="INFO"]');
    },
    isAopTraceVisible: function() {
        console.log($(this));
        console.log($(this).find('.aop-report:visible'));
        return $(this).find('.aop-report:visible').length > 0;
    },
    hideAopTraces: function() {
        $(this).find('.aop-report').hide();
    },
    showAopTraces: function() {
        if ($(this).isListExpanded()) {
            $(this).find('.aop-report').show();
        }
    }
});
function registerToggler(where, toggler, toToggle) {
    var tglr = $(where).find(toggler);
    var target = $(where).find(toToggle);
    if (tglr.length > 0 && target.length > 0) {
        target.hide();
        tglr.click(function(e) {
            target.toggle();
            target.scrollToMe();
        });
    }
}
function getIds(clsPrefix, where) {
    var ids = [];
    var r = new RegExp("^" + clsPrefix);
    $(where).find("li[class*='" + clsPrefix + "']").each(function() {
        var clss = $(this).attr('class').split(' ');
        for (var i = 0; i < clss.length; i++) {
            if (clss[i].search(r) == 0) {
                if (ids.indexOf(clss[i]) < 0) {
                    ids.push(clss[i]);
                }
                break;
            }
        }
    });
    return ids;
}
$(document).ready(function() {
    $('.link').pointer();
    $('.message-container').each(function() {
        registerToggler(this, '.toggle-screenshot', '.image');
        registerToggler(this, '.toggle-stacktrace', '.exception');
        registerToggler(this, '.toggle-pagesource', '.pagesource');
        registerToggler(this, '.toggle-errordb', '.errordb');
    });

    $(getIds('suite', document)).each(function() {
        $('.' + this).wrapAll("<li><ol/></li>");
    });

    $(getIds('test', document)).each(function() {
        $('.' + this).wrapAll("<li><ol/></li>");
    });

    $('ol > li > ol').before(
        '<div class="expanders">[<span class="link toggle-one">+</span>][<span class="link toggle-all">all</span>][<span class="link toggle-traces">traces</span>]<span class="success-summary INFO"></span><span class="error-summary ERROR"></span></div>');

    $('.toggle-one').each(function() {
        $(this).pointer();
        $(this).click(function() {
            $(this).parent().find("+ol").toggleList();
        });
    });

    $('.toggle-all').each(function() {
        $(this).pointer();
        $(this).click(function() {
            $(this).parent().find('+ol').toggleList(true);
        });
    });

    $('.toggle-traces').each(function() {
        $(this).pointer();
        $(this).click(function() {
            var list = $(this).parent().find("+ol");
            if (list.isAopTraceVisible()) {
                list.hideAopTraces();
            } else {
                list.showAopTraces();
            }
        });
    });

    $('body > ol > li').children('ol').each(function() {
        $(this).collapseList(true);
    });

    $('ol').each(function() {
        var list = $(this);
        if (list.has('.expanders')) {
            var expander = list.prev();
            expander.find('.error-summary').text('Errors: ' + list.findErrors().length);
            expander.find('.success-summary').text('Success: ' + list.findSuccesses().length);
        }
    });
});