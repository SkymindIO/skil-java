package ai.skymind;

import org.junit.Test;

public class SkilTest {

    @Test
    public void testDefaultServer() throws ApiException, Exception {
        Skil skil = new Skil();
        System.out.println(skil.getDefaultServerId());
    }
}
