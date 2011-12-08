$(document).bind("mobileinit", function () {
    
    $.mobile.defaultPageTransition = "none";
})    
    
$(function () {
    var m = model($("#countdownlist"));
    var action = actions(m);
    
    $("#newcountdownForm").bind("submit", action.newCountdown);
    
    $("#random").click(action.random);
    
    $("#clear").click(action.clear);
    
    $("#fetchWeek").click(action.nextWeek);
    $("#fetchTomorrow").click(action.tomorrow);
    $("#fetchMonth").click(action.nextMonth);
    $("#fetchWeekend").click(action.nextWeekend);
    $("#fetchYear").click(action.nextYear);
    $("#fetchAll").click(action.all);
    
    $("#searchForm").bind("submit", action.search);
    
    $("#searchcountdowns").click(function () {
        $("#searchbar").toggle();
    });
    
    $("#searchbox").keyup(function (e) {
        if (e.which == 13) {
            e.preventDefault();
            action.search(parseSearchData($("#searchbox").val()));
        } else if (e.keyCode == 27) {
            e.preventDefault();
            $("#searchbar").toggle();
        }
    });
    
    // "Start" the interface by requesting all 
    /*_.defer(function () {
        //tf.nextWeek();
        $("#fetchWeek").trigger("click");
    });*/
    
});
    

    
