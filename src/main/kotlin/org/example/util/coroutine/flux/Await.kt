package org.example.util.coroutine.flux


import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.Flux

suspend fun <T> Flux<T>.await(): List<T> = this.collectList().awaitSingle()
