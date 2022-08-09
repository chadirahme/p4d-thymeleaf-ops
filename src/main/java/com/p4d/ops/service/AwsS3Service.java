package com.p4d.ops.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class AwsS3Service {

    @Value("${amazon.aws.bucketName}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @Autowired
    public AwsS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String generatePreSignedUrl(String filePath,
                                       String bucketName,
                                       HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10); //validity of 10 minutes
        return amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(), httpMethod).toString();
    }

    public List<Bucket> getS3Buckets(){
        List<Bucket> buckets = amazonS3.listBuckets();
//        for(Bucket bucket : buckets) {
//            System.out.println(bucket.getName());
//        }
        return buckets;
    }

    public List<S3ObjectSummary> getBucketObjects(){
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        return objectListing.getObjectSummaries();
//        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
//            System.out.println(os.getKey());
//        }
    }

    //https://www.section.io/engineering-education/spring-boot-amazon-s3/
    public void putObject(MultipartFile file) throws IOException {

        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        Optional<Map<String, String>> optionalMetaData= Optional.of(metadata);

        ObjectMetadata objectMetadata = new ObjectMetadata();

        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });

        //Concatenate the folder and file name to get the full destination path
        String destinationPath = bucketName + file.getName();

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        File convFile = new File(file.getOriginalFilename());
//        try {
//            file.transferTo(convFile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
       // PutObjectRequest request = new PutObjectRequest(bucketName, fileName, convFile);
        //String path = String.format("%s/%s", bucketName, UUID.randomUUID());
        String path = String.format("%s/%s", bucketName,"assets/img/home");

        amazonS3.putObject(path, fileName, file.getInputStream(), objectMetadata);
      //  amazonS3.putObject(request);
    }
}