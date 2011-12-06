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
        
        putCountdowns: function (countdowns) {
            if (countdowns.length == 0) {
                $(countdownHolder).html('<h1>Nothing to see here...</h1>');
                return;
            }
            $.mobile.showPageLoadingMsg();
            var that = this;
            _(countdowns).each(function (countdown) {
                that._putCountdown(countdown);
            });
            $.mobile.hidePageLoadingMsg();
            _.isFunction(countdownHolder["listview"]) && countdownHolder.listview("refresh");
        },
        
        // adds a countdown, and refreshes the view
        putCountdown: function (c) {
            var o = this._putCountdown(c);
            _.isFunction(countdownHolder["listview"]) && countdownHolder.listview("refresh");
            return o;
        },
        
        //ads a countdown, does not refresh the view
        _putCountdown: function (c) {
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
            
            return $(outside);
        },
        
        pending: 0,
        
        getCountdown: function (countdownInfo) {
            
            $.mobile.showPageLoadingMsg();
            
            this.pending += 1;
            var that = this;
            $.ajax({
                url: "/countdown/" + countdownInfo.url,
                dataType: "json",
                success: function (o) {
                    that._putCountdown(o);
                    that.pending -= 1;
                    if (that.pending == 0) {
                        _.isFunction(countdownHolder["listview"]) && countdownHolder.listview("refresh");
                        $.mobile.hidePageLoadingMsg();
                    } 
                },
                error: function (o) {
                    that.pending -= 1;
                    if (that.pending == 0) {
                        _.isFunction(countdownHolder["listview"]) && countdownHolder.listview("refresh");
                        $(countdownHolder).show();
                        $.mobile.hidePageLoadingMsg();
                    }
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

 
