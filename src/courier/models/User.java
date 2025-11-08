package courier.models;
public class User {
    private int id;
    private String name, contact, address;
    public User(String n, String c, String a) { name=n; contact=c; address=a; }
    public String getName(){return name;}
    public String getContact(){return contact;}
    public String getAddress(){return address;}
}

