package test4nn;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.Pipe;

/**
 * Created by Alexandru Giurovici on 31.08.2015.
 */
public class ExpectTest {
    @BeforeClass
    public static void setup() throws Exception {
    }

    @AfterClass
    public static void close() throws Exception {
    }

    @Test
    public void testExpect4NN()
    {
        final Pipe pipe;
        try {
            pipe = Pipe.open();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to open pipe!");
        }
    }



}
