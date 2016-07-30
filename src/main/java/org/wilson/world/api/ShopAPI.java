package org.wilson.world.api;

import java.util.Collections;
import java.util.Comparator;
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
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.ShopManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ShopBuyItem;
import org.wilson.world.model.ShopSellItem;

@Path("/shop")
public class ShopAPI {
    private static final Logger logger = Logger.getLogger(ShopAPI.class);
    
    @GET
    @Path("/list_buy")
    @Produces("application/json")
    public Response listBuy(
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
        
        try {
            List<ShopBuyItem> buyItems = ShopManager.getInstance().getShopBuyItems();
            
            Collections.sort(buyItems, new Comparator<ShopBuyItem>(){

                @Override
                public int compare(ShopBuyItem o1, ShopBuyItem o2) {
                    int ret = o1.type.compareTo(o2.type);
                    if(ret == 0) {
                        return o1.name.compareTo(o2.name);
                    }
                    else {
                        return ret;
                    }
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Shop items have been successfully fetched.");
            result.list = buyItems;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_sell")
    @Produces("application/json")
    public Response listSell(
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
        
        try {
            List<ShopSellItem> sellItems = ShopManager.getInstance().getShopSellItems();
            
            Collections.sort(sellItems, new Comparator<ShopSellItem>(){

                @Override
                public int compare(ShopSellItem o1, ShopSellItem o2) {
                    int ret = o1.type.compareTo(o2.type);
                    if(ret == 0) {
                        return o1.name.compareTo(o2.name);
                    }
                    else {
                        return ret;
                    }
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Inventory items have been successfully fetched.");
            result.list = sellItems;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/restock")
    @Produces("application/json")
    public Response restock(
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
        
        try {
            int coins = CharManager.getInstance().getCoins();
            if(coins <= 0) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No enough coins to make the shop restock."));
            }
            coins -= 1;
            CharManager.getInstance().setCoins(coins);
            
            ShopManager.getInstance().restock();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Shop has restocked successfully.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to restock", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/buy")
    @Produces("application/json")
    public Response buy(
            @QueryParam("id") int id,
            @QueryParam("amount") int amount,
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
        
        try {
            String ret = ShopManager.getInstance().buy(id, amount);
            if(!StringUtils.isBlank(ret)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Successfully bought shop item.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to buy item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/sell")
    @Produces("application/json")
    public Response sell(
            @QueryParam("id") int id,
            @QueryParam("amount") int amount,
            @QueryParam("price") int price,
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
        
        try {
            String ret = ShopManager.getInstance().sell(id, amount, price);
            if(!StringUtils.isBlank(ret)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Successfully sold inventory item.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to sell item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
