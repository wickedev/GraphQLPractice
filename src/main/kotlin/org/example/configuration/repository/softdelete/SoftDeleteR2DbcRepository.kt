package org.example.configuration.repository.softdelete

import org.example.configuration.repository.interfaces.Node
import org.example.configuration.repository.mixin.ReactiveOrderedSortingRepositoryMixin
import org.example.configuration.repository.mixin.GraphQLRelayRepositoryMixin
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.repository.query.RelationalEntityInformation


class SoftDeleteR2DbcRepository<T : Node, ID>(
    entity: RelationalEntityInformation<T, ID>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : SoftDeleteBaseR2DbcRepository<T, ID>(entity, entityOperations, converter),
    ReactiveOrderedSortingRepositoryMixin<T, ID>,
    GraphQLRelayRepositoryMixin<T, ID> {

    override fun whereIdGreaterThanQuery(after: ID?): Query {
        return Query.query(whereId().greaterThan(after as Any).and(whereDeletedAtIsNullOrZero()))
    }

    override fun whereIdLessThanQuery(before: ID?): Query {
        return Query.query(whereId().lessThan(before as Any).and(whereDeletedAtIsNullOrZero()))
    }
}
