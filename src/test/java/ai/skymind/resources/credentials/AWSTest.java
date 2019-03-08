package ai.skymind.resources.credentials;

import ai.skymind.Skil;
import org.junit.Test;

public class AWSTest {

    @Test
    public void createCredentials() throws Exception {
        Skil skil = new Skil();
        String uri = "The Bends";
        String name = "Bullet Proof ... I Wish I Was";

        Credentials credentials = new AWS(skil, uri, name);

    }
}
