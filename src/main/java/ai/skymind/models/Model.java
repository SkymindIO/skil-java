package ai.skymind.models;

import ai.skymind.*;
import ai.skymind.services.Service;
import ai.skymind.skil.model.ImportModelRequest;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.ModelInstanceEntity;
import ai.skymind.skil.model.EvaluationResultsEntity;
import lombok.Data;


import java.io.File;
import java.io.IOException;
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

    public String getModelPath() {
        return modelPath;
    }

    public String getName() {
        return name;
    }

    public Service deploy(Deployment deployment, boolean startServer, int scale,
                          List<String> inputNames, List<String> outputNames, boolean verbose)
            throws ApiException, IOException, InterruptedException {

        List<String> uris = new ArrayList<String>();
        uris.add(deployment.getName() + "/model/" + name + "/default");
        uris.add(deployment.getName() + "/model/" + name + "/default");

        if (this.service == null) {
            ImportModelRequest request = new ImportModelRequest().name(this.name).scale(scale).uri(uris)
                    .modelType("model").fileLocation(this.modelPath).inputNames(inputNames).outputNames(outputNames);


            //TODO from Python: self.deployment = deployment.response ???
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
                this.service.start();
            }
        }
        return this.service;
    }

    public void addEvaluation(double accuracy, String evalId, String name, Integer version) throws Exception {
        // TODO defaults for id, name, version
        Long created = (new Date().getTime()/1000);

        EvaluationResultsEntity evaluationResultsEntity = new EvaluationResultsEntity().evaluation("")
                .created(created).evalName(name).evalVersion(version).evalId(evalId)
                .modelInstanceId(this.id).accuracy(accuracy);

        skil.getApi().addEvaluationResult(skil.getDefaultServerId(), evaluationResultsEntity);
        this.evaluations.put(evalId, evaluationResultsEntity);

    }

    public void undeploy() throws ApiException {
        skil.getApi().deleteModel(this.deployment.getDeploymentId(), this.id);
    }


    public void delete() throws ApiException {
        skil.getApi().deleteModelInstance(this.skil.getWorkspaceServerId(), this.id);
    }


    public static Model getModelById(Experiment experiment, String modelId) throws Exception {
        return new Model(modelId, experiment, "");
    }
}
