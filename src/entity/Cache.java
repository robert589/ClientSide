package entity;

import java.security.Timestamp;

/**
 * Created by user on 1/4/2016.
 */
public class Cache {
    private String filePath;

    private Timestamp last_validated;

    private byte content;

    public Cache(String filePath, byte content){
        this.filePath = filePath;
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getLast_validated() {
        return last_validated;
    }

    public void setLast_validated(Timestamp last_validated) {
        this.last_validated = last_validated;
    }

    public byte getContent() {
        return content;
    }

    public void setContent(byte content) {
        this.content = content;
    }
}
