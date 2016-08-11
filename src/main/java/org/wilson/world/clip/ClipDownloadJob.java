package org.wilson.world.clip;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.ClipManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class ClipDownloadJob extends SystemWebJob {

    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        List<ClipInfo> infos = (List<ClipInfo>) WebManager.getInstance().get(ClipListJob.CLIP_LIST);
        if(infos != null && !infos.isEmpty()) {
            int n = DiceManager.getInstance().random(infos.size());
            ClipInfo info = infos.get(n);
            
            if(StringUtils.isBlank(info.url) || StringUtils.isBlank(info.name)) {
                return;
            }
            
            ClipManager.getInstance().downloadClipInfo(info, this.getMonitor());
        }
    }

}
