package ai.skymind.resources.storage;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.GoogleStorageResourceDetails;

import java.util.Map;

/**GoogleStorage
 *
 * SKIL Google storage resource.
 *
 * @author Max Pumperla
 */
public class GoogleStorage extends StorageResource {

    private String name;
    private String credentialUri;
    private String projectId;
    private String bucketName;

    /**
     *
     * @param skil Skil instance
     * @param name Resource name
     * @param projectId GCE project ID
     * @param bucketName GCE bucket name
     * @param credentialUri path to credentials file
     */
    public GoogleStorage(Skil skil, String name, String projectId, String bucketName, String credentialUri)
            throws ApiException {
        super(skil);
        this.name = name;
        this.credentialUri = credentialUri;
        this.projectId = projectId;
        this.bucketName = bucketName;

        GoogleStorageResourceDetails details = new GoogleStorageResourceDetails()
                .projectId(this.projectId)
                .bucketName(this.bucketName);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details.toString())
                .type(AddResourceRequest.TypeEnum.STORAGE)
                .subType(AddResourceRequest.SubTypeEnum.GOOGLESTORAGE);

        Object response = this.skil.getApi().addResource(request);

        // TODO test if this casting works
        this.resourceId = (Long) ((Map<String, Object>) response).get("resourceId");
    }

    /**
     *
     * @param skil Skil instance
     * @param name resource name
     * @param projectId GCE project ID
     * @param bucketName GCE bucket name
     * @param resourceId SKIL resource ID
     */
    public GoogleStorage(Skil skil, String name, String projectId, String bucketName, Long resourceId) {
        super(skil, resourceId);
        this.name = name;
        this.projectId = projectId;
        this.bucketName = bucketName;
    }
}
