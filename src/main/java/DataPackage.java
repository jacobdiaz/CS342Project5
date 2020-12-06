import java.io.Serializable;

class DataPackage implements Serializable {
    private String type;
    private String recipients;
    private Object data;

    // Constructor Overloading
    public DataPackage(String type, Object data, String recipients) {
        this.type = type;
        this.data = data;
        this.recipients = recipients;
    }
    public DataPackage(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    public DataPackage(String type){
        this.type = type;
    }
    public DataPackage(){}

    //Setters
    public void setType(String type) { this.type = type; }
    public void setData(Object data) { this.data = data; }
    public void setRecipients(String recipients) { this.recipients = recipients; }

    // Getters
    public String getType() { return type; }
    public Object getData() { return data; }
    public String getRecipients() { return recipients; }

    // Utility
    public void printDetails(){
        System.out.println("DataPackage Type:\t"+this.getType()+"\nDataPackage Message: \t"+this.getData()+"\n"); // Send dataPackage back to client
    }
    public void printDmDetails(){
        System.out.println("DataPackage Type:\t"+this.getType()+"\nDataPackage Message: \t"+this.getData()+"\n Recipients: "+recipients); // Send dataPackage back to client
    }
}
