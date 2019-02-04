package ai.skymind.spark;

import ai.skymind.spark.ParameterAveraging.ParameterAveragingBuilder;
import org.junit.Test;

/**
 * Parameter averaging config test
 *
 * @author Max Pumperla
 */
public class ParameterAveragingTest {

    @Test
    public void builderTest() {
        ParameterAveragingBuilder pab = new ParameterAveragingBuilder();

        ParameterAveraging pa = pab.averagingFrequency(42)
                .batchSize(10).collectStats(true)
                .numBatchesPrefetch(0).numWorkers(8).build();


        System.out.println(pa.toJson());
    }

    @Test
    public void averagingConfig() {
        ParameterAveraging pa = new ParameterAveraging(
                0,0,0,0,false);
        System.out.println(pa.toJson());
    }

}
