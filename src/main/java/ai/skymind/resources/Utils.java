package ai.skymind.resources;

import ai.skymind.ApiException;
import ai.skymind.Skil;
import ai.skymind.resources.compute.DataProc;
import ai.skymind.resources.compute.EMR;
import ai.skymind.resources.compute.HDInsight;
import ai.skymind.resources.storage.AzureStorage;
import ai.skymind.resources.storage.GoogleStorage;
import ai.skymind.resources.storage.HDFS;
import ai.skymind.resources.storage.S3;
import ai.skymind.skil.model.AddResourceRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Common SKIL resource utility functionality.
 */
public class Utils {

    /**
     * Get all currently registered SKIL resources as List of Resource instances.
     *
     * @return List of Resources
     */
    public static List<Resource> getAllResources(Skil skil) throws ApiException {

        List<ai.skymind.skil.model.Resource> resourceList = skil.getApi().getResources();
        ArrayList<Resource> resources = new ArrayList<>();
        for (ai.skymind.skil.model.Resource res: resourceList) {
            Resource resource = new Resource(skil, res.getResourceId());
            resources.add(resource);
        }
        return resources;
    }

    /**
     * Get a SKIL Resource by ID
     * @param skil Skil instance
     * @param resourceId resource ID
     * @return Resource
     */
    public static Resource getResourceById(Skil skil, Long resourceId) throws ApiException {

        ai.skymind.skil.model.Resource res = skil.getApi().getResourceById(resourceId);
        return new Resource(skil, res.getResourceId());
    }

    /**
     * Get a list of Resource instances by type ('COMPUTE' or 'STORAGE').
     * @param skil Skil instance
     * @param resourceType Resource type enum value
     * @return List of Resource objects
     */
    public static List<Resource> getResourceByType(Skil skil, ResourceType resourceType) throws ApiException {

        String type = resourceType.toString().toLowerCase();
        List<ai.skymind.skil.model.Resource> resourceList =  skil.getApi().getResourceByType(type);

        ArrayList<Resource> resources = new ArrayList<>();
        for (ai.skymind.skil.model.Resource res: resourceList) {
            Resource resource = new Resource(skil, res.getResourceId());
            resources.add(resource);
        }
        return resources;
    }


    /**
     * Get a list of resources by ResourceSubType.
     *
     * @param skil Skil instance
     * @param subType ResourceSubType
     * @return List of Resource objects
     */
    public static List<Resource> getResourceBySubType(Skil skil, ResourceSubType subType) throws ApiException {
        String type = subType.toString().toLowerCase();
        List<ai.skymind.skil.model.Resource> resourceList =  skil.getApi().getResourceBySubType(type);

        ArrayList<Resource> resources = new ArrayList<>();
        for (ai.skymind.skil.model.Resource res: resourceList) {
            Resource resource = new Resource(skil, res.getResourceId());
            resources.add(resource);
        }
        return resources;
    }

    /**
     * Get a concrete resource implementation of
     * an ai.skymind.resources.Resource by resource ID. For instance, if
     * your resource ID corresponds to a resource of subtype "HDFS",
     * this will return an ai.skymind.resources.storage.HDFS object.
     *
     * @param skil Skil instance
     * @param resourceId Valid resource ID
     * @return Resource
     * @throws ApiException
     */
    public static Resource getResourceDetailsById(Skil skil, Long resourceId) throws ApiException, IOException {
        ai.skymind.skil.model.Resource resource = skil.getApi().getResourceById(resourceId);
        ai.skymind.skil.model.Resource.SubTypeEnum resourceType = resource.getSubType();

        //TODO: check casting
        Map<String, Object> details = (Map<String, Object>) skil.getApi().getResourceDetailsById(resourceId);
        Resource res;
        switch (resourceType) {
            case S3:
                res = new S3(skil, resource.getName(), (String) details.get("bucket"),
                        (String) details.get("region"), resourceId);
                break;
            case HDFS:
                res = new HDFS(skil, resource.getName(), (String) details.get("nameNodeHost"),
                        (String) details.get("nameNodePort"), resourceId);
                break;
            case EMR:
                res = new EMR(skil, resource.getName(), (String) details.get("region"),
                        (String) details.get("clusterId"), resourceId);
                break;
            case GOOGLESTORAGE:
                res = new GoogleStorage(skil, resource.getName(), (String) details.get("projectId"),
                        (String) details.get("bucketName"), resourceId);
                break;
            case DATAPROC:
                res = new DataProc(skil, resource.getName(), (String) details.get("projectId"),
                        (String) details.get("region"), (String) details.get("sparkClusterName"), resourceId);
                break;
            case HDINSIGHT:
                res = new HDInsight(skil, resource.getName(), (String) details.get("subscriptionId"),
                        (String) details.get("resourceGroupName"), (String) details.get("clusterName"),
                        resourceId);
                break;
            case AZURESTORAGE:
                res = new AzureStorage(skil, resource.getName(), (String) details.get("containerName"), resourceId);
                break;
            default:
                throw new IOException("Unsupported resource type " + resourceType.toString());

        }
        return res;
    }

}
