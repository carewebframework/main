/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.sharedforms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.NumUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.command.CommandUtil;
import org.carewebframework.ui.zk.SplitterPane;
import org.carewebframework.ui.zk.SplitterView;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

/**
 * Controller for list view based forms.
 */
public abstract class ListViewForm extends CaptionedForm {
    
    private static final long serialVersionUID = 1L;
    
    private static final String SORT_TYPE_ATTR = "@sort_type";
    
    private static final String COL_INDEX_ATTR = "@col_index";
    
    private static final String SIZE_ATTR = "@size";
    
    private SplitterView mainView;
    
    private Listbox listbox;
    
    private Listhead listhead;
    
    private SplitterPane detailPane;
    
    private SplitterPane listPane;
    
    private Auxheader status;
    
    private boolean allowPrint;
    
    private String alternateColor = "#F0F0F0";
    
    private int colCount;
    
    private String dataName;
    
    private boolean deferUpdate = true;
    
    private boolean dataNeedsUpdate = true;
    
    private int sortColumn;
    
    private boolean sortAscending;
    
    protected final List<String> itemList = new ArrayList<String>();
    
    private final EventListener<SortEvent> sortListener = new EventListener<SortEvent>() {
        
        @Override
        public void onEvent(SortEvent event) throws Exception {
            sortAscending = event.isAscending();
            sortColumn = (Integer) event.getTarget().getAttribute(COL_INDEX_ATTR);
        }
        
    };
    
    @Override
    protected void init() {
        super.init();
        root = detailPane;
        setSize(50);
        CommandUtil.associateCommand("REFRESH", listbox);
        getContainer().registerProperties(this, "allowPrint", "alternateColor", "deferUpdate", "showDetailPane", "layout",
            "horizontal");
    }
    
    public void destroy() {
        asyncAbort();
    }
    
    protected void setup(String title, String... headers) {
        setup(title, 1, headers);
    }
    
    protected void setup(String title, int sortBy, String... headers) {
        setCaption(title);
        dataName = title;
        String w = Integer.toString(100 / headers.length) + "%";
        
        for (String header : headers) {
            String[] pcs = StrUtil.split(header, StrUtil.U, 2);
            Listheader lhdr = new Listheader();
            listhead.appendChild(lhdr);
            lhdr.setAttribute(SORT_TYPE_ATTR, NumberUtils.toInt(pcs[1]));
            lhdr.setAttribute(COL_INDEX_ATTR, colCount++);
            lhdr.setLabel(pcs[0]);
            lhdr.setWidth(w);
            lhdr.addEventListener(Events.ON_SORT, sortListener);
        }
        
        sortColumn = Math.abs(sortBy) - 1;
        sortAscending = sortBy > 0;
        doSort();
    }
    
    public boolean getHorizontal() {
        return mainView.isHorizontal();
    }
    
    public void setHorizontal(boolean value) {
        mainView.setHorizontal(value);
    }
    
    public String getAlternateColor() {
        return alternateColor;
    }
    
    public void setAlternateColor(String value) {
        this.alternateColor = value;
    }
    
    public boolean getShowDetailPane() {
        return detailPane.isVisible();
    }
    
    public void setShowDetailPane(boolean value) {
        if (getShowDetailPane() != value) {
            if (value) {
                listPane.setRelativeSize(getSize());
            } else {
                setSize(listPane.getRelativeSize());
                listPane.setRelativeSize(100);
            }
            
            detailPane.setVisible(value);
        }
    }
    
    public boolean getAllowPrint() {
        return allowPrint;
    }
    
    public void setAllowPrint(boolean value) {
        this.allowPrint = value;
    }
    
    public boolean getDeferUpdate() {
        return deferUpdate;
    }
    
    public void setDeferUpdate(boolean value) {
        this.deferUpdate = value;
    }
    
    /**
     * Getter method for Layout property. Format:
     * 
     * <pre>
     *   List Pane Size:Sort Column:Sort Direction;Column 0 Index:Column 0 Width;...
     * @return
     */
    public String getLayout() {
        StringBuilder sb = new StringBuilder();
        sb.append(NumUtil.toString(getSize())).append(':').append(sortColumn).append(':').append(sortAscending);
        
        for (Component comp : listhead.getChildren()) {
            Listheader lhdr = (Listheader) comp;
            sb.append(';').append(lhdr.getAttribute(COL_INDEX_ATTR)).append(':').append(lhdr.getWidth());
        }
        return sb.toString();
    }
    
