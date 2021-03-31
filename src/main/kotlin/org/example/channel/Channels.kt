package org.example.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.example.entity.Post
import org.example.entity.User

@OptIn(ExperimentalCoroutinesApi::class)
class UserCreatedChannel(
    broadcastChannel: BroadcastChannel<User>
) : BroadcastChannel<User> by broadcastChannel

@OptIn(ExperimentalCoroutinesApi::class)
class PostCreatedChannel(
    broadcastChannel: BroadcastChannel<Post>
) : BroadcastChannel<Post> by broadcastChannel