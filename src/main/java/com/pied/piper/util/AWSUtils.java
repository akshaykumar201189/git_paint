package com.pied.piper.util;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.inject.Inject;
import com.pied.piper.core.config.AwsCredentials;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by akshay.kesarwan on 29/07/16.
 */
public class AWSUtils {
    private static final String BUCKET_NAME = "paintgit";
    private final AwsCredentials credentials;

    @Inject
    public AWSUtils(AwsCredentials credentials) {
        this.credentials = credentials;
    }

    public void uploadImageToS3(String imageData, String fileName) {
        byte[] bi = Base64.decodeBase64(imageData);
        InputStream fis = new ByteArrayInputStream(bi);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(credentials.getAccessKey(), credentials.getSecretKey());
        AmazonS3 s3 = new AmazonS3Client(awsCredentials);
        Region usWest02 = Region.getRegion(Regions.EU_CENTRAL_1);
        s3.setRegion(usWest02);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bi.length);
        metadata.setContentType("image/jpeg");
        metadata.setCacheControl("public, max-age=31536000");
        s3.putObject(BUCKET_NAME, fileName, fis, metadata);
        s3.setObjectAcl(BUCKET_NAME, fileName, CannedAccessControlList.PublicRead);
    }
}
