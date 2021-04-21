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
    val subscription: ReceiveChannel<E> by lazy { openSubscription() }

    return flux {
        while (!subscription.isClosedForReceive) {
            select<Unit> {
                subscription.onReceiveOrClosed {
                    if (it.isClosed) {
                        this@flux.close(it.closeCause)
                    } else {
                        this@flux.offer(it.value)
                    }
                }
            }
        }
    }.doOnCancel {
        subscription.cancel(CancellationException("closed"))
    }
}