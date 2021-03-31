package org.example.model

import org.example.repository.UserRepository
import org.example.util.BeanUtil
import org.example.util.DEFAULT_ID_VALUE
import org.example.util.Identifier
import org.example.util.coroutine.mono.await
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Post(
    @Id val id: Identifier = DEFAULT_ID_VALUE,
    val authorId: Identifier = null,
    val title: String,
    val content: String? = null,
    val published: Boolean = false,
) {
    suspend fun author(): User? {
        val repository = BeanUtil.getBean(UserRepository::class)
        return authorId?.let { repository.findById(it).await() }
    }
}