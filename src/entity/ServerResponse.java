package entity;

/**
 * Created by user on 3/4/2016.
 */
public class ServerResponse {
    private String status;

    private String seq_num;

    private String template;

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
