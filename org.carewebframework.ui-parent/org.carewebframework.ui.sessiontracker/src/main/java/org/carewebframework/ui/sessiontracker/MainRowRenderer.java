/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.sessiontracker;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.api.domain.IInstitution;
import org.carewebframework.api.domain.IUser;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.Application;
import org.carewebframework.ui.Application.DesktopInfo;
import org.carewebframework.ui.Application.SessionInfo;
import org.carewebframework.ui.spring.FrameworkAppContext;
import org.carewebframework.ui.zk.AbstractRowRenderer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

/**
 * RowRenderer to define rows within the Session/Desktop Tracking Grid
 */
public class MainRowRenderer extends AbstractRowRenderer<SessionInfo, Object> {
    
    private static final String[] DETAIL_COL_WIDTHS = { "12%", "10%", "10%", "38%", "15%", "15%" };
    
    private static final String[] DETAIL_COL_LABELS = { "@cwf.sessiontracker.detail.col1.label",
            "@cwf.sessiontracker.detail.col2.label", "@cwf.sessiontracker.detail.col3.label",
            "@cwf.sessiontracker.detail.col4.label", "@cwf.sessiontracker.detail.col5.label",
            "@cwf.sessiontracker.detail.col6.label" };
    
    private static final Log log = LogFactory.getLog(MainRowRenderer.class);
    
    /**
     * @see AbstractRowRenderer#renderRow
     */
    @Override
    protected Component renderRow(final Row row, final SessionInfo sInfo) {
        final Session session = sInfo == null ? null : sInfo.getSession();
        final HttpSession nativeSession = session == null ? null : (HttpSession) session.getNativeSession();
        //Because it's possible that the session could be invalidated but yet still in the list
        String sessionId = null;
        String institution = StrUtil.formatMessage("@cwf.sessiontracker.msg.unknown");
        Date creationTime = null;
        Date lastAccessedTime = null;
        int maxInactiveInterval = 0;
        String clientAddress = null;
        
        try {
            if (nativeSession != null) {
                sessionId = nativeSession.getId();
                creationTime = new Date(nativeSession.getCreationTime());
                lastAccessedTime = new Date(nativeSession.getLastAccessedTime());
                maxInactiveInterval = nativeSession.getMaxInactiveInterval();
                clientAddress = session.getRemoteAddr();
            }
        } catch (final IllegalStateException e) {
            log.warn("The following session was still in the list of activeSessions yet was invalidated: " + session);
            return null;
        }
        
        createCell(row, sessionId);
        createCell(row, clientAddress);
        createCell(row, institution);
        createCell(row, creationTime);
        createCell(row, lastAccessedTime);
        createCell(row, String.valueOf(maxInactiveInterval));
        return sInfo == null || sInfo.getDesktops().isEmpty() ? null : row;
    }
    
    @Override
    protected void renderDetail(Detail detail, SessionInfo sInfo) {
        detail.setOpen(true);
        Grid detailGrid = createDetailGrid(detail, DETAIL_COL_WIDTHS, DETAIL_COL_LABELS);
        Rows detailRows = detailGrid.getRows();
        
        for (Desktop desktop : sInfo.getDesktops()) {
            final DesktopInfo desktopInfo = Application.getDesktopInfo(desktop);
            final ClientInfoEvent clientInfo = desktopInfo == null ? null : desktopInfo.getClientInformation();
            final String screenDimensions = clientInfo == null ? "" : (clientInfo.getScreenWidth() + "x" + clientInfo
                    .getScreenHeight());
            final IUser user = getUser(desktop);
            final IInstitution inst = user == null ? null : user.getInstitution();
            String usr = user == null ? StrUtil.formatMessage("@cwf.sessiontracker.msg.unknown")
                    : (user.getFullName() + (inst == null ? "" : "@" + inst.getName()));
            final Row detailRow = new Row();
            detailRow.setParent(detailRows);
            createCell(detailRow, desktop.getId());
            createCell(detailRow, desktop.getDeviceType());
            createCell(detailRow, usr);
            createCell(detailRow, desktopInfo == null ? "" : desktopInfo.getUserAgent());
            createCell(detailRow, clientInfo == null ? "" : clientInfo.getTimeZone().getDisplayName());
            createCell(detailRow, screenDimensions);
        }
    }
    
    private IUser getUser(Desktop desktop) {
        try {
            final FrameworkAppContext ctx = FrameworkAppContext.getAppContext(desktop);
            final UserContext uctx = ctx == null ? null : ctx.getBean("userContext", UserContext.class);
            return uctx == null ? null : uctx.getContextObject(false);
        } catch (Exception e) {
            return null;
        }
    }
    
}