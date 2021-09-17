package org.example.configuration.repository.common

import org.example.configuration.repository.interfaces.GraphQLRelayNodeRepository
import org.example.configuration.repository.interfaces.Node

class SimpleGraphQLRelayNodeRepository<ID> : GraphQLRelayNodeRepository<ID> {
    override fun node(id: ID): Node? {
        TODO("Not yet implemented")
    }
}