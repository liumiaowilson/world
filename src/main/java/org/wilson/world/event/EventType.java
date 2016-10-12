package org.wilson.world.event;

public enum EventType {
    /* Make sure event type name is less than 25 characters */
    ConfigOverrideUploaded("javascript:jumpTo('config.jsp')"),
    ClearTable("javascript:jumpTo('database.jsp')"),
    Login("javascript:jumpTo('../signin.jsp')"),
    
    BatchCreateIdea("javascript:jumpTo('idea_new_batch.jsp')"),
    CreateIdea("javascript:jumpTo('idea_new.jsp')"),
    UpdateIdea("javascript:jumpTo('idea_list.jsp')"),
    DeleteIdea("javascript:jumpTo('idea_list.jsp')"),
    SplitIdea("javascript:jumpTo('idea_list.jsp')"),
    MergeIdea("javascript:jumpTo('idea_list.jsp')"),
    
    CreateAction("javascript:jumpTo('action_new.jsp')"),
    UpdateAction("javascript:jumpTo('action_list.jsp')"),
    DeleteAction("javascript:jumpTo('action_list.jsp')"),
    
    GainExperience(null),
    
    StarComplete(null),
    
    CreateTask("javascript:jumpTo('task_new.jsp')"),
    UpdateTask("javascript:jumpTo('task_list.jsp')"),
    DeleteTask("javascript:jumpTo('task_list.jsp')"),
    SplitTask("javascript:jumpTo('task_list.jsp')"),
    MergeTask("javascript:jumpTo('task_list.jsp')"),
    IdeaToTask("javascript:jumpTo('idea_list.jsp')"),
    FinishTask("javascript:jumpTo('task_list.jsp')"),
    AbandonTask("javascript:jumpTo('task_list.jsp')"),
    
    CreateTaskAttrDef("javascript:jumpTo('task_attr_def_new.jsp')"),
    UpdateTaskAttrDef("javascript:jumpTo('task_attr_def_list.jsp')"),
    DeleteTaskAttrDef("javascript:jumpTo('task_attr_def_list.jsp')"),
    
    CreateTaskAttrRule("javascript:jumpTo('task_attr_rule_new.jsp')"),
    UpdateTaskAttrRule("javascript:jumpTo('task_attr_rule_list.jsp')"),
    DeleteTaskAttrRule("javascript:jumpTo('task_attr_rule_list.jsp')"),
    
    PostProcessIdea("javascript:jumpTo('post_process.jsp')"),
    
    CreateContext("javascript:jumpTo('context_new.jsp')"),
    UpdateContext("javascript:jumpTo('context_list.jsp')"),
    DeleteContext("javascript:jumpTo('context_list.jsp')"),
    
    CreateHabit("javascript:jumpTo('habit_new.jsp')"),
    UpdateHabit("javascript:jumpTo('habit_list.jsp')"),
    DeleteHabit("javascript:jumpTo('habit_list.jsp')"),
    
    CheckHabit("javascript:jumpTo('habit_trace_check.jsp')"),
    
    CreateTaskSeed("javascript:jumpTo('task_seed_new.jsp')"),
    UpdateTaskSeed("javascript:jumpTo('task_seed_list.jsp')"),
    DeleteTaskSeed("javascript:jumpTo('task_seed_list.jsp')"),
    
    CreateQuote("javascript:jumpTo('quote_new.jsp')"),
    UpdateQuote("javascript:jumpTo('quote_list.jsp')"),
    DeleteQuote("javascript:jumpTo('quote_list.jsp')"),
    
    SortTaskAttrRule("javascript:jumpTo('task_attr_rule_sort.jsp')"),
    
    DeleteErrorInfo("javascript:jumpTo('error_info_list.jsp')"),
    ClearErrorInfo("javascript:jumpTo('error_info_list.jsp')"),
    
    CreateQuestDef("javascript:jumpTo('quest_def_new.jsp')"),
    UpdateQuestDef("javascript:jumpTo('quest_def_list.jsp')"),
    DeleteQuestDef("javascript:jumpTo('quest_def_list.jsp')"),
    
    CreateQuest("javascript:jumpTo('quest_new.jsp')"),
    UpdateQuest("javascript:jumpTo('quest_list.jsp')"),
    DeleteQuest("javascript:jumpTo('quest_list.jsp')"),
    
