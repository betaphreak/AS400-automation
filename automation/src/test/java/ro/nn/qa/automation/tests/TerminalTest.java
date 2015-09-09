package ro.nn.qa.automation.tests;

import org.junit.Before;
import org.junit.Test;

import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.tools.LangTool;

import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;


import java.lang.reflect.InvocationTargetException;

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

        Screen5250 screen = term.startNewSession().getSession().getScreen();


        for (int i = 1; i <= 10; i++ ) {

            sleep(5000);
            ScreenField[] fields = screen.getScreenFields().getFields();

            ScreenField userName = fields[0];
            userName.setString("GIUROAL");

            ScreenField pass = fields[1];
            pass.setString("Bucuresti1");

            screen.sendKeys("[enter]");

            sleep(1000);
            screen.sendKeys("[enter]");

            screen.sendKeys("72");
            screen.sendKeys("[enter]");

            sleep(1000);
            screen.sendKeys("[pf3]");

            sleep(1000);
            screen.sendKeys("[pf3]");
        }

    }




}
