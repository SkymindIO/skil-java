package ai.skymind.resources.storage;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.Resource;
import ai.skymind.skil.model.AddResourceRequest;
import ai.skymind.skil.model.HDFSResourceDetails;

import java.util.Map;

/**HDFS
 *
 * SKIL HDFS resource.
 *
 * @author Max Pumperla
 */
public class HDFS extends StorageResource {

    private String name;
    private String nameNodeHost;
    private String nameNodePort;
    private String credentialUri;

    /**
     *
     * @param skil Skil instance
     * @param name Resource name
     * @param nameNodeHost name node host url
     * @param nameNodePort name node port
     * @param credentialUri path to credentials file
     */
    public HDFS(Skil skil, String name, String nameNodeHost, String nameNodePort, String credentialUri)
            throws ApiException {
        super(skil);
        this.name = name;
        this.nameNodeHost = nameNodeHost;
        this.nameNodePort = nameNodePort;
        this.credentialUri = credentialUri;

        HDFSResourceDetails details = new HDFSResourceDetails()
                .nameNodeHost(this.nameNodeHost)
                .nameNodePort(this.nameNodePort);

        AddResourceRequest request = new AddResourceRequest()
                .resourceName(this.name)
                .credentialUri(this.credentialUri)
                .resourceDetails(details)
                .type(AddResourceRequest.TypeEnum.STORAGE)
                .subType(AddResourceRequest.SubTypeEnum.HDFS);

        Object response = this.skil.getApi().addResource(request);

        this.resourceId = ((Double) ((Map<String, Object>) response).get("resourceId")).longValue();
    }

    /**
     *
     * @param skil Skil instance
     * @param name Resource name
     * @param nameNodeHost name node host url
     * @param nameNodePort name node port
     * @param resourceId SKIL resource ID
     */
    public HDFS(Skil skil, String name, String nameNodeHost, String nameNodePort, Long resourceId) {
        super(skil, resourceId);
        this.name = name;
        this.nameNodeHost = nameNodeHost;
        this.nameNodePort = nameNodePort;
    }

    public String getName(){
        return this.name;
    }

    public String getNameNodePort(){
        return this.nameNodePort;
    }

    public String getNameNodeHost(){
        return this.nameNodeHost;
    }

    public String getCredentialId(){
        return this.credentialUri;
    }
}
