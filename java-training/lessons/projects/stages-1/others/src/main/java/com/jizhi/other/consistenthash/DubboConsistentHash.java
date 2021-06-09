package com.jizhi.other.consistenthash;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DubboConsistentHash {

    private static final class ConsistentHashSelector<T> {

        private final TreeMap<Long, T> virtualServices;

        // 副本数
        private final int replicaNumber;

        // 方法的唯一标识
        private final int identityHashCode;

        private final int[] argumentIndex;

        /**
         *
         * @param services 服务集合
         * @param methodName 方法名称
         * @param identityHashCode 方法的唯一标识
         */
        ConsistentHashSelector(List<T> services, String methodName, int identityHashCode, int nodeNums) {
            this.virtualServices = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            this.replicaNumber = nodeNums;
            String[] index = COMMA_SPLIT_PATTERN.split(url.getMethodParameter(methodName, HASH_ARGUMENTS, "0"));
            argumentIndex = new int[index.length];
            for (int i = 0; i < index.length; i++) {
                argumentIndex[i] = Integer.parseInt(index[i]);
            }
            for (T service : services) {
                String address = service.getUrl().getAddress();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = Bytes.getMD5(address + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualServices.put(m, service);
                    }
                }
            }
        }

        public T select(T service) {
            String key = toKey(service.getArguments());
            byte[] digest = Bytes.getMD5(key);
            return selectForKey(hash(digest, 0));
        }

        private String toKey(Object[] args) {
            StringBuilder buf = new StringBuilder();
            for (int i : argumentIndex) {
                if (i >= 0 && i < args.length) {
                    buf.append(args[i]);
                }
            }
            return buf.toString();
        }

        private T selectForKey(long hash) {
            Map.Entry<Long, T> entry = virtualServices.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualServices.firstEntry();
            }
            return entry.getValue();
        }

        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }
    }

}
