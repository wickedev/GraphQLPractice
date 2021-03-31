package org.example.channel

import kotlinx.coroutines.channels.BroadcastChannel
import org.example.model.Post
import org.example.model.User

class UserCreatedChannel(
    broadcastChannel: BroadcastChannel<User>
) : BroadcastChannel<User> by broadcastChannel

class PostCreatedChannel(
    broadcastChannel: BroadcastChannel<Post>
) : BroadcastChannel<Post> by broadcastChannel
