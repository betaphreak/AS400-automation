package ro.nn.qa.automation.steps;

import io.codearte.jfairy.Fairy;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;

import static java.lang.Thread.sleep;

public class StepsRunner extends BaseSteps
{
    Screen5250 screen;
    ScreenField current;
    Fairy fairy = Fairy.create();

    protected void send(String chars, int numTabs) {
        screen.sendKeys(chars);
        for (int i = 0; i < numTabs; i++)
            screen.sendKeys("[tab]");
        try {
            sleep(250);
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }
        screen.repaintScreen();
    }

    protected void send(String chars) {
        send(chars, 1);
    }


    protected void enter() {
        try {
            screen.repaintScreen();
            sleep(2000);
        } catch (InterruptedException e) {
            log.warn(e.getCause());
        }
        screen.sendKeys("[enter]");
    }

}
