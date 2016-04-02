package controller;

import entity.Cache;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by user on 1/4/2016.
 */
public class CacheController {
    private ArrayList<Cache> cacheList = new ArrayList<Cache>();

    public CacheController(){

    }

    private Cache checkExist(String filePath){
        for(Cache cache: cacheList){
            if(cache.getFilePath().equals( filePath)){
                return cache;
            }
        }

        return null;
    }


    byte[] readFile(String filePath, int offset, int numOfBytes){
        Cache cache = this.checkExist(filePath);
        if(cache != null){
            if(cache.getOffset() <= offset && ( cache.getNumOfBytes() == -1 ||  cache.getNumOfBytes() > numOfBytes )){
                if(cache.getNumOfBytes() == -1){
                    return Arrays.copyOfRange(cache.getContent(),offset - cache.getOffset(), cache.getContent().length);

                }
                else{
                    return Arrays.copyOfRange(cache.getContent(),offset - cache.getOffset(), numOfBytes);

                }
            }
            else{
                System.out.println("Offset or num of bytes not enough");
            }
        }

        return null;

    }

    public void addNew(String filePath, int offset, int numOfBytes, byte[] content){

        //remove old cache
        cacheList.remove(this.checkExist(filePath));

        Cache cache = new Cache(filePath, content, offset, numOfBytes);
        cacheList.add(cache);

    }



}