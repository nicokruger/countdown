$(document).bind("mobileinit", function () {
    $(function () {
    
        $("#newcountdownForm").bind("submit", function (e) {
            e.preventDefault();
             
            $.mobile.showPageLoadingMsg();
            
            var ed;
            try {
                ed = Date.parse($("#countdownDatetime").val());
            } catch (err) {
                alert("cannot parse date: " + err);
                return;
            }
            
            var data = {
                name: $("#countdownName").val(),
                tags: $("#countdownTags").val(),
                eventDate:  ed
            };
            
            $.ajax({
                url: "/countdown/new", 
                data: data,
                type: "POST",
                success: function (o) {
                        $.mobile.hidePageLoadingMsg();
                        
                        $.mobile.changePage($("#mainview"), "none");
                    
                        var n = model.putCountdown(o, true);
                    
                        // the following could also be "position"
                        _.defer(function () { $.mobile.silentScroll($(n).offset().top); } );
                        
                    }, 
                error: function (e) {
                    $.mobile.hidePageLoadingMsg();
                    alert("an error occurred");
                }
            });
        });
    });
});


