package ai.skymind.examples;

import ai.skymind.*;
import ai.skymind.models.Model;
import ai.skymind.services.Service;
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

        Deployment deployment = new Deployment(skil, "myDeployment42");
        /* Uploading with a scale <= 2, for the test to be applicable to SKIL CE. Later on we can
         * update this number as required.
         */
        Service service = model.deploy(
                deployment, true, 1, null, null, false
        );

        int retryCount = 2;
        int retries = 0;

        do {
            retries++;

            try {
                INDArray data = Nd4j.rand(1, 784);
                // Single Predict
                System.out.println(service.predictSingle(data, "default"));
                // Multi Predict
                System.out.println(Arrays.toString(service.predict(new INDArray[]{data}, "default")));

                break;
            } catch (ApiException e) {
                e.printStackTrace();

                System.out.println("Couldn't try predictions with the model server, retrying: " + retries);
                Thread.sleep(10000); // Safe sleeping time for the model server to successfully wake up.
            }
        } while (retries < retryCount);
    }
}
