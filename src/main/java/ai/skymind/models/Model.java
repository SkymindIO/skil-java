package ai.skymind.models;

import ai.skymind.*;
import ai.skymind.services.Service;
import ai.skymind.services.ServiceCallbackInterface;
import ai.skymind.skil.model.ImportModelRequest;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.ModelInstanceEntity;
import ai.skymind.skil.model.EvaluationResultsEntity;
import com.google.gson.Gson;
import lombok.Data;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * SKIL wrapper for DL4J, Keras, TensorFlow and other models
 *
 * SKIL has a robust model storage, serving, and import system for supporting major
 * deep learning libraries.
 * SKIL can be used for end-to-end training, configuration, and deployment of models
 * or alternatively you can import models into SKIL.
 *
 * @author Max Pumperla
 */
@Data
public class Model {

    protected Experiment experiment;
    protected WorkSpace workSpace;
    protected Skil skil;
    protected Deployment deployment = null;
    protected ModelEntity modelDeployment;

    protected String id;
    protected String name;
    protected String modelPath;
    protected String version;
    protected String labels;
    protected HashMap<String, EvaluationResultsEntity> evaluations = new HashMap<>();

    protected Service service = null;

    private Logger logger = Logger.getLogger(Model.class.getName());

    public Model() {}


    private Model(String modelId, Experiment experiment, String modelName) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = this.workSpace.getSkil();
        this.id = modelId;


        ModelInstanceEntity modelEntity = this.skil.getApi().getModelInstance(
                this.skil.getWorkspaceServerId(), this.id);

