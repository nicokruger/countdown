// Converts to DDD:HH:MM:SS representation
var convertTime = function (now_millis, then_millis) {
    var DAY = 1000*60*60*24;
    var HOUR = 1000*60*60;
    var MINUTE = 1000*60;
    var SECOND = 1000;
    
    var now = new Date(now_millis);
    var then = new Date(then_millis);
    
    var pad = function (num, pads) {
        var s = ''+num;
        return s.length == pads ? ''+num : _(_.range(pads - s.length)).map(function() { return "0" }).join('') + num;
    };
    
    var diff = then.getTime() - now.getTime();
    var days = Math.floor(diff / DAY);
    var hours = Math.floor( (diff - DAY*days) / HOUR );
    var minutes = Math.floor( (diff - DAY*days - hours*HOUR) / MINUTE);
    var seconds = Math.floor( (diff - DAY*days - hours*HOUR - minutes*MINUTE) / SECOND);
    
    return {
        days: pad(days, 3),
        hours: pad(hours, 2),
        minutes: pad(minutes, 2),
        seconds: pad(seconds, 2)
    }
}