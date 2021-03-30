package org.example.model

import org.example.util.BeanUtil
import org.example.util.coroutine.flux.await
import org.example.util.coroutine.mono.await
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

typealias Identifier = Long

const val DEFAULT_ID_VALUE = 0L


@Table
data class User(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val email: String,
    val name: String?,
) {
    suspend fun posts(): List<Post> {
        val repository = BeanUtil.getBean(PostRepository::class)
        return repository.findByAuthorId(id).await()
    }
}

@Table
data class Post(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val authorId: Identifier? = null,
    val title: String,
    val content: String? = null,
    val published: Boolean = false,
) {
    suspend fun author(): User? {
        val repository = BeanUtil.getBean(UserRepository::class)
        return authorId?.let { repository.findById(it).await() }
    }
}
