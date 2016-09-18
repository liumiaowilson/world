package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Fraud;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class FraudManager implements ItemTypeProvider {
    public static final String NAME = "fraud";
    
    private static FraudManager instance;
    
    private DAO<Fraud> dao = null;
    
    @SuppressWarnings("unchecked")
    private FraudManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Fraud.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Fraud fraud : getFrauds()) {
                    boolean found = fraud.name.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = fraud.id;
                        content.name = fraud.name;
                        content.description = fraud.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FraudManager getInstance() {
        if(instance == null) {
            instance = new FraudManager();
        }
        return instance;
    }
    
    public void createFraud(Fraud fraud) {
        ItemManager.getInstance().checkDuplicate(fraud);
        
        this.dao.create(fraud);
    }
    
    public Fraud getFraud(int id, boolean lazy) {
        Fraud fraud = this.dao.get(id, lazy);
        if(fraud != null) {
            return fraud;
        }
        else {
            return null;
        }
    }
    
    public Fraud getFraud(int id) {
        return this.getFraud(id, true);
    }
    
    public Fraud getFraud(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(Fraud fraud : this.getFrauds()) {
            if(name.equals(fraud.name)) {
                return fraud;
            }
        }
        
        return null;
    }
    
    public List<Fraud> getFrauds() {
        List<Fraud> result = new ArrayList<Fraud>();
        for(Fraud fraud : this.dao.getAll()) {
            result.add(fraud);
        }
        return result;
    }
    
    public void updateFraud(Fraud fraud) {
        this.dao.update(fraud);
    }
    
    public void deleteFraud(int id) {
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
        return target instanceof Fraud;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Fraud fraud = (Fraud)target;
        return String.valueOf(fraud.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Fraud fraud = (Fraud)target;
        return fraud.name;
    }
    
    public Fraud randomFraud() {
        List<Fraud> frauds = this.getFrauds();
        if(frauds.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(frauds.size());
        return frauds.get(n);
    }
}
