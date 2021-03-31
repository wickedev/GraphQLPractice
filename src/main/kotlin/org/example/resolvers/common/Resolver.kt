package org.example.resolvers.common

import com.expediagroup.graphql.server.operations.Mutation
import io.r2dbc.spi.ConnectionFactory
import org.example.util.coroutine.mono.await
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component

@Component
class CommonMutation(
    private val databaseClient: DatabaseClient,
    private val connectionFactory: ConnectionFactory,
    private val databasePopulator: DatabasePopulator
) : Mutation {

    suspend fun internal_unsafe_reset(): Int {
        databaseClient.sql("drop table post cascade").then().await()
        databaseClient.sql("drop table user cascade").then().await()
        databasePopulator.populate(connectionFactory).await()
        return 0
    }
}