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
import org.wilson.world.manager.BalanceManager;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NPCManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.manager.TickManager;
import org.wilson.world.manager.UserManager;
import org.wilson.world.manager.UserSkillManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.UserSkill;
import org.wilson.world.skill.SkillStyle;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.GameSkill;
import org.wilson.world.tick.MessageInfo;
import org.wilson.world.useritem.LootItem;

@Path("/game")
public class GameAPI {
    @GET
    @Path("/start")
    @Produces("application/json")
    public Response start(
            @QueryParam("id") int id,
            @QueryParam("type") String type,
            @QueryParam("strategy") String strategy,
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
        try {
            SkillStyle style = SkillStyle.valueOf(strategy);
            if(style == null) {
                style = SkillStyle.Balanced;
            }
            user.setStyle(style);
        }
        catch(Exception e) {
            user.setStyle(SkillStyle.Balanced);
        }
        
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
        
        ThreadPoolManager.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                List<GameSkill> gsList = user.getSkills();
                for(GameSkill gs : gsList) {
                    UserSkill us = UserSkillManager.getInstance().getUserSkillsBySkillId(gs.getId());
                    if(gs.getExp() > us.exp) {
                        us.exp = gs.getExp();
                        if(us.exp > 100) {
                            us.exp = 100;
                        }
                        UserSkillManager.getInstance().updateUserSkill(us);
                    }
                }
            }
            
        });
        
        if(!"try".equals(type)) {
            int energy = BalanceManager.getInstance().getEnergyBalance();
            energy -= 1;
            BalanceManager.getInstance().setEnergyBalance(energy);
            
            ThreadPoolManager.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    int hp = user.getHp();
                    if(hp < 0) {
                        int penalty = ExpManager.getInstance().getLevel() * 10;
                        int old_exp = ExpManager.getInstance().getExp();
                        int exp = old_exp - penalty;
                        if(exp < 0) {
                            exp = 0;
                        }
                        ExpManager.getInstance().setExp(exp);
                        
                        NotifyManager.getInstance().notifyDanger("Lost [" + (old_exp - exp) + "] experience point because of death.");
                        
                        hp = 0;
                    }
                    user.setHp(hp);
                    
                    int mp = user.getMp();
                    if(mp < 0) {
                        mp = 0;
                    }
                    user.setMp(mp);
                    
                    hp = npc.getHp();
                    if(hp < 0) {
                        NPCManager.getInstance().removeNPC(npc);
                    }
                    
                    CharManager.getInstance().setAttacker(user);
                    
                    if(npc.getHp() < 0) {
                        int kills = CharManager.getInstance().getKills();
                        kills += 1;
                        CharManager.getInstance().setKills(kills);
                        
                        LootItem loot = NPCManager.getInstance().loot(user, npc);
                        if(loot != null) {
                            if(npc.isElite()) {
                                loot.amount = loot.amount * 2;
                            }
                            
                            InventoryItemManager.getInstance().addUserItem(loot.item, loot.amount);
                            
                            NotifyManager.getInstance().notifySuccess("Gained [" + loot.item.getName() + " x " + loot.amount + "] from loot");
                        }
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
