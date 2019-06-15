package ai.skymind;

import ai.skymind.skil.model.ExperimentEntity;
import com.google.gson.Gson;
import org.apache.zeppelin.spark.ZeppelinContext;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * Experiment
 *
 * Experiments in SKIL are useful for defining different model configurations,
 * encapsulating training of models, and carrying out different data cleaning tasks.
 *
 * Experiments have a one-to-one relationship with Notebooks and have their own
 * storage mechanism for saving different model configurations when seeking a best
 * candidate.
 *
 * @author Max Pumperla
 */
public class Experiment {

    private ExperimentEntity experimentEntity;
    private WorkSpace workSpace;
    private Skil skil;
    private String id;
    private String name;
    private String description;

    private Logger logger = Logger.getLogger(Experiment.class.getName());


    public Experiment(WorkSpace workSpace, String experimentId, String name,
                      String description, boolean verbose) throws ApiException {

        this.workSpace = workSpace;
        this.skil = workSpace.getSkil();
        this.description = description;
        this.name = name;
        this.id = experimentId;

        experimentEntity = new ExperimentEntity()
                .experimentId(this.id)
                .experimentDescription(this.description)
                .experimentName(this.name)
                .modelHistoryId(this.workSpace.getId());

        skil.getApi().addExperiment(
                skil.getWorkspaceServerId(),
                experimentEntity
        );

        if (verbose) {
            logger.info(experimentEntity.toString());
        }
    }

    private Experiment(Skil skil, String experimentId) throws ApiException {
        this.skil = skil;
        ExperimentEntity experimentEntity = skil.getApi().getExperiment(
                skil.getWorkspaceServerId(),
                experimentId
        );

        String workSpaceId = experimentEntity.getModelHistoryId();
        WorkSpace ws = WorkSpace.getWorkSpaceById(skil, workSpaceId);
        this.experimentEntity = experimentEntity;
        this.workSpace = ws;
        this.id = experimentId;
        this.name = experimentEntity.getExperimentName();
    }

    public Experiment(WorkSpace workSpace) throws  ApiException {
        this(workSpace, workSpace + "_experiment_" +  UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), "", false);
    }

    /**
     * Get the experiment config as Map
     *
     * @return experiment config
     */
    public Map<String, Object> getConfig() {

        HashMap<String, Object> config = new HashMap<>();
        config.put("experimentId", id);
        config.put("experimentName", name);
        config.put("workspaceId", workSpace.getId());
        return config;
    }

    /**
     * Save the experiment configuration as JSON file
     *
     * @param fileName name of the file in which to store the experiment config
     */
    public void save(String fileName) {
        Map config = getConfig();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes this experiment from SKIL.
     */
    public void delete() throws ApiException {
       skil.getApi().deleteExperiment(this.skil.getWorkspaceServerId(), this.id);
    }

    /**
     * Load an experiment from file
     *
     * @param skil Skil instance
     * @param fileName file name for file with experiment config JSON
     * @return Experiment instance
     *
     * @throws FileNotFoundException File not found
     * @throws ApiException SKIL API exception
     */
    public static Experiment load(Skil skil, String fileName) throws FileNotFoundException, ApiException {
        FileInputStream fis = new FileInputStream(new File(fileName));
        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        HashMap<String, Object> config = gson.fromJson(reader, HashMap.class);
        String experimentId = (String) config.get("experimentId");


        return getExperimentById(skil, experimentId);
    }

    /**
     * Get the SKIL experiment associated with this Zeppelin notebook.
     *
     * Note that this can only be called from within a SKIL Zeppelin notebook.
     *
     * @param skil Skil instance
     * @param context ZeppelinContext instance
     * @return The SKIL Experiment for this notebook.
     */
    public static Experiment currentSkilExperiment(Skil skil, ZeppelinContext context) throws Exception {
        String noteId = context.getInterpreterContext().getNoteId();
        List<ExperimentEntity> experiments = getAllExperimentEntities(skil);
        Optional<String> experimentId = experiments
                .stream()
                .filter(e -> e.getNotebookUrl().contains(noteId))
                .map(ExperimentEntity::getExperimentId)
                .findFirst();
        if (experimentId.isPresent()) {
            return getExperimentById(skil, experimentId.get());
        } else {
            throw new Exception("Experiment ID not found.");
        }
        // TODO: we don't have SKILEnvironment here, which is needed for saveModel and copyModel
    }

    public WorkSpace getWorkSpace() {
        return workSpace;
    }

    public String getId() {
        return id;
    }

    /**
     * Get experiment by ID
     *
     * @param skil SKIL instance
     * @param experimentId Valid experiment ID in that workspace
     * @return the Experiment for that ID
     * @throws ApiException API exception
     */
    public static Experiment getExperimentById(Skil skil, String experimentId) throws ApiException {
        return new Experiment(skil, experimentId);
    }

    /**
     * Get all experiments from a workspace
     *
     * @param workSpace a WorkSpace
     * @return a list of Experiments
     */
    public static List<Experiment> getAllWorkspaceExperiments(WorkSpace workSpace) throws ApiException {

        // TODO: this is simply wrong in skil-clients. Needs fix upstream
        ExperimentEntity experimentEntities = workSpace.getSkil().getApi().getExperimentsForModelHistory(
                workSpace.getSkil().getWorkspaceServerId(),
                workSpace.getId()
        );
        String id = experimentEntities.getExperimentId();
        return Collections.singletonList(getExperimentById(workSpace.getSkil(), id));
    }

    /**
     * Get all experiments from a workspace
     *
     * @param skil a Skil instance
     * @return a list of Experiments
     */
    public static List<Experiment> getAllExperiments(Skil skil) throws ApiException {

        List<ExperimentEntity> experimentEntities = getAllExperimentEntities(skil);
        ArrayList result = new ArrayList();
        for (ExperimentEntity exp: experimentEntities) {
            result.add(getExperimentById(skil, exp.getExperimentId()));
        }
        return result;
    }

    /**
     * Get all experiment entities from a workspace
     *
     * @param skil a Skil instance
     * @return a list of ExperimentEntity objects
     */
    private static List<ExperimentEntity> getAllExperimentEntities(Skil skil) throws ApiException {
        return skil.getApi().listAllExperiments(
                skil.getWorkspaceServerId()
        );
    }

}
