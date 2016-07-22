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
    IdeaToTask,
    FinishTask,
    AbandonTask,
    
    CreateTaskAttrDef,
    UpdateTaskAttrDef,
    DeleteTaskAttrDef,
    
    CreateTaskAttrRule,
    UpdateTaskAttrRule,
    DeleteTaskAttrRule,
    
    PostProcessIdea,
    
    CreateContext,
    UpdateContext,
    DeleteContext,
    
    CreateHabit,
    UpdateHabit,
    DeleteHabit,
    
    CheckHabit,
    
    CreateTaskSeed,
    UpdateTaskSeed,
    DeleteTaskSeed,
    
    CreateQuote,
    UpdateQuote,
    DeleteQuote,
    
    SortTaskAttrRule,
}
