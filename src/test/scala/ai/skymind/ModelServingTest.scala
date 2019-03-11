package ai.skymind

import ai.skymind.models.Model
import ai.skymind.services.Service
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.io.ClassPathResource
import org.scalatest.FunSpec


class ModelServingTest extends FunSpec {

  def testSkilBasics(): Unit = {
    val skil = new Skil
    val workSpace = new WorkSpace(skil)
    val experiment = new Experiment(workSpace)
    val modelFile = new ClassPathResource("keras_mnist.h5").getFile
    val model = new Model(modelFile, experiment)
    val deployment = new Deployment(skil, "bulletproof.. I wish I was")

    model.deploy(deployment, true, 1, null, null, false, null)
  }

}
