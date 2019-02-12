package ai.skymind

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import ai.skymind.models.Model
import ai.skymind.services.Service


class ModelTest {

  val skil = new Skil
  val workSpace = new WorkSpace(skil)
  val experiment = new Experiment(workSpace)

  val modelFile = "keras_model.h5"
  val model = new Model(modelFile, experiment)

  val deployment = new Deployment(skil, "myDeployment")
  val service: Service = model.deploy(deployment, true, 10, null, null, false)

  val data: Array[INDArray] = Array[INDArray](Nd4j.create(100, 784))
  service.predict(data, "default")

}
