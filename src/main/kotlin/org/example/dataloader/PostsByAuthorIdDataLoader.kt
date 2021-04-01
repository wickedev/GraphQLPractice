package org.example.dataloader

import org.example.entity.Post
import org.example.service.PostService
import org.example.util.CoroutineDataLoader
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PostsByAuthorIdDataLoader(private val service: PostService) : CoroutineDataLoader<Identifier, List<Post>>() {
    private val log = LoggerFactory.getLogger(PostsByAuthorIdDataLoader::class.java)

    override suspend fun batchLoad(keys: List<Identifier>): List<List<Post>> {
        log.info("batchLoad() called with: keys = $keys")
        return keys.map { service.postsByAuthorId(it) }
    }
}
