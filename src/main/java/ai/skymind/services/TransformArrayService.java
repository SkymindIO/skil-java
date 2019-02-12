package ai.skymind.services;

import ai.skymind.ApiException;
import ai.skymind.Deployment;
import ai.skymind.Skil;
import ai.skymind.models.Model;
import ai.skymind.skil.model.Base64NDArrayBody;
import ai.skymind.skil.model.BatchRecord;
import ai.skymind.skil.model.ModelEntity;
import ai.skymind.skil.model.SingleRecord;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * A service for transforming array data
 *
 * @author Max Pumperla
 */
// TODO this is not useful in any sense.
public class TransformArrayService extends Service {

    public  TransformArrayService() {}

    public TransformArrayService(Skil skil, Model model, Deployment deployment, ModelEntity modelDeployment) {
        super(skil, model, deployment, modelDeployment);
    }

    public String predict(BatchRecord data, String version) throws ApiException {

        Base64NDArrayBody array = skil.getApi().transformarray(deployment.getName(), version, model.getName(), data);
        return array.getNdarray();
    }

    public String predictSingle(SingleRecord data, String version) throws ApiException {
        Base64NDArrayBody array = skil.getApi().transformincrementalarray(deployment.getName(),
                version, model.getName(), data);
        return array.getNdarray();
    }
}
