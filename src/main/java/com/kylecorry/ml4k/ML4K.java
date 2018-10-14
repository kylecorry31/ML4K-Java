package com.kylecorry.ml4k;

import com.google.gson.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import java.io.*;
import java.net.*;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.*;

public class ML4K {

    /**
     * The version number of the API.
     */
    public static final String VERSION = "0.6";

    /**
     * The URL endpoint for ML4K, where the %s is the API key.
     */
    private static final String ENDPOINT_URL = "https://machinelearningforkids.co.uk/api/scratch/%s/classify";

    /**
     * The API key for ML4K.
     */
    private String key;

    /**
     * Create an instance of ML4K.
     *
     * @param key Your API key for ML4K.
     */
    public ML4K(String key) {
        this.key = key;
    }

    /**
     * Get the API key.
     *
     * @return The API key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the API key.
     *
     * @param key The API key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    // Methods

    /**
     * Classify an image using ML4K.
     *
     * @param path The path to the image.
     */
    public Classification classifyImage(final String path) {
        try {
            // Get the data
            final String imageData = getImageData(path);
            JsonObject obj = new JsonObject();
            obj.addProperty("data", imageData);
            String dataStr = obj.toString();

            // Setup the request
            URL url = new URL(getURL());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setFixedLengthStreamingMode(dataStr.length());
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Content-Type", "application/json");

            // Send image data
            conn.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(dataStr);
            os.flush();
            os.close();

            // Parse
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final String json = read(conn.getInputStream());
                conn.disconnect();

                // Parse JSON
                try {
                    return Classification.fromJson(path, json);
                } catch (JsonParseException e) {
                    throw new ClassificationException("Bad data from server " + json);
                }
            } else {
                int response = conn.getResponseCode();
                System.out.println(read(conn.getErrorStream()));
                conn.disconnect();
                throw new ClassificationException("Bad response from server: " + response);
            }

        } catch (UnsupportedEncodingException e) {
            throw new ClassificationException("Could not encode image");
        } catch (MalformedURLException e) {
            throw new ClassificationException("Could not generate URL");
        } catch (FileNotFoundException e) {
            throw new ClassificationException("Could not open image file.");
        } catch (IOException e) {
            throw new ClassificationException("No Internet connection.");
        }
    }

    /**
     * Classify text using ML4K.
     *
     * @param text The text to classify.
     */
    public Classification classifyText(final String text) {
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
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final String json = read(conn.getInputStream());
                conn.disconnect();

                // Parse JSON
                try {
                    return Classification.fromJson(text, json);
                } catch (JsonParseException e) {
                    throw new ClassificationException("Bad data from server.");
                }
            } else {
                int response = conn.getResponseCode();
                conn.disconnect();
                throw new ClassificationException("Bad response from server: " + response);
            }

        } catch (UnsupportedEncodingException e) {
            throw new ClassificationException("Could not encode text");
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new ClassificationException("Could not generate URL");
        } catch (IOException e) {
            throw new ClassificationException("No Internet connection.");
        }
        return null;
    }

    /**
     * Classify numbers using ML4K.
     *
     * @param numbers The numbers to classify.
     */
    public Classification classifyNumbers(final double... numbers) {
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
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final String json = read(conn.getInputStream());
                conn.disconnect();

                // Parse JSON
                try {
                    return Classification.fromJson(numberStr, json);
                } catch (JsonParseException e) {
                    throw new ClassificationException("Bad data from server.");
                }
            } else {
                int response = conn.getResponseCode();
                conn.disconnect();
                throw new ClassificationException("Bad response from server: " + response);
            }

        } catch (UnsupportedEncodingException e) {
            throw new ClassificationException("Could not encode text");
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new ClassificationException("Could not generate URL");
        } catch (IOException e) {
            throw new ClassificationException("No Internet connection.");
        }
        return null;
    }

    // Helpers

    /**
     * Read an input stream to a String.
     *
     * @param is The input stream.
     * @return The data from the input stream as a String.
     */
    private String read(InputStream is) {
        Scanner scanner = new Scanner(is);

        StringBuilder sb = new StringBuilder();

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }

        return sb.toString();
    }

    /**
     * Get the ENDPOINT_URL for ML4K.
     *
     * @return The ENDPOINT_URL with the key for ML4K.
     */
    private String getURL() {
        return String.format(ENDPOINT_URL, key);
    }

    /**
     * Turn the data of an image into base 64.
     *
     * @param path The path to the image.
     * @return The data of the image as a base 64 string.
     */
    private String getImageData(final String path) throws IOException {
       byte[] byteArray = Files.readAllBytes(new File(path).toPath());
        return base64Encoding(byteArray);
    }


    private String base64Encoding(byte[] s) {
        return Base64.getEncoder().encodeToString(s);
    }

    /**
     * Encode a list for a URL get request.
     *
     * @param paramName The name of the parameter.
     * @param list      The list to encode.
     * @return The encoded list.
     */
    private String urlEncodeList(String paramName, double[] list) {
        StringBuilder sb = new StringBuilder();
        if (list == null || list.length == 0) {
            return "";
        }

        for (int i = 0; i < list.length; i++) {
            sb.append(paramName);
            sb.append('=');
            sb.append(list[i]);
            if (i != list.length - 1) {
                sb.append('&');
            }
        }

        return sb.toString();
    }

}
