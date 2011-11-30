$(function () {
    // Retrieve inital list of countdowns
    $.ajax({
        url: "http://localhost:55555/countdownlist",
        dataType: "json",
        success: function (o) {
            $("#countdownlist").html(""); // clear
            // iterate through countdowns
           _(o.countdowns).each(function (countdownInfo) {
                $("#countdownlist").append("<h3>" + countdownInfo.label + "</h3>");
                $("#countdownlist").append("<div  id=\"" + countdownInfo.url + "\">Retriving data....</div>");
                
                $.ajax({
                    url: "http://localhost:55555/countdown/" + countdownInfo.url,
                    dataType: "json",
                    success: function (c) {
                        $("#" + countdownInfo.url).html(""); // clear
                        countdown($("#" + countdownInfo.url), c.eventDate);
                    },
                    error: function (o) {
                        $("#" + countdownInfo.url).html("Error retrieving countdown timer");
                    }
                });
            });           
        },
        error: function (o) {
            alert("error retrieving data");
        }
    });
});
