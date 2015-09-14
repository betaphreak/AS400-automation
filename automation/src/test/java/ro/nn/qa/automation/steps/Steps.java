package ro.nn.qa.automation.steps;

import cucumber.api.CucumberOptions;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import javafx.stage.Screen;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.tools.LangTool;
import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 14.09.2015.
 */

public class Steps extends BaseSteps
{
    Screen5250 screen;

    @Given("^I am connected to NRO$")
    public void I_am_connected_to_NRO() throws InterruptedException
    {
        controller = new Controller();
        controller.start();

        terminal = new Terminal();

        if (controller != null)
            controller.addListener(terminal);

        LangTool.init();

        screen = terminal.startNewSession().getSession().getScreen();
        sleep(2500);
    }

    @And("^I login$")
    public void I_login() throws Throwable
    {

        ScreenField[] fields = screen.getScreenFields().getFields();

        ScreenField userName = fields[0];
        userName.setString("GIUROAL");

        ScreenField pass = fields[1];
        pass.setString("Bucuresti1");

        screen.sendKeys("[enter]");

        sleep(1000);
        screen.sendKeys("[enter]");

    }

    @Then("^I should be on the main page$")
    public void I_should_be_on_the_main_page() throws Throwable
    {
        sleep(1000);
        screen.sendKeys("[enter]");

        screen.sendKeys("72");
        screen.sendKeys("[enter]");

    }
}
