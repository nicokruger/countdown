package co.za.countdown;



/**
 * Created by IntelliJ IDEA.
 * User: nicok
 * Date: 12/8/11
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class CountdownInfo {
    
    private final String name;
    private final String url;
    private final long eventDate;
    //private final List<String> tags;

    public CountdownInfo(String name, String url, long eventDate) {
        this.name = name;
        this.url = url;
        this.eventDate = eventDate;
        //this.tags = tags;
    }
    
    public String toJson() {
        return "{ \"name\" : \"" + name + "\", \"url\" : \"" + url + "\", \"eventDate\" : " + eventDate + " }";
    }
}
