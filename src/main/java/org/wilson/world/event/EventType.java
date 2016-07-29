package org.wilson.world.event;

public enum EventType {
    /* Make sure event type name is less than 25 characters */
    ConfigOverrideUploaded,
    ClearTable,
    Login,
    
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
    
    DeleteErrorInfo,
    ClearErrorInfo,
    
    CreateQuestDef,
    UpdateQuestDef,
    DeleteQuestDef,
    
    CreateQuest,
    UpdateQuest,
    DeleteQuest,
    
    CreateContactAttrDef,
    UpdateContactAttrDef,
    DeleteContactAttrDef,
    
    CreateContact,
    UpdateContact,
    DeleteContact,
    
    CreateScenario,
    UpdateScenario,
    DeleteScenario,
    
    TrainScenario,
    
    CreateQuery,
    UpdateQuery,
    DeleteQuery,
    
    CreateTaskFollower,
    UpdateTaskFollower,
    DeleteTaskFollower,
    
    CreateStatus,
    UpdateStatus,
    DeleteStatus,
    
    CreateAccount,
    UpdateAccount,
    DeleteAccount,
}
