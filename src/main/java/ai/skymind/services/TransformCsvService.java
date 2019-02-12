package ai.skymind.services;

import ai.skymind.ApiException;
import ai.skymind.Deployment;
import ai.skymind.Skil;
import ai.skymind.models.Model;
import ai.skymind.skil.model.*;

public class TransformCsvService extends Service {

    public TransformCsvService() {}

    public TransformCsvService(Skil skil, Model model, Deployment deployment, ModelEntity modelDeployment) {
        super(skil, model, deployment, modelDeployment);
    }

    public BatchCSVRecord predict(BatchCSVRecord data, String version) throws ApiException {
        BatchCSVRecord array = skil.getApi().transformCsv(deployment.getName(), version, model.getName(), data);
        return array;
    }

    public SingleCSVRecord predictSingle(SingleCSVRecord data, String version) throws ApiException {
        SingleCSVRecord array = skil.getApi().transformincrementalCsv(deployment.getName(),
                version, model.getName(), data);
        return array;
    }
}
