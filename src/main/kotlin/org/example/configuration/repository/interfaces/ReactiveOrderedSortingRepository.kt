package org.example.configuration.repository.interfaces

import org.reactivestreams.Publisher
import org.springframework.data.domain.Sort
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ReactiveOrderedSortingRepository<T, ID> {
    fun findFirst(direction: Sort.Direction): Mono<T>

    fun findLast(direction: Sort.Direction): Mono<T>

    fun findAllById(ids: Iterable<ID>, sort: Sort): Flux<T>

    fun findAllById(idStream: Publisher<ID>, sort: Sort): Flux<T>
}