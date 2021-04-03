package org.example.dataloader

import org.example.entity.User
import org.example.service.UserService
import org.example.util.CoroutineDataLoader
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuthorDataLoader(private val service: UserService) : CoroutineDataLoader<Identifier, User>() {
    private val log = LoggerFactory.getLogger(AuthorDataLoader::class.java)

    override suspend fun batchLoad(keys: List<Identifier>): List<User> {
        log.info("batchLoad() called with: ids = $keys")
        return service.users(keys)
    }
}