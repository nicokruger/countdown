package co.za.countdown;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.sourceforge.htmlunit.corejs.javascript.Node;
import net.sourceforge.htmlunit.corejs.javascript.ast.ScriptNode;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nicok
 * Date: 12/8/11
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class CountdownHtml {
    
    public static String getHtml(List<CountdownInfo> countdowns) throws IOException {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);


        System.out.println(Thread.currentThread().getContextClassLoader().getResource("static/index.html"));
        HtmlPage page = webClient.getPage(Thread.currentThread().getContextClassLoader().getResource("static/index.html"));

        String js = "var m = model($(\"#countdownlist\"));\n";
        page.executeJavaScript(js);

        for (CountdownInfo countdownInfo : countdowns) {
            page.executeJavaScript("m.putCountdown(" + countdownInfo.toJson() +");");
        }

        return page.asXml();
    }
}
