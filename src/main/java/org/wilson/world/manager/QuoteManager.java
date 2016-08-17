package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Quote;
import org.wilson.world.quote.QuoteIdeaConverter;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class QuoteManager implements ItemTypeProvider {
    public static final String NAME = "quote";
    
    private Random r = null;
    
    private static QuoteManager instance;
    
    private DAO<Quote> dao = null;
    
    @SuppressWarnings("unchecked")
    private QuoteManager() {
        this.r = new Random();
        this.dao = DAOManager.getInstance().getCachedDAO(Quote.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new QuoteIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Quote quote : getQuotes()) {
                    boolean found = quote.name.contains(text) || quote.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = quote.id;
                        content.name = quote.name;
                        content.description = quote.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static QuoteManager getInstance() {
        if(instance == null) {
            instance = new QuoteManager();
        }
        return instance;
    }
    
    public void createQuote(Quote quote) {
        ItemManager.getInstance().checkDuplicate(quote);
        
        this.dao.create(quote);
    }
    
    public Quote getQuote(int id) {
        Quote quote = this.dao.get(id);
        if(quote != null) {
            return quote;
        }
        else {
            return null;
        }
    }
    
    public List<Quote> getQuotes() {
        List<Quote> result = new ArrayList<Quote>();
        for(Quote quote : this.dao.getAll()) {
            result.add(quote);
        }
        return result;
    }
    
    public void updateQuote(Quote quote) {
        this.dao.update(quote);
    }
    
    public void deleteQuote(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Quote;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Quote quote = (Quote)target;
        return String.valueOf(quote.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public Quote randomQuote() {
        List<Quote> all = this.dao.getAll();
        if(all.isEmpty()) {
            return null;
        }
        int idx = this.r.nextInt(all.size());
        return all.get(idx);
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Quote quote = (Quote)target;
        return quote.name;
    }
}
