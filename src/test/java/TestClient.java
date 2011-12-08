import co.za.countdown.CountdownHtml;
import co.za.countdown.CountdownInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nicok
 * Date: 12/8/11
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {

    @Test
    public void client() throws IOException {
        List<CountdownInfo> countdowns = new ArrayList<CountdownInfo> ();
        countdowns.add(new CountdownInfo("Testing Testing", "URL", 0));

        String html = CountdownHtml.getHtml(countdowns);
        Assert.assertTrue("page should contain countdown", html.contains("Testing Testing"));
        
        System.out.println(html);
    }

    public void bla() {
/*        OutputFormat outputFormat = new OutputFormat(page);
        outputFormat.setIndent(4);
        outputFormat.setLineWidth(65);
        outputFormat.setIndenting(true);

        Writer out = new StringWriter();
        XMLSerializer xmlSerializer;
        xmlSerializer = new XMLSerializer(out, outputFormat);
        xmlSerializer.serialize(page);

        System.out.println(page.asXml());
  */
    }
}
