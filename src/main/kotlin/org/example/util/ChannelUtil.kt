package org.example.util

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.selects.select
import reactor.core.publisher.Flux

@OptIn(InternalCoroutinesApi::class)
@Suppress("EXPERIMENTAL_API_USAGE")
fun <E> BroadcastChannel<E>.asFlux(): Flux<E> = flux {
    val subscription = openSubscription()

    while (!subscription.isClosedForReceive) {
        select<Unit> {
            subscription.onReceiveOrClosed {
                println(it)
                this@flux.offer(it.value)
            }
        }
    }
}