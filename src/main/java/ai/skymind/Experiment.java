package ai.skymind;

import ai.skymind.skil.model.ExperimentEntity;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private Experiment(WorkSpace workSpace, String experimentId) throws ApiException {
        this.skil = workSpace.getSkil();
        ExperimentEntity experimentEntity = skil.getApi().getExperiment(
                skil.getWorkspaceServerId(),
                experimentId
        );
        this.experimentEntity = experimentEntity;
        this.workSpace = workSpace;
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
        String workSpaceId = (String) config.get("workspaceId");
        String experimentId = (String) config.get("experimentId");


        WorkSpace workSpace = WorkSpace.getWorkSpaceById(skil, workSpaceId);

        return getExperimentById(workSpace, experimentId);
    }

    public WorkSpace getWorkSpace() {
        return workSpace;
    }

    public String getId() {
        return id;
    }

    public static Experiment getExperimentById(WorkSpace workSpace, String experimentId) throws ApiException {
        return new Experiment(workSpace, experimentId);
    }

}
