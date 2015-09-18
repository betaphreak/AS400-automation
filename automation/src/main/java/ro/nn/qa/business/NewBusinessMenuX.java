package ro.nn.qa.business;

import static java.lang.Thread.sleep;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class NewBusinessMenuX extends BusinessObjectX
{
    public NewBusinessMenuX(BusinessObjectX owner)
    {
        this.screen = owner.getScreen();
    }

    public MasterMenuX back() throws InterruptedException {
        f3();
        return new MasterMenuX(this);
    }

    public NewContractProposalX getNewContractProposal() throws InterruptedException {
        sleep(PAGE_DELAY);
        enter();
        return new NewContractProposalX(this);
    }


}
