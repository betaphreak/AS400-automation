package ro.nn.qa.test4nn;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Level;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.nn.qa.expect4nn.Expect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 31.08.2015.
 */
public class ExpectTest {

    public class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
    }

    @AfterClass
    public static void close() throws Exception {
    }

    @Test
    public void testTelnet() throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(null, "NRO", 992);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(5 * 1000);
        com.jcraft.jsch.Channel channel = session.openChannel("shell");


        Expect expect = new Expect(channel.getInputStream(),
                channel.getOutputStream());
        channel.connect();
        expect.expect("$");
        System.out.println(expect.before + expect.match);
        expect.send("ls\n");
        expect.expect("$");
        System.out.println(expect.before + expect.match);
        expect.send("exit\n");
        expect.expectEOF();
        System.out.println(expect.before);
        expect.close();

        session.disconnect();
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

        final InputStream in = Channels.newInputStream(pipe.source());
        final OutputStream out = Channels.newOutputStream(pipe.sink());

        new Thread(() -> {
            try {
                sleep(5);
                out.write("hello".getBytes());
                sleep(10);
                out.write(" world".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try { out.close(); } catch (IOException e) {}
            }
        }).start();

        Expect.addLogToConsole(Level.ALL);

        Expect xp = new Expect(in, new NullOutputStream());
        xp.expects(10, Pattern.compile(".*llo"));
        if (!xp.match.equals("hello")) throw new AssertionError();
        int retv = xp.expects(5, "world");
        if (xp.match == null) throw new AssertionError();
        if (retv != Expect.RETV_TIMEOUT) throw new AssertionError();
        xp.expects(20, "world");
        if (!xp.match.equals("world")) throw new AssertionError();
        xp.expectEOF(60);
        if (!xp.isSuccess) throw new AssertionError();
        xp.close();

        Expect.turnOffLogging();


    }



}
