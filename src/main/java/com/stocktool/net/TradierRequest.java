package com.stocktool.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class TradierRequest {

    public static void main(String[] args) throws IOException {

        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpGet request = new HttpGet("https://sandbox.tradier.com/v1/markets/timesales?symbol=NFLX&interval=15min&start=2017-03-01%2010:00&end2017-03-01%2011:00");

            request.addHeader("Accept" , "application/json");
            request.addHeader("Authorization", "Bearer u5gkiqREijmT01cAFHxCTRJvIAaR");

            final HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            } else {
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = responseBody.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
}