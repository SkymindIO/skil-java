package ai.skymind.resources;


public enum ResourceSubType {

    EMR,                // AWS Elastic Map Reduce(Compute)
    DataProc,          // Google Big Data Compute Engine
    HDInsight,         // Azure Compute

    S3,                 // AWS Simple Storage Service
    GoogleStorage,     // Google Cloud Storage
    AzureStorage,      // Azure Blob Storage
    HDFS                // In-house Hadoop distributed file system

    // TODO: we leave out YARN, as it will be removed

}
