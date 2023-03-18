package com.spamdetector.service;

import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.util.List;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

    private SpamDetector detector;
    private List<TestFile> testResults;
    private double accuracy;
    private double precision;

    public SpamResource() {
        System.out.print("Training and testing the model, please wait");
        detector = new SpamDetector();
        File mainDirectory = new File("test");
        try {
            testResults = trainAndTest();
            // Calculate accuracy and precision here
            // Set values to accuracy and precision variables
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<TestFile> trainAndTest() throws IOException {
        if (detector == null) {
            detector = new SpamDetector();
        }

        // Load the main directory "data" from the Resources folder
        File mainDirectory = new File("src/main/resources/data");
        return detector.performTrainAndTest(mainDirectory);
    }


    @GET
    @Produces("application/json")
    public Response getSpamResults() {
        return Response.ok(testResults).build();
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
        return Response.ok(accuracy).build();
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
        return Response.ok(precision).build();
    }
}
