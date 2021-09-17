package org.example.entity

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.example.configuration.repository.interfaces.Node
import org.example.dataloader.AuthorDataLoader
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.example.util.getValueFromDataLoader
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture

@Table
@KeyDirective(fields = FieldSet("id"))
data class Post(
    @Id override val id: Identifier = DEFAULT_ID_VALUE,
    val authorId: Identifier? = null,
    val title: String,
    val content: String? = null,
    val postedAt: ZonedDateTime = ZonedDateTime.now(),
    val deletedAt: ZonedDateTime? = null,
    val published: Boolean = false,
) : Node {
    companion object {
        private val log = LoggerFactory.getLogger(Post::class.java)
    }

    fun author(env: DataFetchingEnvironment): CompletableFuture<User?>? {
        log.info("author() called with: authorId: $authorId")
        return authorId?.let { env.getValueFromDataLoader(AuthorDataLoader::class, authorId) }
    }
}