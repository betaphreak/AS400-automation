package ro.nn.qa.bootstrap;

import org.tn5250j.event.BootEvent;
import org.tn5250j.event.BootListener;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Alexandru Giurovici on 02.09.2015.
 */
public class Controller extends Thread
{
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private Vector<BootListener> listeners;
    private BootEvent event;
    public static final int CONTROLLER_PORT = 3036;

    private TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());

    public Controller()
    {
        super("QA Controller");
        try
        {
            serverSocket = new ServerSocket(CONTROLLER_PORT);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot bind automation controller to port " + CONTROLLER_PORT);
        }
    }

    public void run()
    {
        log.info("Automation Controller listening");
        while (true)
        {
            listen();
            getNewSessionOptions();
            log.info("Got one");
        }

    }

    // add a listener to list.
    public synchronized void addListener(BootListener listener) {

        if (listeners == null)
        {
            listeners = new java.util.Vector<>(3);
        }
        listeners.addElement(listener);
        log.info("Added new listener.");

    }

    // notify all registered listeners of the event.
    private void fireBootEvent() {

        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++)
            {
                BootListener target = listeners.elementAt(i);
                target.bootOptionsReceived(event);
            }
        }
    }

    // Listen for a connection from another session starting.
    private void listen() {

        try
        {
            socket = serverSocket.accept();
        }
        catch (IOException e) {
            log.warn(this.getName() + ": " + e.getMessage());
        }

    }

     // Retrieve the boot options from the other JVM wanting to start a new session.
    private void getNewSessionOptions()
    {
        try {

            BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            event = new BootEvent(this,in.readLine());
            System.out.println(event.getNewSessionOptions());
            fireBootEvent();
            in.close();
            socket.close();
        }
        catch (IOException e)
        {
            log.warn(e.getLocalizedMessage());
        }

    }



}
