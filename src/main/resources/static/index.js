$(document).bind("mobileinit", function () {
    
    $.mobile.defaultPageTransition = "none";

    $(function () {
        retrieveAll();
    });
    
});

var retrieveAll = function () {
    // Retrieve inital list of countdowns
    
    $.mobile.showPageLoadingMsg();

    $.ajax({
        url: "/countdownlist",
        dataType: "json",
        success: function (o) {
            
            $.mobile.hidePageLoadingMsg();
            
            model.clear();
            // iterate through countdowns
           _(o.countdowns).each(function (countdownInfo) {
               model.getCountdown(countdownInfo);
            });           
            
        },
        error: function (o) {
            alert("error retrieving data");
        }
    });
};

var ledColors = {
    lit: "rgba(0, 112, 60, 1.0)",
    unlit: "rgba(0, 0, 0, 0.0)",
    outline: "rgba(0, 0, 0, 0.0)"
}

var findByDate = function (countdownInfo) {
    if (this.countdowns.length == 0) {
        return undefined;
    }
    
    var sortFunc = function (x) { return x.eventDate };
    var sorted = _(this.countdowns).sortBy(sortFunc);
    var i = _(sorted).sortedIndex(countdownInfo, sortFunc);
    return i < sorted.length ? sorted[i].url : undefined;
}

var model = {
    
    countdowns: [],
}

model.find = _.bind(findByDate, model);

model.putCountdown = function (c) {
    
    var where = this.find(c);
    var outside;
    
    if (where === undefined) {
        outside = $('<li></li>').appendTo("#countdownlist");
    } else {
        outside = $('<li></li>').insertBefore($("#" + where).parent());
    }
    $(outside).append("<h5>" + c.name + "</h5>");
    $(outside).append("<div id=\"" + c.url + "\"></div>");
    
    countdown($("#" + c.url), c.eventDate, 24, 32, ledColors);
    
    $("#countdownlist").listview("refresh");
    
    this.countdowns.push(c);
    
    return $(outside);
}

model.getCountdown = function (countdownInfo) {
    $.ajax({
        url: "/countdown/" + countdownInfo.url,
        dataType: "json",
        success: _.bind(this.putCountdown, model),
        error: function (o) {
            // TODO: handle the error
        }
    });
}

model.clear = function () {
    this.countdowns = [];
    $("#countdownlist").html(""); // clear
 }
 
