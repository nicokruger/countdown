package co.za.countdown;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import java.util.List;

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
    private final List<String> tags;

    public CountdownInfo(String name, String url, long eventDate, List<String> tags) {
        this.name = name;
        this.url = url;
        this.eventDate = eventDate;
        this.tags = tags;
    }
    
    public String toJson() {
        String tagsJson = "[" + Joiner.on(".").join(Collections2.transform(tags, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return "\"" + input + "\"";
            }
        })) + "]";

        return "{ \"name\" : \"" + name + "\", \"url\" : \"" + url + "\", \"eventDate\" : " + eventDate + ", \"tags\" : " + tagsJson + " }";
    }
}
