package ro.nn.qa.automation.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

import cucumber.api.java.en.Then;
import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;
import ro.nn.qa.business.*;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class NewContractSteps extends StepsRunner
{
    MasterMenuX mainPage;
    NewContractProposalX newContractProposal;
    NewEndowmentX1 endowment;

    @Given("^I am connected to NRO \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void connect(String user, String pass, String env) throws Throwable {

        controller = new Controller();
        controller.start();
        terminal = new Terminal();
        controller.addListener(terminal);

        // this is the first instance of the business object that needs to own the terminal
        BusinessObjectX bo = new BusinessObjectX(terminal);

        mainPage = bo.login(user, pass, env);
    }

    @And("^I navigate to New Contract Proposal$")
    public void clientsMenu() throws Throwable {
        assert mainPage != null;
        // to get to the clients menu you need to be previously logged in
        NewBusinessMenuX newBusinessMenu = mainPage.getNewBusinessMenu();

        assert newBusinessMenu != null;
        newContractProposal = newBusinessMenu.getNewContractProposal();
    }

    @And("^I create a new Contract of type \"([^\"]*)\"$")
    public void newContract(String contractType) throws Throwable
    {
        // to create a new contract you need to be on the new contract proposal screen
        assert newContractProposal != null;
        endowment = newContractProposal.createNewContract(contractType);
    }

    @Then("^I go back$")
    public void goBack() throws Throwable
    {
        assert endowment != null;
        newContractProposal = endowment.back();

        assert newContractProposal != null;
        NewBusinessMenuX newBusinessMenu = newContractProposal.back();
    }

    @And("^I set the contract owner to \"([^\"]*)\"$")
    public void setContractOwner(String owner) throws Throwable
    {
        assert endowment != null;
        endowment.setContractOwner(owner);
    }

    @And("^I set the date to \"([^\"]*)\"$")
    public void setRiskCommDate(String date) throws Throwable
    {
        assert endowment != null;
        endowment.setRiskCommDate(date);
    }

    @And("^I set the billing frequency to \"([^\"]*)\"$")
    public void setBillingFreq(String freq) throws Throwable {
        assert endowment != null;
        endowment.setBillingFreq(freq);

    }

    @And("^I set the method of payment to \"([^\"]*)\"$")
    public void setPaymentMethod(String method) throws Throwable {
        assert endowment != null;
        endowment.setPaymentMethod(method);
    }

    @And("^I set the serial number to \"([^\"]*)\"$")
    public void setSerialNumber(String arg1) throws Throwable {
        assert endowment != null;
        endowment.setSerialNumber(arg1);
    }

    @And("^I set the agent to \"([^\"]*)\"$")
    public void setAgent(String arg1) throws Throwable {
        assert endowment != null;
        endowment.setAgent(arg1);

    }
}
