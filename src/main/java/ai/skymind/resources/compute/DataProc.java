package ai.skymind.resources.compute;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.DataProcResourceDetails;

import java.util.Map;

/**
 * DataProc
 *
 * Google cloud engine DataProc compute resource
 *
 * @author Max Pumperla
 */
public class DataProc extends Resource {

    private String name;
    private String projectId;
    private String region;
    private String clusterName;
    private String credentialUri;

    /**
     *
     * @param skil SKIL instance
     * @param name Resource name
     * @param projectId GCE project ID
     * @param region GCE region
     * @param clusterName DataProc cluster name
     * @param credentialUri Path to credential file

     */
    public DataProc(Skil skil, String name, String projectId, String region, String clusterName,
                    String credentialUri) throws ApiException {

        super(skil);
        this.name = name;
        this.projectId = projectId;
        this.region = region;
        this.clusterName = clusterName;
        this.credentialUri = credentialUri;

        DataProcResourceDetails details = new DataProcResourceDetails()
                .projectId(this.projectId)
                .region(this.region)
                .sparkClusterName(this.clusterName);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details.toString())
                .type(AddResourceRequest.TypeEnum.COMPUTE)
                .subType(AddResourceRequest.SubTypeEnum.DATAPROC);

        Object response = this.skil.getApi().addResource(request);

        // TODO test if this casting works
        this.resourceId = (Long) ((Map<String, Object>) response).get("resourceId");
    }

    public DataProc(Skil skil, Long resourceId) {
        super(skil, resourceId);
    }
}
