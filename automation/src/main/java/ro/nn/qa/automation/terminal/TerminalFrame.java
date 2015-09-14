package ro.nn.qa.automation.terminal;

import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.*;
import org.tn5250j.gui.ButtonTabComponent;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.tools.GUIGraphicsUtils;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by Alexandru Giurovici on 01.09.2015.
 */
public class TerminalFrame extends TerminalViewInterface implements ChangeListener, TabClosedListener, SessionListener, SessionJumpListener
{
    private static final long serialVersionUID = 1L;

    private JTabbedPane sessTabbedPane = new JTabbedPane();
    private boolean embedded = false;
    private boolean hideTabBar = false;
    private TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());

    //Construct the frame
    public TerminalFrame(Terminal m) {
        super(m);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try  {
            jbInit();
        } catch(Exception e) {
            log.warn("Error during initializing!", e);
        }
    }

    //Component initialization
    private void jbInit() throws Exception  {

        this.getContentPane().setLayout(new BorderLayout());

        // update the frame sequences
        frameSeq = sequence++;

        sessTabbedPane.setBorder(BorderFactory.createEtchedBorder());
        sessTabbedPane.setBounds(new Rectangle(78, 57, 5, 5));
        sessTabbedPane.setOpaque(true);
        sessTabbedPane.setRequestFocusEnabled(false);
        sessTabbedPane.setDoubleBuffered(false);

        sessTabbedPane.addChangeListener(this);

        Properties props = ConfigureFactory.getInstance().
                getProperties(ConfigureFactory.SESSIONS);

        // TODO: permanently remove toolbar

        /*
        if (props.getProperty("emul.hideTabBar","no").equals("yes"))
            hideTabBar = true;

        if (!hideTabBar)
        */
        {
            this.getContentPane().add(sessTabbedPane, BorderLayout.CENTER);
        }

        if (packFrame)
            pack();
        else
            validate();


    }

    //Overridden so we can exit on System Close
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if(e.getID() == WindowEvent.WINDOW_CLOSING) {
            final int oldidx = sessTabbedPane.getSelectedIndex();
            boolean close = true;

            if (hideTabBar && sessTabbedPane.getTabCount() == 0) {
                for (int i=0,len=this.getContentPane().getComponentCount(); i < len; i++) {
                    if (this.getContentPane().getComponent(i) instanceof SessionPanel) {
                        SessionPanel sesspanel = (SessionPanel)this.getContentPane().getComponent(i);
                        close &= sesspanel.confirmCloseSession(false);
                        break;
                    }
                }
            }

            for (int i=0,len=sessTabbedPane.getTabCount(); i<len && close; i++) {
                sessTabbedPane.setSelectedIndex(i);
                updateSessionTitle();
                SessionPanel sesspanel = (SessionPanel)sessTabbedPane.getSelectedComponent();
                close &= sesspanel.confirmCloseSession(false);
            }
            if (!close) {
                // restore old selected index
                sessTabbedPane.setSelectedIndex(oldidx);
                updateSessionTitle();
                return;
            }
            // process regular window closing ...
            super.processWindowEvent(e);
            me.closingDown(this);
        }
    }


    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void onSessionJump(SessionJumpEvent jumpEvent) {

        switch (jumpEvent.getJumpDirection()) {

            case TN5250jConstants.JUMP_PREVIOUS:
                prevSession();
                break;
            case TN5250jConstants.JUMP_NEXT:
                nextSession();
                break;
        }
    }

    private void nextSession() {

        final int index = sessTabbedPane.getSelectedIndex();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int index1 = index;
                if (index1 < sessTabbedPane.getTabCount() - 1) {
                    sessTabbedPane.setSelectedIndex(++index1);
                }
                else {
                    sessTabbedPane.setSelectedIndex(0);
                }
                updateSessionTitle();
            }
        });

    }

    private void prevSession() {

        final int index = sessTabbedPane.getSelectedIndex();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int index1 = index;
                if (index1 == 0) {
                    sessTabbedPane.setSelectedIndex(sessTabbedPane.getTabCount() - 1);
                } else {
                    sessTabbedPane.setSelectedIndex(--index1);
                }
                updateSessionTitle();
            }
        });
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane p = (JTabbedPane)e.getSource();
        setSessionTitle((SessionPanel) p.getSelectedComponent());
    }

    /**
     * Sets the frame title to the same as the newly selected tab's title.
     *
     * @param session can be null, but then nothing happens ;-)
     */
    private void setSessionTitle(final SessionPanel session) {
        if (session != null && session.isConnected()) {
            final String name = determineTabName(session);
            if (sequence - 1 > 0)
                setTitle(name + " - tn5250j <" + sequence + ">");
            else
                setTitle(name + " - tn5250j");
        } else {
            if (sequence - 1 > 0)
                setTitle("tn5250j <" + sequence + ">");
            else
                setTitle("tn5250j");
        }
    }

    /**
     * Determines the name, which is configured for one tab ({@link SessionPanel})
     *
     * @param sessiongui
     * @return
     * @NotNull
     */
    private String determineTabName(final SessionPanel sessiongui) {
        assert sessiongui != null;
        final String name;
        if (sessiongui.getSession().isUseSystemName()) {
            name = sessiongui.getSessionName();
        } else {
            if (sessiongui.getAllocDeviceName() != null) {
                name = sessiongui.getAllocDeviceName();
            } else {
                name = sessiongui.getHostName();
            }
        }
        return name;
    }

    /**
     * Sets the main frame title to the same as the current selected tab's title.
     * @see {@link #setSessionTitle(SessionPanel)}
     */
    private void updateSessionTitle() {
        SessionPanel selectedComponent = (SessionPanel)this.sessTabbedPane.getSelectedComponent();
        setSessionTitle(selectedComponent);
    }

    @Override
    public void addSessionView(final String tabText, final SessionPanel sesspanel) {

        if (hideTabBar && sessTabbedPane.getTabCount() == 0 && !embedded) {
            // put Session just in the main content window and don't create any tabs

            this.getContentPane().add(sesspanel, BorderLayout.CENTER);
            sesspanel.addSessionListener(this);

            // TODO: bypass private resizeMe() method for
            // sesspanel.resizeMe();

            Method method = null;
            try {
                method = sesspanel.getClass().getDeclaredMethod("resizeMe");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            method.setAccessible(true);
            try {
                method.invoke(sesspanel);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            repaint();
            if (packFrame)
                pack();
            else
                validate();
            embedded = true;
            sesspanel.requestFocusInWindow();
            setSessionTitle(sesspanel);
        }
        else {

            if (hideTabBar && sessTabbedPane.getTabCount() == 0 ) {
                // remove first component in the main window,
                // create first tab and put first session into first tab

                SessionPanel firstsesgui = null;
                for (int x=0; x < this.getContentPane().getComponentCount(); x++) {

                    if (this.getContentPane().getComponent(x) instanceof SessionPanel) {
                        firstsesgui = (SessionPanel)(this.getContentPane().getComponent(x));
                        this.getContentPane().remove(x);
                        break;
                    }
                }

                createTabWithSessionContent(tabText, firstsesgui, false);

                sessTabbedPane.setTitleAt(0,determineTabName(firstsesgui));

                this.getContentPane().add(sessTabbedPane, BorderLayout.CENTER);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        repaint();
                    }
                });
            }

            createTabWithSessionContent(tabText, sesspanel, true);
        }
    }

    private final void createTabWithSessionContent(final String tabText, final SessionPanel sessionGui, final boolean focus) {

        // TODO: sessionGui.session is private; need to workaround this!

        Field s = null;
        try {
            s = sessionGui.getClass().getDeclaredField("session");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        s.setAccessible(true);

        try {
            sessTabbedPane.addTab(tabText, determineIconForSession( (Session5250) s.get(sessionGui)), sessionGui);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        final int idx = sessTabbedPane.indexOfComponent(sessionGui);
        // add the [x] to the tab
        final ButtonTabComponent bttab = new ButtonTabComponent(this.sessTabbedPane);
        bttab.addTabCloseListener(this);
        sessTabbedPane.setTabComponentAt(idx, bttab);

        // add listeners
        sessionGui.addSessionListener(this);
        sessionGui.addSessionJumpListener(this);
        sessionGui.addSessionListener(bttab);

        // visual cleanups
        SwingUtilities.invokeLater(
                () -> {

                    // TODO: bypass private resizeMe() method for sessionGui.resizeMe();

                    Method method = null;
                    try {
                        method = sessionGui.getClass().getDeclaredMethod("resizeMe");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    method.setAccessible(true);
                    try {
                        method.invoke(sessionGui);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    sessionGui.repaint();
                    if (focus) {
                        sessTabbedPane.setSelectedIndex(idx);
                        sessionGui.requestFocusInWindow();
                    }
                }
        );
    }

    @Override
    public void onTabClosed(int tabToBeClosed) {
        final SessionPanel sesspanel = this.getSessionAt(tabToBeClosed);
        sesspanel.confirmCloseSession(true);
    }

    @Override
    public void removeSessionView(SessionPanel targetSession) {
        if (hideTabBar && sessTabbedPane.getTabCount() == 0) {
            for (int x=0; x < getContentPane().getComponentCount(); x++) {
                if (getContentPane().getComponent(x) instanceof SessionPanel) {
                    getContentPane().remove(x);
                }
            }
        }
        else {
            int index = sessTabbedPane.indexOfComponent(targetSession);
            log.info("session found and closing down " + index);
            targetSession.removeSessionListener(this);
            targetSession.removeSessionJumpListener(this);
            sessTabbedPane.remove(index);
        }
    }

    @Override
    public int getSessionViewCount() {

        if (hideTabBar && sessTabbedPane.getTabCount() == 0) {
            for (int x=0; x < this.getContentPane().getComponentCount(); x++) {

                if (this.getContentPane().getComponent(x) instanceof SessionPanel) {
                    return 1;
                }
            }
            return 0;
        }
        return sessTabbedPane.getTabCount();
    }

    @Override
    public SessionPanel getSessionAt( int index) {

        if (hideTabBar && sessTabbedPane.getTabCount() == 0) {
            for (int x=0; x < this.getContentPane().getComponentCount(); x++) {

                if (this.getContentPane().getComponent(x) instanceof SessionPanel) {
                    return (SessionPanel)getContentPane().getComponent(x);
                }
            }
            return null;
        }
        if (sessTabbedPane.getTabCount() <= 0) return null;
        return (SessionPanel)sessTabbedPane.getComponentAt(index);
    }

    @Override
    public void onSessionChanged(SessionChangeEvent changeEvent) {

        Session5250 ses5250 = (Session5250)changeEvent.getSource();
        final SessionPanel sesgui = ses5250.getGUI();
        final int tabidx = sessTabbedPane.indexOfComponent(sesgui);
        // be aware, when the first tab is not shown
        if (tabidx >= 0 && tabidx < sessTabbedPane.getTabCount()) {
            this.sessTabbedPane.setIconAt(tabidx, determineIconForSession(ses5250));
        }
        switch (changeEvent.getState()) {
            case TN5250jConstants.STATE_CONNECTED:

                final String devname = sesgui.getAllocDeviceName();
                if (devname != null) {
                    if (log.isDebugEnabled()) {
                        this.log.debug("SessionChangedEvent: " + changeEvent.getState() + " " + devname);
                    }
                    if (tabidx >= 0 && tabidx < sessTabbedPane.getTabCount()) {
                        Runnable tc = () -> sessTabbedPane.setTitleAt(tabidx,determineTabName(sesgui));
                        SwingUtilities.invokeLater(tc);
                    }
                    updateSessionTitle();
                }
                break;
        }
    }

    private static final Icon determineIconForSession(Session5250 session) {
        if (session != null && session.isSslConfigured()) {
            if (session.isSslSocket()) {
                return GUIGraphicsUtils.getClosedLockIcon();
            } else {
                return GUIGraphicsUtils.getOpenLockIcon();
            }
        }
        return null;
    }

    @Override
    public boolean containsSession(SessionPanel session) {

        if (hideTabBar && sessTabbedPane.getTabCount() == 0) {
            for (int x=0; x < this.getContentPane().getComponentCount(); x++) {

                if (this.getContentPane().getComponent(x) instanceof SessionPanel) {
                    return ((SessionPanel)getContentPane().getComponent(x)).equals(session);
                }
            }
            return false;
        }
        return (sessTabbedPane.indexOfComponent(session) >= 0);

    }
}
