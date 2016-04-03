package entity;

import java.security.Timestamp;

/**
 * Cache entity  is used to store file temporarily inside main memory, it is primarily used for storing information
 * about the file
 * Created by Robert Limanto on 1/4/2016.
 */
public class Cache {
    /**
     * Location of the file in the Server
     */
    private String filePath;

    /**
     * Last validation time of the file in the client
     */
    private long last_validated;

    /**
     * The start offset of the file that is stored in the client cache
     */
    private int offset;

    /**
     * The number of bytes that is stored in the client cache
     */
    private int numOfBytes;

    private byte[] content;

    /**
     *
     * @param filePath
     * @param content
     * @param offset
     * @param numOfBytes
     */
    public Cache(String filePath, byte[] content, int offset, int numOfBytes){
        this.filePath = filePath;
        this.content = content;
        this.offset= offset;
        this.numOfBytes = numOfBytes;
        this.last_validated = System.currentTimeMillis();
        System.out.println("Successfuly stored the cache named "  + this.filePath + " at: " + this.last_validated);
    }

    /**
     * Getter for file path
     * @return String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Setter for file path
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     *
     * @return
     */
    public long getLast_validated() {
        return last_validated;
    }

    /**
     *
     * @param last_validated
     */
    public void setLast_validated(long last_validated) {
        this.last_validated = last_validated;
    }

    /**
     *
     * @return
     */
    public byte[] getContent() {
        return content;
    }

    /**
     *
     * @param content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     *
     * @return
     */
    public int getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     *
     * @return
     */
    public int getNumOfBytes() {
        return numOfBytes;
    }

    /**
     *
     * @param numOfBytes
     */
    public void setNumOfBytes(int numOfBytes) {
        this.numOfBytes = numOfBytes;
    }
}
