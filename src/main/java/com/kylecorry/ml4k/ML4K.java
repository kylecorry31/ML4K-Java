package com.kylecorry.ml4k;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class ML4K {

    /**
     * The URL endpoint for ML4K, where the %s is the API key.
     */
    private static final String ENDPOINT_URL = "https://machinelearningforkids.co.uk/api/scratch/%s/classify";

    /**
     * The API key for ML4K.
     */
    private String key;

    /**
     * The listeners to be called during classification.
     */
    private OnClassificationListener classificationListener;
    private OnClassificationErrorListener errorListener;

    /**
     * Create an instance of ML4K.
     *
     * @param key Your API key for ML4K.
     * @param classificationListener  The listener which will be notified when a classification completes.
     * @param errorListener The listener which will be notified when a classification fails.
     */
    public ML4K(String key, OnClassificationListener classificationListener, OnClassificationErrorListener errorListener) {
        this.key = key;
        this.classificationListener = classificationListener;
        this.errorListener = errorListener;
    }

    /**
     * Get the API key.
     * @return The API key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the API key.
     * @param key The API key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    // Methods
    /**
     * Classify an image using ML4K.
     * @param path The path to the image.
     */
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

    /**
     * Classify text using ML4K.
     * @param text The text to classify.
     */
    public void classifyText(final String text){
            try {
                // Get the data
                String urlStr = getURL() + "?data=" + URLEncoder.encode(text, "UTF-8");

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
                        Classification classification = Classification.fromJson(text, json);
                        gotClassification(classification);
                    } catch (JsonParseException e){
                        gotError(text, "Bad data from server.");
                    }
                } else {
                    gotError(text, "Bad response from server: " + conn.getResponseCode());
                    conn.disconnect();
                }

            } catch (UnsupportedEncodingException e) {
                gotError(text, "Could not encode text");
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                gotError(text, "Could not generate URL");
            } catch (IOException e) {
                gotError(text, "No Internet connection.");
            }
    }

    /**
     * Classify numbers using ML4K.
     * @param numbers The numbers to classify.
     */
    public void classifyNumbers(final double... numbers){
        final String numberStr = Arrays.toString(numbers);
        try {
            // Get the data
            String urlStr = getURL() + "?" + urlEncodeList("data", numbers);

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
                    Classification classification = Classification.fromJson(numberStr, json);
                    gotClassification(classification);
                } catch (JsonParseException e){
                    gotError(numberStr, "Bad data from server.");
                }
            } else {
                gotError(numberStr, "Bad response from server: " + conn.getResponseCode());
                conn.disconnect();
            }

        } catch (UnsupportedEncodingException e) {
            gotError(numberStr, "Could not encode text");
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            gotError(numberStr, "Could not generate URL");
        } catch (IOException e) {
            gotError(numberStr, "No Internet connection.");
        }
    }

    /**
     * Classify text in the background.
     * @param text The text to classify.
     */
    public void asyncClassifyText(final String text){
        runInBackground(() -> classifyText(text));
    }

    /**
     * Classify an image in the background.
     * @param path The path to the image.
     */
    public void asyncClassifyImage(final String path){
        runInBackground(() -> classifyImage(path));
    }


    // Events
    /**
     * Called when an error is received.
     * @param data The data to be classified.
     * @param error The error received.
     */
    private void gotError(String data, String error){
        errorListener.gotError(data, error);
    }

    /**
     * Called when a classification completes.
     * @param classification The classification results.
     */
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

    /**
     * Run in the background.
     * @param runnable The code to run in the background.
     */
    private void runInBackground(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * Encode a list for a URL get request.
     * @param paramName The name of the parameter.
     * @param list The list to encode.
     * @return The encoded list.
     */
    private String urlEncodeList(String paramName, double[] list) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (list == null || list.length == 0){
            return "";
        }

        for (int i = 0; i < list.length; i++) {
            sb.append(paramName);
            sb.append('=');
            sb.append(list[i]);
            if (i != list.length - 1){
                sb.append('&');
            }
        }

        return sb.toString();
    }

}
