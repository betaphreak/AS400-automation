package ro.nn.qa.business;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class MasterMenuX extends BusinessObjectX
{
    public MasterMenuX(BusinessObjectX owner)
    {
        this.screen = owner.getScreen();
    }

    public ClientsMenuX getClientsMenu()
    {
        send("", 3);
        enter();
        return new ClientsMenuX(this);
    }

}
