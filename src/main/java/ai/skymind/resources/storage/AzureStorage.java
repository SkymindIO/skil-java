package ai.skymind.resources.storage;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.AzureStorageResourceDetails;

import java.util.Map;

/**AzureStorage
 *
 * SKIL Azure container storage resource.
 *
 * @author Max Pumperla
 */
public class        AzureStorage extends StorageResource {

    private String name;
    private String credentialUri;
    private String containerName;

    /**
     *
     * @param skil Skil instance
     * @param name Resource name
     * @param containerName Azure container name
     * @param credentialUri path to credentials file
     */
    public AzureStorage(Skil skil, String name, String containerName, String credentialUri)
            throws ApiException {
        super(skil);
        this.name = name;
        this.credentialUri = credentialUri;
        this.containerName = containerName;

        AzureStorageResourceDetails details = new AzureStorageResourceDetails()
                .containerName(this.containerName);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details)
                .type(AddResourceRequest.TypeEnum.STORAGE)
                .subType(AddResourceRequest.SubTypeEnum.AZURESTORAGE);

        Object response = this.skil.getApi().addResource(request);

        this.resourceId = ((Double) ((Map<String, Object>) response).get("resourceId")).longValue();
    }

    public AzureStorage(Skil skil, String name, String containerName, Long resourceId) {
        super(skil, resourceId);
        this.name = name;
        this.containerName = containerName;
    }

    public String getName(){
        return this.name;
    }

    public String getContainerName(){
        return this.containerName;
    }

    public String getCredentialUri(){
        return this.credentialUri;
    }
}
