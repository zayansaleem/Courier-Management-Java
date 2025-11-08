package courier.models;

public class History {
    private String trackingId;
    private String sender;
    private String receiver;
    private String source;
    private String destination;
    private String weight;
    private String status;

    public History(String trackingId, String sender, String receiver,
                   String source, String destination, String weight, String status) {
        this.trackingId = trackingId;
        this.sender = sender;
        this.receiver = receiver;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.status = status;
    }

    public String getTrackingId() { return trackingId; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getWeight() { return weight; }
    public String getStatus() { return status; }
}
