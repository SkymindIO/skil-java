package ai.skymind.models;

import ai.skymind.*;
import ai.skymind.services.Service;
import ai.skymind.services.TransformArrayService;
import ai.skymind.services.TransformCsvService;
import ai.skymind.services.TransformImageService;
import ai.skymind.skil.model.ImportModelRequest;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.ModelInstanceEntity;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;
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

    private TransformType transformType;

    private Transform(String transformId, TransformType transformType, Experiment experiment) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = this.workSpace.getSkil();
        this.id = transformId;
        this.transformType = transformType;

        ModelInstanceEntity modelEntity = this.skil.getApi().getModelInstance(
                this.skil.getWorkspaceServerId(), this.id);

        this.name = modelEntity.getModelName();
        this.version = modelEntity.getModelVersion();
        this.modelPath = modelEntity.getUri();

    }

    public Transform(File transformFile, TransformType transformType, Experiment experiment) throws Exception {
        this(transformFile, transformType, experiment, "id_" + UUID.randomUUID().toString(),
                "name_" + UUID.randomUUID().toString(), "1", "", false);
    }

    public Transform(File transformFile, TransformType transformType, Experiment experiment, String transformId,
                     String name, String version, String labels, boolean verbose) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = workSpace.getSkil();
        this.transformType = transformType;

        skil.uploadModel(transformFile);

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

    /**
     * Get the transform config as Map
     *
     * @return transform config
     */
    @Override
    public Map<String, Object> getConfig() {

        HashMap<String, Object> config = new HashMap<>();
        config.put("transformId", id);
        config.put("transformName", name);
        config.put("transformType", transformType.getType());
        config.put("experimentId", experiment.getId());
        config.put("workspaceId", workSpace.getId());
        return config;
    }


    /**
     * Load a transform from file
     *
     * @param skil Skil instance
     * @param fileName file name for file with transform config JSON
     * @return Transform instance
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
        String transformId = (String) config.get("transformId");
        TransformType transformType =  TransformType.fromString((String) config.get("transformType"));

        Transform transform = getTransformById(transformId, transformType, exp);
        transform.setName((String) config.get("transformName"));
        return transform;
    }


    public Service deploy(Deployment deployment, boolean startServer, int scale,
                       List<String> inputNames, List<String> outputNames, boolean verbose)
            throws ApiException, InterruptedException {

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

            if (this.transformType.getType().equals("CSV")){
                this.service = new TransformCsvService(skil, this, this.deployment, this.modelDeployment);
            } else if (this.transformType.getType().equals("array")) {
                this.service = new TransformArrayService(skil, this, this.deployment, this.modelDeployment);
            } else if (this.transformType.getType().equals("image")) {
                this.service = new TransformImageService(skil, this, this.deployment, this.modelDeployment);
            }

            if (startServer) {
                this.service.start();
            }
        }
        return this.service;
    }

    public String getType() {
        return transformType.getType();
    }

    public static Transform getTransformById(String transformId, TransformType transformType, Experiment experiment)
            throws Exception {
        return new Transform(transformId, transformType, experiment);
    }

}
