package entity;

import java.security.Timestamp;

/**
 * Created by user on 1/4/2016.
 */
public class Cache {
    private String filePath;

    private long last_validated;

    private int offset;

    private int numOfBytes;

    private byte[] content;

    public Cache(String filePath, byte[] content, int offset, int numOfBytes){
        this.filePath = filePath;
        this.content = content;
        this.offset= offset;
        this.numOfBytes = numOfBytes;
        this.last_validated = System.currentTimeMillis();
        System.out.println("Successfuly stored the cache named "  + this.filePath + " at: " + this.last_validated);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getLast_validated() {
        return last_validated;
    }

    public void setLast_validated(long last_validated) {
        this.last_validated = last_validated;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNumOfBytes() {
        return numOfBytes;
    }

    public void setNumOfBytes(int numOfBytes) {
        this.numOfBytes = numOfBytes;
    }
}