    CreateContactAttrDef("javascript:jumpTo('contact_attr_def_new.jsp')"),
    UpdateContactAttrDef("javascript:jumpTo('contact_attr_def_list.jsp')"),
    DeleteContactAttrDef("javascript:jumpTo('contact_attr_def_list.jsp')"),
    
    CreateContact("javascript:jumpTo('contact_new.jsp')"),
    UpdateContact("javascript:jumpTo('contact_list.jsp')"),
    DeleteContact("javascript:jumpTo('contact_list.jsp')"),
    
    CreateScenario("javascript:jumpTo('scenario_new.jsp')"),
    UpdateScenario("javascript:jumpTo('scenario_list.jsp')"),
    DeleteScenario("javascript:jumpTo('scenario_list.jsp')"),
    
    TrainScenario("javascript:jumpTo('scenario_read.jsp')"),
    
    CreateQuery("javascript:jumpTo('query_new.jsp')"),
    UpdateQuery("javascript:jumpTo('query_list.jsp')"),
    DeleteQuery("javascript:jumpTo('query_list.jsp')"),
    
    CreateTaskFollower("javascript:jumpTo('task_follower_new.jsp')"),
    UpdateTaskFollower("javascript:jumpTo('task_follower_list.jsp')"),
    DeleteTaskFollower("javascript:jumpTo('task_follower_list.jsp')"),
    
    CreateStatus("javascript:jumpTo('status_new.jsp')"),
    UpdateStatus("javascript:jumpTo('status_list.jsp')"),
    DeleteStatus("javascript:jumpTo('status_list.jsp')"),
    
    CreateAccount("javascript:jumpTo('account_new.jsp')"),
    UpdateAccount("javascript:jumpTo('account_list.jsp')"),
    DeleteAccount("javascript:jumpTo('account_list.jsp')"),
    
    CreateDocument("javascript:jumpTo('document_new.jsp')"),
    UpdateDocument("javascript:jumpTo('document_list.jsp')"),
    DeleteDocument("javascript:jumpTo('document_list.jsp')"),
    
    CreateJournal("javascript:jumpTo('journal_new.jsp')"),
    UpdateJournal("javascript:jumpTo('journal_list.jsp')"),
    DeleteJournal("javascript:jumpTo('journal_list.jsp')"),
    
    CreateVariable("javascript:jumpTo('variable_new.jsp')"),
    UpdateVariable("javascript:jumpTo('variable_list.jsp')"),
    DeleteVariable("javascript:jumpTo('variable_list.jsp')"),
    
    CreateUserItemData("javascript:jumpTo('user_item_data_new.jsp')"),
    UpdateUserItemData("javascript:jumpTo('user_item_data_list.jsp')"),
    DeleteUserItemData("javascript:jumpTo('user_item_data_list.jsp')"),
    
    CreateInventoryItem("javascript:jumpTo('inventory_item_new.jsp')"),
    UpdateInventoryItem("javascript:jumpTo('inventory_item_list.jsp')"),
    DeleteInventoryItem("javascript:jumpTo('inventory_item_list.jsp')"),
    
    CreateFaq("javascript:jumpTo('faq_new.jsp')"),
    UpdateFaq("javascript:jumpTo('faq_list.jsp')"),
    DeleteFaq("javascript:jumpTo('faq_list.jsp')"),
    
    CreateRomanceFactor("javascript:jumpTo('romance_factor_new.jsp')"),
    UpdateRomanceFactor("javascript:jumpTo('romance_factor_list.jsp')"),
    DeleteRomanceFactor("javascript:jumpTo('romance_factor_list.jsp')"),
    
    CreateRomance("javascript:jumpTo('romance_new.jsp')"),
    UpdateRomance("javascript:jumpTo('romance_list.jsp')"),
    DeleteRomance("javascript:jumpTo('romance_list.jsp')"),
    
    AcceptRomanceTraining("javascript:jumpTo('romance_train.jsp')"),
    DiscardRomanceTraining("javascript:jumpTo('romance_train.jsp')"),
    TrainRomance("javascript:jumpTo('romance_train.jsp')"),
    
