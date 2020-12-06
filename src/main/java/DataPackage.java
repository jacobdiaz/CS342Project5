import java.io.Serializable;

class DataPackage implements Serializable {
    private String type;
    private Object data;
    public DataPackage(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    public DataPackage(String type){ // Constructor Overloading
        this.type = type;
    }
    public DataPackage(){} // Constructor Overloading
    public void setType(String type) { this.type = type; }
    public void setData(Object data) { this.data = data; }
    public String getType() { return type; }
    public Object getData() { return data; }
    public void printDetails(){
        System.out.println("DataPackage Type:\t"+this.getType()+"\nDataPackage Message: \t"+this.getData()+"\n"); // Send dataPackage back to client
    }
}
