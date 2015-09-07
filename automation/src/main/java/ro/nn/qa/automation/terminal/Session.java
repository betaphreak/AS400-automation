package ro.nn.qa.automation.terminal;

import org.tn5250j.Session5250;
import org.tn5250j.SessionConfig;
import org.tn5250j.SessionPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;

import static org.tn5250j.TN5250jConstants.SESSION_LOCALE;

/**
 * Created by Alexandru Giurovici on 02.09.2015.
 */
public class Session extends SessionPanel
{
    private static final long serialVersionUID = 1L;

    private Dimension preferredSize;
    private Rectangle prevRect;
    private Properties sessionProperties;

    private boolean embeddedSignon;
    private String user;
    private String password;
    private String library;
    private String menu;
    private String program;
    private String initialCommand;
    private String afterSignon;
    private int visibilityInterval;

    public Session(String configurationResource, String sessionName)
    {
        this(new Properties(), configurationResource, sessionName);
    }

    public Session(Properties sessionProperties, String configurationResource, String sessionName)
    {
        this(new Session5250(sessionProperties, null, sessionName, new SessionConfig(configurationResource, sessionName)));
    }

    public Session(Session5250 session) {

        super(session);

        // TODO: replace this nasty workaround for sesProps

        Field f = null;
        try
        {
            f = session.getClass().getDeclaredField("sesProps");
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        f.setAccessible(true);
        try
        {
            this.sessionProperties = (Properties) f.get(session);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException(e.getMessage());
        }

        this.sessionProperties.put(SESSION_LOCALE, Locale.getDefault());
        this.getSession().getConfiguration().addSessionConfigListener(this);
    }

    private boolean isFieldLength(String param)
    {
        return ( (param != null) && (param.length() == 10) );
    }

    private void failIfConnected()
    {
        if ((session.getVT() != null) && (isConnected()))
            throw new IllegalStateException("Cannot change property after being connected!");
    }


    private void failIfNot10(String param)
    {
        if ( (param != null) && (param.length() > 10))
            throw new IllegalArgumentException("The length of the parameter cannot exceed 10 positions!");
    }

    private static boolean isSignificant(String param)
    {
        if ( (param != null) && (param.length() != 0))
            return true;

        return false;
    }

    private static Dimension deriveOptimalSize(JComponent comp, Font f
            , int nrChars, int nrLines)
    {
        return deriveOptimalSize(comp, f, comp.getBorder(), nrChars, nrLines);
    }


    private static Dimension deriveOptimalSize(JComponent comp, Font f, Border brdr, int nrChars, int nrLines)
    {
        if (comp == null)
            return null;

        FontMetrics fm = null;
        Graphics g = comp.getGraphics();

        if (g != null)
            fm = g.getFontMetrics(f);
        else
            fm = comp.getFontMetrics(f);

        Insets insets = (brdr == null) ? new Insets(0, 0, 0, 0)
                : brdr.getBorderInsets(comp);
        int height = (fm.getHeight() * nrLines) + insets.top+ insets.bottom;
        int width = (nrChars * fm.charWidth('M')) + insets.left + insets.right;

        return new Dimension(width + 2, height);
    }

    public void setNoSaveConfigFile()
    {
        this.sesConfig.removeProperty("saveme");
    }

    public void signoff()
    {
        if (session.getVT() != null)
        {
            if (this.isConnected())
                this.session.getVT().systemRequest("90");
        }
    }

    protected class DoVisible implements ActionListener, Runnable
    {
        public void actionPerformed(ActionEvent event)
        {
            SwingUtilities.invokeLater(this);
        }


        public void run()
        {
            Method method;
            Session.this.setVisible(true);

            // TODO: nasty workaround for SessionBean.this.resizeMe();

            try
            {
                method = Session.this.getClass().getDeclaredMethod("resizeMe");
                method.setAccessible(true);
                method.invoke(Session.this);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Session.this.requestFocusInWindow();
        }
    }

}


