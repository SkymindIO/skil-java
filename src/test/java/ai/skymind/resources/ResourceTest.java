package ai.skymind.resources;

import ai.skymind.Skil;
import org.junit.Test;

public class ResourceTest {

    @Test
    public void basicResourceTest() throws Exception {
        Skil skil = new Skil();
        Long id = 1L;
        Resource resource = new Resource(skil);

        Resource idResource = new Resource(skil, id);
    }
}
