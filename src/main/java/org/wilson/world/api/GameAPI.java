package org.wilson.world.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NPCManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.manager.TickManager;
import org.wilson.world.manager.UserManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.MessageInfo;

@Path("/game")
public class GameAPI {
    @GET
    @Path("/start")
    @Produces("application/json")
    public Response start(
            @QueryParam("id") int id,
            @QueryParam("type") String type,
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
        
        int stamina = CharManager.getInstance().getStamina();
        if(stamina < 10) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No enough stamina to carry out the game."));
        }
        stamina -= 10;
        CharManager.getInstance().setStamina(stamina);
        
        final Attacker user = CharManager.getInstance().getAttacker();
        user.setName(UserManager.getInstance().getCurrentUser().username);
        
        final Attacker npc = NPCManager.getInstance().getNPC(id);
        
        Attacker player1 = null;
        Attacker player2 = null;
        if("try".equals(type)) {
            player1 = user;
            player2 = Attacker.clone(npc);
        }
        else {
            player1 = user;
            player2 = npc;
        }
        
        TickManager tm = TickManager.getInstance();
        tm.reset();
        
        tm.addTickable(player1);
        tm.addTickable(player2);
        
        tm.play();
        
        if(!"try".equals(type)) {
            int hp = user.getHp();
            if(hp < 10) {
                hp = 10;
            }
            user.setHp(hp);
            
            hp = npc.getHp();
            if(hp < 0) {
                NPCManager.getInstance().removeNPC(npc);
            }
            
            ThreadPoolManager.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    CharManager.getInstance().setAttacker(user);
                    
                    if(npc.getHp() < 0) {
                        int kills = CharManager.getInstance().getKills();
                        kills += 1;
                        CharManager.getInstance().setKills(kills);
                    }
                }
                
            });
        }
        
        List<MessageInfo> messages = tm.getMessages();
        
        APIResult result = APIResultUtils.buildOKAPIResult("Game OK");
        result.list = messages;
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
