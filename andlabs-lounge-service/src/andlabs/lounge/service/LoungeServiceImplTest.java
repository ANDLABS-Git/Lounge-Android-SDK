/**
 * 
 */
package andlabs.lounge.service;

import android.os.Message;
import android.os.RemoteException;
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
        try {
            classUnderTest.connect();
        } catch (RemoteException e) {
            e.printStackTrace();
            fail("Received " + e.getLocalizedMessage());
        }
        fail("Test failes only if a response does not come within a certain time. How to make the test wait?");
    }


    /**
     * Test method for {@link andlabs.lounge.service.LoungeServiceImpl#login(java.lang.String)}.
     */
    public void testLogin() {
        fail("Not yet implemented");
    }

}
