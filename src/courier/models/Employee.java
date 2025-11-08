package courier.models;
public class Employee {
    private int id;
    private String name, contact;
    public Employee(String n, String c){name=n;contact=c;}
    public String getName(){return name;}
    public String getContact(){return contact;}
}
