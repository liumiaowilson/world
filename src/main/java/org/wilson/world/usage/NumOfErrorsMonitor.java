package org.wilson.world.usage;

import java.util.List;

import org.wilson.world.manager.ErrorInfoManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.ErrorInfo;
import org.wilson.world.monitor.MonitorParticipant;

public class NumOfErrorsMonitor implements MonitorParticipant {
    private Alert alert;
    
    public NumOfErrorsMonitor() {
        this.alert = new Alert();
        this.alert.id = "ERRORS";
        this.alert.message = "There are errors in the system.";
        this.alert.url = "error_info_list.jsp";
    }

    @Override
    public boolean isOK() {
        List<ErrorInfo> infos = ErrorInfoManager.getInstance().getErrorInfos();
        return infos.isEmpty();
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }
}
