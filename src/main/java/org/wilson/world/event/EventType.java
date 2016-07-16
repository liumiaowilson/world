package org.wilson.world.event;

public enum EventType {
    /* Make sure event type name is less than 25 characters */
    ConfigOverrideUploaded,
    ClearTable,
    
    BatchCreateIdea,
    CreateIdea,
    UpdateIdea,
    DeleteIdea,
    SplitIdea,
    MergeIdea,
    
    CreateAction,
    UpdateAction,
    DeleteAction,
    
    GainExperience,
    
    StarComplete,
    
    CreateTask,
    UpdateTask,
    DeleteTask,
    SplitTask,
    MergeTask,
}
