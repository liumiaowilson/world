package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Faq;

public class FaqManager implements ItemTypeProvider {
    public static final String NAME = "faq";
    
    private static FaqManager instance;
    
    private DAO<Faq> dao = null;
    
    @SuppressWarnings("unchecked")
    private FaqManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Faq.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static FaqManager getInstance() {
        if(instance == null) {
            instance = new FaqManager();
        }
        return instance;
    }
    
    public void createFaq(Faq faq) {
        this.dao.create(faq);
    }
    
    public Faq getFaq(int id) {
        Faq faq = this.dao.get(id);
        if(faq != null) {
            return faq;
        }
        else {
            return null;
        }
    }
    
    public List<Faq> getFaqs() {
        List<Faq> result = new ArrayList<Faq>();
        for(Faq faq : this.dao.getAll()) {
            result.add(faq);
        }
        return result;
    }
    
    public void updateFaq(Faq faq) {
        this.dao.update(faq);
    }
    
    public void deleteFaq(int id) {
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
        return target instanceof Faq;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Faq faq = (Faq)target;
        return String.valueOf(faq.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
