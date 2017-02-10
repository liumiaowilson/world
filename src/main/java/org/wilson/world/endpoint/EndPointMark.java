package org.wilson.world.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface EndPointMark {
	public String name();
	
	public EndPointMethodType type();
	
	public EndPointMethodScope scope();
}
