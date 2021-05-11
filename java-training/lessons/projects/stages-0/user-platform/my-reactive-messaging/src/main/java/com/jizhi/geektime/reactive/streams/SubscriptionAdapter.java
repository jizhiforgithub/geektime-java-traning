package com.jizhi.geektime.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * (Internal) Subscription Adapter with one {@link Subscriber}
 * 订阅适配器，一个订阅会有一个订阅者
 */
class SubscriptionAdapter implements Subscription {

    private final DecoratingSubscriber<?> subscriber;

    public SubscriptionAdapter(Subscriber<?> subscriber) {
        this.subscriber = new DecoratingSubscriber(subscriber);
    }

    /**
     * 请求数据
     *
     * @param n
     */
    @Override
    public void request(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("The number of elements to requests must be more than zero!");
        }
        this.subscriber.setMaxRequest(n);
    }

    /**
     * 取消订阅数据
     */
    @Override
    public void cancel() {
        this.subscriber.cancel();
    }

    /**
     * 获取订阅者
     *
     * @return
     */
    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Subscriber getSourceSubscriber() {
        return subscriber.getSource();
    }
}
