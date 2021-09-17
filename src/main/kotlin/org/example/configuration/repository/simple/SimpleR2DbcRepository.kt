package org.example.configuration.repository.simple

import org.example.configuration.repository.interfaces.Node
import org.example.configuration.repository.mixin.GraphQLRelayRepositoryMixin
import org.example.configuration.repository.mixin.ReactiveOrderedSortingRepositoryMixin
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.repository.query.RelationalEntityInformation


class SimpleR2DbcRepository<T : Node, ID>(
    entity: RelationalEntityInformation<T, ID>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : SimpleBaseR2DbcRepository<T, ID>(entity, entityOperations, converter),
    ReactiveOrderedSortingRepositoryMixin<T, ID>,
    GraphQLRelayRepositoryMixin<T, ID>
