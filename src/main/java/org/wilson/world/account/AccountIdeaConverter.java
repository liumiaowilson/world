package org.wilson.world.account;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.AccountManager;
import org.wilson.world.model.Account;
import org.wilson.world.model.Idea;

public class AccountIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Account";
    }

    @Override
    public Object convert(Idea idea) {
        Account account = new Account();
        account.name = idea.name;
        account.description = idea.content;
        account.identifier = "Undefined";
        account.amount = 0;
        return account;
    }

    @Override
    public void save(Object converted) {
        AccountManager.getInstance().createAccount((Account) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToAccount;
    }

}
