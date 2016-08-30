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
    
    CreateDocument,
    UpdateDocument,
    DeleteDocument,
    
    CreateJournal,
    UpdateJournal,
    DeleteJournal,
    
    CreateVariable,
    UpdateVariable,
    DeleteVariable,
    
    CreateUserItemData,
    UpdateUserItemData,
    DeleteUserItemData,
    
    CreateInventoryItem,
    UpdateInventoryItem,
    DeleteInventoryItem,
    
    CreateFaq,
    UpdateFaq,
    DeleteFaq,
    
    CreateRomanceFactor,
    UpdateRomanceFactor,
    DeleteRomanceFactor,
    
    CreateRomance,
    UpdateRomance,
    DeleteRomance,
    
    AcceptRomanceTraining,
    DiscardRomanceTraining,
    
    CreateSkillData,
    UpdateSkillData,
    DeleteSkillData,
    
    CreateImaginationItem,
    UpdateImaginationItem,
    DeleteImaginationItem,
    
    TrainImagination,
    
    CreateExpenseItem,
    UpdateExpenseItem,
    DeleteExpenseItem,
    
    CreateHumorPattern,
    UpdateHumorPattern,
    DeleteHumorPattern,
    
    CreateHumor,
    UpdateHumor,
    DeleteHumor,
    
    CreateHopper,
    UpdateHopper,
    DeleteHopper,
    
    TrainCreativity,
    
    TrainMemory,
    
    CreateDetail,
    UpdateDetail,
    DeleteDetail,
    
    CreateFestivalData,
    UpdateFestivalData,
    DeleteFestivalData,
    
    TrainImage,
    
    CreateFeed,
    UpdateFeed,
    DeleteFeed,
    
    TrainFeed,
    
    CreateGoalDef,
    UpdateGoalDef,
    DeleteGoalDef,
    
    ReportGoal,
    CompleteGoalDef,
    
    TrainArticleSpeed,
    
    TrainBeauty,
    
    TrainPorn,
    
    TrainFashion,
    
    CreateStorage,
    UpdateStorage,
    DeleteStorage,
    
    CreateStorageAsset,
    DeleteStorageAsset,
    
    SavePorn,
    
    IdeaToAccount,
    IdeaToContact,
    IdeaToDetail,
    IdeaToDocument,
    IdeaToExpenseItem,
    IdeaToHabit,
    IdeaToHumorPattern,
    IdeaToQuestDef,
    IdeaToQuote,
    IdeaToImaginationItem,
    IdeaToRomanceFactor,
    IdeaToRomance,
    IdeaToScenario,
    
    TrainHowTo,
    
    CreateFlashCardSet,
    UpdateFlashCardSet,
    DeleteFlashCardSet,
    
    CreateFlashCard,
    UpdateFlashCard,
    DeleteFlashCard,
    
    CreateQuizData,
    UpdateQuizData,
    DeleteQuizData,
    
    CreateWord,
    UpdateWord,
    DeleteWord,
    
    TrainWord,
    
    CreateChecklistDef,
    UpdateChecklistDef,
    DeleteChecklistDef,
    
    CreateChecklist,
    UpdateChecklist,
    DeleteChecklist,
    
    CreateBehaviorDef,
    UpdateBehaviorDef,
    DeleteBehaviorDef,
    
    CreateBehavior,
    UpdateBehavior,
    DeleteBehavior,
    
    CreateProxy,
    UpdateProxy,
    DeleteProxy,
    
    CreatePenalty,
    UpdatePenalty,
    DeletePenalty,
    
    CreateAlias,
    UpdateAlias,
    DeleteAlias,
    
    DoRhetoricQuiz,
    
    DoMetaModelQuiz,
    
    DoStrategyQuiz,
}
