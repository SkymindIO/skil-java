package ai.skymind.resources.credentials;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.AddCredentialsRequest;

public class GoogleCloud extends Credentials {

    public GoogleCloud(Skil skil, String uri, String name) throws ApiException {
        super(skil, AddCredentialsRequest.TypeEnum.GOOGLECLOUD, uri, name);
    }
}
