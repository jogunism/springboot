package com.range.shipon.component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpDispatcher {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(HttpDispatcher.class);
	
	public String request(String targetURL) {
		return request(targetURL, "POST");
	}

    public String request(String targetURL, String method) {

        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder("");
        try {
	        // Create connection
	        URL url = new URL(targetURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod(method);
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestProperty("Content-Language", "en-US");
	        connection.setUseCaches(false);
	        connection.setDoOutput(true);
	
	        // Send request
	        DataOutputStream outputstream = new DataOutputStream(connection.getOutputStream());
	        outputstream.close();
	
	        // Get Response  
	        InputStream is = connection.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	        String line;
	        while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\r");
	        }
	        rd.close();

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }

}
