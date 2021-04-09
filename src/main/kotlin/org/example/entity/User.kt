package org.example.entity

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.example.dataloader.PostsByAuthorIdDataLoader
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.example.util.getDataLoader
import org.example.util.getValueFromDataLoader
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Table
data class User(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val email: String,
    val name: String?,
) {
    companion object {
        private val log = LoggerFactory.getLogger(User::class.java)
    }

    @Transient
    lateinit var posts: List<Post>

    /*fun posts(env: DataFetchingEnvironment): CompletableFuture<List<Post>> {
        log.info("posts() called with: authorId: $id")
        return env.getDataLoader(PostsByAuthorIdDataLoader::class).load(id)
    }*/
}

