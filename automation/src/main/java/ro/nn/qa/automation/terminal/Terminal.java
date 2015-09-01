package ro.nn.qa.automation.terminal;

import org.tn5250j.*;
import org.tn5250j.event.*;
import org.tn5250j.framework.Tn5250jController;
import org.tn5250j.framework.common.SessionManager;
import org.tn5250j.framework.common.Sessions;
import org.tn5250j.gui.TN5250jSplashScreen;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.interfaces.GUIViewInterface;
import org.tn5250j.tools.LangTool;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

/**
 * Created by Alexandru Giurovici on 01.09.2015.
 */
public class Terminal implements BootListener, SessionListener, EmulatorActionListener
{
    private static final String PARAM_START_SESSION = "-s";

    private TerminalViewInterface frame1;
    private String[] sessionArgs = null;
    private static Properties sessions = new Properties();
    private static BootStrapper strapper = null;
    private SessionManager manager;
    private static List<TerminalViewInterface> frames;
    private TN5250jSplashScreen splash;
    private int step;
    StringBuilder viewNamesForNextStartBuilder = null;

    private TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());

    public Terminal()
    {
        loadSessions();
        frames = new ArrayList<>();
        newView();
        setDefaultLocale();
        manager = SessionManager.instance();

        Tn5250jController.getCurrent();
    }

    private void setDefaultLocale() {

        if (sessions.containsKey("emul.locale")) {
            Locale.setDefault(parseLocal(sessions.getProperty("emul.locale")));
        }
    }

    private static Locale parseLocal(String localString) {
        int x = 0;
        String[] s = {"","",""};
        StringTokenizer tokenizer = new StringTokenizer(localString, "_");
        while (tokenizer.hasMoreTokens()) {
            s[x++] = tokenizer.nextToken();
        }
        return new Locale(s[0],s[1],s[2]);
    }

    private static void loadSessions()
    {
        sessions = (ConfigureFactory.getInstance()).getProperties(ConfigureFactory.SESSIONS);
    }

    private void newView()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height - 80;
        frame1 = new TerminalFrame(this);

        frame1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        frame1.setSize(width, height);
        frame1.centerFrame();
        frames.add(frame1);
    }

    protected void closingDown(TerminalViewInterface view)
    {

        Sessions sess = manager.getSessions();

        if (log.isDebugEnabled()) {
            log.debug("number of active sessions is " + sess.getCount());
        }

        if (viewNamesForNextStartBuilder == null) {
            // preserve sessions for next boot
            viewNamesForNextStartBuilder = new StringBuilder();
        }
        while (view.getSessionViewCount() > 0) {
            SessionPanel sesspanel = view.getSessionAt(0);
            viewNamesForNextStartBuilder.append("-s ")
                    .append(sesspanel.getSessionName())
                    .append(" ");
            closeSessionInternal(sesspanel);
        }

        sessions.setProperty("emul.frame" + view.getFrameSequence(),
                view.getX() + "," +
                        view.getY() + "," +
                        view.getWidth() + "," +
                        view.getHeight());

        frames.remove(view);
        view.dispose();

        if (log.isDebugEnabled()) {
            log.debug("number of active sessions we have after shutting down " + sess.getCount());
        }

        log.info("view settings " + viewNamesForNextStartBuilder);
        if (sess.getCount() == 0) {

            sessions.setProperty("emul.width",Integer.toString(view.getWidth()));
            sessions.setProperty("emul.height",Integer.toString(view.getHeight()));
            sessions.setProperty("emul.view",viewNamesForNextStartBuilder.toString());

            // save off the session settings before closing down
            ConfigureFactory.getInstance().saveSettings(ConfigureFactory.SESSIONS,
                    ConfigureFactory.SESSIONS, "------ Defaults --------");
            if (strapper != null) {
                strapper.interrupt();
            }
            System.exit(0);
        }
    }

    protected void closeSessionInternal(SessionPanel sesspanel) {
        TerminalViewInterface f = getParentView(sesspanel);
        if (f == null) {
            return;
        }
        Sessions sessions = manager.getSessions();
        if ((sessions.item(sesspanel.getSession())) != null) {
            f.removeSessionView(sesspanel);
            manager.closeSession(sesspanel);
        }
        if (manager.getSessions().getCount() < 1) {
            closingDown(f);
        }
    }

    private TerminalViewInterface getParentView(SessionPanel session) {

        TerminalViewInterface f = null;
        for (int x = 0; x < frames.size(); x++)
        {
            f = frames.get(x);
            if (f.containsSession(session))
                return f;
        }
        return null;
    }


    public void onSessionChanged(SessionChangeEvent changeEvent) {

        Session5250 ses5250 = (Session5250)changeEvent.getSource();
        SessionPanel ses = ses5250.getGUI();

        switch (changeEvent.getState()) {
            case TN5250jConstants.STATE_REMOVE:
                closeSessionInternal(ses);
                break;
        }
    }

    public void onEmulatorAction(EmulatorActionEvent actionEvent)
    {
    SessionPanel ses = (SessionPanel)actionEvent.getSource();

        switch (actionEvent.getAction()) {
            case EmulatorActionEvent.CLOSE_SESSION:
                closeSessionInternal(ses);
                break;
            case EmulatorActionEvent.CLOSE_EMULATOR:
                throw new UnsupportedOperationException("Not yet implemented!");
            case EmulatorActionEvent.START_NEW_SESSION:
                startNewSession();
                break;

            /* TODO: remove session duplication
            case EmulatorActionEvent.START_DUPLICATE:
                startDuplicateSession(ses);
                break;
             */
        }
    }

    private static boolean containsNotOnlyNullValues(String[] stringArray) {
        if (stringArray != null) {
            for (String s : stringArray) {
                if (s != null) {
                    return true;
                }
            }
        }
        return false;
    }


    private static String getDefaultSession()
    {
        String defaultSession = sessions.getProperty("emul.default");
        if (defaultSession != null && !defaultSession.trim().isEmpty()) {
            return defaultSession;
        }
        return null;
    }

    private static void parseArgs(String theStringList, String[] s) {
        int x = 0;
        StringTokenizer tokenizer = new StringTokenizer(theStringList, " ");
        while (tokenizer.hasMoreTokens()) {
            s[x++] = tokenizer.nextToken();
        }
    }


    private String openConnectSessionDialog ()
    {
        splash.setVisible(false);
        ConnectDialog sc = new ConnectDialog(frame1, LangTool.getString("ss.title"),sessions);

        // load the new session information from the session property file
        loadSessions();
        return sc.getConnectKey();
    }


    private void openConnectSessionDialogAndStartSelectedSession() {
        String sel = openConnectSessionDialog();
        Sessions sess = manager.getSessions();
        if (sel != null) {
            String selArgs = sessions.getProperty(sel);
            sessionArgs = new String[TN5250jConstants.NUM_PARMS];
            parseArgs(selArgs, sessionArgs);

            newSession(sel, sessionArgs);
        } else {
            if (sess.getCount() == 0)
                System.exit(0);
        }
    }

    private void startNewSession()
    {
        String sel = "";
        if (containsNotOnlyNullValues(sessionArgs) && !sessionArgs[0].startsWith("-"))
        {
            sel = sessionArgs[0];
        } else {
            sel = getDefaultSession();
        }

        Sessions sess = manager.getSessions();

        if (sel != null && sess.getCount() == 0 && sessions.containsKey(sel))
        {
            sessionArgs = new String[TN5250jConstants.NUM_PARMS];
            parseArgs(sessions.getProperty(sel), sessionArgs);
        }

        if (sessionArgs == null || sess.getCount() > 0 || sessions.containsKey("emul.showConnectDialog")) {
            openConnectSessionDialogAndStartSelectedSession();
        } else {
            newSession(sel, sessionArgs);
        }
    }

    private static boolean isSpecified(String parm, String[] args)
    {
        if (args == null)
            return false;

        for (int x = 0; x < args.length; x++)
        {
            if (args[x] != null && args[x].equals(parm))
                return true;
        }
        return false;
    }

    static private String getParm(String parm, String[] args)
    {
        for (int x = 0; x < args.length; x++)
        {
            if (args[x].equals(parm))
                return args[x+1];
        }
        return null;
    }

    private synchronized void newSession(String sel,String[] args) {

        Properties sesProps = new Properties();

        String propFileName = null;
        String session = args[0];

        // Start loading properties
        sesProps.put(TN5250jConstants.SESSION_HOST,session);

        if (isSpecified("-e",args))
            sesProps.put(TN5250jConstants.SESSION_TN_ENHANCED,"1");

        if (isSpecified("-p",args)) {
            sesProps.put(TN5250jConstants.SESSION_HOST_PORT,getParm("-p",args));
        }

        if (isSpecified("-f",args))
            propFileName = getParm("-f",args);

        /*  TODO: remove default codepage behavior

        if (isSpecified("-cp",args))
            sesProps.put(TN5250jConstants.SESSION_CODE_PAGE ,getParm("-cp",args));

        */
        sesProps.put(TN5250jConstants.SESSION_CODE_PAGE, 870);


        if (isSpecified("-gui",args))
            sesProps.put(TN5250jConstants.SESSION_USE_GUI,"1");

        if (isSpecified("-t", args))
            sesProps.put(TN5250jConstants.SESSION_TERM_NAME_SYSTEM, "1");

        if (isSpecified("-132",args))
            sesProps.put(TN5250jConstants.SESSION_SCREEN_SIZE,TN5250jConstants.SCREEN_SIZE_27X132_STR);
        else
            sesProps.put(TN5250jConstants.SESSION_SCREEN_SIZE,TN5250jConstants.SCREEN_SIZE_24X80_STR);

        /*  are we to use a ssl and if we are what type
        if (isSpecified("-sslType",args)) {

            sesProps.put(TN5250jConstants.SSL_TYPE,getParm("-sslType",args));
        }
        */
        sesProps.put(TN5250jConstants.SSL_TYPE, TN5250jConstants.SSL_TYPE_TLS);


        // check if device name is specified
        if (isSpecified("-dn=hostname",args)){
            String dnParam;

            // use IP address as device name
            try{
                dnParam = InetAddress.getLocalHost().getHostName();
            }
            catch(UnknownHostException uhe){
                dnParam = "UNKNOWN_HOST";
            }

            sesProps.put(TN5250jConstants.SESSION_DEVICE_NAME ,dnParam);
        }
        else if (isSpecified("-dn",args)){

            sesProps.put(TN5250jConstants.SESSION_DEVICE_NAME ,getParm("-dn",args));
        }

        if (isSpecified("-hb",args))
            sesProps.put(TN5250jConstants.SESSION_HEART_BEAT,"1");

        int sessionCount = manager.getSessions().getCount();

        Session5250 s2 = manager.openSession(sesProps,propFileName,sel);
        SessionPanel s = new SessionPanel(s2);


        if (!frame1.isVisible()) {
            splash.updateProgress(++step);

            // Here we check if this is the first session created in the system.
            //  We have to create a frame on initialization for use in other scenarios
            //  so if this is the first session being added in the system then we
            //  use the frame that is created and skip the part of creating a new
            //  view which would increment the count and leave us with an unused
            //  frame.
            if (isSpecified("-noembed",args) && sessionCount > 0) {
                newView();
            }
            splash.setVisible(false);
            frame1.setVisible(true);
            frame1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        else {
            if (isSpecified("-noembed",args)) {
                splash.updateProgress(++step);
                newView();
                splash.setVisible(false);
                frame1.setVisible(true);
                frame1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        }

        if (isSpecified("-t",args))
            frame1.addSessionView(sel,s);
        else
            frame1.addSessionView(session,s);

        s.connect();

        s.addEmulatorActionListener(this);
    }


    public void bootOptionsReceived(BootEvent bootEvent) {
        log.info(" boot options received " + bootEvent.getNewSessionOptions());

        // reload setting, to ensure correct bootstraps
        ConfigureFactory.getInstance().reloadSettings();

        // If the options are not equal to the string 'null' then we have boot options
        if (!bootEvent.getNewSessionOptions().equals("null"))
        {
            // check if a session parameter is specified on the command line
            String[] args = new String[TN5250jConstants.NUM_PARMS];
            parseArgs(bootEvent.getNewSessionOptions(), args);

            if (isSpecified("-s",args)) {

                String sd = getParm("-s",args);
                if (sessions.containsKey(sd)) {
                    parseArgs(sessions.getProperty(sd), args);
                    final String[] args2 = args;
                    final String sd2 = sd;
                    SwingUtilities.invokeLater( () -> newSession(sd2, args2) );
                }
            }
            else {

                if (args[0].startsWith("-")) {
                    SwingUtilities.invokeLater( () -> startNewSession() );
                }
                else {
                    final String[] args2 = args;
                    final String sd2 = args[0];
                    SwingUtilities.invokeLater( () -> newSession(sd2,args2) );
                }
            }
        }
        else
        {
            SwingUtilities.invokeLater(() -> startNewSession() );
        }
    }


}
