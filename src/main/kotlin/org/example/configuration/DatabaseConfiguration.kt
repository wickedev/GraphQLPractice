package org.example.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
class DatabaseConfiguration {

    @Bean
    fun populator(): DatabasePopulator {
        val populator = CompositeDatabasePopulator()
        val scheme = ClassPathResource("db/scheme.sql")
        populator.addPopulators(ResourceDatabasePopulator(scheme))
        return populator
    }

    @Bean
    fun initializer(
        connectionFactory: ConnectionFactory,
        populator: DatabasePopulator
    ): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}
