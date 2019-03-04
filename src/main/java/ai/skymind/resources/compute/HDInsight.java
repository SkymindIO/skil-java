package ai.skymind.resources.compute;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.HDInsightResourceDetails;

import java.util.Map;

/** HDInsight
 *
 * Azure HDInsight compute resource.
 *
 * @author Max Pumperla
 */
public class HDInsight extends ComputeResource {

    private String name;
    private String subscriptionId;
    private String resourceGroupName;
    private String clusterName;
    private String credentialUri;

    /**
     *
     * @param skil Skil instance
     * @param name resource name
     * @param subscriptionId Azure subscription ID
     * @param resourceGroupName Azure resource group name
     * @param clusterName HDInsight cluster name
     * @param credentialUri path to credential file
     */
    public HDInsight(Skil skil, String name, String subscriptionId, String resourceGroupName,
                     String clusterName, String credentialUri) throws ApiException {
        super(skil);
        this.name = name;
        this.subscriptionId = subscriptionId;
        this.resourceGroupName = resourceGroupName;
        this.clusterName = clusterName;
        this.credentialUri = credentialUri;

        HDInsightResourceDetails details = new HDInsightResourceDetails()
                .clusterName(this.clusterName)
                .subscriptionId(this.subscriptionId)
                .resourceGroupName(this.resourceGroupName);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details.toString())
                .type(AddResourceRequest.TypeEnum.COMPUTE)
                .subType(AddResourceRequest.SubTypeEnum.HDINSIGHT);

        Object response = this.skil.getApi().addResource(request);

        this.resourceId = (Long) ((Map<String, Object>) response).get("resourceId");
    }

    public HDInsight(Skil skil, String name, String subscriptionId, String resourceGroupName,
                     String clusterName, Long resourceId) {
        super(skil, resourceId);
        this.name = name;
        this.subscriptionId = subscriptionId;
        this.resourceGroupName = resourceGroupName;
        this.clusterName = clusterName;
    }

    public String getName(){
        return this.name;
    }

    public String getSubscriptionId(){
        return this.subscriptionId;
    }

    public String geteResourceGroupName(){
        return this.resourceGroupName;
    }

    public String getClusterName(){
        return this.clusterName;
    }

    public String getCredentialId(){
        return this.credentialUri;
    }
}
