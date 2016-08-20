package org.wilson.world.model;

public class Word {
    public int id;
    
    public int cardId;
    
    public int step;
    
    public long time;
    
    //for display only
    public String name;
    
    public String meaning;
    
    public String lastReviewed;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
