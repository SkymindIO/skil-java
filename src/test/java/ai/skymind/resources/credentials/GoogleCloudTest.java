package ai.skymind.resources.credentials;

import ai.skymind.Skil;
import org.junit.Test;

public class GoogleCloudTest {

    @Test
    public void createCredentials() throws Exception {
        Skil skil = new Skil();
        String uri = "The Bends";
        String name = "High and Dry";

        Credentials credentials = new GoogleCloud(skil, uri, name);

    }
}
