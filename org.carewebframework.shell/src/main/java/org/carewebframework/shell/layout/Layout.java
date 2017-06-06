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
package org.carewebframework.shell.layout;

import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.common.XMLUtil;
import org.carewebframework.shell.designer.IClipboardAware;
import org.carewebframework.shell.elements.ElementBase;
import org.carewebframework.shell.elements.ElementDesktop;
import org.carewebframework.shell.layout.LayoutElement.LayoutRoot;
import org.carewebframework.shell.plugins.PluginDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents the layout of the visual interface.
 */
public class Layout implements IClipboardAware<Layout> {
    
    private static final Log log = LogFactory.getLog(Layout.class);
    
    private static final String NULL_VALUE = "\\null\\";

    public static final String LAYOUT_VERSION = "4.0";

    private LayoutRoot root;
    
    private String layoutName;
    
    public Layout() {
        clear();
    }
    
    public Layout(Layout layout) {
        this(layout.root);
    }
    
    public Layout(String layoutName) {
        this();
        setName(layoutName);
    }
    
    public Layout(LayoutRoot root) {
        init(root);
    }
    
    /**
     * Materializes the layout, under the specified parent, starting from the layout origin.
     *
     * @param parent Parent UI element at this level of the hierarchy. May be null.
     * @return The UI element created during this pass.
     */
    public ElementBase materialize(ElementBase parent) {
        boolean isDesktop = parent instanceof ElementDesktop;
        
        if (isDesktop) {
            parent.getDefinition().initElement(parent, root);
        }
        
        materializeChildren(parent, root, !isDesktop);
        ElementBase element = parent.getLastVisibleChild();

        if (element != null) {
            element.getRoot().activate(true);
        }
        
        return element;
    }
    
    /**
     * Materializes the layout, under the specified parent.
     *
     * @param parent Parent UI element at this level of the hierarchy. May be null.
     * @param node The current layout element.
     * @param ignoreInternal Ignore internal elements.
     */
    private void materializeChildren(ElementBase parent, LayoutElement node, boolean ignoreInternal) {
        for (LayoutElement child : node.getElements()) {
            PluginDefinition def = child.getDefinition();
            ElementBase element = ignoreInternal && def.isInternal() ? null : def.createElement(parent, child, true);
            
            if (element != null) {
                materializeChildren(element, child, false);
            }
        }
    }
    
    /**
     * Returns the name of the currently loaded layout, or null if none loaded.
     *
     * @return The layout name.
     */
    public String getName() {
        return layoutName;
    }
    
    /**
     * Sets the name of the current layout.
     *
     * @param value New name for the layout.
     */
    public void setName(String value) {
        layoutName = value;

        if (root != null) {
            root.getAttributes().put("name", value);
        }
    }
    
    /**
     * Performs some simple validation of the newly loaded layout.
     *
     * @param root The root layout element.
     */
    private void init(LayoutRoot root) {
        this.root = root;
        layoutName = readString("name", "");
    }
    
    /**
     * Reset the layout to not loaded state.
     */
    private void clear() {
        root = null;
        layoutName = null;
    }
    
    /**
     * Saves the layout as a property value using the specified identifier.
     *
     * @param layoutId Layout identifier
     * @return True if operation succeeded.
     */
    public boolean saveToProperty(LayoutIdentifier layoutId) {
        setName(layoutId.name);
        
        try {
            LayoutUtil.saveLayout(layoutId, toString());
        } catch (Exception e) {
            log.error("Error saving application layout.", e);
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns the class of the element at the root of the layout.
     *
     * @return Class of the element at the root of the layout, or null if none.
     */
    public Class<? extends ElementBase> getRootClass() {
        LayoutElement top = root == null || root.getElements().size() != 1 ? null : root.getElements().get(0);
        return top == null ? null : top.getDefinition().getClazz();
    }
    
    /**
     * Return value of named attribute as a string.
     *
     * @param name Attribute name.
     * @param deflt Default value if not found.
     * @return Value of the attribute.
     */
    private String readString(String name, String deflt) {
        String value = root == null ? deflt : root.hasProperty(name) ? root.getProperty(name) : deflt;
        return NULL_VALUE.equals(value) ? null : value;
    }
    
    /**
     * Returns true if the layout has no content.
     *
     * @return True if the layout has no content.
     */
    public boolean isEmpty() {
        return root == null || root.getElements().isEmpty();
    }
    
    /**
     * Returns the layout as an xml-formatted string.
     */
    @Override
    public String toString() {
        if (root == null) {
            return "";
        }

        try {
            Document doc = XMLUtil.parseXMLFromString("<layout/>");
            Element node = doc.getDocumentElement();
            buildDocument(node, root);
            copyAttributes(root, node);
            node.setAttribute("version", LAYOUT_VERSION);
            node.setAttribute("name", layoutName);
            return XMLUtil.toString(doc);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private void buildDocument(Element node, LayoutElement ele) {
        for (LayoutTrigger trigger : ele.getTriggers()) {
            Element child = createDOMNode(trigger, node);
            child.setAttribute("_condition", trigger.getCondition());
            child.setAttribute("_action", trigger.getAction());
        }
        
        for (LayoutElement element : ele.getElements()) {
            Element child = createDOMNode(element, node);
            child.setAttribute("_type", element.getDefinition().getId());
            buildDocument(child, element);
        }
    }
    
    private Element createDOMNode(LayoutNode layoutNode, Element parentDOMNode) {
        Element child = parentDOMNode.getOwnerDocument().createElement(layoutNode.getTagName());
        copyAttributes(layoutNode, child);
        parentDOMNode.appendChild(child);
        return child;
    }
    
    private void copyAttributes(LayoutNode from, Element to) {
        for (Entry<String, String> entry : from.getAttributes().entrySet()) {
            to.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Converts to clipboard format.
     */
    @Override
    public String toClipboard() {
        return toString();
    }
    
    /**
     * Converts from clipboard format.
     */
    @Override
    public Layout fromClipboard(String data) {
        init(LayoutParser.parseText(data).root);
        return this;
    }
}
