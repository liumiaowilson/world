package org.wilson.world.idea;

import org.wilson.world.model.Idea;
import org.wilson.world.star.StarProvider;

public class IdeaStarProvider implements StarProvider {

    @Override
    public boolean accept(Object target) {
        return target instanceof Idea;
    }

    @Override
    public void star(Object target) {
        Idea idea = (Idea) target;
        idea.starred = true;
    }

    @Override
    public boolean isStarred(Object target) {
        Idea idea = (Idea) target;
        return idea.starred;
    }

    @Override
    public void unstar(Object target) {
        Idea idea = (Idea) target;
        idea.starred = false;
    }

    @Override
    public String getDisplayName(Object target) {
        Idea idea = (Idea) target;
        return idea.name;
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        Idea i1 = (Idea)o1;
        Idea i2 = (Idea)o2;
        return i1.id == i2.id;
    }

}
