package ai.skymind.resources.storage;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.S3ResourceDetails;

import java.util.Map;

/**S3
 *
 * SKIL S3 resource.
 *
 * @author Max Pumperla
 */
public class S3 extends StorageResource {

    private String name;
    private String bucket;
    private String region;
    private String credentialUri;

    /**
     *
     * @param skil Skil instance
     * @param name Resource name
     * @param bucket AWS S3 bucket
     * @param region AWS region
     * @param credentialUri path to credentials file
     */
    public S3(Skil skil, String name, String bucket, String region, String credentialUri) throws ApiException {
        super(skil);
        this.name = name;
        this.bucket = bucket;
        this.region = region;
        this.credentialUri = credentialUri;

        S3ResourceDetails details = new S3ResourceDetails()
                .bucket(this.bucket)
                .region(this.region);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details.toString())
                .type(AddResourceRequest.TypeEnum.STORAGE)
                .subType(AddResourceRequest.SubTypeEnum.S3);

        Object response = this.skil.getApi().addResource(request);

        // TODO test if this casting works
        this.resourceId = (Long) ((Map<String, Object>) response).get("resourceId");
    }

    /**
     *
     * @param skil SKIL instance
     * @param name resource name
     * @param bucket S3 bucket
     * @param region AWS region
     * @param resourceId SKIL resource ID
     */
    public S3(Skil skil, String name, String bucket, String region, Long resourceId) {
        super(skil, resourceId);
        this.name = name;
        this.bucket = bucket;
        this.region = region;
    }
}
