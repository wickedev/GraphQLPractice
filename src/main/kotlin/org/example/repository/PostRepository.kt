package org.example.repository

import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import org.example.entity.Post
import org.springframework.stereotype.Repository


@Repository
interface PostRepository : GraphQLR2dbcRepository<Post>
