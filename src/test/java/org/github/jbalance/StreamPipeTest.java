package org.github.jbalance;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * User: jjb
 * DateTime: 2013-05-18 01:51
 */
public class StreamPipeTest {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StreamPipeTest.class);

    @Test
    public void testPipeInput() throws Exception, StreamException {
        String str = "byte array stream";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        StreamPipe pipe = new StreamPipe(input, output, input, output);
        pipe.pipeInput();
        String out = new String(output.toByteArray());
        System.out.println("[" + out + "]");
        assert out.equals(str);
    }

    @Test
    public void testPipeOutput() throws Exception, StreamException {
        String str = "byte array stream";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        StreamPipe pipe = new StreamPipe(input, output, input, output);
        pipe.pipeOutput();
        String out = new String(output.toByteArray());
        System.out.println("[" + out + "]");
        assert out.equals(str);
    }
}
