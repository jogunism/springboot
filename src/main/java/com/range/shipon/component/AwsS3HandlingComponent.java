package com.range.shipon.component;

import java.io.File;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
public class AwsS3HandlingComponent {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3HandlingComponent.class);
	
	@Value("${aws.bucket.name}")
	private String bucketName;

	public void upload(String path) {
		String keyName = Paths.get(path).getFileName().toString();
		try {
			AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
			s3.putObject(this.bucketName, "static/zara/"+ keyName, new File(path));
			logger.info("uploaed : "+ keyName);
		} catch (AmazonServiceException e) {
		    logger.error(e.getErrorCode() +" : "+ e.getErrorMessage());
		}
	}

	public void upload(String localPath, String remotePath) throws Exception {
		String keyName = Paths.get(localPath).getFileName().toString();
		try {
			AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
			s3.putObject(this.bucketName, remotePath + keyName, new File(localPath));
			logger.info("uploaed : "+ keyName);
		} catch (AmazonServiceException e) {
		    logger.error(e.getErrorCode() +" : "+ e.getErrorMessage());
		    throw new Exception ("AmazonServiceException - "+ e.getErrorMessage() +"("+ e.getErrorCode() +")");
		}
	}
}