    /**
     * Setter method for Layout property. This property allows an application to control the
     * position of the splitter bar and ordering of columns.
     * 
     * @param layout
     */
    public void setLayout(String layout) {
        String[] pcs = StrUtil.split(layout, ";");
        
        if (pcs.length > 0) {
            String[] spl = StrUtil.split(pcs[0], ":", 3);
            setSize(NumberUtils.toInt(spl[0]));
            sortColumn = NumberUtils.toInt(spl[1]);
            sortAscending = BooleanUtils.toBoolean(spl[2]);
        }
        
        for (int i = 1; i < pcs.length; i++) {
            String[] col = StrUtil.split(pcs[i], ":", 2);
            Listheader lhdr = getColumnByIndex(NumberUtils.toInt(col[0]));
            
            if (lhdr != null) {
                lhdr.setWidth(col[1]);
                ZKUtil.moveChild(lhdr, i - 1);
            }
        }
        
        doSort();
    }
    
    private double getSize() {
        return (Double) listPane.getAttribute(SIZE_ATTR);
    }
    
    private void setSize(double value) {
        listPane.setAttribute(SIZE_ATTR, value);
    }
    
    private void doSort() {
        getColumnByIndex(sortColumn).sort(sortAscending);
    }
    
    /**
     * Returns the column corresponding to the specified index.
     * 
     * @param index
     * @return
     */
    private Listheader getColumnByIndex(int index) {
        for (Component comp : listhead.getChildren()) {
            if (((Integer) comp.getAttribute(COL_INDEX_ATTR)).intValue() == index) {
                return (Listheader) comp;
            }
        }
        
        return null;
    }
    
    /**
     * Clears the list and status.
     */
    protected void reset() {
        itemList.clear();
        listbox.getItems().clear();
        status(null);
    }
    
    protected Listitem getSelectedItem() {
        return listbox.getSelectedItem();
    }
    
    /**
     * Abort any pending async call.
     */
    protected abstract void asyncAbort();
    
    /**
     * Async request to fetch list data.
     */
    protected abstract void fetchList();
    
    /**
     * Initiate asynchronous call to retrieve data from host.
     */
    protected void loadList() {
        dataNeedsUpdate = false;
        asyncAbort();
        reset();
        status("Retrieving " + dataName + "...");
        
        try {
            fetchList();
        } catch (Throwable t) {
            status("Error Retrieving " + dataName + "...^" + t.getMessage());
        }
    }
    
    /**
     * Render the list view from the current item list.
     */
    protected void renderList() {
        listbox.getItems().clear();
        
        for (String item : itemList) {
            if (isError(item)) {
                return;
            }
            addItem(item);
        }
        
        if (listbox.getItems().isEmpty()) {
            status("No " + dataName + " Found");
        } else {
            status(null);
        }
        
        alphaSort();
    }
    
    /**
     * Returns true if data represents an error.
     * 
     * @param data
     * @return
     */
    private boolean isError(String data) {
        String error = getError(data);
        
        if (error != null) {
            status(error);
            return true;
        }
        
        return false;
        
    }
    
    /**
     * Add an item to the list view.
     * 
     * @param data Item data, each column delimited by '^'.
     * @return The newly added list item.
     */
    protected Listitem addItem(String data) {
        Listitem li = null;
        String fmt = data.isEmpty() ? data : formatData(data);
        
        if (!fmt.isEmpty()) {
            li = new Listitem();
            listbox.appendChild(li);
            li.setValue(data);
            li.addForward(Events.ON_CLICK, listbox, Events.ON_SELECT);
            
            for (String pc : StrUtil.split(fmt, StrUtil.U)) {
                Listcell cell = new Listcell(pc);
                li.appendChild(cell);
            }
        }
        
        return li;
    }
    
    /**
     * Format data before processing. Default action is to return original.
     * 
     * @param data
     * @return
     */
    protected String formatData(String data) {
        return data;
    }
    
    /**
     * Return error message if data represents an error. Otherwise, returns null.
     * 
     * @param data
     * @return
     */
    protected String getError(String data) {
        return null;
    }
    
    protected void alphaSort() {
        
    }
    
    /**
     * Forces an update of displayed list.
     */
    @Override
    public void refresh() {
        dataNeedsUpdate = true;
        
        if (!deferUpdate || isActive()) {
            loadList();
        } else {
            reset();
        }
    }
    
    protected void status(String message) {
        if (message != null) {
            status.setLabel(StrUtil.piece(message, StrUtil.U));
            status.setTooltiptext(StrUtil.piece(message, StrUtil.U, 2, 999));
            status.setVisible(true);
            listhead.setVisible(false);
        } else {
            status.setVisible(false);
            listhead.setVisible(true);
        }
    }
    
    public void onClick$mnuRefresh() {
        refresh();
    }
    
    public void onCommand$listbox() {
        refresh();
    }
    
    public void onSelect$listbox(Event event) {
        event = ZKUtil.getEventOrigin(event);
        
        if (getShowDetailPane() == (event instanceof SelectEvent)) {
            itemSelected(getSelectedItem());
        }
    }
    
    /**
     * Called when an item is selected. Override for specialized handling.
     * 
     * @param li Selected list item.
     */
    protected void itemSelected(Listitem li) {
        
    }
    
    @Override
    public void onActivate() {
        super.onActivate();
        
        if (dataNeedsUpdate) {
            loadList();
        }
    }
    
}