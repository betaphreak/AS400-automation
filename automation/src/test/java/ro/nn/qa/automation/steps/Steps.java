package ro.nn.qa.automation.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.apache.commons.lang3.RandomStringUtils;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.tools.LangTool;
import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;

import java.text.SimpleDateFormat;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 14.09.2015.
 */

public class Steps extends BaseSteps
{
    Screen5250 screen;
    ScreenField current;
    Fairy fairy = Fairy.create();

    protected void enter()
    {
        try
        {
            screen.repaintScreen();
            sleep(2000);
        } catch (InterruptedException e) {
            log.warn(e.getCause());
        }
        screen.sendKeys("[enter]");
    }


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
        sleep(5000);
    }

    @And("^I login with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void I_login_with_user_and_password(String user, String password)
    {

        ScreenField[] fields = screen.getScreenFields().getFields();
        try
        {
            fields[0].setString(user);
            fields[1].setString(password);
            enter();
        }
        catch (NullPointerException e)
        {
            throw new RuntimeException("Cannot find login fields");
        }
        enter();
    }

    @Then("^I logout$")
    public void I_logout() throws InterruptedException
    {
        sleep(1000);
        screen.sendKeys("[pf3]");
        sleep(1000);
        screen.sendKeys("[pf3]");
    }

    @And("^I should be on the main page of \"([^\"]*)\"$")
    public void I_should_be_on_the_main_page_of(String environment) throws InterruptedException
    {
        screen.sendKeys(environment);
        enter();
    }


    @Then("^I can select option at column <(\\d+)> row <(\\d+)>$")
    public void I_can_see_field_at_row_column_(int column, int row) throws InterruptedException
    {
        sleep(1000);
        for (ScreenField f : screen.getScreenFields().getFields())
        {
            if (f.startRow() == row && f.startCol() == column)
            {
                current = f;
                screen.setCursor(column, row);
                enter();
                return;
            }
        }
    }

    @Then("^I navigate to contract creation$")
    public void I_navigate_to_contract_creation() throws Throwable {
        sleep(1000);
        screen.sendKeys("[tab][tab][enter]");
        enter();
        enter();
    }

    @And("^I add personal client$")
    public void I_add_personal_client() throws Throwable {
        sleep(1000);
        //screen.sendKeys("[tab]");
        enter();
    }

    @And("^I create a new person$")
    public void I_create_a_new_person() throws Throwable {
        Person person = fairy.person();
        screen.sendKeys(person.lastName() + "[tab]");
        screen.sendKeys(person.firstName() + "[tab][tab][tab]");
        screen.sendKeys("Barosan[tab]" + (person.isMale() ? "MN" : "FN"));
        screen.sendKeys(person.getAddress().street() + " " + person.getAddress().streetNumber() + "[tab][tab]");
        screen.sendKeys(/*person.getAddress().getPostalCode() + */"014155[tab][tab][tab]");
        screen.sendKeys(person.telephoneNumber() + "[tab][tab][tab]");
        screen.sendKeys(person.email() + "[tab][tab][tab][tab][tab][tab][tab][tab][tab]");
        screen.sendKeys("01/01/1980[tab][tab][tab]");
        screen.sendKeys(RandomStringUtils.randomNumeric(13) + "[tab]");
        enter();
    }
}
