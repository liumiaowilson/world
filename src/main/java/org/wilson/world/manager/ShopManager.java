package org.wilson.world.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.shop.ShopItem;
import org.wilson.world.useritem.UserItem;

public class ShopManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(ShopManager.class);
    
    private static ShopManager instance;
    
    private Cache<Integer, ShopItem> shopItems = null;
    
    private int maxValue = 0;
    private int minValue = 0;
    
    private static int GLOBAL_ID = 1;
    
    private ShopManager() {
        this.shopItems = new DefaultCache<Integer, ShopItem>("shop_manager_shop_items", false);
    }
    
    public static ShopManager getInstance() {
        if(instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }
    
    public void restock() {
        this.shopItems.clear();
        
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
}
