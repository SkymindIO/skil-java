package ai.skymind.resources;


import ai.skymind.models.TransformType;

/**
 * Resource sub-types
 *
 * @author Max Pumperla
 */
public enum ResourceSubType {

    EMR("EMR"),                           // AWS Elastic Map Reduce(Compute)
    DataProc("DataProc"),                 // Google Big Data Compute Engine
    HDInsight("HDInsight"),               // Azure Compute

    S3("S3"),                             // AWS Simple Storage Service
    GoogleStorage("GoogleStorage"),       // Google Cloud Storage
    AzureStorage("AzureStorage"),         // Azure Blob Storage
    HDFS("HDFS");                         // In-house Hadoop distributed file system

    private String type;

    ResourceSubType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransformType fromString(String text) {
        for (TransformType type : TransformType.values()) {
            if (type.getType().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
