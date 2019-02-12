package ai.skymind.examples;

import ai.skymind.Deployment;
import ai.skymind.Experiment;
import ai.skymind.Skil;
import ai.skymind.WorkSpace;
import ai.skymind.models.Model;
import ai.skymind.services.Service;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ModelServingTest {

    @Test
    public void testSkilBasics() throws Exception {

        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        String modelFile = "keras_model.h5";
        Model model = new Model(modelFile, experiment);

        Deployment deployment = new Deployment(skil, "myDeployment");
        Service service = model.deploy(
                deployment, true, 10, null, null, false
        );

        INDArray[] data = new INDArray[] {Nd4j.create(100, 784)};
        service.predict(data, "default");
    }
}
