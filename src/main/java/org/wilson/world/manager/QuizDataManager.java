package org.wilson.world.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.QuizData;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

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
    
    public String getSampleContent() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("quiz_data_content.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        
        br.close();
        
        return sb.toString();
    }
}
