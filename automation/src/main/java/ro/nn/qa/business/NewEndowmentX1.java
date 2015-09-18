package ro.nn.qa.business;

import ro.nn.qa.business.f4.BillingFreqF4;
import ro.nn.qa.business.f4.LocateClientF4;

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
        LocateClientF4 client = new LocateClientF4(this);
        client.search(search);
        client.submit();
        f5();
    }

    public void setRiskCommDate(String date) throws InterruptedException {
        send(date);
        f5();
    }

    public void setFreq() throws InterruptedException {
        f4();
        BillingFreqF4 freq = new BillingFreqF4(this);
        freq.search("");
        freq.submit();
        f5();
    }





}
