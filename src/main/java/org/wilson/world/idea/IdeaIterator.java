package org.wilson.world.idea;

import java.util.List;

import org.wilson.world.manager.IdeaManager;
import org.wilson.world.model.Idea;

public class IdeaIterator {
    private static IdeaIterator instance;
    
    private boolean enabled;
    
    private int cursor;
    
    private IdeaIterator() {
        
    }
    
    public static IdeaIterator getInstance() {
        if(instance == null) {
            instance = new IdeaIterator();
        }
        return instance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }
    
    public void setIdeaId(int id) {
        List<Idea> ideas = IdeaManager.getInstance().getIdeas();
        if(ideas.isEmpty()) {
            return;
        }
        
        for(int i = 0; i < ideas.size(); i++) {
            Idea idea = ideas.get(i);
            if(idea.id == id) {
                this.setCursor(i);
                return;
            }
        }
    }
    
    public void reset() {
        this.setCursor(0);
    }

    public Idea next() {
        List<Idea> ideas = IdeaManager.getInstance().getIdeas();
        if(ideas.isEmpty()) {
            return null;
        }
        
        this.cursor += 1;
        if(this.cursor >= ideas.size()) {
            this.cursor = ideas.size() - 1;
        }
        
        return ideas.get(this.cursor);
    }
    
    public Idea previous() {
        List<Idea> ideas = IdeaManager.getInstance().getIdeas();
        if(ideas.isEmpty()) {
            return null;
        }
        
        this.cursor -= 1;
        if(this.cursor < 0) {
            this.cursor = 0;
        }
        
        return ideas.get(this.cursor);
    }
}
