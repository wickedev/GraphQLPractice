package org.example.configuration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.example.channel.PostCreatedChannel
import org.example.channel.UserCreatedChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OptIn(ExperimentalCoroutinesApi::class)
class CommonConfiguration {

    @Bean
    fun userCreatedChannel() = UserCreatedChannel(BroadcastChannel(1))

    @Bean
    fun postCreatedChannel() = PostCreatedChannel(BroadcastChannel(1))
}