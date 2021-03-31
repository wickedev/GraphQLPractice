package org.example.model

import org.example.repository.PostRepository
import org.example.util.BeanUtil
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.example.util.coroutine.flux.await
import org.example.util.notExist
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class User(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val email: String,
    val name: String?,
) {
    suspend fun posts(): List<Post> {
        val repository = BeanUtil.getBean(PostRepository::class)
        return if (id.notExist) {
            emptyList()
        } else {
            repository.findByAuthorId(id).await()
        }
    }
}