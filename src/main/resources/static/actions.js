var actions = function (model) {
    
    var countdownAction = function (url, data, method, success) {
        $.ajax({
            url: url, 
            data: data,
            type: method,
            success: function (o) {
                model.clear();
                o.hasOwnProperty("countdowns") ? model.putCountdowns(o.countdowns) : model.putCountdown(o);
                
                success !== undefined && success(o);
            }, 
            error: function (e) {
                alert("an error occurred");
                
                $("#info").html("An error occurred... Please try again");
            }
        });
    }
    
    var timeSearch = function (endTime) {
        var start = Date.parse("today").getTime();
        var data = {
            start: start,
            end: endTime
        };
        countdownAction("/countdown/search", data, "POST", function () {
            var format = "yyyy-MM-dd HH:mm";
            $("#info").html("<h4>From " + (new Date(start)).toString(format) + " to " + (new Date(endTime)).toString(format));
        });
    }        
    
    return {
        newCountdown: function (e) {
            e.preventDefault();
            var ed;
            try {
                ed = Date.parseExact($("#countdownDatetime").val(), "yyyy-MM-ddTHH:mm");
            } catch (err) {
                alert("cannot parse date: " + err);
                return;
            }
            
            if (ed == null ) {
                alert("invalid date");
                return;
            }
            var data = {
                name: $("#countdownName").val(),
                tags: $("#countdownTags").val(),
                eventDate:  ed.getTime()
            };

            countdownAction("/countdown/new", data, "POST", function () {
            });
        },
        
        random: function (e) {
            countdownAction("/countdown/random", {}, "GET");
        },
        clear: function (e) {
            model.clear();
        },
    
        nextWeek: function (e) {
            e !== undefined && e.preventDefault();
            timeSearch(Date.today().add(7).days().getTime());
        },
        nextMonth: function (e) {
            e !== undefined && e.preventDefault();
            timeSearch(Date.today().add(1).month().getTime());
        },
        tomorrow: function (e) {
            e !== undefined && e.preventDefault();
            timeSearch(Date.today().add(1).day().getTime());
        },
        nextWeekend: function (e) {
            e !== undefined && e.preventDefault();
            timeSearch(Date.parse("next sunday").getTime());
        },
        nextYear: function (e) {
            e !== undefined && e.preventDefault();
            timeSearch(Date.today().add(1).year().getTime());
        },
        
        all: function(e) {
            e !== undefined && e.preventDefault();
            
            countdownAction("/countdownlist", {}, "GET");
        },
        
        search: function(data) {
            countdownAction("/countdown/search", data, "POST");
        }

                
    }
}

var parseSearchData = function (text) {
    var i =  _(text.split(' ')).chain().reduce(function (o, ww) {
        if (ww[0] == "#") {
            o.tags.push(ww.slice(1, o.length));
        } else {
            o.names.push(ww);
        }
        return o;
    }, {names: [], tags: []}).value();
    
    var sd = {};
    
    if (i.tags.length > 0) {
        sd["tags"] = i.tags.join(",");        
    }
    if (i.names.length > 0) {
        sd["name"] = i.names.join(" ");
    }
    return (i.names.length > 0 || i.tags.length > 0) ? sd : { name: "" };
}

