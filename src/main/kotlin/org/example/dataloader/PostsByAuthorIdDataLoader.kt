package org.example.dataloader

import org.example.entity.Post
import org.example.service.PostService
import org.example.util.CoroutineDataLoader
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PostsByAuthorIdDataLoader(private val service: PostService) :
    CoroutineDataLoader<Identifier, List<Post>>() {
    private val log = LoggerFactory.getLogger(PostsByAuthorIdDataLoader::class.java)

    override suspend fun getDataLoader(keys: List<Identifier>): List<List<Post>> {
        log.info("getDataLoader() called with: ids = $keys")
        return keys.map { service.postsByAuthorId(it) }
    }
}
