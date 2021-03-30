package org.exmaple

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.github.javafaker.Faker
import io.r2dbc.spi.Closeable
import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.example.model.DEFAULT_ID_VALUE
import org.example.model.Post
import org.example.model.User
import org.spekframework.spek2.dsl.LifecycleAware
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

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

    val r2dbcEntityTemplate by spek.memoized {
        R2dbcEntityTemplate(connectionFactory)
    }

    val databaseClient by spek.memoized {
        r2dbcEntityTemplate.databaseClient
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