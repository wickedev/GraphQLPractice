package org.example.util

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.selects.select
import reactor.core.publisher.Flux
import java.util.concurrent.CancellationException

@OptIn(InternalCoroutinesApi::class)
@Suppress("EXPERIMENTAL_API_USAGE")
fun <E> BroadcastChannel<E>.asFlux(): Flux<E> {
    var subscription: ReceiveChannel<E>? = null

    return flux {
        subscription = openSubscription()
        while (subscription?.isClosedForReceive == false) {
            select<Unit> {
                subscription?.onReceiveOrClosed {
                    if (it.isClosed) {
                        this@flux.close(it.closeCause)
                    } else {
                        this@flux.offer(it.value)
                    }
                }
            }
        }
    }.doOnCancel {
        subscription?.cancel(CancellationException("closed"))
    }
}