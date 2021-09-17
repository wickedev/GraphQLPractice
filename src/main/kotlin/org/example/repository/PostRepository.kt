package org.example.repository

import org.example.configuration.repository.interfaces.SoftDeleteRepository
import org.example.entity.Post
import org.example.util.Identifier
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
interface PostRepository: SoftDeleteRepository<Post, Identifier> {
    fun findByAuthorId(authorId: Identifier): Flux<Post>
}
