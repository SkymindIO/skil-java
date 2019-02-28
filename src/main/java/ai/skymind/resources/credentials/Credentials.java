package ai.skymind.resources.credentials;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.AddCredentialsRequest;
import ai.skymind.skil.model.ResourceCredentials;

/**
 * Credentials
 *
 * SKIL resource credentials manage cloud provider and other credentials for you.
 * Currently supported credentials are AWS, Azure, GoogleCloud and Hadoop
 *
 * @author Max Pumperla
 */
public class Credentials {

    private Skil skil;
    private AddCredentialsRequest.TypeEnum type;
    private String uri;
    private String name;
    private Long id;

    /**
     *
     * @param skil Skil instance
     * @param type Credentials type
     * @param uri path to credentials
     * @param name Credentials name
     * @throws ApiException SKIL API exception
     */
    public Credentials(Skil skil, AddCredentialsRequest.TypeEnum type, String uri, String name)
            throws ApiException {
        this.skil = skil;
        this.type = type;
        this.uri = uri;
        this.name = name;

        AddCredentialsRequest request = new AddCredentialsRequest()
                .type(this.type)
                .name(this.name)
                .uri(this.uri);
        ResourceCredentials credentials = this.skil.getApi().addCredentials(request);
        this.id = credentials.getCredentialId();
    }

    public Credentials(Skil skil, AddCredentialsRequest.TypeEnum type, String uri, String name, Long credentialsId) {
        this.id = credentialsId;
        this.type = type;
        this.name = name;
        this.uri = uri;
        this.skil = skil;
    }
}
