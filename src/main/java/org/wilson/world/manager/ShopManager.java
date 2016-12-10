package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.InventoryItem;
import org.wilson.world.model.ShopBuyItem;
import org.wilson.world.model.ShopSellItem;
import org.wilson.world.shop.ShopItem;
import org.wilson.world.shop.ShopRestockJob;
import org.wilson.world.skill.TradeType;
import org.wilson.world.useritem.UserItem;
import org.wilson.world.useritem.UserItemStatus;
import org.wilson.world.useritem.UserItemType;

public class ShopManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(ShopManager.class);
    
    private static ShopManager instance;
    
    private Cache<Integer, ShopItem> shopItems = null;
    
    private int maxValue = 0;
    private int minValue = 0;
    
    private static int GLOBAL_ID = 1;
    
    private ShopManager() {
        this.shopItems = new DefaultCache<Integer, ShopItem>("shop_manager_shop_items", false);
        
        ScheduleManager.getInstance().addJob(new ShopRestockJob());
    }
    
    public static ShopManager getInstance() {
        if(instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }
    
    public void restock() {
        this.shopItems.clear();
        GLOBAL_ID = 1;
        
        for(UserItem userItem : UserItemDataManager.getInstance().getUserItems()) {
            if(userItem.getValue() > this.maxValue) {
                this.maxValue = userItem.getValue();
            }
            if(userItem.getValue() < this.minValue) {
                this.minValue = userItem.getValue();
            }
        }
        
        for(UserItem userItem : UserItemDataManager.getInstance().getUserItems()) {
            this.restock(userItem);
        }
    }
    
    private void restock(UserItem userItem) {
        if(userItem == null) {
            return;
        }
        
        //tickets cannot be bought
        if(UserItemType.Ticket.name().equals(userItem.getType())) {
            return;
        }
        
        if(DiceManager.getInstance().dice(5, 50, this.maxValue, this.minValue, userItem.getValue())) {
            int price = DiceManager.getInstance().roll(userItem.getValue(), 0.5, 1.5);
            int amount = DiceManager.getInstance().roll(1, 10, this.maxValue, this.minValue, userItem.getValue());
            ShopItem item = new ShopItem();
            item.id = GLOBAL_ID++;
            item.itemId = userItem.getId();
            item.name = userItem.getName();
            item.type = userItem.getType();
            item.price = price;
            item.amount = amount;
            this.shopItems.put(item.id, item);
        }
    }
    
    @Override
    public void start() {
        logger.info("Start to restock");
        this.restock();
    }

    @Override
    public void shutdown() {
    }
    
    public List<ShopItem> getShopItems() {
        return this.shopItems.getAll();
    }
    
    public ShopItem getShopItem(int id) {
        return this.shopItems.get(id);
    }
    
    public String buy(int shopItemId, int amount) {
        ShopItem item = this.getShopItem(shopItemId);
        if(item == null) {
            return "Invalid shop item to buy.";
        }
        if(amount <= 0) {
            return "Invalid amount to buy.";
        }
        
        if(amount > item.amount) {
            return "The shop does not have enough item to sell.";
        }
        
        int cost = item.price * amount;
        int coins = CharManager.getInstance().getCoins();
        if(cost > coins) {
            return "User does not have enough coins to buy.";
        }
        coins -= cost;
        CharManager.getInstance().setCoins(coins);
        
        InventoryItem invItem = new InventoryItem();
        invItem.itemId = item.itemId;
        invItem.name = item.name;
        invItem.type = item.type;
        invItem.price = item.price;
        invItem.amount = amount;
        invItem.status = UserItemStatus.READY.name();
        InventoryItemManager.getInstance().addInventoryItem(invItem);
        
        item.amount -= amount;
        if(item.amount == 0) {
            this.shopItems.delete(item.id);
        }
        
        UserSkillManager.getInstance().useTradeSkill(cost, TradeType.Buy);
        
        return null;
    }
    
    public List<ShopBuyItem> getShopBuyItems() {
        List<ShopItem> items = getShopItems();
        List<ShopBuyItem> buyItems = new ArrayList<ShopBuyItem>();
        for(ShopItem item : items) {
            ShopBuyItem buyItem = new ShopBuyItem();
            buyItem.id = item.id;
            buyItem.name = item.name;
            buyItem.type = item.type;
            buyItem.price = item.price;
            buyItem.amount = item.amount;
            List<InventoryItem> invItems = InventoryItemManager.getInstance().getInventoryItemsByUserItemId(item.itemId);
            if(invItems == null || invItems.isEmpty()) {
                buyItem.invPrice = 0;
                buyItem.invAmount = 0;
            }
            else {
                int sum = 0;
                int total = 0;
                for(InventoryItem invItem : invItems) {
                    sum += invItem.price * invItem.amount;
                    total += invItem.amount;
                }
                buyItem.invPrice = sum / total;
                buyItem.invAmount = total;
            }
            UserItem userItem = UserItemDataManager.getInstance().getUserItem(item.itemId);
            if(userItem == null) {
                continue;
            }
            buyItem.description = userItem.getDescription();
            buyItems.add(buyItem);
        }
        
        return buyItems;
    }
    
    public ShopItem getShopItemByUserItemId(int userItemId) {
        for(ShopItem item : this.shopItems.getAll()) {
            if(item.itemId == userItemId) {
                return item;
            }
        }
        return null;
    }
    
    public List<ShopSellItem> getShopSellItems() {
        List<ShopSellItem> ret = new ArrayList<ShopSellItem>();
        
        for(InventoryItem invItem : InventoryItemManager.getInstance().getInventoryItems()) {
            if(!UserItemStatus.READY.name().equals(invItem.status)) {
                continue;
            }
            
            UserItem userItem = UserItemDataManager.getInstance().getUserItem(invItem.itemId);
            //tickets cannot be bought or sold
            if(UserItemType.Ticket.name().equals(userItem.getType())) {
                continue;
            }
            
            ShopSellItem item = new ShopSellItem();
            item.id = invItem.id;
            item.name = userItem.getName();
            item.type = userItem.getType();
            item.description = userItem.getDescription();
            item.invPrice = invItem.price;
            item.amount = invItem.amount;
            
            ShopItem shopItem = this.getShopItemByUserItemId(invItem.itemId);
            if(shopItem == null) {
                item.price = userItem.getValue();
            }
            else {
                item.price = shopItem.price / 2;
            }
            
            ret.add(item);
        }
        
        return ret;
    }
    
    public String sell(int invItemId, int amount, int price) {
        InventoryItem item = InventoryItemManager.getInstance().getInventoryItem(invItemId);
        if(item == null) {
            return "Invalid inventory item to sell.";
        }
        if(!UserItemStatus.READY.name().equals(item.status)) {
            return "Only items with ready status can be sold.";
        }
        if(amount <= 0) {
            return "Invalid amount to sell.";
        }
        
        if(amount > item.amount) {
            return "The user does not have enough item to sell.";
        }
        
        int earning = price * amount;
        int coins = CharManager.getInstance().getCoins();
        coins += earning;
        CharManager.getInstance().setCoins(coins);
        
        InventoryItem invItem = new InventoryItem();
        invItem.itemId = item.itemId;
        invItem.name = item.name;
        invItem.type = item.type;
        invItem.price = price;
        invItem.amount = amount;
        invItem.status = UserItemStatus.READY.name();
        InventoryItemManager.getInstance().removeInventoryItem(invItem);
        
        ShopItem shopItem = this.getShopItemByUserItemId(item.itemId);
        if(shopItem == null) {
            shopItem = new ShopItem();
            shopItem.id = GLOBAL_ID++;
            shopItem.name = item.name;
            shopItem.type = item.type;
            shopItem.itemId = item.itemId;
            shopItem.price = price * 2;
            shopItem.amount = amount;
            this.shopItems.put(shopItem.id, shopItem);
        }
        else {
            shopItem.amount += amount;
        }
        
        UserSkillManager.getInstance().useTradeSkill(earning, TradeType.Sell);
        
        return null;
    }
}
