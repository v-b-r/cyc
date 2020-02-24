package www.coralinnovations.cyc.Model;

import java.util.ArrayList;

public class Group {

    private String Name ;
    private ArrayList<Child> Items ;

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> items) {
        Items = items;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
