package org.example.subscription

import com.expediagroup.graphql.server.operations.Subscription
import org.example.channel.UserCreatedChannel
import org.example.configuration.graphql.IsAuthenticated
import org.example.entity.User
import org.example.util.asFlux
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class UserSubscription(private val userCreatedChannel: UserCreatedChannel) : Subscription {
    private val log = LoggerFactory.getLogger(UserSubscription::class.java)

    @IsAuthenticated
    fun users(): Flux<User> {
        log.info("users() called")
        return userCreatedChannel.asFlux()
    }
}