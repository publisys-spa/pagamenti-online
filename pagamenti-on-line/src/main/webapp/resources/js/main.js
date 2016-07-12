
var currentLang = 'it';

//
//function timedUpdate() {
//    moment.lang(currentLang);
//    $("#datetime").html(moment().format('dddd, D MMMM YYYY, HH:mm:ss'));
//    setTimeout(timedUpdate, 1000);
//}

function notify(msg, type, align) {
    if (align === undefined)
        align = "right";
    $.bootstrapGrowl(msg, {
        type: type,
        align: align,
        offset: {from: 'top', amount: 55},
        delay: 5000
    });
    /*$.bootstrapGrowl("another message, yay!", {
     ele: 'body', // which element to append to
     type: 'info', // (null, 'info', 'error', 'success')
     offset: {from: 'top', amount: 20}, // 'top', or 'bottom'
     align: 'right', // ('left', 'right', or 'center')
     width: 250, // (integer, or 'auto')
     delay: 4000,
     allow_dismiss: true,
     stackup_spacing: 10 // spacing between consecutively stacked growls.
     });*/
}

$(function () {
    Application.init();
});

var Application = function () {

    return {init: init};

    function init() {
        enableBackToTop();
        enableIconLoading();
        //timedUpdate();

        $.fn.datepicker.dates['it'] = {
            days: ["Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"],
            daysShort: ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"],
            daysMin: ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"],
            months: ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
            monthsShort: ["Gen", "Feb", "Mar", "Apr", "Mag", "Giu", "Lug", "Ago", "Set", "Ott", "Nov", "Dic"],
            today: "Oggi",
            clear: "Pulisci",
            format: "dd/mm/yyyy",
            titleFormat: "MM yyyy",
            weekStart: 0
        };

        $('.datepicker').datepicker({
            format: 'dd/mm/yyyy',
            language: 'it'
        });
        $('.ui-tooltip').tooltip();
        $('.ui-popover').popover();
    }

    function enableBackToTop() {
        var backToTop = $('<a>', {id: 'back-to-top', href: '#top'});
        var icon = $('<i>', {class: 'glyphicon glyphicon-chevron-up'});

        backToTop.appendTo('body');
        icon.appendTo(backToTop);

        backToTop.hide();

        $(window).scroll(function () {
            if ($(this).scrollTop() > 150) {
                backToTop.fadeIn();
            } else {
                backToTop.fadeOut();
            }
        });

        backToTop.click(function (e) {
            e.preventDefault();

            $('body, html').animate({
                scrollTop: 0
            }, 600);
        });
    }

    function enableIconLoading() {
        var $icon = $(".icon-refresh").find(".glyphicon-refresh");
        var animateClass = "icon-refresh-animate";

        $icon.removeClass(animateClass);
        $icon.hide();

        $("form").on("submit", function (e) {
            if (!endsWith(this.action, "#") && !endsWith(this.action, "#top")
                    && !endsWith(this.action, "/ok") && !endsWith(this.action, "/ko")
                    && !endsWith(this.action, "/print")) {
                $icon.addClass(animateClass);
                $icon.show();
            }
        });
        $("a").on("click", function (e) {
            if (!endsWith(this.href, "#") && !endsWith(this.href, "#top")
                    && !endsWith(this.action, "/ok") && !endsWith(this.action, "/ko")
                    && !endsWith(this.action, "/print")) {
                $icon.addClass(animateClass);
                $icon.show();
            }
        });
    }

    function timedUpdate() {
        moment.lang(currentLang);
        $("#datetime").html(moment().format('dddd, D MMMM YYYY, HH:mm:ss'));
        setTimeout(timedUpdate, 1000);
    }

}();