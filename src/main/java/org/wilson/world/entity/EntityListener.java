package org.wilson.world.entity;

import java.util.List;

import org.wilson.world.model.Entity;

public interface EntityListener {

	public void created(Entity entity);
	
	public void removed(Entity entity);
	
	public void reloaded(List<Entity> entities);
}
