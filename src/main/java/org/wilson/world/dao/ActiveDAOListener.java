package org.wilson.world.dao;

public interface ActiveDAOListener {
	@SuppressWarnings("rawtypes")
	public void created(ActiveDAO dao);
	
	@SuppressWarnings("rawtypes")
	public void removed(ActiveDAO dao);
}
