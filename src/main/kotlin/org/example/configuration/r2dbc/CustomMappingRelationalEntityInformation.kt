package org.example.configuration.r2dbc

import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation

class CustomMappingRelationalEntityInformation<T, ID>(
    entity: RelationalPersistentEntity<T>,
    private val additionalIsNewStrategy: AdditionalIsNewStrategy?,
) :
    MappingRelationalEntityInformation<T, ID>(entity) {

    private var valueType: Class<*> = entity.requiredIdProperty.type

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private var valueLookup: (source: T) -> Any? = { source: T ->
        entity.getIdentifierAccessor(source).identifier
    }

    override fun isNew(obj: T): Boolean {
        val value = valueLookup(obj)

        return if (additionalIsNewStrategy?.isNew(valueType, value) == true) {
            true
        } else {
            super.isNew(obj)
        }
    }
}