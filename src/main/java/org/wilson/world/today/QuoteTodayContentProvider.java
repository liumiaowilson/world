package org.wilson.world.today;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.QuoteManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.Quote;

public class QuoteTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Quote Of The Day";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        String qotd = (String)WebManager.getInstance().get("quote_of_the_day");
        if(qotd == null) {
            return null;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("<div id=\"qotd_content\">");
            sb.append(qotd);
            sb.append("</div><hr/>");
            Quote quote = QuoteManager.getInstance().getQuoteByContent(qotd);
            if(quote == null) {
                sb.append("<a href=\"javascript:saveQuote()\">Save</a>");
            }
            else {
                sb.append("Saved");
            }
            return  sb.toString() ;
        }
    }

}
