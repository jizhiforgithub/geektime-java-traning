package com.jizhi.geektime.reactive.message.demo;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

public interface DefaultService {

    @Outgoing("my-channel")
    Publisher<Integer> data();
}
