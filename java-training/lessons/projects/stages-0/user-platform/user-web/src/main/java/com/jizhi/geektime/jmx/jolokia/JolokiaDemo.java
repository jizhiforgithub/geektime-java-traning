package com.jizhi.geektime.jmx.jolokia;

/**
 * 2021/3/16
 * jizhi7
 **/
public class JolokiaDemo {

    public static void main(String[] args) throws Exception {

       /* J4pClient j4pClient = new J4pClient("http://localhost:8888/jolokia");
        J4pReadRequest req = new J4pReadRequest("java.lang:type=Memory",
                "HeapMemoryUsage");
        J4pReadResponse resp = j4pClient.execute(req);
        Map<String,Long> vals = resp.getValue();
        long used = vals.get("used");
        long max = vals.get("max");
        int usage = (int) (used * 100 / max);
        System.out.println("Memory usage: used: " + used +
                " / max: " + max + " = " + usage + "%");*/
    }

}
