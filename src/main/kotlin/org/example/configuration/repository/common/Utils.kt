package org.example.configuration.repository.common

import graphql.relay.*
import org.example.configuration.repository.interfaces.Node
import org.example.configuration.repository.mixin.GraphQLRelayRepositoryMixin
import org.springframework.core.NestedRuntimeException
import org.springframework.data.domain.Sort
import kotlin.reflect.KClass


val Sort.Direction.inverted: Sort.Direction
    get() = if (isAscending) Sort.Direction.DESC else Sort.Direction.ASC

fun <T : Node> List<T>.toConnection(cursors: GraphQLRelayRepositoryMixin.TableCursors): Connection<T> {
    val edges = map { node -> DefaultEdge(node, DefaultConnectionCursor(node.id.value)) }

    val firstCursor = edges.first().cursor
    val lastCursor = edges.last().cursor

    val pageInfo = DefaultPageInfo(
        firstCursor,
        lastCursor,
        cursors.first == firstCursor,
        cursors.last == lastCursor
    )

    return DefaultConnection(
        edges,
        pageInfo
    )
}

fun <T : Any> KClass<T>.isAssignableFrom(cls: Class<*>): Boolean {
    return java.isAssignableFrom(cls)
}

open class InvalidRepositoryImplementationException : NestedRuntimeException {
    /**
     * Constructor for InvalidDataAccessApiUsageException.
     * @param msg the detail message
     */
    constructor(msg: String) : super(msg)

    /**
     * Constructor for InvalidDataAccessApiUsageException.
     * @param msg the detail message
     * @param cause the root cause from the data access API in use
     */
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}
