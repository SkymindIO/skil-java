package ai.skymind.resources;

import ai.skymind.ApiException;
import ai.skymind.Skil;

/** ResourceGroup
 *
 * SKIL resource groups can be used to group skil.resources.base.Resource instances
 * into logical groups. You first create a group and then add resources to the group
 * later on, using `addResource`.
 *
 * @author Max Pumperla
 */
public class ResourceGroup {

    private Skil skil;
    private String groupName;
    private Long groupId;

    /**
     * Create new Resource group
     *
     * @param skil Skil instance
     * @param groupName Name of the resource group
     * @throws ApiException SKIL API exception
     */
    public ResourceGroup(Skil skil, String groupName) throws ApiException {
        this.skil = skil;
        this.groupName = groupName;

        ai.skymind.skil.model.ResourceGroup group = this.skil.getApi().addResourceGroup(this.groupName);
        this.groupId = group.getGroupId();
    }

    /**
     * Retrieve resource group by ID
     * @param skil Skil instance
     * @param groupId Existing group ID
     * @throws ApiException SKIL API exception
     */
    public ResourceGroup(Skil skil, Long groupId) throws ApiException {
        this.skil = skil;
        ai.skymind.skil.model.ResourceGroup group = skil.getApi().getResourceGroupById(groupId);
        this.groupName = group.getGroupName();
        this.groupId = group.getGroupId();

    }
}
