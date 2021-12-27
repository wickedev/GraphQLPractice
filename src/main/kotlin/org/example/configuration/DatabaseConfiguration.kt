package org.example.configuration

import io.r2dbc.spi.ConnectionFactory
import name.nkonev.r2dbc.migrate.autoconfigure.R2dbcMigrateAutoConfiguration.R2dbcMigrateBlockingInvoker
import name.nkonev.r2dbc.migrate.autoconfigure.R2dbcMigrateAutoConfiguration.SpringBootR2dbcMigrateProperties
import name.nkonev.r2dbc.migrate.core.SqlQueries
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator


@Configuration
@EnableConfigurationProperties(SpringBootR2dbcMigrateProperties::class)
class DatabaseConfiguration {

    @Bean
    fun populator(): DatabasePopulator {
        val resourcePaths = listOf("db/scheme.sql")
        return CompositeDatabasePopulator(
            resourcePaths.map {
                ResourceDatabasePopulator(
                    ClassPathResource(it)
                )
            }
        )
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

    @Bean(name = ["r2dbcMigrate"], initMethod = "migrate")
    fun r2dbcMigrate(
        connectionFactory: ConnectionFactory,
        properties: SpringBootR2dbcMigrateProperties,
        @Autowired(required = false) maybeUserDialect: SqlQueries?
    ): R2dbcMigrateBlockingInvoker {
        return R2dbcMigrateBlockingInvoker(connectionFactory, properties, maybeUserDialect)
    }
}
