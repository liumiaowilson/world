package org.wilson.world.manager;

import org.junit.Test;
import org.wilson.world.event.EventType;

public class EventManagerTest {

    @Test
    public void testEventTypes() {
        System.out.println(EventType.CreateIdea);
        
        System.out.println(EventType.valueOf("CreateIdea"));
        
        try {
            System.out.println(EventType.valueOf("xxx"));
        }
        catch(Exception e) {
        }
    }
}
