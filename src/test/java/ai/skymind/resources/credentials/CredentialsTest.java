package ai.skymind.resources.credentials;

import ai.skymind.Skil;
import ai.skymind.skil.model.AddCredentialsRequest;
import org.junit.Test;

public class CredentialsTest {

    @Test
    public void createCredentials() throws Exception {
        Skil skil = new Skil();
        AddCredentialsRequest.TypeEnum type = AddCredentialsRequest.TypeEnum.AWS;
        String uri = "The Bends";
        String name = "Fake Plastic Trees";

        Credentials credentials = new Credentials(skil, type, uri, name);

        Long id = 1L;
        Credentials idCredentials = new Credentials(skil, type, uri, name, id);
    }
}
