package s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Region;

import java.io.File;

/**
 * @author Tushar Chokshi @ 8/2/17.
 */
public class S3JavaSDKExample {
    public static void main(String[] args)throws Exception {

        //demoServerSideEncryptionNotResource();
        createAndPopulateSimpleBucket();

    }

    public static void createAndPopulateSimpleBucket() throws Exception {


        // Create AWSCredentials object using your access key and secret access key
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);


        // S3 Client
        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);


        // Delete existing buckets
        BucketUtils.deleteAllBuckets(s3Client);


        // Creating new bucket
        String newBucketName = "chokshi" + System.currentTimeMillis();
        s3Client.createBucket(newBucketName, Region.US_West_2);

        // Putting a file in a bucket
        final String fileName = "sometext.txt";
        File file = new File(S3JavaSDKExample.class.getClassLoader().getResource(fileName).toURI());

        /*
        remember this is a new bucket and "folders" dont exist in S3, they are logical entities derived from the
        path specified in the key. S3 is just a key value store.
        they are created on the fly when we upload an object with a specific key path
        Also, the folder setting in the console on the S3 folder for server side encryption is a slightly misleading
        instruction to encrypt the selected resources - it does NOT set a persistant setting on all resources uploaded
        into that folder
         */


        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "unencrypted/" + fileName + "." + System.currentTimeMillis(), file);
            PutObjectResult response1 = s3Client.putObject(putRequest1);
            System.out.println("Uploaded object encryption status is " +
                    response1.getSSEAlgorithm());
        }


    }



    public static void demoServerSideEncryption() throws Exception {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);

        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

        for (Bucket bucket:s3Client.listBuckets()){
            BucketUtils.deleteBucket(bucket.getName(), s3Client);
        }

        String newBucketName = "chokshi" + System.currentTimeMillis();
        s3Client.createBucket(newBucketName);

        String policy = BucketUtils.readFileFromResources("encrypted-folder-policy.txt").replace("bucketname",newBucketName);

        /*
        This is a bucket policy - the bucket itself must be mentioned in the policy explicitly
         */

        System.out.println(policy);
        s3Client.setBucketPolicy(newBucketName, policy);

        final String fileName = "sometext.txt";

        File file = new File(S3JavaSDKExample.class.getResource(fileName).toURI());

        /*
        remember this is a new bucket and "folders" dont exist in S3, they are logical entities derived from the
        path specified in the key. S3 is just a key value store.
        they are created on the fly when we upload an object with a specific key path
        Also, the folder setting in the console on the S3 folder for server side encryption is a slightly misleading
        instruction to encrypt the selected resources - it does NOT set a persistant setting on all resources uploaded
        into that folder
         */


        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "unencrypted/" + fileName + "." + System.currentTimeMillis(), file);
            PutObjectResult response1 = s3Client.putObject(putRequest1);
            System.out.println("Uploaded object encryption status is " +
                    response1.getSSEAlgorithm());
        }
        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "encrypted/" + fileName + "." + System.currentTimeMillis(), file);

            try {
                PutObjectResult response1 = s3Client.putObject(putRequest1);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("was not able to store an unencrypted file in this folder");
            }

        }
        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "encrypted/" + fileName + "." + System.currentTimeMillis(), file);
            ObjectMetadata objectMetadata1 = new ObjectMetadata();
            objectMetadata1.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            putRequest1.setMetadata(objectMetadata1);


            PutObjectResult response1 = s3Client.putObject(putRequest1);
            System.out.println("Uploaded object encryption status is " +
                    response1.getSSEAlgorithm());
        }

    }


   /* public static void demoServerSideEncryptionNotResource() throws Exception {


        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);

        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

        BucketUtils.deleteAllBuckets(s3Client);

        String newBucketName = "chokshi" + System.currentTimeMillis();
        s3Client.createBucket(newBucketName);

        String policy = BucketUtils.readFileFromResources("encrypted-folder-policy-notresource.txt").replace("bucketname",newBucketName);

        *//*
        This is a bucket policy - the bucket itself must be mentioned in the policy explicitly
         *//*

        System.out.println(policy);
        s3Client.setBucketPolicy(newBucketName, policy);

        final String fileName = "sometext.txt";

        File file = new File(S3JavaSDKExample.class.getResource(fileName).toURI());

        *//*
        remember this is a new bucket and "folders" dont exist in S3, they are logical entities derived from the
        path specified in the key. S3 is just a key value store.
        they are created on the fly when we upload an object with a specific key path
        Also, the folder setting in the console on the S3 folder for server side encryption is a slightly misleading
        instruction to encrypt the selected resources - it does NOT set a persistant setting on all resources uploaded
        into that folder
         *//*


        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "unencrypted/" + fileName + "." + System.currentTimeMillis(), file);
            PutObjectResult response1 = s3Client.putObject(putRequest1);
            System.out.println("Uploaded object encryption status is " +
                    response1.getSSEAlgorithm());
        }
        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "bananas/" + fileName + "." + System.currentTimeMillis(), file);

            try {
                PutObjectResult response1 = s3Client.putObject(putRequest1);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("was not able to store an unencrypted file in this folder");
            }

        }
        {
            PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, "bananas/" + fileName + "." + System.currentTimeMillis(), file);
            ObjectMetadata objectMetadata1 = new ObjectMetadata();
            objectMetadata1.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            putRequest1.setMetadata(objectMetadata1);


            PutObjectResult response1 = s3Client.putObject(putRequest1);
            System.out.println("Uploaded object encryption status is " +
                    response1.getSSEAlgorithm());
        }

    }*/

}
