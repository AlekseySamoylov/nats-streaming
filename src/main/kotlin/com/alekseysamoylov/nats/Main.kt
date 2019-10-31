package com.alekseysamoylov.nats

import io.nats.streaming.Message
import io.nats.streaming.StreamingConnectionFactory
import io.nats.streaming.Subscription
import io.nats.streaming.SubscriptionOptions
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class Main {
}

fun main() {

    val connectionFactory = StreamingConnectionFactory("test-cluster", "bar")

    val streamingConnection = connectionFactory.createConnection()
    var subscription: Subscription? = null
    try {
        while (true) {
            streamingConnection.publish("foo", "Hello World".toByteArray())
            val doneSignal = CountDownLatch(1);

            subscription = streamingConnection.subscribe(
                "foo",
                { message: Message ->
                    println("Received a message: ${String(message.data)}")
                    doneSignal.countDown()
                },
                SubscriptionOptions.Builder().deliverAllAvailable().build()
            )

            doneSignal.await()
            subscription.unsubscribe()

            TimeUnit.SECONDS.sleep(4)
        }

    } finally {
        subscription!!.unsubscribe()
        streamingConnection.close()

    }


}
