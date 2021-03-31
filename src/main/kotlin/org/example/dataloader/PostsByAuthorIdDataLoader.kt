package org.example.dataloader

import com.expediagroup.graphql.server.execution.KotlinDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import org.example.entity.Post
import org.example.service.PostService
import org.example.util.Identifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PostsByAuthorIdDataLoader(private val service: PostService) : KotlinDataLoader<Identifier, List<Post>> {
    private val log = LoggerFactory.getLogger(PostsByAuthorIdDataLoader::class.java)

    override val dataLoaderName: String = PostsByAuthorIdDataLoader::class.java.name

    override fun getDataLoader(): DataLoader<Identifier, List<Post>> {
        return DataLoader<Identifier, List<Post>> { ids ->
            log.info("getDataLoader() called with: ids = $ids")
            CoroutineScope(Dispatchers.Unconfined).future {
                ids.map { service.postsByAuthorId(it) }
            }
        }
    }
}