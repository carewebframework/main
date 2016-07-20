#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import static org.junit.Assert.assertEquals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.ui.test.CommonTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for ${cwpName}
 */
public class ${cwpUCC}Test extends CommonTest {
    
    private static final Log log = LogFactory.getLog(${cwpUCC}Test.class);
    
    private static final String HELLO_WORLD = "Hello World";
    
    private String testBean;
    
    /**
     * Unit Test initialization
     */
    @Before
    public final void init() {
        log.info("Initializing Test Class");
        testBean = (String) desktopContext.getBean("testBean");
    }
    
    /**
     * Performs unit test A
     */
    @Test
    public void performUnitTestA() {
        log.info("Performing Unit Test A");
        assertEquals(HELLO_WORLD, testBean);
    }
    
}
