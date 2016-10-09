package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Interview;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class InterviewManager implements ItemTypeProvider {
    public static final String NAME = "interview";
    
    private static InterviewManager instance;
    
    private DAO<Interview> dao = null;
    
    @SuppressWarnings("unchecked")
    private InterviewManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Interview.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Interview interview : getInterviews()) {
                    boolean found = interview.name.contains(text) || interview.question.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = interview.id;
                        content.name = interview.name;
                        content.description = interview.question;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static InterviewManager getInstance() {
        if(instance == null) {
            instance = new InterviewManager();
        }
        return instance;
    }
    
    public void createInterview(Interview interview) {
        ItemManager.getInstance().checkDuplicate(interview);
        
        this.dao.create(interview);
    }
    
    public Interview getInterview(int id) {
        Interview interview = this.dao.get(id);
        if(interview != null) {
            return interview;
        }
        else {
            return null;
        }
    }
    
    public List<Interview> getInterviews() {
        List<Interview> result = new ArrayList<Interview>();
        for(Interview interview : this.dao.getAll()) {
            result.add(interview);
        }
        return result;
    }
    
    public void updateInterview(Interview interview) {
        this.dao.update(interview);
    }
    
    public void deleteInterview(int id) {
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
        return target instanceof Interview;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Interview interview = (Interview)target;
        return String.valueOf(interview.id);
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
        
        Interview interview = (Interview)target;
        return interview.name;
    }
}
