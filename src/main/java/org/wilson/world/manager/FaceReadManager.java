package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.FaceRead;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class FaceReadManager {
    public static final String NAME = "face_read";
    
    private static FaceReadManager instance;
    
    private DAO<FaceRead> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private FaceReadManager() {
        this.dao = DAOManager.getInstance().getDAO(FaceRead.class);
        
        int id = 1;
        for(FaceRead read : this.getFaceReads()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "<img src=\"" + read.image + "\"/>";
            pair.bottom = read.indicator;
            pair.url = "javascript:jumpTo('face_read_edit.jsp?id=" + read.id + "')";
            this.pairs.put(pair.id, pair);
        }
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(FaceRead read : getFaceReads()) {
                    boolean found = read.name.contains(text) || read.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = read.id;
                        content.name = read.name;
                        content.description = read.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FaceReadManager getInstance() {
        if(instance == null) {
            instance = new FaceReadManager();
        }
        return instance;
    }
    
    public void createFaceRead(FaceRead read) {
    }
    
    public FaceRead getFaceRead(int id) {
        FaceRead read = this.dao.get(id);
        if(read != null) {
            return read;
        }
        else {
            return null;
        }
    }
    
    public List<FaceRead> getFaceReads() {
        List<FaceRead> result = new ArrayList<FaceRead>();
        for(FaceRead read : this.dao.getAll()) {
            result.add(read);
        }
        return result;
    }
    
    public void updateFaceRead(FaceRead read) {
    }
    
    public void deleteFaceRead(int id) {
    }
    
    public List<QuizPair> getFaceReadQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}
