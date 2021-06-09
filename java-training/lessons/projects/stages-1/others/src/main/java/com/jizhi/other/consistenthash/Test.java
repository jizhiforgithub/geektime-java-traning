package com.jizhi.other.consistenthash;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {

    public static void main(String[] args) {

        // 服务器节点
        String[] services = {"127.0.0.1:8080", "127.0.0.2:8080", "127.0.0.3:8080", "127.0.0.4:8080",
                "127.0.0.5:8080", "127.0.0.6:8080", "127.0.0.7:8080", "127.0.0.8:8080",
                "127.0.0.9:8080", "127.0.0.10:8080"};

        // 记录每个服务器存储的数量
        Map<String, Integer> count = new HashMap<>();

        // 一致性hash算法
        ConsistencyHash consistencyHash = new ConsistencyHash();
        consistencyHash.setVirtualNodeNum(150);
        consistencyHash.addServices(services);

        // 随机数key，
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            int data = random.nextInt();
            String service = consistencyHash.getService(data);

            if (count.containsKey(service)) {
                count.put(service, (count.get(service) + 1));
            } else {
                count.put(service, 1);
            }
        }

        System.out.println(count);

    }

}
