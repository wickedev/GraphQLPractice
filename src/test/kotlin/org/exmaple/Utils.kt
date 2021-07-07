package org.exmaple

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.github.javafaker.Faker
import io.r2dbc.spi.Closeable
import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.example.configuration.*
import org.example.configuration.r2dbc.CustomAdditionalIsNewStrategy
import org.example.configuration.r2dbc.CustomMappingR2dbcConverter
import org.example.configuration.r2dbc.CustomSimpleR2dbcRepositoryFactory
import org.example.entity.Post
import org.example.entity.User
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.DefaultExtendedDatabaseClient
import org.spekframework.spek2.dsl.LifecycleAware
import org.springframework.core.convert.converter.Converter
import org.springframework.core.io.ClassPathResource
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.core.DatabaseClient
import java.util.ArrayList

val faker = Faker()
val fixture = kotlinFixture {
    javaFakerStrategy {
        property(User::id) { DEFAULT_ID_VALUE }
        property(Post::id) { DEFAULT_ID_VALUE }
    }
}

class DatabaseContainer(spek: LifecycleAware) {
    private var created = false

    val connectionFactory = ConnectionFactories.get("r2dbc:tc:mariadb:///test?TC_IMAGE_TAG=focal")

    val dialect = DialectResolver.getDialect(connectionFactory)

    val additionalIsNewStrategy by spek.memoized { CustomAdditionalIsNewStrategy() }

    val conversions by spek.memoized {
        R2dbcCustomConversions.of(
            dialect,
            IDToLongWritingConverter(),
            LongToIDReadingConverter(),
            OffsetDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToOffsetDateTimeReadingConverter(),
            ZonedDateTimeToLocalDateTimeWritingConverter(),
            LocalDateTimeToZonedDateTimeReadingConverter(),
        )
    }

    val mappingContext by spek.memoized {
        val relationalMappingContext = R2dbcMappingContext(NamingStrategy.INSTANCE)
        relationalMappingContext.setSimpleTypeHolder(conversions.simpleTypeHolder)
        relationalMappingContext
    }

    val converter by spek.memoized {
        CustomMappingR2dbcConverter(mappingContext, conversions, additionalIsNewStrategy)
    }

    val databaseClient by spek.memoized {
        DatabaseClient.create(connectionFactory)
    }

    val extendedDatabaseClient by spek.memoized {
        DefaultExtendedDatabaseClient(
            databaseClient,
            converter
        )
    }


    val r2dbcEntityTemplate by spek.memoized {
        R2dbcEntityTemplate(databaseClient, dialect, converter)
    }

    private val repositoryFactory by spek.memoized {
        CustomSimpleR2dbcRepositoryFactory(
            databaseClient,
            r2dbcEntityTemplate.dataAccessStrategy,
            additionalIsNewStrategy
        )
    }

    fun create() {
        runBlocking {
            connectionFactory.create().awaitFirstOrNull()
            created = true
        }
    }

    fun destroy() {
        if (connectionFactory is Closeable) {
            runBlocking {
                connectionFactory.close().awaitFirstOrNull()
            }
        }
    }

    fun r2dbcEntityTemplateFactory(): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory)
    }

    fun <T> getRepository(repositoryInterface: Class<T>): T {
        return repositoryFactory.getRepository(repositoryInterface)
    }

    fun <T> getRepository(repositoryInterface: Class<T>, customImplementation: Any): T {
        return repositoryFactory.getRepository(repositoryInterface, customImplementation)
    }

    fun populate(vararg scriptPaths: String) {
        if (!created) {
            create()
        }

        val populator = CompositeDatabasePopulator(
            scriptPaths.map {
                ResourceDatabasePopulator(
                    ClassPathResource(it)
                )
            }
        )
        runBlocking {
            populator.populate(connectionFactory).awaitFirstOrNull()
        }
    }
}