package ai.skymind.models;

import ai.skymind.*;
import ai.skymind.services.Service;
import ai.skymind.services.TransformArrayService;
import ai.skymind.services.TransformCsvService;
import ai.skymind.services.TransformImageService;
import ai.skymind.skil.model.ImportModelRequest;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.ModelInstanceEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Transform
 *
 * SKIL wrapper for for preprocessing (transform) steps. Currently only
 * supports `TransformProcess` instances from pydatavec or their serialized
 * versions (JSON format).
 *
 * @author Max Pumperla
 */
public class Transform extends Model {

    private Logger logger = Logger.getLogger(Transform.class.getName());

    private String transformType;

    private Transform(String transformId, Experiment experiment, String transformName) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = this.workSpace.getSkil();
        this.id = transformId;

        ModelInstanceEntity modelEntity = this.skil.getApi().getModelInstance(
                this.skil.getWorkspaceServerId(), this.id);

        this.name = modelEntity.getModelName();
        this.version = modelEntity.getModelVersion();
        this.modelPath = modelEntity.getUri();

    }

    public Transform(String transformFile, String transformType, Experiment experiment, String transformId,
                     String name, String version, String labels, boolean verbose) throws ApiException, Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = workSpace.getSkil();
        this.transformType = transformType;

        // TODO proper file path
        skil.uploadModel(transformFile);

        this.name = transformFile;
        this.modelPath = skil.getModelPath(transformFile);
        this.id = transformId;
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

    public Service deploy(Deployment deployment, boolean startServer, int scale,
                       List<String> inputNames, List<String> outputNames, boolean verbose)
            throws ApiException, IOException, InterruptedException {

        List<String> uris = new ArrayList<String>();
        // TODO: those endpoints should be called "transform"
        uris.add(deployment.getName() + "/datavec/" + name + "/default");
        uris.add(deployment.getName() + "/datavec/" + name + "/default");

        if (this.service == null) {
            ImportModelRequest request = new ImportModelRequest()
                    .name(this.name).scale(scale).uri(uris)
                    .modelType("transform").fileLocation(this.modelPath)
                    .inputNames(inputNames).outputNames(outputNames);

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

            if (this.transformType.equals("CSV")){
                this.service = new TransformCsvService(skil, this, this.deployment, this.modelDeployment);
            } else if (this.transformType.equals("array")) {
                this.service = new TransformArrayService(skil, this, this.deployment, this.modelDeployment);
            } else if (this.transformType.equals("image")) {
                this.service = new TransformImageService(skil, this, this.deployment, this.modelDeployment);
            } else {
                throw new IOException("Unsupported transform type: " + this.transformType);
            }

            if (startServer) {
                this.service.start();
            }
        }
        return this.service;
    }

    public static Transform getTransformById(Experiment experiment, String transformId) throws Exception {
        return new Transform(transformId, experiment, "");
    }

}