    CreateSkillData("javascript:jumpTo('skill_data_new.jsp')"),
    UpdateSkillData("javascript:jumpTo('skill_data_list.jsp')"),
    DeleteSkillData("javascript:jumpTo('skill_data_list.jsp')"),
    
    CreateImaginationItem("javascript:jumpTo('imagination_item_new.jsp')"),
    UpdateImaginationItem("javascript:jumpTo('imagination_item_list.jsp')"),
    DeleteImaginationItem("javascript:jumpTo('imagination_item_list.jsp')"),
    
    TrainImagination("javascript:jumpTo('imagination_item_train.jsp')"),
    
    CreateExpenseItem("javascript:jumpTo('expense_item_new.jsp')"),
    UpdateExpenseItem("javascript:jumpTo('expense_item_list.jsp')"),
    DeleteExpenseItem("javascript:jumpTo('expense_item_list.jsp')"),
    
    CreateHumorPattern("javascript:jumpTo('humor_pattern_new.jsp')"),
    UpdateHumorPattern("javascript:jumpTo('humor_pattern_list.jsp')"),
    DeleteHumorPattern("javascript:jumpTo('humor_patter_list.jsp')"),
    
    CreateHumor("javascript:jumpTo('humor_new.jsp')"),
    UpdateHumor("javascript:jumpTo('humor_list.jsp')"),
    DeleteHumor("javascript:jumpTo('humor_list.jsp')"),
    
    CreateHopper("javascript:jumpTo('hopper_new.jsp')"),
    UpdateHopper("javascript:jumpTo('hopper_list.jsp')"),
    DeleteHopper("javascript:jumpTo('hopper_list.jsp')"),
    
    TrainCreativity("javascript:jumpTo('creativity_train.jsp')"),
    
    TrainMemory("javascript:jumpTo('memory_train.jsp')"),
    
    CreateDetail("javascript:jumpTo('detail_new.jsp')"),
    UpdateDetail("javascript:jumpTo('detail_list.jsp')"),
    DeleteDetail("javascript:jumpTo('detail_list.jsp')"),
    
    CreateFestivalData("javascript:jumpTo('festival_data_new.jsp')"),
    UpdateFestivalData("javascript:jumpTo('festival_data_list.jsp')"),
    DeleteFestivalData("javascript:jumpTo('festival_data_list.jsp')"),
    
    TrainImage("javascript:jumpTo('image_train.jsp')"),
    
    CreateFeed("javascript:jumpTo('feed_new.jsp')"),
    UpdateFeed("javascript:jumpTo('feed_list.jsp')"),
    DeleteFeed("javascript:jumpTo('feed_list.jsp')"),
    
    TrainFeed("javascript:jumpTo('feed_train.jsp')"),
    
    CreateGoalDef("javascript:jumpTo('goal_def_new.jsp')"),
    UpdateGoalDef("javascript:jumpTo('goal_def_list.jsp')"),
    DeleteGoalDef("javascript:jumpTo('goal_def_list.jsp')"),
    
    ReportGoal("javascript:jumpTo('goal_def_list.jsp')"),
    CompleteGoalDef("javascript:jumpTo('goal_def_list.jsp')"),
    
    TrainArticleSpeed("javascript:jumpTo('article_speed_train.jsp')"),
    
    TrainBeauty("javascript:jumpTo('beauty_train.jsp')"),
    
    TrainParn("javascript:jumpTo('parn_train.jsp')"),
    
    TrainFashion("javascript:jumpTo('fashion_train.jsp')"),
    
    CreateStorage("javascript:jumpTo('storage_new.jsp')"),
    UpdateStorage("javascript:jumpTo('storage_list.jsp')"),
    DeleteStorage("javascript:jumpTo('storage_list.jsp')"),
    
    CreateStorageAsset("javascript:jumpTo('storage_asset_new.jsp')"),
    DeleteStorageAsset("javascript:jumpTo('storage_asset_list.jsp')"),
    
    SaveParn("javascript:jumpTo('parn_train.jsp')"),
    
