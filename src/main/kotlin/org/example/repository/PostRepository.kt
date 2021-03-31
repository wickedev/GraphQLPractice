package org.example.repository

import org.example.util.Identifier
import org.example.model.Post
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
interface R2dbcPostRepository : ReactiveSortingRepository<Post, Identifier> {
    fun findByAuthorId(authorId: Identifier): Flux<Post>
}

@Repository
class PostRepository(repository: R2dbcPostRepository) : R2dbcPostRepository by repository
