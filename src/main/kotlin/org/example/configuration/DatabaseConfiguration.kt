package org.example.configuration

import io.r2dbc.spi.ConnectionFactory
import org.example.configuration.r2dbc.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.io.ClassPathResource
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
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

    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory)
        val converters = listOf(
            *dialect.converters.toTypedArray(),
            *R2dbcCustomConversions.STORE_CONVERTERS.toTypedArray()
        )
        val storeConversions = CustomConversions.StoreConversions.of(
            dialect.simpleTypeHolder,
            converters
        )
        val converterList: List<Converter<*, *>> = listOf(
            IDToLongWritingConverter(),
            LongToIDReadingConverter(),
            OffsetDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToOffsetDateTimeReadingConverter(),
            ZonedDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToZonedDateTimeReadingConverter(),
        )
        return R2dbcCustomConversions(storeConversions, converterList)
    }

    @Bean
    fun additionalIsNewStrategy() = CustomAdditionalIsNewStrategy()

    @Bean
    fun mappingR2dbcConverter(
        context: R2dbcMappingContext,
        conversions: CustomConversions,
        additionalIsNewStrategy: AdditionalIsNewStrategy
    ): MappingR2dbcConverter {
        return CustomMappingR2dbcConverter(context, conversions, additionalIsNewStrategy)
    }
}
