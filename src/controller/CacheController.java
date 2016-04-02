package controller;

import entity.Cache;

import java.util.ArrayList;

/**
 * Created by user on 1/4/2016.
 */
public class CacheController {
    ArrayList<Cache> cacheList = new ArrayList<Cache>();

    public CacheController(){

    }

    public Cache checkExist(String filePath){
        for(Cache cache: cacheList){
            if(cache.getFilePath().equals( filePath)){
                return cache;
            }
        }

        return null;
    }


    public byte readFile(String filePath){
        if(this.checkExist(filePath) != null){
            Cache cache = this.checkExist(filePath);
            return cache.getContent();
        }
        else{
            return 0;
        }
    }
}