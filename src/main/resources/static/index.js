$(document).bind("mobileinit", function () {
    
    $.mobile.defaultPageTransition = "none";

    
    $(function () {
        var controller = controllers(model($("#countdownlist")));
        
        $("#newcountdownForm").bind("submit", controller.newCountdown);
        
        $("#random").click(controller.random);
        
        $("#all").click(controller.all);
        
        $("#clear").click(controller.clear);
        
        $("#searchForm").bind("submit", controller.search);
        
        // "Start" the interface by requesting all 
        _.defer(controller.all);
        
    });
    
});

