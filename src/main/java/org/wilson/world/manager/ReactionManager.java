package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Reaction;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.reaction.ReactionIdeaConverter;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ReactionManager implements ItemTypeProvider {
    public static final String NAME = "reaction";
    
    private static ReactionManager instance;
    
    private DAO<Reaction> dao = null;
    
    @SuppressWarnings("unchecked")
    private ReactionManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Reaction.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Reaction reaction : getReactions()) {
                    boolean found = reaction.name.contains(text) || reaction.condition.contains(text) || reaction.result.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = reaction.id;
                        content.name = reaction.name;
                        content.description = reaction.condition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new ReactionIdeaConverter());
    }
    
    public static ReactionManager getInstance() {
        if(instance == null) {
            instance = new ReactionManager();
        }
        return instance;
    }
    
    public void createReaction(Reaction reaction) {
        ItemManager.getInstance().checkDuplicate(reaction);
        
        this.dao.create(reaction);
    }
    
    public Reaction getReaction(int id) {
        Reaction reaction = this.dao.get(id);
        if(reaction != null) {
            return reaction;
        }
        else {
            return null;
        }
    }
    
    public List<Reaction> getReactions() {
        List<Reaction> result = new ArrayList<Reaction>();
        for(Reaction reaction : this.dao.getAll()) {
            result.add(reaction);
        }
        return result;
    }
    
    public void updateReaction(Reaction reaction) {
        this.dao.update(reaction);
    }
    
    public void deleteReaction(int id) {
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
        return target instanceof Reaction;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Reaction reaction = (Reaction)target;
        return String.valueOf(reaction.id);
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
        
        Reaction reaction = (Reaction)target;
        return reaction.name;
    }
    
    public List<QuizPair> getReactionQuizPairs() {
        List<QuizPair> ret = new ArrayList<QuizPair>();

        List<Reaction> reactions = this.getReactions();
        for(int i = 0; i < reactions.size(); i++) {
            Reaction reaction = reactions.get(i);
            QuizPair pair = new QuizPair();
            pair.id = i + 1;
            pair.top = reaction.condition;
            pair.bottom = reaction.result;
            pair.url = "javascript:jumpTo('reaction_edit.jsp?id=" + reaction.id + "')";
            ret.add(pair);
        }
        
        return ret;
    }
}
