package ai.skymind.examples;

import ai.skymind.*;
import ai.skymind.models.Model;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.util.Arrays;

public class ModelServingTest {

    @Test(timeout=900000)
    public void testSkilBasics() throws Exception {

        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment);

        Deployment deployment = new Deployment(skil, "bulletproof.. I wish I was");

        model.deploy(
            deployment, true, 1, null, null, false,

            (service) -> {
                INDArray data = Nd4j.rand(1, 784);
                System.out.println(service.predictSingle(data, "default"));
                System.out.println(Arrays.toString(service.predict(new INDArray[] {data}, "v1")));

                // Cleaning up
                service.stop(() -> {
                    service.delete();
                    deployment.delete();
                    workSpace.delete();
                }); // You Never Wash Up After Yourself
            }
        );
    }
}
