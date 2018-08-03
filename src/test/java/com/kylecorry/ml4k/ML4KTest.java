package com.kylecorry.ml4k;

import org.junit.Test;

public class ML4KTest {

    @Test
    public void testImage(){
        String path = "image.jpg"; // Replace with your image
        String key = "YOUR API KEY HERE";
        ML4K ml4K = new ML4K(key);
        System.out.println(ml4K.classifyImage(path));
    }

    @Test
    public void testText(){
        String text = "Example text"; // Replace with your text
        String key = "YOUR API KEY HERE";
        ML4K ml4K = new ML4K(key);
        System.out.println(ml4K.classifyText(text));
    }

    @Test
    public void testNumbers(){
        double[] numbers = new double[]{0, 0, 0}; // Replace with your numbers
        String key = "YOUR API KEY HERE";
        ML4K ml4K = new ML4K(key);
        System.out.println(ml4K.classifyNumbers(numbers));
    }

}