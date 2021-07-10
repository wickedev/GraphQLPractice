@file:OptIn(ExperimentalCoroutinesApi::class)

package org.example.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.example.entity.Post
import org.example.entity.User

@OptIn(ObsoleteCoroutinesApi::class)
class UserCreatedChannel constructor(
    broadcastChannel: BroadcastChannel<User>
) : BroadcastChannel<User> by broadcastChannel

@OptIn(ObsoleteCoroutinesApi::class)
class PostCreatedChannel(
    broadcastChannel: BroadcastChannel<Post>
) : BroadcastChannel<Post> by broadcastChannel