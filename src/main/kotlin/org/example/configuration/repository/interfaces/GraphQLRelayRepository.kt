package org.example.configuration.repository.interfaces


interface GraphQLRelayRepository<T : Node, ID> :
    GraphQLRelayNodeRepository<ID>,
    GraphQLRelayPaginationRepository<T, ID>