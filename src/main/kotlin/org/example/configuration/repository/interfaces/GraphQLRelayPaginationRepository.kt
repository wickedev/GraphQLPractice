package org.example.configuration.repository.interfaces

import graphql.relay.Connection
import reactor.core.publisher.Mono


interface GraphQLRelayPaginationRepository<T : Node, ID> {
    fun forwardPagination(first: Int? = null, after: ID? = null): Mono<Connection<T>>

    fun backwardPagination(last: Int? = null, before: ID? = null): Mono<Connection<T>>
}