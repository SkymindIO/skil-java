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

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class ResourcesTest {

    @Before
    public void cleanResources() throws Exception {
        Skil skil = new Skil();
        Utils.deleteAllResources(skil); // Clean up before test, to make sure length of resources tests work
    }

    @Test
    public void testGetResourceById() throws Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region", "credentials",
                "test_cluster_id");
        Long resourceId = emrRes.getResourceId();

        Resource res = Utils.getResourceById(skil, resourceId);

        assertTrue(resourceId.equals(res.getResourceId()));
        emrRes.delete();
    }

    @Test
    public void testGetAllResources() throws Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region", "credentials",
                "test_cluster_id");

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", "credentials");

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluser", "credentials");

        List<Resource> resList = Utils.getAllResources(skil);

        assertTrue(resList.size() == 3);

        emrRes.delete();
        dataRes.delete();
        hdRes.delete();
    }

    @Test
    public void testGetResourceByType() throws Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region", "credentials",
                "test_cluster_id");

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", "credenteials");

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluser", "credentials");

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", "credentials");

        GoogleStorage gsRes = new GoogleStorage( skil,"google" + UUID.randomUUID(),
                "test_project", "test_bucket", "credentials");


        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "12345", "credentials"); // Make sure NameNodePort is not too long. (Should be <=5 characters)

        S3 s3Res = new S3( skil, "s3" + UUID.randomUUID(),
                "test_bucket", "test_region", "credentials");

        List<Resource> computeList = Utils.getResourceByType(skil, ResourceType.COMPUTE);
        List<Resource> storageList = Utils.getResourceByType(skil, ResourceType.STORAGE);

        assertTrue(computeList.size() == 3);

//        assertTrue(computeList.get(0).getResourceId() == 0L);
//        assertTrue(computeList.get(1).getResourceId() == 1L);
//        assertTrue(computeList.get(2).getResourceId() == 2L);

        assertTrue(storageList.size() == 4);

