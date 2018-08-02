package com.kylecorry.ml4k;

import org.junit.Test;

public class ML4KTest {

    @Test
    public void testImage(){
        String path = "image.jpg"; // Replace with your image
        String key = "YOU'RE API KEY HERE";
        OnClassificationListener listener = System.out::println;
        OnClassificationErrorListener errorListener = (data, error) -> System.out.println(data + ": " + error);
        ML4K ml4K = new ML4K(key, listener, errorListener);
        ml4K.classifyImage(path);
    }

    @Test
    public void testText(){
        String text = "I'm happy";
        String key = "YOU'RE API KEY HERE";
        OnClassificationListener listener = System.out::println;
        OnClassificationErrorListener errorListener = (data, error) -> System.out.println(data + ": " + error);
        ML4K ml4K = new ML4K(key, listener, errorListener);
        ml4K.classifyText(text);
    }

}