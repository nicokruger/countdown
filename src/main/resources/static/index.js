$(document).bind("mobileinit", function () {
    
    $.mobile.defaultPageTransition = "none";

    
    $(function () {
        var m = model($("#countdownlist"));
        var controller = controllers(m);
        var tf = timeFunctions(m);
        
        $("#newcountdownForm").bind("submit", controller.newCountdown);
        
        $("#random").click(controller.random);
        
        $("#clear").click(controller.clear);
        
        $("#fetchWeek").click(tf.nextWeek);
        $("#fetchTomorrow").click(tf.tomorrow);
        $("#fetchMonth").click(tf.nextMonth);
        $("#fetchWeekend").click(tf.nextWeekend);
        $("#fetchYear").click(tf.nextYear);
        $("#fetchAll").click(tf.all);
        
        $("#searchForm").bind("submit", controller.search);
        
        $("#searchcountdowns").click(function () {
            $("#searchbar").toggle();
        });
        
        $("#searchbox").keyup(function (e) {
            if (e.which == 13) {
                e.preventDefault();
                tf.search({"name": $("#searchbox").val()});
            } else if (e.keyCode == 27) {
                e.preventDefault();
                $("#searchbar").toggle();
            }
        });
        
        // "Start" the interface by requesting all 
        _.defer(function () {
            //tf.nextWeek();
            $("#fetchWeek").trigger("click");
        });
        
    });
    
});

