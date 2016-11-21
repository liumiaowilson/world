package org.wilson.world.novel;

import java.util.List;

import org.wilson.world.ext.Scriptable;

public interface NovelRoleReportBuilder {
    public static final String EXTENSION_POINT = "novel_role.reports";
    
    @Scriptable(name = EXTENSION_POINT, description = "Build the reports of the novel roles.", params = {})
    public List<NovelRoleReport> build();
}
