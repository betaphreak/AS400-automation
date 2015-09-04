package ro.nn.qa.automation.tests;

import org.junit.Before;
import org.junit.Test;

import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;
import org.tn5250j.framework.tn5250.tnvt;
import org.tn5250j.tools.LangTool;

import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;


import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 02.09.2015.
 */

public class TerminalTest
{

    protected Controller controller;

    @Before
    public void start()
    {
        controller = new Controller();
        controller.start();
    }

    @Test
    public void myTerminalTest() throws InterruptedException, InvocationTargetException {
        Terminal term = new Terminal();

        if (controller != null)
            controller.addListener(term);

        LangTool.init();

        tnvt vt = term.startNewSession().getSession().getVT();

        while(true) {
            sleep(10000);
        }
    }




}
