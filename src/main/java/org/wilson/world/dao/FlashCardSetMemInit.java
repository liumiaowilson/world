package org.wilson.world.dao;

import java.util.Collections;
import java.util.List;

import org.wilson.world.manager.SignManager;
import org.wilson.world.manager.WordManager;
import org.wilson.world.model.FlashCardSet;

public class FlashCardSetMemInit implements MemInit<FlashCardSet> {
    @Override
    public void init(DAO<FlashCardSet> dao) {
        dao.create(this.createFlashCardSet(WordManager.FLASHCARD_SET_NAME, "Words flash card set"));
        dao.create(this.createFlashCardSet(SignManager.FLASHCARD_SET_NAME, "Signs flash card set"));
    }
    
    private FlashCardSet createFlashCardSet(String name, String description) {
        FlashCardSet set = new FlashCardSet();
        set.name = name;
        set.description = description;
        return set;
    }

    @Override
    public List<QueryTemplate<FlashCardSet>> getQueryTemplates() {
        return Collections.emptyList();
    }
}
