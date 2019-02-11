package ai.skymind.resources;

import ai.skymind.ApiException;
import ai.skymind.Skil;

import java.util.ArrayList;
import java.util.List;

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

}