    IdeaToAccount("javascript:jumpTo('idea_list.jsp')"),
    IdeaToContact("javascript:jumpTo('idea_list.jsp')"),
    IdeaToDetail("javascript:jumpTo('idea_list.jsp')"),
    IdeaToDocument("javascript:jumpTo('idea_list.jsp')"),
    IdeaToExpenseItem("javascript:jumpTo('idea_list.jsp')"),
    IdeaToHabit("javascript:jumpTo('idea_list.jsp')"),
    IdeaToHumorPattern("javascript:jumpTo('idea_list.jsp')"),
    IdeaToQuestDef("javascript:jumpTo('idea_list.jsp')"),
    IdeaToQuote("javascript:jumpTo('idea_list.jsp')"),
    IdeaToImaginationItem("javascript:jumpTo('idea_list.jsp')"),
    IdeaToRomanceFactor("javascript:jumpTo('idea_list.jsp')"),
    IdeaToRomance("javascript:jumpTo('idea_list.jsp')"),
    IdeaToScenario("javascript:jumpTo('idea_list.jsp')"),
    
    TrainHowTo("javascript:jumpTo('howto_train.jsp')"),
    
    CreateFlashCardSet("javascript:jumpTo('flashcard_set_new.jsp')"),
    UpdateFlashCardSet("javascript:jumpTo('flashcard_set_list.jsp')"),
    DeleteFlashCardSet("javascript:jumpTo('flashcard_set_list.jsp')"),
    
    CreateFlashCard("javascript:jumpTo('flashcard_new.jsp')"),
    UpdateFlashCard("javascript:jumpTo('flashcard_list.jsp')"),
    DeleteFlashCard("javascript:jumpTo('flashcard_list.jsp')"),
    
    CreateQuizData("javascript:jumpTo('quiz_data_new.jsp')"),
    UpdateQuizData("javascript:jumpTo('quiz_data_list.jsp')"),
    DeleteQuizData("javascript:jumpTo('quiz_data_list.jsp')"),
    
    CreateWord("javascript:jumpTo('word_new.jsp')"),
    UpdateWord("javascript:jumpTo('word_list.jsp')"),
    DeleteWord("javascript:jumpTo('word_list.jsp')"),
    
    TrainWord("javascript:trainWord()"),
    
    CreateChecklistDef("javascript:jumpTo('checklist_def_new.jsp')"),
    UpdateChecklistDef("javascript:jumpTo('checklist_def_list.jsp')"),
    DeleteChecklistDef("javascript:jumpTo('checklist_def_list.jsp')"),
    
    CreateChecklist("javascript:jumpTo('checklist_new.jsp')"),
    UpdateChecklist("javascript:jumpTo('checklist_list.jsp')"),
    DeleteChecklist("javascript:jumpTo('checklist_list.jsp')"),
    
    CreateBehaviorDef("javascript:jumpTo('behavior_def_new.jsp')"),
    UpdateBehaviorDef("javascript:jumpTo('behavior_def_list.jsp')"),
    DeleteBehaviorDef("javascript:jumpTo('behavior_def_list.jsp')"),
    
    CreateBehavior("javascript:jumpTo('behavior_new.jsp')"),
    UpdateBehavior("javascript:jumpTo('behavior_list.jsp')"),
    DeleteBehavior("javascript:jumpTo('behavior_list.jsp')"),
    
    CreateProxy("javascript:jumpTo('proxy_new.jsp')"),
    UpdateProxy("javascript:jumpTo('proxy_list.jsp')"),
    DeleteProxy("javascript:jumpTo('proxy_list.jsp')"),
    
    CreatePenalty("javascript:jumpTo('penalty_new.jsp')"),
    UpdatePenalty("javascript:jumpTo('penalty_list.jsp')"),
    DeletePenalty("javascript:jumpTo('penalty_list.jsp')"),
    
    CreateAlias("javascript:jumpTo('alias_new.jsp')"),
    UpdateAlias("javascript:jumpTo('alias_list.jsp')"),
    DeleteAlias("javascript:jumpTo('alias_list.jsp')"),
    
    DoRhetoricQuiz("javascript:doRhetoricQuiz()"),
    
    DoMetaModelQuiz("javascript:doMetaModelQuiz()"),
    
    DoStrategyQuiz("javascript:doStrategyQuiz()"),
    
    CreateAlgorithmProblem("javascript:jumpTo('algorithm_problem_new.jsp')"),
    UpdateAlgorithmProblem("javascript:jumpTo('algorithm_problem_list.jsp')"),
    DeleteAlgorithmProblem("javascript:jumpTo('algorithm_problem_list.jsp')"),
    
