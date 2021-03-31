package com.jizhi.geektime.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

/**
 *  数据发布者
 *
 *
 *  Publisher -> [data] 调用Subscriber的onNext
 *
 * @param <T>
 */
public class SimplePublisher<T> implements Publisher<T> {

    /**
     * 关联多个订阅者
     */
    private List<Subscriber> subscribers = new LinkedList<>();

    /**
     * 订阅者订阅这个发布
     * @param s
     */
    @Override
    public void subscribe(Subscriber<? super T> s) {
        SubscriptionAdapter subscription = new SubscriptionAdapter(s);
        s.onSubscribe(subscription);
        subscribers.add(subscription.getSubscriber());
    }

    public void publish(T data) {
        subscribers.forEach(subscriber -> {
            subscriber.onNext(data);
        });
    }

    public static void main(String[] args) {
        SimplePublisher publisher = new SimplePublisher();

        publisher.subscribe(new BusinessSubscriber(8));

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }
}