        this.name = modelEntity.getModelName();
        this.version = modelEntity.getModelVersion();
        this.modelPath = modelEntity.getUri();

    }

    public Model(File modelFile, Experiment experiment) throws  Exception {
        this(modelFile, experiment, "id_" + UUID.randomUUID().toString(),
                "name_" + UUID.randomUUID().toString(), "1", "", false);
    }

    public Model(File modelFile, Experiment experiment, String modelId, String name, String version, String labels,
                 boolean verbose) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = this.workSpace.getSkil();

        this.skil.uploadModel(modelFile);

        this.modelPath = skil.getModelPath(modelFile);
        this.id = modelId;
        this.name = name;
        this.version = version;
        this.labels = labels;

        Long created = (new Date().getTime()/1000);

        ModelInstanceEntity entity = skil.getApi().addModelInstance(
                skil.getWorkspaceServerId(),
                new ModelInstanceEntity()
                        .uri(this.modelPath)
                        .modelId(this.id)
                        .modelLabels(this.labels)
                        .modelName(this.name)
                        .modelVersion(this.version)
                        .modelLabels(this.labels)
                        .created(created)
                        .experimentId(this.experiment.getId())
                );
        if (verbose) {
            logger.info(entity.toString());
        }
    }

    /**
     * Get the model config as Map
     *
     * @return model config
     */
    public Map<String, Object> getConfig() {

        HashMap<String, Object> config = new HashMap<>();
        config.put("modelId", id);
        config.put("modelName", name);
        config.put("experimentId", experiment.getId());
        config.put("workspaceId", workSpace.getId());
        return config;
    }

    /**
     * Save the model configuration as JSON file
     *
     * @param fileName name of the file in which to store the model config
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
     * Load a model from file
     *
     * @param skil Skil instance
     * @param fileName file name for file with model config JSON
     * @return Model instance
     *
     * @throws FileNotFoundException File not found
     * @throws ApiException SKIL API exception
     */
    public static Model load(Skil skil, String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(new File(fileName));
        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        HashMap<String, Object> config = gson.fromJson(reader, HashMap.class);
        String workSpaceId = (String) config.get("workspaceId");
        String experimentId = (String) config.get("experimentId");


        WorkSpace workSpace = WorkSpace.getWorkSpaceById(skil, workSpaceId);

        Experiment exp = Experiment.getExperimentById(workSpace, experimentId);
        String modelId = (String) config.get("modelId");

        Model model = getModelById(modelId, exp);
        model.setName((String) config.get("modelName"));
        return model;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Deploy a model as a service.
     *
     * @param deployment Deployment instance
     * @param startServer boolean, whether to start the server
     * @param scale number of servers to deploy this model to
     * @param inputNames Space-separated input names to the model are required for multi-input models
     * @param outputNames Space-separated output names to the model are required for multi-output models
     * @param verbose boolean, turn on verbose logging
     * @return SKIL Service instance
     * @throws ApiException SKIL API exception
     * @throws InterruptedException Interrupted exception
     */
    public Service deploy(Deployment deployment, boolean startServer, int scale,
                          List<String> inputNames, List<String> outputNames, boolean verbose)
            throws ApiException, InterruptedException {
        return deploy(deployment, startServer, scale, inputNames, outputNames, verbose);
    }

    /**
     * Deploy a model as a service.
     *
     * @param deployment Deployment instance
     * @param startServer boolean, whether to start the server
     * @param scale number of servers to deploy this model to
     * @param inputNames Space-separated input names to the model are required for multi-input models
     * @param outputNames Space-separated output names to the model are required for multi-output models
     * @param verbose boolean, turn on verbose logging
     * @param callback A callback function for when a model is started.
     * @return SKIL Service instance
     * @throws ApiException SKIL API exception
     * @throws InterruptedException Interrupted exception
     */
    public Service deploy(Deployment deployment, boolean startServer, int scale,
                          List<String> inputNames, List<String> outputNames, boolean verbose,
                          ServiceCallbackInterface callback)
            throws ApiException, InterruptedException {

        List<String> uris = new ArrayList<>();
        uris.add(deployment.getDeploymentSlug() + "/model/" + name + "/default");
        uris.add(deployment.getDeploymentSlug() + "/model/" + name + "/v1");

        if (this.service == null) {
            ImportModelRequest request = new ImportModelRequest().name(this.name).scale(scale).uri(uris)
                    .modelType("model").fileLocation(this.modelPath).inputNames(inputNames).outputNames(outputNames);

            this.deployment = deployment;

            List<ModelEntity> models = skil.getApi().models(this.deployment.getDeploymentId());
            ModelEntity deployedModel = null;
            for (ModelEntity model: models) {
                if (model.getName().equals(this.name)) {
                    deployedModel = model;
                }
            }
            if (deployedModel != null) {
                this.modelDeployment = deployedModel;
            } else {
                this.modelDeployment = skil.getApi().deployModel(this.deployment.getDeploymentId(), request);
                if (verbose) {
                    logger.info(this.modelDeployment.toString());
                }
            }

            this.service = new Service(this.skil, this, this.deployment, this.modelDeployment);

            if (startServer) {
                this.service.start(callback);
            }
        }
        return this.service;
    }

    /**
     * Register an evaluation data point for this model, e.g. add model accuracy.
     *
     * @param accuracy model accuracy
     * @param evalId evaluation ID
     * @param name name of the evaluation
     * @param version evaluation version
     * @throws Exception Exception
     */
    public void addEvaluation(double accuracy, String evalId, String name, Integer version) throws Exception {
        // TODO defaults for id, name, version
        Long created = (new Date().getTime()/1000);

        EvaluationResultsEntity evaluationResultsEntity = new EvaluationResultsEntity()
                .evaluation("")
                .created(created)
                .evalName(name)
                .evalVersion(version)
                .evalId(evalId)
                .modelInstanceId(this.id)
                .accuracy(accuracy);

        skil.getApi().addEvaluationResult(skil.getDefaultServerId(), evaluationResultsEntity);
        this.evaluations.put(evalId, evaluationResultsEntity);

    }

    /**
     * Delete this model.
     *
     * @throws ApiException SKIL API exception
     */
    public void delete() throws ApiException {
        skil.getApi().deleteModelInstance(this.skil.getWorkspaceServerId(), this.id);
    }


    /**
     * Get a SKIL model by ID
     *
     * @param modelId valid SKIL Model ID
     * @param experiment SKIL experiment instance
     * @return Model instance
     * @throws Exception Exception
     */
    public static Model getModelById( String modelId, Experiment experiment) throws Exception {
        return new Model(modelId, experiment, "");
    }
}
