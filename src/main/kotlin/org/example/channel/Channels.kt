package org.example.channel

import kotlinx.coroutines.channels.BroadcastChannel
import org.example.entity.Post
import org.example.entity.User

class UserCreatedChannel(
    broadcastChannel: BroadcastChannel<User>
) : BroadcastChannel<User> by broadcastChannel

class PostCreatedChannel(
    broadcastChannel: BroadcastChannel<Post>
) : BroadcastChannel<Post> by broadcastChannel
