package ai.skymind.resources.credentials;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.AddCredentialsRequest;

public class Hadoop extends Credentials {

    public Hadoop(Skil skil, String uri, String name) throws ApiException {
        super(skil, AddCredentialsRequest.TypeEnum.HADOOP, uri, name);
    }
}