//        assertTrue(storageList.get(3).getResourceId() == 3L);
//        assertTrue(storageList.get(4).getResourceId() == 4L);
//        assertTrue(storageList.get(5).getResourceId() == 5L);
//        assertTrue(storageList.get(6).getResourceId() == 6L);

        emrRes.delete();
        dataRes.delete();
        asRes.delete();
        hdRes.delete();
        gsRes.delete();
        hdfsRes.delete();
        s3Res.delete();
    }

    @Test
    public void testGetResourceBySubType() throws Exception {
        Skil skil = new Skil();

        EMR emrRes = new EMR(skil, "emr" + UUID.randomUUID(), "test_region", "credentials",
                "test_cluster_id");

        DataProc dataRes = new DataProc(skil, "dataProc" + UUID.randomUUID(), "test_project",
                "test_region", "test_cluster_id", "credentials");

        HDInsight hdRes = new HDInsight(skil, "hdInsight" + UUID.randomUUID(),
                "test_subscription", "test_resource_group",
                "test_cluster", "credentials");

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", "credentials");

        GoogleStorage gsRes = new GoogleStorage( skil,"google" + UUID.randomUUID(),
                "test_project", "test_bucket", "credentials");


        HDFS hdfsRes = new HDFS(skil, "hdfs" + UUID.randomUUID(), "test_host",
                "12345", "credentials");

        S3 s3Res = new S3( skil, "s3" + UUID.randomUUID(),
                "test_bucket", "test_region", "credentials");

        List<Resource> emrList = Utils.getResourceBySubType(skil, ResourceSubType.EMR);
        assertTrue(emrList.size() == 1);
//        assertTrue(emrList.get(0).getResourceId() == 0L);
        emrRes.delete();

        List<Resource> dataProcList = Utils.getResourceBySubType(skil, ResourceSubType.DataProc);
        assertTrue(dataProcList.size() == 1);
//        assertTrue(dataProcList.get(0).getResourceId() == 1L);
        dataRes.delete();

        List<Resource> hdInsightList = Utils.getResourceBySubType(skil, ResourceSubType.HDInsight);
        assertTrue(hdInsightList.size() == 1);
//        assertTrue(hdInsightList.get(0).getResourceId() == 2L);
        hdRes.delete();

        List<Resource> azureList = Utils.getResourceBySubType(skil, ResourceSubType.AzureStorage);
        assertTrue(azureList.size() == 1);
//        assertTrue(azureList.get(0).getResourceId() == 3L);
        asRes.delete();

        List<Resource> googleList = Utils.getResourceBySubType(skil, ResourceSubType.GoogleStorage);
        assertTrue(googleList.size() == 1);
//        assertTrue(googleList.get(0).getResourceId() == 4L);
        gsRes.delete();

        List<Resource> hdfsList = Utils.getResourceBySubType(skil, ResourceSubType.HDFS);
        assertTrue(hdfsList.size() == 1);
//        assertTrue(hdfsList.get(0).getResourceId() == 5L);
        hdfsRes.delete();

        List<Resource> s3List = Utils.getResourceBySubType(skil, ResourceSubType.S3);
        assertTrue(s3List.size() == 1);
//        assertTrue(s3List.get(0).getResourceId() == 6L);
        s3Res.delete();
    }

    @Test
    public void testGetDataProcDetailsById() throws Exception {
        Skil skil = new Skil();

        String name = "dataProc" + UUID.randomUUID();

        DataProc dataRes = new DataProc(skil, name, "test_project",
                "test_region", "test_cluster_id", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, dataRes.getResourceId());
        assertTrue( ((DataProc) res).getName().equals(dataRes.getName()));
        assertTrue( ((DataProc) res).getProjectId().equals("test_project"));
        assertTrue( ((DataProc) res).getRegion().equals("test_region"));
        assertTrue( ((DataProc) res).getClusterName().equals("test_cluster_id"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetEMRDetailsById() throws Exception {
        Skil skil = new Skil();

        String name = "emr" + UUID.randomUUID();

        EMR emrRes = new EMR(skil, name, "test_region", "credentials",
                "test_cluster_id");

        Resource res = Utils.getResourceDetailsById(skil, emrRes.getResourceId());
        assertTrue( ((EMR) res).getName().equals(emrRes.getName()));
        assertTrue( ((EMR) res).getClusterId().equals("test_cluster_id"));
        assertTrue( ((EMR) res).getRegion().equals("test_region"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetHDInsightDetailsById() throws Exception {
        Skil skil = new Skil();

        String name = "hd" + UUID.randomUUID();

        HDInsight hdRes = new HDInsight(skil, name,
                "test_subscription", "test_resource_group",
                "test_cluster", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, hdRes.getResourceId());
        assertTrue( ((HDInsight) res).getName().equals(hdRes.getName()));
        assertTrue( ((HDInsight) res).getClusterName().equals("test_cluster"));
        assertTrue( ((HDInsight) res).geteResourceGroupName().equals("test_resource_group"));
        assertTrue( ((HDInsight) res).getSubscriptionId().equals("test_subscription"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetAzureStorageDetailsById() throws Exception {
        Skil skil = new Skil();

        String name = "azure" + UUID.randomUUID();

        AzureStorage asRes = new AzureStorage(skil, name,
                "test_container", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, asRes.getResourceId());
        assertTrue( ((AzureStorage) res).getName().equals(asRes.getName()));
        assertTrue( ((AzureStorage) res).getContainerName().equals("test_container"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetGoogleStorageDetailsbyId() throws Exception {
        Skil skil = new Skil();

        String name = "google" + UUID.randomUUID();

        GoogleStorage gsRes = new GoogleStorage( skil, name,
                "test_project", "test_bucket", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, gsRes.getResourceId());
        assertTrue( ((GoogleStorage) res).getName().equals(gsRes.getName()));
        assertTrue( ((GoogleStorage) res).getBucketName().equals("test_bucket"));
        assertTrue( ((GoogleStorage) res).getProjectId().equals("test_project"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetS3DetailsbyId() throws Exception {
        Skil skil = new Skil();

        String name = "s3" + UUID.randomUUID();

        S3 s3Res = new S3( skil, name,
                "test_bucket", "test_region", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, s3Res.getResourceId());
        assertTrue( ((S3) res).getName().equals(s3Res.getName()));
        assertTrue( ((S3) res).getBucket().equals("test_bucket"));
        assertTrue( ((S3) res).getRegion().equals("test_region"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testGetHDFSDetailsbyId() throws Exception {
        Skil skil = new Skil();

        String name = "hdfs" + UUID.randomUUID();

        HDFS hdfsRes = new HDFS(skil, name, "test_host",
                "12345", "credentials");

        Resource res = Utils.getResourceDetailsById(skil, hdfsRes.getResourceId());
        assertTrue( ((HDFS) res).getName().equals(hdfsRes.getName()));
        assertTrue( ((HDFS) res).getNameNodeHost().equals("test_host"));
        assertTrue( ((HDFS) res).getNameNodePort().equals("12345"));
//        assertTrue(res.getResourceId() == 0L);
        res.delete();
    }

    @Test
    public void testCast() throws Exception {
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
                "12345", "test_credential");
        hdfsRes.delete();

        AzureStorage asRes = new AzureStorage(skil, "azure" + UUID.randomUUID(),
                "test_container", "test_credential");
        asRes.delete();
    }
}
