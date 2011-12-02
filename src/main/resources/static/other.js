$(document).bind("mobileinit", function () {
    $(function () {
        
        $("#random").click(function () {
            
            $.mobile.showPageLoadingMsg();
            
            $.ajax({
                url: "/countdown/random", 
                type: "GET",
                success: function (o) {
                        $.mobile.hidePageLoadingMsg();
                        model.clear();
                        model.putCountdown(o);
                        $.mobile.changePage($("#mainview"), "none");
                    }, 
                error: function (e) {
                    $.mobile.hidePageLoadingMsg();
                    alert("an error occurred");
                }
            });
        });
        
        $("#all").click(function () {
            retrieveAll();
        });
        
        $("#clear").click(function () {
            model.clear();
        });
    });
});
