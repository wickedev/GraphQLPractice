package org.example.configuration

import io.r2dbc.spi.ConnectionFactory
import name.nkonev.r2dbc.migrate.autoconfigure.R2dbcMigrateAutoConfiguration.R2dbcMigrateBlockingInvoker
import name.nkonev.r2dbc.migrate.autoconfigure.R2dbcMigrateAutoConfiguration.SpringBootR2dbcMigrateProperties
import name.nkonev.r2dbc.migrate.core.SqlQueries
import org.example.configuration.r2dbc.AdditionalIsNewStrategy
import org.example.configuration.r2dbc.CustomAdditionalIsNewStrategy
import org.example.configuration.r2dbc.CustomMappingR2dbcConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver.getDialect
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
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

    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = getDialect(connectionFactory)
        return R2dbcCustomConversions.of(
            dialect,
            IDToLongWritingConverter(),
            LongToIDReadingConverter(),
            StringToRoleReadingConverter(),
            RoleToStringWritingConverter(),
            OffsetDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToOffsetDateTimeReadingConverter(),
            ZonedDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToZonedDateTimeReadingConverter(),
        )
    }

    @Bean
    fun additionalIsNewStrategy(): AdditionalIsNewStrategy {
        return CustomAdditionalIsNewStrategy()
    }

    @Bean
    fun mappingR2dbcConverter(
        context: R2dbcMappingContext,
        conversions: CustomConversions,
        additionalIsNewStrategy: AdditionalIsNewStrategy
    ): MappingR2dbcConverter {
        return CustomMappingR2dbcConverter(context, conversions, additionalIsNewStrategy)
    }
}
