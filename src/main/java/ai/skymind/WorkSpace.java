package ai.skymind;

import java.util.UUID;
import java.util.logging.Logger;

import ai.skymind.skil.model.AddModelHistoryRequest;
import ai.skymind.skil.model.InlineResponse200;
import ai.skymind.skil.model.ModelHistoryEntity;

/**
 * WorkSpace
 *
 * Workspaces are a collection of features that enable different tasks such as
 * conducting experiments, training models, and test different dataset transforms.
 *
 * Workspaces are distinct from Deployments by operating as a space for
 * non-production work.
 *
 * @author Max Pumperla
 */
public class WorkSpace {

    private Skil skil;
    private String id;
    private String name;
    private boolean verbose = false;
    private String labels;
    private ModelHistoryEntity workSpace;

    private Logger logger = Logger.getLogger(WorkSpace.class.getName());

    /**
     * Get a WorkSpace from a ModelHistoryEntity
     *
     * @param skil
     * @param workSpace
     * @param id
     */
    private WorkSpace(Skil skil, ModelHistoryEntity workSpace, String id) {
        this.skil = skil;
        this.workSpace = workSpace;
        this.id = id;
        this.name = workSpace.getModelName();
    }

    /**
     *
     * @param skil Skil instance
     * @param name Name of the Workspace
     * @param labels Labels associated with the workspace, useful for searching (comma seperated).
     * @param verbose  boolean. If True, api response will be printed.
     * @throws ApiException
     */
    public WorkSpace(Skil skil, String name, String labels, boolean verbose) throws ApiException {
        this.skil = skil;
        this.name = name;
        this.verbose = verbose;
        this.labels = labels;

        workSpace = skil.getApi().addModelHistory(
                skil.getWorkspaceServerId(),
                new AddModelHistoryRequest().modelName(name).modelLabels(labels)
        );
        this.id = workSpace.getModelHistoryId();

        if (this.verbose) {
            logger.info(workSpace.toString());
        }
    }

    public Skil getSkil() {
        return skil;
    }

    public String getId() {
        return id;
    }

    public WorkSpace(Skil skil, String name) throws ApiException {
        this(skil, name, "", false);
    }

    public WorkSpace(Skil skil) throws  ApiException {
        this(skil, UUID.randomUUID().toString(), "", false);

    }

    /**
     * Delete this workspace.
     *
     * @throws ApiException SKIL API Exception
     */
    public void delete() throws ApiException {
        try {
            InlineResponse200 response = skil.getApi().deleteModelHistory(skil.getWorkspaceServerId(), id);
            logger.info(response.toString());
        } catch(ApiException e) {
            logger.warning("Workspace couldn't be deleted, error message: " + e.toString());
        }
    }

    /**
     * Get a workspace by ID
     *
     * @param skil Skil instance
     * @param id Workspace ID
     * @return Retrieved workspace
     *
     * @throws ApiException SKIL API exception
     */
    public static WorkSpace getWorkSpaceById(Skil skil, String id) throws ApiException {
        String serverId = skil.getWorkspaceServerId();
        ModelHistoryEntity response = skil.getApi().getModelHistory(serverId, id);
        return new WorkSpace(skil, response, id);
    }

}
