package courier.models;
public class Courier {
    private String trackingId, sender, receiver, source, destination, status;
    private double weight;
    public Courier(String t, String s, String r, String src, String dest, double w, String st) {
        trackingId=t; sender=s; receiver=r; source=src; destination=dest; weight=w; status=st;
    }
    public String getTrackingId(){return trackingId;}
    public String getStatus(){return status;}
}
