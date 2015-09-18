package ro.nn.qa.business;

import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.tools.LangTool;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;
import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class BusinessObjectX extends Screen5250
{
    protected Terminal terminal;
    protected Screen5250 screen;
    protected TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());
    protected final int PAGE_DELAY = 1000;
    protected final int TAB_DELAY = 250;

    public Terminal getTerminal()
    {
        return terminal;
    }

    public Screen5250 getScreen()
    {
        return screen;
    }

    public BusinessObjectX()
    {
        // explicitly disallow construction through parameterless constructor
        terminal = null;
        screen = null;
    }

    public BusinessObjectX(BusinessObjectX owner)
    {
        this.terminal = owner.getTerminal();
        this.screen = owner.getScreen();
    }

    protected void enter() throws InterruptedException
    {
        screen.sendKeys("[enter]");
        sleep(PAGE_DELAY);
        screen.repaintScreen();

    }

    protected void tab(int numTabs) throws InterruptedException {
        for (int i = 0; i < numTabs; i++)
        {
            screen.sendKeys("[tab]");
            sleep(TAB_DELAY);
            screen.repaintScreen();
        }
    }

    protected void navigate(int numTabs) throws InterruptedException {
        sleep(PAGE_DELAY);
        tab(numTabs);
        enter();
        screen.repaintScreen();
    }

    protected void f3() throws InterruptedException {
        screen.sendKeys("[pf3]");
        sleep(TAB_DELAY);
        screen.repaintScreen();
    }

    protected void f4() throws InterruptedException {
        screen.sendKeys("[pf4]");
        sleep(TAB_DELAY);
        screen.repaintScreen();
    }

    protected void f5() throws InterruptedException {
        screen.sendKeys("[pf5]");
        sleep(TAB_DELAY);
        screen.repaintScreen();
    }

    protected void send(String chars, int numTabs)
    {
        if (chars.length() > 0)
        {
            screen.sendKeys(chars);
        }

        for (int i = 0; i < numTabs; i++)
        {
            screen.sendKeys("[tab]");
        }

        try
        {
            sleep(TAB_DELAY);
        }
        catch (InterruptedException e)
        {
            log.warn(e.getMessage());
        }

        screen.repaintScreen();
    }

    protected void send(String chars) {
        send(chars, 1);
    }

    public BusinessObjectX(Terminal term) throws InterruptedException
    {
        LangTool.init();
        screen = term.startNewSession().getSession().getScreen();
        sleep((TAB_DELAY * PAGE_DELAY) / 100);
        terminal = term;
    }

    public MasterMenuX login(String env, String user, String pass) throws InterruptedException {
        ScreenField[] fields = screen.getScreenFields().getFields();
        try {
            fields[0].setString(user);
            fields[1].setString(pass);
            enter();
        } catch (NullPointerException e) {
            throw new RuntimeException("Cannot find login fields");
        }
        enter();
        screen.sendKeys(env);
        enter();

        return new MasterMenuX(this);
    }



}
