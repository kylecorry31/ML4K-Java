package com.kylecorry.ml4k;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

public class ML4K {

    private static final String ENDPOINT_URL = "https://machinelearningforkids.co.uk/api/scratch/%s/classify";

    private String key;

    private OnClassificationListener classificationListener;
    private OnClassificationErrorListener errorListener;

    public ML4K(String key, OnClassificationListener classificationListener, OnClassificationErrorListener errorListener) {
        this.key = key;
        this.classificationListener = classificationListener;
        this.errorListener = errorListener;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Methods

    public void classifyImage(final String path){
            try {
                // Get the data
                final String imageData = getImageData(path);
                String dataStr = "{\"data\": " + "\"" + URLEncoder.encode(imageData, "UTF-8") + "\"}";

                // Setup the request
                URL url = new URL(getURL());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setFixedLengthStreamingMode(dataStr.length());
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Type", "application/json");

                // Send image data
                conn.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(dataStr);
                os.flush();
                os.close();

                // Parse
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    final String json = read(conn.getInputStream());
                    conn.disconnect();

                    // Parse JSON
                    try {
                        Classification classification = Classification.fromJson(path, json);
                        gotClassification(classification);
                    } catch (JsonParseException e){
                        gotError(path, "Bad data from server " + json);
                    }
                } else {
                    gotError(path, "Bad response from server: " + conn.getResponseCode());
                    conn.disconnect();
                }

            } catch (UnsupportedEncodingException e) {
                gotError(path, "Could not encode image");
            } catch (MalformedURLException e) {
                gotError(path, "Could not generate URL");
            } catch (IOException e) {
                gotError(path, "No Internet connection.");
            }
    }

    public void classifyText(final String data){
            try {
                // Get the data
                String urlStr = getURL() + "?data=" + URLEncoder.encode(data, "UTF-8");

                // Setup the request
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0");

                // Parse
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    final String json = read(conn.getInputStream());
                    conn.disconnect();

                    // Parse JSON
                    try {
                        Classification classification = Classification.fromJson(data, json);
                        gotClassification(classification);
                    } catch (JsonParseException e){
                        gotError(data, "Bad data from server.");
                    }
                } else {
                    gotError(data, "Bad response from server: " + conn.getResponseCode());
                    conn.disconnect();
                }

            } catch (UnsupportedEncodingException e) {
                gotError(data, "Could not encode text");
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                gotError(data, "Could not generate URL");
            } catch (IOException e) {
                gotError(data, "No Internet connection.");
            }
    }

    public void asyncClassifyText(final String data){
        runInBackground(() -> classifyText(data));
    }

    public void asyncClassifyImage(final String path){
        runInBackground(() -> classifyImage(path));
    }


    // Events
    private void gotError(String data, String error){
        errorListener.gotError(data, error);
    }

    private void gotClassification(Classification classification) {
        classificationListener.gotClassification(classification);
    }


    // Helpers

    /**
     * Read an input stream to a String.
     * @param is The input stream.
     * @return The data from the input stream as a String.
     */
    private String read(InputStream is){
        Scanner scanner = new Scanner(is);

        StringBuilder sb = new StringBuilder();

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }

        return sb.toString();
    }

    /**
     * Get the ENDPOINT_URL for ML4K.
     * @return The ENDPOINT_URL with the key for ML4K.
     */
    private String getURL(){
        return String.format(ENDPOINT_URL, key);
    }

    /**
     * Turn the data of an image into base 64.
     * @param path The path to the image.
     * @return The data of the image as a base 64 string.
     */
    private String getImageData(final String path){
        try {
            Scanner scanner = new Scanner(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            while(scanner.hasNext()){
                sb.append(scanner.next());
            }
            scanner.close();
            byte[] encodedBytes = Base64.getEncoder().encode(sb.toString().getBytes());
            return new String(encodedBytes);
        } catch (FileNotFoundException e) {
            gotError(path, "File not found");
        }
        return "";
    }


    private void runInBackground(Runnable runnable){
        new Thread(runnable).start();
    }

}
