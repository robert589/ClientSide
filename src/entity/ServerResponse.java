package entity;

/**
 * Response from server has status/content, seq_num, template, and message_type,
 * This information will be encapsulated in this entity
 * Created by user on 3/4/2016.
 */
public class ServerResponse {
    /**
     * Status/Content from the Server
     */
    private String status;

    /**
     * The sequence number from the server
     */
    private String seq_num;

    /**
     * Used for printing to the console
     */
    private String template;

    /**
     * To notice client that the response is replied from which  request
     */
    private int message_type;

    public ServerResponse(String status, String seq_num,String template, int message_type){
        this.status = status;
        this.seq_num = seq_num;
        this.template = template;
        this.message_type = message_type;
    }

    public ServerResponse(String status, String template, int message_type){
        this.status = status;
        this.message_type = message_type;
        this.template = template;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeq_num() {
        return seq_num;
    }

    public void setSeq_num(String seq_num) {
        this.seq_num = seq_num;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }
}
