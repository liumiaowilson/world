package org.wilson.world.today;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.WebManager;

public class WordTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Word Of The Day";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        String wotd = (String)WebManager.getInstance().get("word_of_the_day");
        if(wotd == null) {
            return null;
        }
        else {
            return "<a href=\"http://www.merriam-webster.com/dictionary/" + wotd + "\">" + wotd + "</a>";
        }
    }

}
