// create a DDD:HH:MM:SS countdown
// where - jquery object
var countdown = (function () {
    // helper function to pad numbers with leading "0"
    var pad = function (num, pads) {
        var s = ''+num;
        return s.length == pads ? ''+num : _(_.range(pads - s.length)).map(function() { return "0" }).join('') + num;
    };
    
    // holds all counters
    var counters = [];
    
    // the following code is the *single* function that gets called very second
    // and runs through the countdowns and updates them
    setInterval(function () {
        var now = (new Date()).getTime();
        _(counters).each(function (counter) {
            var c = convertTime(now, counter[0]);
            
            counter[1].update(pad(c.days, 3));
            counter[2].update(pad(c.hours, 2));
            counter[3].update(pad(c.minutes, 2));
            counter[4].update(pad(c.seconds, 2));
        });
    }, 1000);
    
    var num = 0; // another counter to identify countdowns
    
    return function (where, target) {
        num+=1;
        var id = "countdown" + num;
        where.append( '<div id="' + id + '"></div>');
        $("#" + id).append('<span id="days' + id + '"></span>');
        $("#" + id).append('<span class="seperator">:</span>');
        $("#" + id).append('<span id="hours' + id + '"></span>');
        $("#" + id).append('<span class="seperator">:</span>');
        $("#" + id).append('<span id="minutes'+ id + '"></span>');
        $("#" + id).append('<span class="seperator">:</span>');
        $("#" + id).append('<span id="seconds' + id + '"></span>');
        
        var counter = [
            target,
            timer($("#days"+id), 3, 32, 32), 
            timer($("#hours" + id), 2, 32, 32), 
            timer($("#minutes" + id), 2, 32, 32), 
            timer($("#seconds" + id), 2, 32, 32),

        ]
        
        counters.push(counter);
        
        counter[1].update("000");
        counter[2].update("00");
        counter[3].update("00");
        counter[4].update("00");
    }
})();
