package org.example.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.io.ClassPathResource
import org.springframework.data.convert.CustomConversions.StoreConversions
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import java.util.*


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

    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory)
        val converters: MutableList<Any> = ArrayList(dialect.converters)
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS)
        val storeConversions = StoreConversions.of(dialect.simpleTypeHolder, converters)
        val converterList: List<Converter<*, *>> = listOf(
            IDToLongWritingConverter(),
            LongToIDReadingConverter()
        )
        return R2dbcCustomConversions(storeConversions, converterList)
    }
}