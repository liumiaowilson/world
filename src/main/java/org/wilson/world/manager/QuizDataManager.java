package org.wilson.world.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.QuizData;
import org.wilson.world.quiz.QuizItem;
import org.wilson.world.quiz.QuizItemMode;
import org.wilson.world.quiz.QuizItemOption;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QuizDataManager implements ItemTypeProvider {
    public static final String NAME = "quiz_data";
    
    private static QuizDataManager instance;
    
    private DAO<QuizData> dao = null;
    
    @SuppressWarnings("unchecked")
    private QuizDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(QuizData.class);
        
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
        InputStream is = QuizDataManager.class.getClassLoader().getResourceAsStream("quiz_data_content.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        
        br.close();
        
        return sb.toString();
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
}
