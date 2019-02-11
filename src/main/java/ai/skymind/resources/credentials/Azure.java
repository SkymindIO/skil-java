package ai.skymind.resources.credentials;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.AddCredentialsRequest;

public class Azure extends Credentials {

    public Azure(Skil skil, String uri, String name) throws ApiException {
        super(skil, AddCredentialsRequest.TypeEnum.AZURE, uri, name);
    }
}
