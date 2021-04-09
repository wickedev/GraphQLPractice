package org.example.datafetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.example.dataloader.PostsByAuthorIdDataLoader
import org.example.entity.Post
import org.example.entity.User
import org.example.util.getValueFromDataLoader
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class PostsByAuthorIdDataFetcher : DataFetcher<CompletableFuture<List<Post>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<Post>> {
        val authorId = environment.getSource<User>().id
        return environment.getValueFromDataLoader(PostsByAuthorIdDataLoader::class, authorId)
    }
}