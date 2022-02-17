package com.gonwan.toys.hash;

import java.nio.charset.StandardCharsets;
import java.util.*;

class Node {

    private String ip;
    private String name;

    public Node(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    /* used for hashing */
    @Override
    public String toString() {
        return ip;
    }

}

public class Main {

    private static final String IP_PREFIX = "192.168.0.";

    public static void main(String[] args) {
        List<Node> nodes = new ArrayList<>();
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            Node node = new Node(IP_PREFIX + i, "node" + i);
            nodes.add(node);
            map.put(node.getIp(), 0);
        }
        ConsistentHash<Node> consistentHash = new ConsistentHash<>(
                key -> Murmur3.hash32(key.getBytes(StandardCharsets.UTF_8)), 100, nodes);
        /* test */
        for (int i = 0; i < 5000; i++) {
            String data = "hahahahaha" + i;
            //String data = UUID.randomUUID().toString() + i;
            Node node = consistentHash.get(data);
            map.put(node.getIp(), map.get(node.getIp()) + 1);
        }
        /* result */
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.printf("%s records: %d\n", e.getKey(), e.getValue());
        }
    }

}
