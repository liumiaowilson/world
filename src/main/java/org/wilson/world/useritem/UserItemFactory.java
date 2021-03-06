package org.wilson.world.useritem;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.balance.BalanceResetEffect;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.manager.ExtManager;

public class UserItemFactory implements JavaExtensionListener<AbstractUserItem> {
    public static final String GALLERY_TICKET_NAME = "Gallery Ticket";
    
    private List<UserItem> items = new ArrayList<UserItem>();
    
    private static UserItemFactory instance;
    
    private UserItemFactory() {
    	ExtManager.getInstance().addJavaExtensionListener(this);
    	
        this.addUserItem("HP Minor Potion", UserItemType.Potion.name(), "Recove minor amount of HP", 5, new HPRecoverEffect(25));
        this.addUserItem("HP Medium Potion", UserItemType.Potion.name(), "Recove medium amount of HP", 10, new HPRecoverEffect(50));
        this.addUserItem("HP Major Potion", UserItemType.Potion.name(), "Recove major amount of HP", 15, new HPRecoverEffect(75));
        
        this.addUserItem("MP Minor Potion", UserItemType.Potion.name(), "Recove minor amount of MP", 5, new MPRecoverEffect(25));
        this.addUserItem("MP Medium Potion", UserItemType.Potion.name(), "Recove medium amount of MP", 10, new MPRecoverEffect(50));
        this.addUserItem("MP Major Potion", UserItemType.Potion.name(), "Recove major amount of MP", 15, new MPRecoverEffect(75));
        
        this.addUserItem("Stamina Minor Potion", UserItemType.Potion.name(), "Recove minor amount of stamina", 5, new StaminaRecoverEffect(25));
        this.addUserItem("Stamina Medium Potion", UserItemType.Potion.name(), "Recove medium amount of stamina", 10, new StaminaRecoverEffect(50));
        this.addUserItem("Stamina Major Potion", UserItemType.Potion.name(), "Recove major amount of stamina", 15, new StaminaRecoverEffect(75));
        
        this.addUserItem("HP Bonus Potion", UserItemType.Potion.name(), "Add extra max HP", 50, new HPBonusEffect(1));
        this.addUserItem("MP Bonus Potion", UserItemType.Potion.name(), "Add extra max MP", 50, new MPBonusEffect(1));
        this.addUserItem("Stamina Bonus Potion", UserItemType.Potion.name(), "Add extra max stamina", 50, new StaminaBonusEffect(1));
        
        this.addUserItem("Strength Bonus Potion", UserItemType.Potion.name(), "Add extra strength", 100, new StrengthBonusEffect(1));
        this.addUserItem("Construction Bonus Potion", UserItemType.Potion.name(), "Add extra construction", 100, new ConstructionBonusEffect(1));
        this.addUserItem("Dexterity Bonus Potion", UserItemType.Potion.name(), "Add extra dexterity", 100, new DexterityBonusEffect(1));
        this.addUserItem("Intelligence Bonus Potion", UserItemType.Potion.name(), "Add extra intelligence", 100, new IntelligenceBonusEffect(1));
        this.addUserItem("Charisma Bonus Potion", UserItemType.Potion.name(), "Add extra charisma", 100, new CharismaBonusEffect(1));
        this.addUserItem("Willpower Bonus Potion", UserItemType.Potion.name(), "Add extra willpower", 100, new WillpowerBonusEffect(1));
        this.addUserItem("Luck Bonus Potion", UserItemType.Potion.name(), "Add extra luck", 100, new LuckBonusEffect(1));
        this.addUserItem("Speed Bonus Potion", UserItemType.Potion.name(), "Add extra speed", 100, new SpeedBonusEffect(1));
        
        this.addUserItem("Minor Green Potion", UserItemType.Potion.name(), "Recover minor balance", 5, new BalanceResetEffect(25));
        this.addUserItem("Medium Green Potion", UserItemType.Potion.name(), "Recover medium balance", 10, new BalanceResetEffect(50));
        this.addUserItem("Major Green Potion", UserItemType.Potion.name(), "Recover major balance", 15, new BalanceResetEffect(75));
        
        this.addUserItem(GALLERY_TICKET_NAME, UserItemType.Ticket.name(), "It grants you the permission to have the gallery view", 1, new TicketEffect());
    }
    
    public void addUserItem(String name, String type, String description, int value, UserItemEffect effect) {
        UserItem item = this.createUserItem(name, type, description, value, effect);
        this.items.add(item);
    }
    
    public List<UserItem> getUserItems() {
        return this.items;
    }
    
    public UserItem createUserItem(String name, String type, String description, int value, UserItemEffect effect) {
        CommonUserItem item = new CommonUserItem(effect);
        item.setName(name);
        item.setType(type);
        item.setDescription(description);
        item.setValue(value);
        return item;
    }
    
    public static UserItemFactory getInstance() {
        if(instance == null) {
            instance = new UserItemFactory();
        }
        return instance;
    }

	@Override
	public Class<AbstractUserItem> getExtensionClass() {
		return AbstractUserItem.class;
	}

	@Override
	public void created(AbstractUserItem t) {
		if(t != null) {
			this.items.add(t);
		}
	}

	@Override
	public void removed(AbstractUserItem t) {
		if(t != null) {
			this.items.remove(t);
		}
	}
}
