package ai.skymind.spark;

import ai.skymind.spark.ParameterSharing.ParameterSharingBuilder;
import org.junit.Test;

/**
 * Parameter averaging config test
 *
 * @author Max Pumperla
 */
public class ParameterSharingTest {

    @Test
    public void builderTest() {
        ParameterSharingBuilder pab = new ParameterSharingBuilder();

        ParameterSharing pa = pab.shakeFrequency(42)
                .batchSize(10).collectStats(true)
                .numBatchesPrefetch(0).numWorkers(8).build();

        System.out.println(pa.toJson());
    }

    @Test
    public void averagingConfig() {
        ParameterSharing pa = new ParameterSharing(0,0);
        System.out.println(pa.toJson());
    }

}
