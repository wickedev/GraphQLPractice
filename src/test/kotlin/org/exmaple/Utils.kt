package org.exmaple

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.github.javafaker.Faker
import io.r2dbc.spi.Closeable
import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.example.configuration.IDToLongWritingConverter
import org.example.configuration.LongToIDReadingConverter
import org.example.entity.Post
import org.example.entity.User
import org.example.util.DEFAULT_ID_VALUE
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
import java.util.function.Supplier

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

    val conversions by spek.memoized {
        val converters: MutableList<Any> = ArrayList(dialect.converters)
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS)
        val storeConversions = CustomConversions.StoreConversions.of(dialect.simpleTypeHolder, converters)
        val converterList: List<Converter<*, *>> = listOf(
            IDToLongWritingConverter(),
            LongToIDReadingConverter()
        )
        R2dbcCustomConversions(storeConversions, converterList)
    }

    val mappingContext by spek.memoized {
        val relationalMappingContext = R2dbcMappingContext(NamingStrategy.INSTANCE)
        relationalMappingContext.setSimpleTypeHolder(conversions.simpleTypeHolder)
        relationalMappingContext
    }

    val converter by spek.memoized {
        MappingR2dbcConverter(mappingContext, conversions)
    }

    val databaseClient by spek.memoized {
        DatabaseClient.create(connectionFactory)
    }

    val r2dbcEntityTemplate by spek.memoized {
        R2dbcEntityTemplate(databaseClient, dialect, converter)
    }

    private val repositoryFactory by spek.memoized {
        R2dbcRepositoryFactory(r2dbcEntityTemplate)
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

    fun r2dbcEntityTemplateFacotry(): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory)
    }

    fun <T> getRepository(repositoryInterface: Class<T>): T {
        return repositoryFactory.getRepository(repositoryInterface)
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