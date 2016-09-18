package org.wilson.world.etiquette;

import org.wilson.world.manager.EtiquetteManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractEtiquetteJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        EtiquetteInfo selected = EtiquetteManager.getInstance().getSelected();
        if(selected == null) {
            EtiquetteManager.getInstance().clear(this.getFrom());
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(EtiquetteInfo info) throws Exception;
}
