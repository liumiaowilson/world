package org.wilson.world.model;

public class Bookmark {
    public int id;
    
    public String name;
    
    public String group;
    
    public String content;
    
    public String url;

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
		Bookmark other = (Bookmark) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
