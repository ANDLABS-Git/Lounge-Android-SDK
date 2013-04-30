/*
 * Copyright (C) 2012, 2013 ANDLABS GmbH. All rights reserved.
 *
 * www.lounge.andlabs.com
 * lounge@andlabs.com
 * 
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
 */

package andlabs.lounge.service;

import android.os.Message;
import junit.framework.Assert;
import junit.framework.TestCase;


/**
 * @author drasko
 *
 */
public class LoungeServiceImplTest extends TestCase {

    private LoungeServiceImpl classUnderTest;
    /**
     * @param name
     */
    public LoungeServiceImplTest(String name) {
        super(name);
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        classUnderTest = new LoungeServiceImpl();
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Test method for {@link andlabs.lounge.service.LoungeServiceImpl#connect()}.
     */
    public void testConnect() {
        classUnderTest.setMessageHandler(new LoungeServiceImpl.MessageHandler() {
            
            @Override
            public void send(Message message) {
                Assert.assertEquals(0, message.what);
            }

        });
        classUnderTest.connect();
        fail("Test failes only if a response does not come within a certain time. How to make the test wait?");
    }


    /**
     * Test method for {@link andlabs.lounge.service.LoungeServiceImpl#login(java.lang.String)}.
     */
    public void testLogin() {
        fail("Not yet implemented");
    }

}
