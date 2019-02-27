package ai.skymind;

import ai.skymind.skil.DefaultApi;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SkilTest {

    @Test
    public void testCreation() throws Exception {
        Skil skil = new Skil();
        assert ! skil.getDefaultServerId().isEmpty();
    }

    @Test
    public void testCreationPass() throws Exception {
        Skil skil = new Skil("localhost", 9008, "admin", "admin", false);
        assertFalse(skil.getDefaultServerId().isEmpty());

        assertTrue(skil.getHost().equals("localhost"));
        assertTrue(skil.getPort() == 9008);
        DefaultApi api = skil.getApi();
    }
}
