package ai.skymind;

import ai.skymind.resources.ResourceSubType;
import ai.skymind.resources.ResourceType;
import ai.skymind.resources.compute.DataProc;
import ai.skymind.resources.compute.EMR;
import ai.skymind.resources.compute.HDInsight;
import ai.skymind.resources.Resource;
import ai.skymind.resources.storage.AzureStorage;
import ai.skymind.resources.storage.GoogleStorage;
import ai.skymind.resources.storage.HDFS;
import ai.skymind.resources.storage.S3;
import ai.skymind.resources.Utils;

import java.util.UUID;
import java.util.List;
import org.junit.Test;


public class ResourcesTest {

    @Test
    public void testGetResourceById() throws ApiException, Exception {
        Skil skil = new Skil();

        Long resourceId = 0L;
        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region",
                "test_cluster_id", resourceId);
        Resource res = Utils.getResourceById(skil, resourceId);

        assert(resourceId == res.getResourceId());
        emrRes.delete();
    }

    @Test
    public void testGetAllResources() throws ApiException, Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region",
                "test_cluster_id", 0L);

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", 1L);

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluser", 2L);

        List<Resource> resList = Utils.getAllResources(skil);

        assert(resList.size() == 3);

        assert(resList.get(0).getResourceId() == 0L);
        assert(resList.get(1).getResourceId() == 1L);
        assert(resList.get(2).getResourceId() == 2L);

        emrRes.delete();
        dataRes.delete();
        hdRes.delete();
    }

    @Test
    public void testGetResourceByType() throws ApiException, Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region",
                "test_cluster_id", 0L);

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", 1L);

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluser", 2L);

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", 3L);

        GoogleStorage gsRes = new GoogleStorage( skil,"google" + UUID.randomUUID(),
                "test_project", "test_bucket", 4L);


        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "test_port", 5L);

        S3 s3Res = new S3( skil, "s3" + UUID.randomUUID(),
                "test_bucket", "test_region", 6L);

        List<Resource> computeList = Utils.getResourceByType(skil, ResourceType.COMPUTE);
        List<Resource> storageList = Utils.getResourceByType(skil, ResourceType.STORAGE);

        assert(computeList.size() == 3);

        assert(computeList.get(0).getResourceId() == 0L);
        assert(computeList.get(1).getResourceId() == 1L);
        assert(computeList.get(2).getResourceId() == 2L);

        assert(storageList.size() == 4);

        assert(storageList.get(3).getResourceId() == 3L);
        assert(storageList.get(4).getResourceId() == 4L);
        assert(storageList.get(5).getResourceId() == 5L);
        assert(storageList.get(6).getResourceId() == 6L);

        emrRes.delete();
        dataRes.delete();
        asRes.delete();
        gsRes.delete();
        hdfsRes.delete();
        s3Res.delete();
    }

    @Test
    public void testGetResourceBySubType() throws ApiException, Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region",
                "test_cluster_id", 0L);

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", 1L);

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluster", 2L);

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", 3L);

        GoogleStorage gsRes = new GoogleStorage( skil,"google" + UUID.randomUUID(),
                "test_project", "test_bucket", 4L);


        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "test_port", 5L);

        S3 s3Res = new S3( skil, "s3" + UUID.randomUUID(),
                "test_bucket", "test_region", 6L);

        List<Resource> emrList = Utils.getResourceBySubType(skil, ResourceSubType.EMR);
        assert(emrList.size() == 1);
        assert(emrList.get(0).getResourceId() == 0L);
        emrRes.delete();

        List<Resource> dataProcList = Utils.getResourceBySubType(skil, ResourceSubType.DataProc);
        assert(dataProcList.size() == 1);
        assert(dataProcList.get(0).getResourceId() == 1L);
        dataRes.delete();

        List<Resource> hdInsightList = Utils.getResourceBySubType(skil, ResourceSubType.HDInsight);
        assert(hdInsightList.size() == 1);
        assert(hdInsightList.get(0).getResourceId() == 2L);
        hdRes.delete();

        List<Resource> azureList = Utils.getResourceBySubType(skil, ResourceSubType.AzureStorage);
        assert(azureList.size() == 1);
        assert(azureList.get(0).getResourceId() == 3L);
        asRes.delete();

        List<Resource> googleList = Utils.getResourceBySubType(skil, ResourceSubType.GoogleStorage);
        assert(googleList.size() == 1);
        assert(googleList.get(0).getResourceId() == 4L);
        gsRes.delete();

        List<Resource> hdfsList = Utils.getResourceBySubType(skil, ResourceSubType.HDFS);
        assert(hdfsList.size() == 1);
        assert(hdfsList.get(0).getResourceId() == 5L);
        hdfsRes.delete();

        List<Resource> s3List = Utils.getResourceBySubType(skil, ResourceSubType.S3);
        assert(s3List.size() == 1);
        assert(s3List.get(0).getResourceId() == 6L);
        s3Res.delete();
    }

    @Test
    public void testGetDataProcDetailsById() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "dataProc" + UUID.randomUUID();

        DataProc dataRes = new DataProc(skil, name, "test_project",
                "test_region", "test_cluster_id", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((DataProc) res).getName() == name);
        assert( ((DataProc) res).getProjectId() == "test_project");
        assert( ((DataProc) res).getRegion() == "test_region");
        assert( ((DataProc) res).getClusterName() == "test_cluster_id");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetEMRDetailsById() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "emr" + UUID.randomUUID();

        EMR emrRes = new EMR(skil, name, "test_region",
                "test_cluster_id", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((EMR) res).getName() == name);
        assert( ((EMR) res).getClusterId() == "test_cluster_id");
        assert( ((EMR) res).getRegion() == "test_region");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetHDInsightDetailsById() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "hd" + UUID.randomUUID();

        HDInsight hdRes = new HDInsight(skil, name,
                "test_subscription", "test_resource_group",
                "test_cluster", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((HDInsight) res).getName() == name);
        assert( ((HDInsight) res).getClusterName() == "test_cluster");
        assert( ((HDInsight) res).geteResourceGroupName() == "test_resource_group");
        assert( ((HDInsight) res).getSubscriptionId() == "test_subscription");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetAzureStorageDetailsById() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "azure" + UUID.randomUUID();

        AzureStorage asRes = new AzureStorage(skil, name,
                "test_container", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((AzureStorage) res).getName() == name);
        assert( ((AzureStorage) res).getContainerName() == "test_container");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetGoogleStorageDetailsbyId() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "google" + UUID.randomUUID();

        GoogleStorage gsRes = new GoogleStorage( skil,name,
                "test_project", "test_bucket", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((GoogleStorage) res).getName() == name);
        assert( ((GoogleStorage) res).getBucketName() == "test_bucket");
        assert( ((GoogleStorage) res).getProjectId() == "test_project");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetS3DetailsbyId() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "s3" + UUID.randomUUID();

        S3 s3Res = new S3( skil, name,
                "test_bucket", "test_region", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((S3) res).getName() == name);
        assert( ((S3) res).getBucket() == "test_bucket");
        assert( ((S3) res).getRegion() == "test_region");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetHDFSDetailsbyId() throws ApiException, Exception {
        Skil skil = new Skil();

        String name = "s3" + UUID.randomUUID();

        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "test_port", 0L);

        Resource res = Utils.getResourceDetailsById(skil, 0L);
        assert( ((HDFS) res).getName() == name);
        assert( ((HDFS) res).getNameNodeHost() == "test_host");
        assert( ((HDFS) res).getNameNodePort() == "test_port");
        assert(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testCast() throws ApiException, Exception{
        Skil skil = new Skil();

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", "test_credential");
        dataRes.delete();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region",
                "test_crendeital","test_cluster_id");
        emrRes.delete();

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluser", "test_credential");
        hdRes.delete();

        S3 s3Res = new S3( skil, "s3" + UUID.randomUUID(),
                "test_bucket", "test_region", "test_credential");
        s3Res.delete();

        GoogleStorage gsRes = new GoogleStorage( skil,"google" + UUID.randomUUID(),
                "test_project", "test_bucket", "test_credential");
        gsRes.delete();

        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "test_port", "test_credential");
        hdfsRes.delete();

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", "test_credential");
        asRes.delete();
    }
}
