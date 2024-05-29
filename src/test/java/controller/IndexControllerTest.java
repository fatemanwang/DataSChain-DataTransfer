package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class IndexControllerTest {

    private IndexController indexControllerUnderTest;

    @Before
    public void setUp() {
        indexControllerUnderTest = new IndexController();
    }

    @Test
    public void testIndex() {
        // Setup
        // Run the test
        indexControllerUnderTest.index();

        // Verify the results
    }

    @Test
    public void testDownfile() {
        // Setup
        // Run the test
        indexControllerUnderTest.downfile();

        // Verify the results
    }

    @Test
    public void testRequest() {
        // Setup
        // Run the test
        final List result = indexControllerUnderTest.request();

        // Verify the results
    }

    @Test
    public void testMain() throws Exception {
        // Setup
        // Run the test
        IndexController.main(new String[]{"args"});

        // Verify the results
    }

    @Test(expected = IOException.class)
    public void testMain_ThrowsIOException() throws Exception {
        // Setup
        // Run the test
        IndexController.main(new String[]{"args"});
    }

    @Test
    public void testDownqukuai() {
        // Setup
        // Run the test
        indexControllerUnderTest.downqukuai();

        // Verify the results
    }

    @Test
    public void testSend() {
        // Setup
        // Run the test
        indexControllerUnderTest.send();

        // Verify the results
    }

    @Test
    public void testRefuse() {
        // Setup
        // Run the test
        indexControllerUnderTest.refuse();

        // Verify the results
    }
}
