package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.ZodiacSign;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.zodiac_sign.QuizType;

public class ZodiacSignManager {
    public static final String NAME = "zodiac_sign";
    
    private static ZodiacSignManager instance;
    
    private DAO<ZodiacSign> dao = null;
    
    private static final int [] MAX_DAYS_IN_MONTH = new int [] { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    @SuppressWarnings("unchecked")
    private ZodiacSignManager() {
        this.dao = DAOManager.getInstance().getDAO(ZodiacSign.class);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ZodiacSign sign : getZodiacSigns()) {
                    boolean found = sign.name.contains(text) || sign.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = sign.id;
                        content.name = sign.name;
                        content.description = sign.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ZodiacSignManager getInstance() {
        if(instance == null) {
            instance = new ZodiacSignManager();
        }
        return instance;
    }
    
    public void createZodiacSign(ZodiacSign sign) {
    }
    
    public ZodiacSign getZodiacSign(int id) {
        ZodiacSign sign = this.dao.get(id);
        if(sign != null) {
            return sign;
        }
        else {
            return null;
        }
    }
    
    public List<ZodiacSign> getZodiacSigns() {
        List<ZodiacSign> result = new ArrayList<ZodiacSign>();
        for(ZodiacSign sign : this.dao.getAll()) {
            result.add(sign);
        }
        return result;
    }
    
    public void updateZodiacSign(ZodiacSign sign) {
    }
    
    public void deleteZodiacSign(int id) {
    }
    
    public List<QuizPair> getZodiacSignQuizPairs(QuizType type) {
        if(type == null) {
            type = QuizType.Date;
        }
        
        List<QuizPair> ret = new ArrayList<QuizPair>();
        List<ZodiacSign> signs = this.getZodiacSigns();
        for(int i = 0; i < signs.size(); i++) {
            ZodiacSign sign = signs.get(i);
            
            QuizPair pair = new QuizPair();
            pair.id = i + 1;
            pair.bottom = sign.name;
            if(QuizType.Date == type) {
                pair.top = this.randomDate(sign);
            }
            else if(QuizType.Strengths == type) {
                pair.top = sign.strengths;
            }
            else if(QuizType.Weaknesses == type) {
                pair.top = sign.weaknesses;
            }
            else if(QuizType.Likes == type) {
                pair.top = sign.likes;
            }
            else if(QuizType.Dislikes == type) {
                pair.top = sign.dislikes;
            }
            ret.add(pair);
        }
        
        return ret;
    }
    
    private int getMaxDays(int month) {
        return MAX_DAYS_IN_MONTH[month - 1];
    }
    
    public String randomDate(ZodiacSign sign) {
        if(sign == null) {
            return null;
        }
        
        int month, day;
        if(DiceManager.getInstance().dice(50)) {
            month = sign.fromMonth;
            int max_days = this.getMaxDays(sign.fromMonth);
            day = sign.fromDay + DiceManager.getInstance().random(max_days - sign.fromDay);
        }
        else {
            month = sign.toMonth;
            day = DiceManager.getInstance().random(sign.toDay);
        }
        
        return month + "/" + day;
    }
    
    public ZodiacSign getZodiacSignByDate(int month, int day) {
        for(ZodiacSign sign : this.getZodiacSigns()) {
            if(sign.fromMonth == month) {
                if(day >= sign.fromDay && day <= this.getMaxDays(sign.fromMonth)) {
                    return sign;
                }
            }
            else if(sign.toMonth == month) {
                if(day >= 1 && day <= sign.toDay) {
                    return sign;
                }
            }
        }
        
        return null;
    }
}
