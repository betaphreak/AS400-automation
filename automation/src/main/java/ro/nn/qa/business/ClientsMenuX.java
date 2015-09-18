package ro.nn.qa.business;

/**
 * Created by Alexandru Giurovici on 18.09.2015.
 */
public class ClientsMenuX extends BusinessObjectX
{
    public ClientsMenuX(BusinessObjectX owner)
    {
        this.screen = owner.getScreen();
    }
}
