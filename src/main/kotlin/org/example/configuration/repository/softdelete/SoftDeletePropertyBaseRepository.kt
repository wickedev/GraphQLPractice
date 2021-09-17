package org.example.configuration.repository.softdelete

import org.example.configuration.repository.common.PropertyBaseRepository
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import java.time.LocalDateTime

abstract class SoftDeletePropertyBaseRepository<T, ID>(
    entity: RelationalEntityInformation<T, ID>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : PropertyBaseRepository<T, ID>(entity, entityOperations, converter) {

    override fun getIdQuery(id: ID): Query {
        return Query.query(whereId().`is`(id as Any).and(whereDeletedAtIsNullOrZero()))
    }

    override fun getIdsQuery(ids: List<ID>): Query {
        return Query.query(whereId().`in`(ids).and(whereDeletedAtIsNullOrZero()))
    }

    override fun emptyQuery(): Query {
        return Query.query(whereDeletedAtIsNullOrZero())
    }

    protected fun updateDeletedAtToNow(): Update {
        return Update.update("deleted_at", LocalDateTime.now())
    }

    protected fun whereDeletedAtIsNullOrZero(): Criteria {
        return Criteria.where("deleted_at").isNull
            .or("deleted_at").`is`(0)
    }
}