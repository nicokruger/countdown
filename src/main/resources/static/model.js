var ledColors = {
    lit: "rgba(0, 0, 0, 1.0)",
    unlit: "rgba(0, 0, 0, 0.0)",
    outline: "rgba(0, 0, 0, 0.0)"
}

var model = function (countdownHolder) {
    
    return {
        
        countdowns: [],
        
        find: function (countdownInfo) {
            if (this.countdowns.length == 0) {
                return undefined;
            }
            
            var sortFunc = function (x) { return x.eventDate; };
            var i = _(this.countdowns).sortedIndex(countdownInfo, sortFunc);
            
            return i < this.countdowns.length ? i : undefined;
        },
        
        putCountdown: function (c) {
            
            var where = this.find(c);
            
            var outside;
            
            if (where === undefined) {
                outside = $('<li></li>').appendTo(countdownHolder);
                this.countdowns.push(c);
            } else {
                outside = $('<li></li>').insertBefore($(countdownHolder).find("#" + this.countdowns[where].url).parent());
                this.countdowns.splice(where, 0, c);
            }
            $(outside).append("<h5>" + c.name + "</h5>");
            $(outside).append("<div class=\"countdown\" id=\"" + c.url + "\"></div>");
            
            countdown($(outside).find("#" + c.url), c.eventDate, 24, 32, ledColors);
            
            _.isFunction(countdownHolder["listview"]) && countdownHolder.listview("refresh");

            return $(outside);
        },
        
        getCountdown: function (countdownInfo) {
            $.ajax({
                url: "/countdown/" + countdownInfo.url,
                dataType: "json",
                success: _.bind(this.putCountdown, this),
                error: function (o) {
                    // TODO: handle the error
                }
            });
        },
        
        clear: function () {
            this.countdowns = [];
            countdownHolder.html(""); // clear
        }

    }

}

 
