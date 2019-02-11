package ai.skymind.resources.compute;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.EMRResourceDetails;

/** EMR
 *
 * AWS Elastic Map Reduce compute resource
 *
 * @author Max Pumperla
 */
public class EMR extends Resource {

    private String name;
    private String region;
    private String credentialUri;
    private String clusterId;

    /**
     * @param skil Skil instance
     * @param name Name of the resource
     * @param region Name of the AWS region
     * @param credentialUri Path to the AWS credential file
     * @param clusterId EMR cluster ID
     */
    public EMR(Skil skil, String name, String region, String credentialUri, String clusterId) throws ApiException {
        super(skil);
        this.name = name;
        this.region = region;
        this.credentialUri = credentialUri;
        this.clusterId = clusterId;

        EMRResourceDetails details = new EMRResourceDetails()
                .clusterId(this.clusterId)
                .region(this.region);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .resourceDetails(details.toString())
                .credentialUri(this.credentialUri)
                .type(AddResourceRequest.TypeEnum.COMPUTE)
                .subType(AddResourceRequest.SubTypeEnum.EMR);

        this.skil.getApi().addResource(request);

    }
}
