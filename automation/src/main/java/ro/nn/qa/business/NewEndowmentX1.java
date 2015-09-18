package ro.nn.qa.business;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class NewEndowmentX1 extends BusinessObjectX
{
    public NewEndowmentX1(BusinessObjectX owner)
    {
        this.screen = owner.getScreen();
    }

    public NewContractProposalX back() throws InterruptedException
    {
        f3();
        f3();
        return new NewContractProposalX(this);
    }

    public void setContractOwner(String search) throws InterruptedException
    {
        f4();
        LocateClientX client = new LocateClientX(this);
        client.search(search);
        client.submit();
        f5();
    }


}
