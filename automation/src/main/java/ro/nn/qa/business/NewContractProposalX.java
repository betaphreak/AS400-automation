package ro.nn.qa.business;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */

// this is S5002 02
public class NewContractProposalX extends BusinessObjectX
{
    public NewBusinessMenuX back() throws InterruptedException {
        f3();
        return new NewBusinessMenuX(this);
    }

    public NewEndowmentX1 createNewContract(String contractType) throws InterruptedException {
        sleep(PAGE_DELAY);
        tab(1);
        send(contractType, 1);
        //send("A");
        enter();
        return new NewEndowmentX1(this);
    }

    public NewContractProposalX(BusinessObjectX owner)
    {
        this.screen = owner.getScreen();
    }

}
