package org.example.configuration.repository.mixin

import graphql.relay.Connection
import graphql.relay.ConnectionCursor
import graphql.relay.DefaultConnectionCursor
import org.example.configuration.repository.common.toConnection
import org.example.configuration.repository.interfaces.GraphQLRelayRepository
import org.example.configuration.repository.interfaces.Node
import org.example.configuration.repository.interfaces.PropertyRepository
import org.example.configuration.repository.interfaces.ReactiveOrderedSortingRepository
import org.springframework.data.domain.Sort
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GraphQLRelayRepositoryMixin<T : Node, ID> :
    PropertyRepository<T, ID>,
    GraphQLRelayRepository<T, ID>,
    ReactiveOrderedSortingRepository<T, ID> {

    fun whereIdGreaterThanQuery(after: ID?): Query {
        return Query.query(whereId().greaterThan(after as Any))
    }

    fun whereIdLessThanQuery(before: ID?): Query {
        return Query.query(whereId().lessThan(before as Any))
    }

    override fun node(id: ID): Node? {
        TODO("Not yet implemented")
    }

    override fun forwardPagination(first: Int?, after: ID?): Mono<Connection<T>> {
        var query = if (after == null) emptyQuery() else whereIdGreaterThanQuery(after)

        if (first != null) {
            query = query.limit(first)
        }

        return entityOperations.select(query, entity.javaType).toConnection(Sort.Direction.ASC)
    }

    override fun backwardPagination(last: Int?, before: ID?): Mono<Connection<T>> {
        var query = if (before == null) emptyQuery() else whereIdLessThanQuery(before)

        if (last != null) {
            query = query.limit(last)
        }

        return entityOperations.select(query, entity.javaType).toConnection(Sort.Direction.DESC)
    }

    data class TableCursors(
        val first: ConnectionCursor,
        val last: ConnectionCursor
    )

    private fun <T : Node> Flux<T>.toConnection(direction: Sort.Direction): Mono<Connection<T>> {
        return collectList()
            .zipWith(Mono.zip(findFirst(direction), findLast(direction)) { l, r ->
                TableCursors(
                    first = DefaultConnectionCursor(l.id.value),
                    last = DefaultConnectionCursor(r.id.value)
                )
            }) { l, r -> l to r }
            .map { (list, cursors) -> list.toConnection(cursors) }
    }
}

