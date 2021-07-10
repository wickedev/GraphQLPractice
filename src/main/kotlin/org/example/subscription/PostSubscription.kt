package org.example.subscription

import com.expediagroup.graphql.server.operations.Subscription
import org.example.channel.PostCreatedChannel
import org.example.configuration.graphql.IsAuthenticated
import org.example.entity.Post
import org.example.util.asFlux
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class PostSubscription(private val postCreatedChannel: PostCreatedChannel) : Subscription {
    private val log = LoggerFactory.getLogger(PostSubscription::class.java)

    @IsAuthenticated
    fun posts(): Flux<Post> {
        log.info("posts() called")
        return postCreatedChannel.asFlux()
    }
}