    CreateAlgorithm("javascript:jumpTo('algorithm_new.jsp')"),
    UpdateAlgorithm("javascript:jumpTo('algorithm_list.jsp')"),
    DeleteAlgorithm("javascript:jumpTo('algorithm_list.jsp')"),
    
    TripleThinking(null),
    
    TrainWeaselPhrase("javascript:jumpTo('weasel_phrase_train.jsp')"),
    
    DoSleighOfMouthQuiz("javascript:doSOMPQuiz()"),
    
    IdeaToBehaviorDef("javascript:jumpTo('idea_list.jsp')"),
    
    CreateMeditation("javascript:jumpTo('meditation_exercise.jsp')"),
    UpdateMeditation(null),
    DeleteMeditation(null),
    
    TrainJoke("javascript:jumpTo('joke_view.jsp')"),
    
    CreateExpression("javascript:jumpTo('expression_new.jsp')"),
    UpdateExpression("javascript:jumpTo('expression_list.jsp')"),
    DeleteExpression("javascript:jumpTo('expression_list.jsp')"),
    
    CreateArtifact("javascript:jumpTo('artifact_new.jsp')"),
    UpdateArtifact("javascript:jumpTo('artifact_list.jsp')"),
    DeleteArtifact("javascript:jumpTo('artifact_list.jsp')"),
    
    TrainArticleRead("javascript:jumpTo('article_read_train.jsp')"),
    
    CreateLink("javascript:jumpTo('link_new.jsp')"),
    UpdateLink("javascript:jumpTo('link_list.jsp')"),
    DeleteLink("javascript:jumpTo('link_list.jsp')"),
    
    CreateReminder("javascript:jumpTo('reminder_new.jsp')"),
    UpdateReminder("javascript:jumpTo('reminder_list.jsp')"),
    DeleteReminder("javascript:jumpTo('reminder_list.jsp')"),
    
    CreateSleep("javascript:jumpTo('sleep_new.jsp')"),
    UpdateSleep("javascript:jumpTo('sleep_list.jsp')"),
    DeleteSleep("javascript:jumpTo('sleep_list.jsp')"),
    
    DoZSDateQuiz("javascript:doZodiacSignQuiz('Date')"),
    DoZSStrengthsQuiz("javascript:doZodiacSignQuiz('Strengths')"),
    DoZSWeaknessesQuiz("javascript:doZodiacSignQuiz('Weaknesses')"),
    DoZSLikesQuiz("javascript:doZodiacSignQuiz('Likes')"),
    DoZSDislikesQuiz("javascript:doZodiacSignQuiz('Dislikes')"),
    
    TrainStrategy("javascript:jumpTo('strategy_train.jsp')"),
    
    CreatePersonality("javascript:jumpTo('personality_new.jsp')"),
    UpdatePersonality("javascript:jumpTo('personality_list.jsp')"),
    DeletePersonality("javascript:jumpTo('personality_list.jsp')"),
    
    DoPersonalityQuiz("javascript:doPersonalityQuiz()"),
    
    CreateEmotion("javascript:jumpTo('emotion_new.jsp')"),
    UpdateEmotion("javascript:jumpTo('emotion_list.jsp')"),
    DeleteEmotion("javascript:jumpTo('emotion_list.jsp')"),
    
    DoEmotionQuiz("javascript:doEmotionQuiz()"),
    
    DoColdReadQuiz("javascript:doColdReadQuiz()"),
    
    TrainColdRead("javascript:jumpTo('cold_read_train.jsp')"),
    
    CreateFraud("javascript:jumpTo('fraud_new.jsp')"),
    UpdateFraud("javascript:jumpTo('fraud_list.jsp')"),
    DeleteFraud("javascript:jumpTo('fraud_list.jsp')"),
    
    CreatePeriod("javascript:jumpTo('period_new.jsp')"),
    UpdatePeriod("javascript:jumpTo('period_list.jsp')"),
    DeletePeriod("javascript:jumpTo('period_list.jsp')"),
    
    DoStorySkillQuiz("javascript:doStorySkillQuiz()"),
    TrainStorySkill("javascript:jumpTo('story_skill_train.jsp')"),
    
