package org.wilson.world.manga;

import org.apache.commons.lang.StringUtils;

public class MangaItem {
	public int id;
	
	public String name;
	
	public String getMangaName() {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return name.split("/")[0];
	}

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
		MangaItem other = (MangaItem) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
