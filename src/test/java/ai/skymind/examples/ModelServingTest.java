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
    @Ignore
    public void testSkilBasics() throws Exception {

        Skil skil = new Skil();
        WorkSpace workSpace = new WorkSpace(skil);
        Experiment experiment = new Experiment(workSpace);

        File modelFile = new ClassPathResource("keras_mnist.h5").getFile();
        Model model = new Model(modelFile, experiment);

        Deployment deployment = new Deployment(skil, "myDeployment42");
        /* Uploading with a scale <= 2, for the test to be applicable to SKIL CE. Later on we can
         * update this number as required.
         */
        model.deploy(
            deployment, true, 1, null, null, false,

            // Bulletproof.. I Wish I Was
            (service) -> {
                INDArray data = Nd4j.rand(1, 784);
                // Single Predict
                System.out.println(service.predictSingle(data, "default"));
                // Multi Predict
                System.out.println(Arrays.toString(service.predict(new INDArray[] {data}, "v1")));

                // Cleaning up
                service.stop(() -> {
                    // The IDs of experiment model instance and deployed model instances are
                    // managed separately. Therefore, we have to get the deployed model's ID.
                    String modelId = String.valueOf(model.getModelDeployment().getId());

                    deployment.deleteModel(modelId);
                    deployment.delete();
                    workSpace.delete();
                }); // You Never Wash Up After Yourself
            }
        );
    }
}
