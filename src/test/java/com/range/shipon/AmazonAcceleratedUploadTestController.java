package com.range.shipon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;



@RunWith(SpringRunner.class)
@SpringBootTest
public class AmazonAcceleratedUploadTestController {

	@Test
	public void upload() throws IOException {

        String existingBucketName  = "*** Provide-Your-Existing-BucketName ***"; 
        String keyName             = "*** Provide-Key-Name ***";
        String filePath            = "*** Provide-File-Path ***";   

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(existingBucketName, keyName);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 5242880; // Set part size to 5 MB.

        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
            	partSize = Math.min(partSize, (contentLength - filePosition));
            	
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
										                    .withBucketName(existingBucketName).withKey(keyName)
										                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
										                    .withFileOffset(filePosition)
										                    .withFile(file)
										                    .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
            															existingBucketName, 
            															keyName, 
            															initResponse.getUploadId(), 
            															partETags);
            s3Client.completeMultipartUpload(compRequest);

        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(existingBucketName, keyName, initResponse.getUploadId()));
        }

	}
	
}
