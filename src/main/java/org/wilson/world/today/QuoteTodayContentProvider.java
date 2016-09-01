package org.wilson.world.today;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.WebManager;

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
            return qotd;
        }
    }

}