    DoPushPullQuiz("javascript:doPushPullQuiz()"),
    TrainPushPull("javascript:jumpTo('push_pull_train.jsp')"),
    
    CreateReaction("javascript:jumpTo('reaction_new.jsp')"),
    UpdateReaction("javascript:jumpTo('reaction_list.jsp')"),
    DeleteReaction("javascript:jumpTo('reaction_list.jsp')"),
    
    DoReactionQuiz("javascript:doReactionQuiz()"),
    
    DoChatSkillQuiz("javascript:doChatSkillQuiz()"),
    TrainChatSkill("javascript:jumpTo('chat_skill_train.jsp')"),
    
    CreatePlan("javascript:jumpTo('plan_new.jsp')"),
    UpdatePlan("javascript:jumpTo('plan_list.jsp')"),
    DeletePlan("javascript:jumpTo('plan_list.jsp')"),
    
    AddToPlan("javascript:jumpTo('plan_view.jsp')"),
    RemoveFromPlan("javascript:jumpTo('plan_view.jsp')"),
    
    DoWritingSkillQuiz("javascript:doWritingSkillQuiz()"),
    
    DoDressQuiz("javascript:doDressQuiz()"),
    
    DoTrickRuleQuiz("javascript:doTrickRuleQuiz()"),
    
    DoMicroExpressionQuiz("javascript:doMicroExpressionQuiz()"),
    
    DoDesignPatternQuiz("javascript:doDesignPatternQuiz()"),
    
    CreateChat("javascript:jumpTo('chat_new.jsp')"),
    UpdateChat("javascript:jumpTo('chat_list.jsp')"),
    DeleteChat("javascript:jumpTo('chat_list.jsp')"),
    
    DoBodyLanguageQuiz("javascript:doBodyLanguageQuiz()"),
    
    DoMiltonModelQuiz("javascript:doMiltonModelQuiz()"),
    
    DoOpenerQuiz("javascript:doOpenerQuiz()"),
    
    DoFaceReadQuiz("javascript:doFaceReadQuiz()"),
    
    CreateRoleAttr("javascript:jumpTo('role_attr_new.jsp')"),
    UpdateRoleAttr("javascript:jumpTo('role_attr_list.jsp')"),
    DeleteRoleAttr("javascript:jumpTo('role_attr_list.jsp')"),
    
    CreateRole("javascript:jumpTo('role_new.jsp')"),
    UpdateRole("javascript:jumpTo('role_list.jsp')"),
    DeleteRole("javascript:jumpTo('role_list.jsp')"),
    
    CreateRoleDetail("javascript:jumpTo('role_detail_new.jsp')"),
    UpdateRoleDetail("javascript:jumpTo('role_detail_list.jsp')"),
    DeleteRoleDetail("javascript:jumpTo('role_detail_list.jsp')"),
    
    DoZSComplexQuiz("javascript:doZodiacSignComplexQuiz()"),
    
    CreateInterview("javascript:jumpTo('interview_new.jsp')"),
    UpdateInterview("javascript:jumpTo('interview_list.jsp')"),
    DeleteInterview("javascript:jumpTo('interview_list.jsp')"),
    
    DoInterviewQuiz("javascript:doInterviewQuiz()"),
    
    CreateSpice("javascript:jumpTo('spice_new.jsp')"),
    UpdateSpice("javascript:jumpTo('spice_list.jsp')"),
    DeleteSpice("javascript:jumpTo('spice_list.jsp')"),
    
    DoHoopQuiz("javascript:doHoopQuiz()"),
    TrainHoop("javascript:jumpTo('hoop_train.jsp')"),
    
    CreateAnchor("javascript:jumpTo('anchor_new.jsp')"),
    UpdateAnchor("javascript:jumpTo('anchor_list.jsp')"),
    DeleteAnchor("javascript:jumpTo('anchor_list.jsp')"),
    
    CreateKino("javascript:jumpTo('kino_new.jsp')"),
    UpdateKino("javascript:jumpTo('kino_list.jsp')"),
    DeleteKino("javascript:jumpTo('kino_list.jsp')"),
    
    ;
    
    private String link;
    
    private EventType(String link) {
        this.link = link;
    }
    
    public String getLink() {
        return this.link;
    }
}
