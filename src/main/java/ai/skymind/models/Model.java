package ai.skymind.models;

import ai.skymind.*;
import ai.skymind.skil.model.ModelInstanceEntity;

import java.util.Date;
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
public class Model {
    // TODO public create constructor
    // TODO private retrieve constructor
    // TODO delete model
    // TODO add evaluation
    // TODO deploy
    // TODO undeploy
    // TODO static getModelById

    private Experiment experiment;
    private WorkSpace workSpace;
    private Skil skil;
    private Deployment deployment = null;

    private String id;
    private String name;
    private String modelPath;
    private String version;
    private String labels;

    private Logger logger = Logger.getLogger(Experiment.class.getName());


    public Model(String modelFile, String modelId, String name, String version, Experiment experiment, String labels,
                 boolean verbose) throws Exception {

        this.experiment = experiment;
        this.workSpace = experiment.getWorkSpace();
        this.skil = this.workSpace.getSkil();

        // TODO get proper path from resources
        this.skil.uploadModel(modelFile);

        this.name = modelFile;
        this.modelPath = skil.getModelPath(modelFile);
        this.id = modelId;
        this.name = name;
        this.version = version;

//        this.evaluations = {}
//        this.modelDeployment = None

        Long created = (new Date().getTime()/1000);

        ModelInstanceEntity entity = skil.getApi().addModelInstance(
                skil.getWorkspaceServerId(),
                new ModelInstanceEntity()
                        .uri(this.modelPath)
                        .modelId(this.id)
                        .modelLabels(this.labels)
                        .modelName(this.name)
                        .modelVersion(this.version)
                        .created(created)
                        .experimentId(this.experiment.getId())
                );
        if (verbose) {
            logger.info(entity.toString());
        }
    }

}
