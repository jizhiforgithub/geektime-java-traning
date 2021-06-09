package com.jizhi.other.consistenthash;

import com.sun.istack.internal.Nullable;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;


public class ConsistencyHash {

    private TreeMap<Integer, String> hashServices = new TreeMap<>();

    private int virtualNodeNum = 0;

    public static final String VIRTUAL_NODE_SEG = "V";

    public void setVirtualNodeNum(int num) {
        this.virtualNodeNum = num;
    }

    public void addService(String service) {
        int hashcode = getHash(service);
        hashServices.put(hashcode, service);

        addVirtualNode(service);
    }

    private void addVirtualNode(String service) {
        for (int i = 0; i < virtualNodeNum; i++) {
            int hashcode = getHash(service + VIRTUAL_NODE_SEG + i);

            hashServices.put(hashcode, service);
        }
    }

    public void addServices(String[] services) {
        Stream.of(services).forEach(this::addService);
    }

    @Nullable
    public String getService(Object key) {
        if(hashServices.isEmpty()) {
            return null;
        }
        int hashcode = getHash(key);

        String service = "";

        // 大于hashcode的数据，顺时针取第一个
        SortedMap sortedMap = hashServices.tailMap(hashcode);
        if (sortedMap != null && !sortedMap.isEmpty()) {
            service = hashServices.get(sortedMap.firstKey());
        } else {
            service = hashServices.get(hashServices.firstKey());
        }
        if(service.contains(VIRTUAL_NODE_SEG)) {
            service = service.split(VIRTUAL_NODE_SEG)[0];
        }
        return service;
    }

    private int getHash(Object obj){
        String str = obj.toString();
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

}
