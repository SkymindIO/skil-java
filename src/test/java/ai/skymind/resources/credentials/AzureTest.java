package ai.skymind.resources.credentials;

import ai.skymind.Skil;
import org.junit.Test;

public class AzureTest {

    @Test
    public void createCredentials() throws Exception {
        Skil skil = new Skil();
        String uri = "The Bends";
        String name = "My Iron Lung";

        Credentials credentials = new Azure(skil, uri, name);

    }
}
