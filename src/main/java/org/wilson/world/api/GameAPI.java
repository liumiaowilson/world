package org.wilson.world.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.TickManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.MessageInfo;

@Path("/game")
public class GameAPI {
    @POST
    @Path("/start")
    @Produces("application/json")
    public Response start(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        Attacker a1 = new Attacker("Wilson");
        a1.setMaxHp(100);
        a1.setHp(100);
        a1.setStrenth(20);
        a1.setSpeed(20);
        
        Attacker a2 = new Attacker("Coco");
        a2.setMaxHp(100);
        a2.setHp(100);
        a2.setStrenth(20);
        a2.setSpeed(20);
        
        TickManager tm = TickManager.getInstance();
        tm.reset();
        
        tm.addTickable(a1);
        tm.addTickable(a2);
        
        tm.play();
        
        List<MessageInfo> messages = tm.getMessages();
        
        APIResult result = APIResultUtils.buildOKAPIResult("Game OK");
        result.list = messages;
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
