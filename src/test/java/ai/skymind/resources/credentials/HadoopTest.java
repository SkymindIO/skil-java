package ai.skymind.resources.credentials;

import ai.skymind.Skil;
import org.junit.Test;

public class HadoopTest {

    @Test
    public void createCredentials() throws Exception {
        Skil skil = new Skil();
        String uri = "The Bends";
        String name = "Planet Telex";

        Credentials credentials = new Hadoop(skil, uri, name);

    }
}
