package com.kylecorry.ml4k;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ML4KTest {

    // TODO: Verify that the data is being correctly sent

    private static final String FAKE_KEY = "00000000-0000-1000-0000-00000000000000000000-0000-4000-0000-000000000000";

    @Test
    public void canCreateWithValidAPIKey() throws ML4KException {
        ML4K ml4K = new ML4K(FAKE_KEY);
        assertNotNull(ml4K);
        ml4K.setAPIKey(FAKE_KEY);
    }

    @Test(expected = ML4KException.class)
    public void throwsWithNoKey() throws ML4KException {
        new ML4K("");
    }

    @Test(expected = ML4KException.class)
    public void throwsWithInvalidKey() throws ML4KException {
        new ML4K("000000000");
    }

    @Test
    public void canClassifyText() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", "{\n\"class_name\": \"Good\",\n\"confidence\": 18.9\n}"));
        Classification classification = ml4K.classify("Test");
        assertEquals("Good", classification.getClassification());
        assertEquals(18.9, classification.getConfidence(), 0.0);
    }

    @Test
    public void canClassifyNumbers() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", "{\n\"class_name\": \"Test\",\n\"confidence\": 10.0\n}"));
        Classification classification = ml4K.classify(Arrays.asList(1.2, 3.4));
        assertEquals("Test", classification.getClassification());
        assertEquals(10.0, classification.getConfidence(), 0.0);
    }

    @Test
    public void canClassifyAnImage() throws Exception {
//        MockHttpStrategy http = new MockHttpStrategy();
//        ML4K ml4K = createValidML4K(http);
//        http.setResponse(new APIResponse(200, "OK", "{\n\"class_name\": \"Test\",\n\"confidence\": 10.0\n}"));
//        Classification classification = ml4K.classify(Arrays.asList(1.2, 3.4));
//        assertEquals("Test", classification.getClassification());
//        assertEquals(10.0, classification.getConfidence(), 0.0);
    }

    @Test
    public void canAddTextTrainingData() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", ""));
        ml4K.addTrainingData("Test", "123");
    }

    @Test
    public void canAddNumbersTrainingData() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", ""));
        ml4K.addTrainingData("Test", Arrays.asList(1.2, 3.4));
    }

    @Test
    public void canAddImageTrainingData() throws Exception {
//        MockHttpStrategy http = new MockHttpStrategy();
//        ML4K ml4K = createValidML4K(http);
//        http.setResponse(new APIResponse(200, "OK", ""));
//        ml4K.addTrainingData("Test", Arrays.asList(1.2, 3.4));
    }

    @Test
    public void canGetModelStatus() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", "{\"status\": 2, \"msg\": \"Ready\"}"));
        ModelStatus status = ml4K.getModelStatus();
        assertEquals("Ready", status.getMessage());
        assertEquals(2, status.getStatusCode());
    }

    @Test
    public void canTrainAModel() throws Exception {
        MockHttpStrategy http = new MockHttpStrategy();
        ML4K ml4K = createValidML4K(http);
        http.setResponse(new APIResponse(200, "OK", ""));
        ml4K.train();
    }


    private ML4K createValidML4K(MockHttpStrategy strategy) throws ML4KException {
        ML4K ml4K = new ML4K(FAKE_KEY);
        ml4K.setHttpStrategy(strategy);
        return ml4K;
    }

}