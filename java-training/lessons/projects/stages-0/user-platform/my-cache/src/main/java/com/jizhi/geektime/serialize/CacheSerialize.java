package com.jizhi.geektime.serialize;

import java.util.ServiceLoader;

/**
 * @author jizhi7
 * @since 1.0
 **/
public final class CacheSerialize {

    private static DefaultCacheSerializeProvider DEFAULT = new DefaultCacheSerializeProvider();

    private CacheSerialize() {

    }

    public static SerializeProvider getProvider() {
        SerializeProvider next = ServiceLoader.load(SerializeProvider.class).iterator().next();
        if (next != null) {
            return next;
        }
        return DEFAULT;
    }

}
