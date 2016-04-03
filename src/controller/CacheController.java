package controller;

import entity.Cache;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Store, and Manage all caches in the program, Cache is used primarily to speed up read operation of the file
 * Created by user on 1/4/2016.
 */
public class CacheController {
    /**
     * A list of cache that is stored in this program
     */
    private ArrayList<Cache> cacheList;

    /**
     * If a file stays in the cache longer than this time, it means that the file has to be checked with the server whether the file is up-to-date
     *
     */
    public final static long INTERVAL_FRESHNESS_TIME = 5000;

    /**
     * Used for notifying command controller that the data is outdated on the client side
     * If the cache controller returns this, command controller required to check whether the file in the server is up to date with this program
     */
    public final static byte[] DATA_IS_OUTDATED = new byte[]    {(byte)178,(byte)179,(byte)188,(byte)198,(byte)208,(byte)178};

    /**
     * Default constructor
     */
    public CacheController(){
        cacheList = new ArrayList<Cache>();
    }

    /**
     * return the cache entity if the filepath exist,
     * if the filepath does not exist in the list, return null
     * @param filePath
     * @return Cache|null
     */
    private Cache checkExist(String filePath){
        for(Cache cache: cacheList){
            if(cache.getFilePath().equals( filePath)){
                System.out.println("Cache named " + cache.getFilePath() + " is found");
                return cache;
            }
        }

        return null;
    }


    /**
     * Read file in the cache, it includes check whether the offset and numofbytes are suitable with the cache
     * and whether the file is outdated in the client.
     * For example: if users store part of the file in the cache that starts from offset 50. and the users try to read the cache
     * with offset started from 25, this function will return null since some of the requested bytes are not in the cache.
     * @param filePath
     * @param offset
     * @param numOfBytes
     * @return byte[] | null
     */
    byte[] readFile(String filePath, int offset, int numOfBytes){
        Cache cache = this.checkExist(filePath);
        if(cache != null){
            if(cache.getOffset() <= offset && ( cache.getNumOfBytes() == -1 ||  cache.getNumOfBytes() > numOfBytes )){
                //CHECK WHETHER THE TIME FRESHNESS HAS PASSED
                if((System.currentTimeMillis() - cache.getLast_validated()) < INTERVAL_FRESHNESS_TIME){
                    if(cache.getNumOfBytes() == -1){
                        return Arrays.copyOfRange(cache.getContent(),offset - cache.getOffset(), cache.getContent().length);
                    }
                    else{
                        return Arrays.copyOfRange(cache.getContent(),offset - cache.getOffset(), numOfBytes);
                    }
                }
                else{
                    System.out.println("The cache is outdated, sending command to server");
                    return DATA_IS_OUTDATED;
                }


            }
            else{
                System.out.println("Offset or num of bytes not enough");
            }
        }

        return null;

    }

    /**
     * Add new cache inside the file, if there is a cache with the same filepath, the existing cache will be removed
     * @param filePath
     * @param offset
     * @param numOfBytes
     * @param content
     */
    public void addNew(String filePath, int offset, int numOfBytes, byte[] content){

        //remove old cache
        cacheList.remove(this.checkExist(filePath));

        Cache cache = new Cache(filePath, content, offset, numOfBytes);
        cacheList.add(cache);

    }

    /**
     * This function is performed to check whether the file is uptodate in the server
     * if it is uptodate, then this function will return byte[]
     * @param filePath
     * @param timeServer
     * @return byte[] | null
     */
    public byte[] CheckUptodate(String filePath, long timeServer){
        Cache cache = this.checkExist(filePath);
        System.out.println(cache.getLast_validated() + " " + (timeServer));
        if(cache.getLast_validated() < (timeServer )){
            return null;
        }
        else{
            return this.checkExist(filePath).getContent();
        }
    }


}