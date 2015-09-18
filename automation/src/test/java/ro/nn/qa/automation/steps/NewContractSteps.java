package ro.nn.qa.automation.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

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
        NewEndowmentX1 endowment = newContractProposal.createNewContract(contractType);

        assert endowment != null;
        endowment.setContractOwner("Adrian");

        newContractProposal = endowment.back();
    }
}
