@file:OptIn(ExperimentalCoroutinesApi::class)

package org.example.configuration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.example.channel.PostCreatedChannel
import org.example.channel.UserCreatedChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonConfiguration {

    @Bean
    @OptIn(ObsoleteCoroutinesApi::class)
    fun userCreatedChannel() = UserCreatedChannel(BroadcastChannel(1))

    @Bean
    @OptIn(ObsoleteCoroutinesApi::class)
    fun postCreatedChannel() = PostCreatedChannel(BroadcastChannel(1))
}