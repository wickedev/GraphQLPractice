package org.example.dataloader

import com.expediagroup.graphql.server.execution.KotlinDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import org.example.entity.User
import org.example.service.UserService
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuthorDataLoader(private val service: UserService) : KotlinDataLoader<Identifier, User?> {
    private val log = LoggerFactory.getLogger(AuthorDataLoader::class.java)

    override val dataLoaderName: String = AuthorDataLoader::class.java.name

    override fun getDataLoader(): DataLoader<Identifier, User?> {
        return DataLoader<Identifier, User?> { ids ->
            log.info("getDataLoader() called with: ids = $ids")
            CoroutineScope(Dispatchers.Unconfined).future {
                service.users(ids)
            }
        }
    }
}