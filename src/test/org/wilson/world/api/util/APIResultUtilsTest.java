package org.wilson.world.api.util;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Idea;

public class APIResultUtilsTest {

    @Test
    public void toJSON() {
        APIResult result = APIResultUtils.buildErrorAPIResult("testError");
        Response response = APIResultUtils.buildJSONResponse(result);
        System.out.println(response.getEntity());
    }

    @Test
    public void testIdea() {
        APIResult result = APIResultUtils.buildOKAPIResult("success");
        Idea idea = new Idea();
        idea.id = 1;
        idea.name = "test";
        idea.content = "test content";
        result.data = idea;
        Response response = APIResultUtils.buildJSONResponse(result);
        System.out.println(response.getEntity());
    }
    
    @Test
    public void testIdeas() {
        APIResult result = APIResultUtils.buildOKAPIResult("success");
        Idea idea = new Idea();
        idea.id = 1;
        idea.name = "test";
        idea.content = "test content";
        List<Idea> ideas = new ArrayList<Idea>();
        ideas.add(idea);
        
        idea = new Idea();
        idea.id = 2;
        idea.name = "test 2";
        idea.content = "test content 2";
        ideas.add(idea);
        
        result.list = ideas;
        Response response = APIResultUtils.buildJSONResponse(result);
        System.out.println(response.getEntity());
    }
}
