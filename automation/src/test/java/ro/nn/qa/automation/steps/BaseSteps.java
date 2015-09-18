package ro.nn.qa.automation.steps;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;
import ro.nn.qa.automation.terminal.Terminal;
import ro.nn.qa.bootstrap.Controller;

/**
 * Created by Alexandru Giurovici on 14.09.2015.
 */

@RunWith(Cucumber.class)
@CucumberOptions(
        // plugin = {"pretty", "html:target/cucumber"},
        features="src/test/resources"
)
public class BaseSteps
{
    protected TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());
    protected Controller controller;
    protected Terminal terminal;
}
