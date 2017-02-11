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
package org.carewebframework.shell.designer;

import org.carewebframework.shell.elements.UIElementBase;
import org.carewebframework.shell.property.PropertyInfo;
import org.carewebframework.web.annotation.WiredComponent;
import org.carewebframework.web.component.Radiobutton;
import org.carewebframework.web.component.Radiogroup;
import org.carewebframework.web.event.ChangeEvent;

/**
 * Editor for boolean values.
 */
public class PropertyEditorBoolean extends PropertyEditorBase<Radiogroup> {
    
    @WiredComponent
    private Radiobutton rbTrue;
    
    @WiredComponent
    private Radiobutton rbFalse;
    
    /**
     * Create property editor.
     */
    public PropertyEditorBoolean() {
        super(DesignConstants.RESOURCE_PREFIX + "propertyEditorBoolean.cwf");
    }
    
    @Override
    protected void init(UIElementBase target, PropertyInfo propInfo, PropertyGrid propGrid) {
        super.init(target, propInfo, propGrid);
        rbTrue.addEventForward(ChangeEvent.TYPE, editor, null);
        
        for (Radiobutton radio : editor.getChildren(Radiobutton.class)) {
            String label = propInfo.getConfigValue(radio.getLabel().trim());
            
            if (label != null) {
                radio.setLabel(label);
            }
        }
    }
    
    /**
     * Sets focus to the selected radio button.
     */
    @Override
    public void setFocus() {
        Radiobutton radio = editor.getSelected();
        
        if (radio == null) {
            radio = (Radiobutton) editor.getChildren().get(0);
        }
        
        radio.setFocus(true);
    }
    
    @Override
    protected Boolean getValue() {
        return rbTrue.isChecked();
    }
    
    @Override
    protected void setValue(Object value) {
        boolean val = value instanceof Boolean && (Boolean) value;
        (val ? rbTrue : rbFalse).setChecked(true);
    }
    
}
