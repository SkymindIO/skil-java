package ai.skymind.services;

import ai.skymind.ApiException;
import ai.skymind.Deployment;
import ai.skymind.Skil;
import ai.skymind.models.Model;
import ai.skymind.skil.model.Base64NDArrayBody;
import ai.skymind.skil.model.BatchCSVRecord;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.SingleCSVRecord;

import java.io.File;
import java.util.List;

public class TransformImageService extends Service {

    public TransformImageService() {}

    public TransformImageService(Skil skil, Model model, Deployment deployment, ModelEntity modelDeployment) {

    }

    public Base64NDArrayBody predict(List<byte[]> data, String version) throws ApiException {
        Base64NDArrayBody array = skil.getApi().transformimage(deployment.getName(), version, model.getName(), data);
        return array;
    }

    public Base64NDArrayBody predictSingle(File data, String version) throws ApiException {
        Base64NDArrayBody array = skil.getApi().transformincrementalimage(deployment.getName(),
                version, model.getName(), data);
        return array;
    }
}
