package org.example.entity

import graphql.schema.DataFetchingEnvironment
import org.example.dataloader.AuthorDataLoader
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.example.util.getValueFromDataLoader
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Table
data class Post(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val authorId: Identifier? = null,
    val title: String,
    val content: String? = null,
    val postedAt: LocalDateTime = LocalDateTime.now(),
    val published: Boolean = false,

    ) {
    companion object {
        private val log = LoggerFactory.getLogger(Post::class.java)
    }

    fun author(env: DataFetchingEnvironment): CompletableFuture<User?> {
        log.info("author() called with: authorId: $authorId")
        return env.getValueFromDataLoader(AuthorDataLoader::class, id)
    }
}