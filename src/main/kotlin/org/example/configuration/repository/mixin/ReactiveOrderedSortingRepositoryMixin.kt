package org.example.configuration.repository.mixin

import org.example.configuration.repository.common.inverted
import org.example.configuration.repository.interfaces.PropertyRepository
import org.example.configuration.repository.interfaces.ReactiveOrderedSortingRepository
import org.reactivestreams.Publisher
import org.springframework.data.domain.Sort
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ReactiveOrderedSortingRepositoryMixin<T, ID> :
    PropertyRepository<T, ID>,
    ReactiveOrderedSortingRepository<T, ID> {

    override fun findFirst(direction: Sort.Direction): Mono<T> {
        val idProperty = getIdProperty().name
        val sort = Sort.by(direction, idProperty)

        return entityOperations.selectOne(
            emptyQuery().sort(sort),
            entity.javaType
        )
    }

    override fun findLast(direction: Sort.Direction): Mono<T> {
        val idProperty = getIdProperty().name
        val sort = Sort.by(direction.inverted, idProperty)

        return entityOperations.selectOne(
            emptyQuery().sort(sort),
            entity.javaType
        )
    }

    override fun findAllById(ids: Iterable<ID>, sort: Sort): Flux<T> {
        Assert.notNull(ids, "The iterable of Id's must not be null!")
        return findAllById(Flux.fromIterable(ids), sort)
    }

    override fun findAllById(idStream: Publisher<ID>, sort: Sort): Flux<T> {
        Assert.notNull(idStream, "The Id Publisher must not be null!")
        return Flux.from(idStream).buffer().filter { ids: List<ID> -> ids.isNotEmpty() }.concatMap { ids: List<ID> ->
            if (ids.isEmpty()) {
                return@concatMap Flux.empty()
            }

            entityOperations.select(getIdsQuery(ids).sort(sort), entity.javaType)
        }
    }
}
