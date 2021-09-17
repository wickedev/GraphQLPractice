package org.example.configuration.repository.interfaces


interface GraphQLRelayNodeRepository<ID> {
    fun node(id: ID): Node?
}