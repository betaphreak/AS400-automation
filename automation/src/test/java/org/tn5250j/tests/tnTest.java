package org.tn5250j.tests;

import org.junit.Test;
import org.tn5250j.BootStrapper;
import ro.nn.qa.automation.terminal.Terminal;

/**
 * Created by Alexandru Giurovici on 01.09.2015.
 */
public class tnTest {

    private static BootStrapper strapper = null;

    @Test
    public void runBootstrapTest()
    {
        strapper = new BootStrapper();
        strapper.start();

        Terminal t = new Terminal();

        if (strapper != null)
            strapper.addBootListener(t);
    }

}
