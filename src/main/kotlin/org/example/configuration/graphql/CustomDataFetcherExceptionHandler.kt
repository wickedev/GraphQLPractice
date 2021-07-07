package org.example.configuration.graphql

import graphql.GraphQLError
import graphql.GraphqlErrorException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path
        val error: GraphQLError = GraphqlErrorException.newErrorException()
            .cause(exception)
            .message(exception.message)
            .sourceLocation(sourceLocation)
            .path(path.toList()).apply {
                when (exception) {
                    is AuthenticationError -> extensions(mapOf("UNAUTHENTICATED" to exception))
                    is ForbiddenError -> extensions(mapOf("FORBIDDEN" to exception))
                }
            }
            .build()

        log.warn(error.message, exception)

        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }
}