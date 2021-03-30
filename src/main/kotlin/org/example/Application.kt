package org.example

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.toSchema
import org.example.resolvers.post.PostMutation
import org.example.resolvers.post.PostQuery
import org.example.resolvers.user.UserMutation
import org.example.resolvers.user.UserQuery
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun generateScheme() {
    val config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"))
    val queries = listOf(UserQuery::class, PostQuery::class).map { TopLevelObject(it) }
    val mutations = listOf(UserMutation::class, PostMutation::class).map { TopLevelObject(it) }
    toSchema(config, queries, mutations)
}

fun main(args: Array<String>) {
    generateScheme()
    runApplication<Application>(*args)
}
