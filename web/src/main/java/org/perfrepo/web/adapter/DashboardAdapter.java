package org.perfrepo.web.adapter;

import org.perfrepo.dto.dashboard.DashboardContent;

/**
 * Service adapter for dashboard.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface DashboardAdapter {
    /**
     * Return content of dashboard page (last test executions, favourite reports...)
     *
     * @return dashboard content
     */
    DashboardContent getDashboardContent();
}