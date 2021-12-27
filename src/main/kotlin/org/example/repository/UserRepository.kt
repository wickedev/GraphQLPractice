package org.example.repository

import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import org.example.entity.User
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : GraphQLR2dbcRepository<User>
