package org.example.repository

import org.example.configuration.r2dbc.SoftDeleteRepository
import org.example.entity.Post
import org.example.util.Identifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
interface PostRepository: SoftDeleteRepository<Post, Identifier> {
    fun findByAuthorId(authorId: Identifier): Flux<Post>
}
