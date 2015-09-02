package ro.nn.qa.automation.tests;

import org.junit.Before;
import org.junit.Test;
import org.tn5250j.BootStrapper;
import org.tn5250j.tools.LangTool;
import ro.nn.qa.automation.terminal.Terminal;

/**
 * Created by Alexandru Giurovici on 02.09.2015.
 */

public class TerminalTest
{

    protected BootStrapper strap;

    @Before
    public void start()
    {
        strap = new BootStrapper();
        strap.start();
    }


    @Test
    public void myTerminalTest()
    {
        Terminal term = new Terminal();
        term.getFrame().setSize(1280, 800);
        term.getFrame().centerFrame();
        LangTool.init();
        term.startNewSession();
    }


}
