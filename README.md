# SKIL: Deep learning model lifecycle management for humans

[![Build Status](https://jenkins.ci.skymind.io/buildStatus/icon?job=skymind/skil-java/master)](https://jenkins.ci.skymind.io/blue/organizations/jenkins/skymind%2Fskil-java/activity)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/SkymindIO/skil-java/blob/master/LICENSE)

## WARNING: This project is still pre-release and needs more testing. 

## Java client for Skymind's intelligence layer (SKIL)

SKIL is an end-to-end deep learning platform. Think of it as a unified front-end for your deep learning training and deployment process. SKIL supports many popular deep learning libraries, such as Keras, TensorFlow and Deeplearning4J. SKIL increases time-to-value of your AI applications by closing the common gap between experiments and production - bringing models to production fast and keeping them there. SKIL effectively acts as middleware for your AI applications and solves a range of common _production_ problems, namely:

- _Install and run anywhere_: SKIL integrates with your current cloud provider, custom on-premise solutions and hybrid architectures.
- _Easy distributed training on Spark_: Bring your Keras or TensorFlow model and train it on Apache Spark without any overhead. We support a wide variety of distributed storage and compute resources and can handle all components of your production stack.
- _Seamless deployment process_:  With SKIL, your company's machine learning product lifecycle can be as quick as your data scientist’s experimentation cycle. If you set up a SKIL experiment, model deployment is already accounted for, and makes product integration of deep learning models into a production-grade model server simple - batteries included.
- _Built-in reproducibility and compliance_: What model and data did you use? Which pre-processing steps were done? What library versions were used? Which hardware was utilized? SKIL keeps track of all this information for you.
- _Model organisation and versioning_: SKIL makes it easy to keep your various experiments organised, without interfering with your workflow. Your models are versioned and can be updated at any point.
- _Keep working as you're used to_: SKIL does not impose an entirely new workflow on you, just stay right where you are. Happy with your experiment and want to deploy it? Tell SKIL to deploy a service. Your prototype works and you want to scale out training with Spark? Tell SKIL to run a training job. You have a great model, but massive amounts of data for inference that your model can't process quickly enough? Tell SKIL to run an inference job on Spark.

## Installation

To install SKIL itself, head over to [skymind.ai](https://docs.skymind.ai/docs/installation). Probably the easiest way to get started is by using [docker](https://www.docker.com/):

```bash
docker pull skymindops/skil-ce
docker run --rm -it -p 9008:9008 skymindops/skil-ce bash /start-skil.sh
```

You'll need Java 1.7+ and Maven (or Gradle) to build this project. To install the API client 
library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the 
repository and execute:

```shell
mvn clean deploy
```

Maven users can simply add this dependency to their project's POM:

```xml
<repositories>
    <repository>
        <id>snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>ai.skymind</groupId>
    <artifactId>skil-java</artifactId>
    <version>1.2.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```


## Getting started: Deploying a Keras model at scale in 30 seconds

```java
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

```

Next, have a look at the SKIL UI at [http://localhost:9008](http://localhost:9008) to 
see how everything you just did is automatically tracked by SKIL. The UI is mostly 
self-explanatory and you shouldn't have much trouble navigating it. After logging 
in (use "admin" as user name and password), you will see that SKIL has created a 
workspace_ for you in the "Workspaces" tab. If you click on that workspace, you'll 
find a so called _experiment_, which contains the yolo model you just loaded into 
SKIL. Each SKIL experiment comes with a notebook that you can work in. In fact, 
if you click on "Open notebook" next to the experiment, you will be redirected to 
a live notebook that contains another interesting example that shows how to deploy 
Keras and DL4J models (the former in Python, the latter in Scala - all in the same 
notebook). If you like notebooks and a managed environment that provides you with 
everything you need out of the box, you can SKIL's notebooks for all your workload. 


In the "Deployments" tab of the UI, you can see your deployed prediction service, 
which consists of just one model, and you'll see that it is "Fully deployed". If you 
click on the deployment you'll see more details of it, for instance you can 
explicitly check the endpoints your service is available at. You could, among other 
things, also re-import the model again through the UI (in case you have a better 
version or needed to make other changes).

This completes your very first SKIL example.
