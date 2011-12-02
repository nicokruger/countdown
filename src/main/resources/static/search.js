$(document).bind("mobileinit", function () {
    $(function () {
        $("#searchForm").bind("submit", function (e) {
            e.preventDefault();
             
            $.mobile.showPageLoadingMsg();
            
            var start;

            var data = {};
            try {
                $("#searchName").val() !== "" ? data.name = $("#searchName").val() : data=data;
                $("#searchTags").val() !== "" ? data.tags = $("#searchTags").val() : data=data;
                $("#searchRangeStart").val() !== "" ? data.start = Date.parse($("#searchRangeStart").val()) : data=data;
                $("#searchRangeEnd").val() !== "" ? data.end = Date.parse($("#searchRangeEnd").val()) : data=data;
            } catch (err) {
                alert("cannot parse date: " + err);
                return;
            }
            
            $.ajax({
                url: "/countdown/search", 
                data: data,
                type: "POST",
                success: function (o) {
                        $.mobile.hidePageLoadingMsg();
                        
                        model.clear();
                        
                       _(o.countdowns).each(function (countdownInfo) {
                           model.getCountdown(countdownInfo);
                        });           
                        
                        $.mobile.changePage($("#mainview"), "none");
                    
                    
                    }, 
                error: function (e) {
                    $.mobile.hidePageLoadingMsg();
                    alert("an error occurred");
                }
            });
        });
    });
});


