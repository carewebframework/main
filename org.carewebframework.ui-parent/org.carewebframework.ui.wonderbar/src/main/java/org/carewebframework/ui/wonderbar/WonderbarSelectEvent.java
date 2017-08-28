/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2016 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.ui.wonderbar;

import org.fujion.annotation.EventType;
import org.fujion.annotation.EventType.EventParameter;
import org.fujion.annotation.OnFailure;
import org.fujion.component.BaseComponent;
import org.fujion.event.Event;

/**
 * Fired when an item is selected from the wonder bar.
 */
@EventType(WonderbarSelectEvent.TYPE)
public class WonderbarSelectEvent extends Event {
    
    public static final String TYPE = "wonderbarSelect";
    
    @EventParameter(onFailure = OnFailure.IGNORE)
    private WonderbarItem selectedItem;
    
    @EventParameter(onFailure = OnFailure.IGNORE)
    private int keys;
    
    public WonderbarSelectEvent() {
    }
    
    /**
     * Creates the select event.
     * 
     * @param target Component to receive the event.
     * @param selectedItem The item that triggered the selection event.
     * @param data Arbitrary data to associate with event.
     * @param keys Keypress states at the time of selection.
     */
    public WonderbarSelectEvent(BaseComponent target, WonderbarItem selectedItem, Object data, int keys) {
        super(TYPE, target, data);
        this.selectedItem = selectedItem;
        this.keys = keys;
    }
    
    /**
     * Returns the selected item.
     * 
     * @return The selected item.
     */
    public WonderbarItem getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * Returns the keypress states.
     * 
     * @return The keypress states.
     */
    public int getKeys() {
        return keys;
    }
    
}
