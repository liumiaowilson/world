package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.bodylanguage.BodyLanguageQuiz;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.chatskill.ChatSkillQuiz;
import org.wilson.world.coldread.ColdReadQuiz;
import org.wilson.world.dao.DAO;
import org.wilson.world.designpattern.DesignPatternQuiz;
import org.wilson.world.dress.DressQuiz;
import org.wilson.world.emotion.EmotionQuiz;
import org.wilson.world.faceread.FaceReadQuiz;
import org.wilson.world.flashcard.FlashCardQuiz;
import org.wilson.world.hoop.HoopQuiz;
import org.wilson.world.interview.InterviewQuiz;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.metamodel.MetaModelQuiz;
import org.wilson.world.microexpression.MicroExpressionQuiz;
import org.wilson.world.miltonmodel.MiltonModelQuiz;
import org.wilson.world.model.QuizData;
import org.wilson.world.opener.OpenerQuiz;
import org.wilson.world.personality.PersonalityQuiz;
import org.wilson.world.pushpull.PushPullQuiz;
import org.wilson.world.quiz.DefaultQuiz;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizProcessor;
import org.wilson.world.quiz.SystemQuiz;
import org.wilson.world.reaction.ReactionQuiz;
import org.wilson.world.rhetoric.RhetoricQuiz;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.somp.SOMPQuiz;
import org.wilson.world.storyskill.StorySkillQuiz;
import org.wilson.world.strategy.StrategyQuiz;
import org.wilson.world.test.BigFivePersonalityQuiz;
import org.wilson.world.test.PCompassQuiz;
import org.wilson.world.test.SmalleyPersonalityQuiz;
import org.wilson.world.test.TestQuiz;
import org.wilson.world.trickrule.TrickRuleQuiz;
import org.wilson.world.util.IOUtils;
import org.wilson.world.word.WordQuiz;
import org.wilson.world.writingskill.WritingSkillQuiz;
import org.wilson.world.zodiac_sign.ZodiacSignComplexQuiz;
import org.wilson.world.zodiac_sign.ZodiacSignQuiz;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QuizDataManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(QuizDataManager.class);
    
    public static final String NAME = "quiz_data";
    
    private static QuizDataManager instance;
    
    private DAO<QuizData> dao = null;
    
    private Cache<Integer, Quiz> cache = null;
    private Cache<String, Quiz> nameCache = null;
    
    private static int GLOBAL_ID = 1;
    
    private QuizPaper paper = null;
    
    private static String sampleContent = null;
    
    private String redoUrl = null;
    
    @SuppressWarnings("unchecked")
    private QuizDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(QuizData.class);
        this.cache = new DefaultCache<Integer, Quiz>("quiz_data_manager_cache", false);
        this.nameCache = new DefaultCache<String, Quiz>("quiz_data_manager_name_cache", false);
        ((CachedDAO<QuizData>)this.dao).getCache().addCacheListener(new CacheListener<QuizData>(){

            @Override
            public void cachePut(QuizData old, QuizData v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                loadQuizData(v);
            }

            @Override
            public void cacheDeleted(QuizData v) {
                QuizDataManager.this.cache.delete(v.id);
                QuizDataManager.this.nameCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<QuizData> all) {
                loadSystemQuizes();
            }

            @Override
            public void cacheLoading(List<QuizData> old) {
                QuizDataManager.this.cache.clear();
                QuizDataManager.this.nameCache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(QuizData data : getQuizDatas()) {
                    boolean found = data.name.contains(text) || data.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = data.id;
                        content.name = data.name;
                        content.description = data.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static QuizDataManager getInstance() {
        if(instance == null) {
            instance = new QuizDataManager();
        }
        return instance;
    }
    
    private void loadSystemQuizes() {
        GLOBAL_ID = 1;
        
        this.loadSystemQuiz(new FlashCardQuiz());
        this.loadSystemQuiz(new WordQuiz());
        this.loadSystemQuiz(new RhetoricQuiz());
        this.loadSystemQuiz(new MetaModelQuiz());
        this.loadSystemQuiz(new StrategyQuiz());
        this.loadSystemQuiz(new SOMPQuiz());
        this.loadSystemQuiz(new ZodiacSignQuiz());
        this.loadSystemQuiz(new PersonalityQuiz());
        this.loadSystemQuiz(new EmotionQuiz());
        this.loadSystemQuiz(new ColdReadQuiz());
        this.loadSystemQuiz(new StorySkillQuiz());
        this.loadSystemQuiz(new PushPullQuiz());
        this.loadSystemQuiz(new ReactionQuiz());
        this.loadSystemQuiz(new ChatSkillQuiz());
        this.loadSystemQuiz(new WritingSkillQuiz());
        this.loadSystemQuiz(new DressQuiz());
        this.loadSystemQuiz(new TrickRuleQuiz());
        this.loadSystemQuiz(new MicroExpressionQuiz());
        this.loadSystemQuiz(new DesignPatternQuiz());
        this.loadSystemQuiz(new BodyLanguageQuiz());
        this.loadSystemQuiz(new MiltonModelQuiz());
        this.loadSystemQuiz(new OpenerQuiz());
        this.loadSystemQuiz(new FaceReadQuiz());
        this.loadSystemQuiz(new ZodiacSignComplexQuiz());
        this.loadSystemQuiz(new InterviewQuiz());
        this.loadSystemQuiz(new HoopQuiz());
        
        this.loadTests();
    }
    
    private void loadTests() {
        this.loadSystemQuiz(new BigFivePersonalityQuiz());
        this.loadSystemQuiz(new SmalleyPersonalityQuiz());
        this.loadSystemQuiz(new PCompassQuiz());
    }
    
    private void loadSystemQuiz(Quiz quiz) {
        if(quiz != null) {
            quiz.setId(-GLOBAL_ID++);
            this.cache.put(quiz.getId(), quiz);
            this.nameCache.put(quiz.getName(), quiz);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private void loadQuizData(QuizData data) {
        if(data == null) {
            return;
        }
        
        String impl = data.processor;
        QuizProcessor processor = null;
        try {
            Class clazz = Class.forName(impl);
            processor = (QuizProcessor) clazz.newInstance();
            logger.info("Loaded quiz processor using class [" + impl + "]");
        }
        catch(Exception e) {
            processor = (QuizProcessor) ExtManager.getInstance().wrapAction(impl, QuizProcessor.class);
            if(processor != null) {
                logger.info("Loaded quiz processor using action [" + impl + "]");
            }
            else {
                logger.info("Failed to load quiz processor using [" + impl + "]");
                return;
            }
        }
        
        if(processor != null) {
            DefaultQuiz quiz = new DefaultQuiz(data, processor);
            this.cache.put(quiz.getId(), quiz);
            this.nameCache.put(quiz.getName(), quiz);
        }
    }
    
    public void createQuizData(QuizData data) {
        ItemManager.getInstance().checkDuplicate(data);
        
        this.dao.create(data);
    }
    
    public QuizData getQuizData(int id) {
        QuizData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<QuizData> getQuizDatas() {
        List<QuizData> result = new ArrayList<QuizData>();
        for(QuizData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateQuizData(QuizData data) {
        this.dao.update(data);
    }
    
    public void deleteQuizData(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof QuizData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        QuizData data = (QuizData)target;
        return String.valueOf(data.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        QuizData data = (QuizData)target;
        return data.name;
    }
    
    public static String getSampleContent() throws IOException {
        if(sampleContent == null) {
            InputStream is = QuizDataManager.class.getClassLoader().getResourceAsStream("quiz_data_content.json");
            try {
                sampleContent = IOUtils.toString(is);
            }
            catch(Exception e) {
                is.close();
            }
        }
        return sampleContent;
    }
    
    public static String fromQuizItems(List<QuizItem> items) {
        JSONArray ret = new JSONArray();
        if(items == null || items.isEmpty()) {
            return ret.toString();
        }
        
        for(int i = 0; i < items.size(); i++) {
            QuizItem item = items.get(i);
            JSONObject itemObj = new JSONObject();
            itemObj.put("id", item.id);
            itemObj.put("name", item.name);
            itemObj.put("mode", item.mode.name());
            itemObj.put("question", item.question);
            
            JSONArray optionArray = new JSONArray();
            for(int j = 0; j < item.options.size(); j++) {
                QuizItemOption option = item.options.get(j);
                JSONObject optionObj = new JSONObject();
                optionObj.put("id", option.id);
                optionObj.put("name", option.name);
                optionObj.put("answer", option.answer);
                optionObj.put("value", option.value);
                optionObj.put("next", option.next);
                optionArray.add(optionObj);
            }
            itemObj.put("options", optionArray);
            
            ret.add(itemObj);
        }
        
        return ret.toString();
    }
    
    public static List<QuizItem> toQuizItems(String content) {
        if(StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        
        List<QuizItem> ret = new ArrayList<QuizItem>();
        
        JSONArray array = JSONArray.fromObject(content);
        if(array != null) {
            for(int i = 0; i < array.size(); i++) {
                JSONObject itemObj = array.getJSONObject(i);
                if(itemObj != null) {
                    QuizItem item = new QuizItem();
                    item.id = itemObj.getInt("id");
                    item.question = itemObj.getString("question");
                    
                    if(itemObj.has("name")) {
                        item.name = itemObj.getString("name");
                    }
                    else {
                        item.name = item.question;
                    }
                    
                    if(itemObj.has("mode")) {
                        try {
                            item.mode = QuizItemMode.valueOf(itemObj.getString("mode"));
                        }
                        catch(Exception e) {
                            item.mode = QuizItemMode.Single;
                        }
                    }
                    else {
                        item.mode = QuizItemMode.Single;
                    }
                    
                    JSONArray optionArray = itemObj.getJSONArray("options");
                    if(optionArray != null) {
                        for(int j = 0; j < optionArray.size(); j++) {
                            JSONObject optionObj = optionArray.getJSONObject(j);
                            if(optionObj != null) {
                                QuizItemOption option = new QuizItemOption();
                                option.answer = optionObj.getString("answer");
                                option.value = optionObj.getInt("value");
                                
                                if(optionObj.has("id")) {
                                    option.id = optionObj.getInt("id");
                                }
                                else {
                                    option.id = j + 1;
                                }
                                
                                if(optionObj.has("name")) {
                                    option.name = optionObj.getString("name");
                                }
                                else {
                                    option.name = option.answer;
                                }
                                
                                if(optionObj.has("next")) {
                                    option.next = optionObj.getInt("next");
                                }
                                
                                item.options.add(option);
                            }
                        }
                    }
                    
                    ret.add(item);
                }
            }
        }
        
        return ret;
    }
    
    public String validateContent(String content) {
        if(StringUtils.isBlank(content)) {
            return "Content should not be empty.";
        }
        
        try {
            List<QuizItem> items = toQuizItems(content);
            if(items != null) {
                return null;
            }
            else {
                return "Failed to parse content";
            }
        }
        catch(Exception e) {
            return "Failed to parse content";
        }
    }
    
    public List<Quiz> getQuizes() {
        return this.cache.getAll();
    }
    
    public Quiz getQuiz(int id) {
        return this.cache.get(id);
    }
    
    public QuizPaper getQuizPaper(Quiz quiz) {
        if(quiz == null) {
            return null;
        }
        
        if(this.paper == null) {
            this.paper = new QuizPaper(quiz);
        }
        else {
            if(this.paper.getQuiz().getId() != quiz.getId()) {
                this.paper = new QuizPaper(quiz);
            }
        }
        
        return this.paper;
    }
    
    public void clearQuizPaper() {
        this.paper = null;
        this.redoUrl = null;
    }
    
    @SuppressWarnings("rawtypes")
    public Quiz getQuizOfClass(Class clazz) {
        if(clazz == null) {
            return null;
        }
        
        for(Quiz quiz : this.getQuizes()) {
            if(quiz.getClass() == clazz) {
                return quiz;
            }
        }
        
        return null;
    }
    
    public void setRedoUrl(String url) {
        this.redoUrl = url;
    }
    
    public String getRedoUrl() {
        return this.redoUrl;
    }
    
    public List<Quiz> getPublicQuizes() {
        List<Quiz> ret = new ArrayList<Quiz>();
        
        for(Quiz quiz : this.getQuizes()) {
            if(quiz instanceof SystemQuiz) {
                SystemQuiz systemQuiz = (SystemQuiz)quiz;
                if(systemQuiz.isPublic()) {
                    ret.add(systemQuiz);
                }
            }
        }
        
        return ret;
    }
    
    public List<Quiz> getTestQuizes() {
        List<Quiz> ret = new ArrayList<Quiz>();
        
        for(Quiz quiz : this.getQuizes()) {
            if(quiz instanceof TestQuiz) {
                ret.add(quiz);
            }
        }
        
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    public String getQuizUrl(Class quizClass) {
        if(quizClass == null) {
            return "";
        }
        Quiz quiz = this.getQuizOfClass(quizClass);
        if(quiz == null) {
            return "";
        }
        
        return "javascript:jumpTo('quiz_paper.jsp?id=" + quiz.getId() + "')";
    }
}
