package org.example.configuration.r2dbc

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.data.convert.CustomConversions
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.util.ClassUtils
import java.util.function.BiFunction

class CustomMappingR2dbcConverter(
    context: MappingContext<out RelationalPersistentEntity<*>?, out RelationalPersistentProperty?>,
    conversions: CustomConversions,
    private val additionalIsNewStrategy: AdditionalIsNewStrategy
) : MappingR2dbcConverter(context, conversions) {

    override fun <T : Any?> populateIdIfNecessary(obj: T): BiFunction<Row, RowMetadata, T?> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val userClass = ClassUtils.getUserClass(obj)
        val entity = mappingContext.getRequiredPersistentEntity(userClass)


        return BiFunction { row: Row, metadata: RowMetadata ->
            val propertyAccessor: PersistentPropertyAccessor<*> = entity.getPropertyAccessor(obj)
            val idProperty = entity.requiredIdProperty
            val id = propertyAccessor.getProperty(idProperty)

            val idPropertyUpdateNeeded = when {
                additionalIsNewStrategy.isNew(idProperty.type, id) -> true
                idProperty.type.isPrimitive -> {
                    id is Number && id.toLong() == 0L
                }
                else -> {
                    id == null
                }
            }

            if (idPropertyUpdateNeeded) {
                @Suppress("UNCHECKED_CAST")
                return@BiFunction if (potentiallySetId(row, metadata, propertyAccessor, idProperty)
                ) propertyAccessor.bean as T
                else obj
            }
            obj
        }
    }

    private fun potentiallySetId(
        row: Row, metadata: RowMetadata, propertyAccessor: PersistentPropertyAccessor<*>,
        idProperty: RelationalPersistentProperty
    ): Boolean {
        val columns = metadata.columnNames
        var generatedIdValue: Any? = null
        val idColumnName = idProperty.columnName.reference
        if (columns.contains(idColumnName)) {
            generatedIdValue = row[idColumnName]
        } else if (columns.size == 1) {
            val key = columns.iterator().next()
            generatedIdValue = row[key]
        }
        if (generatedIdValue == null) {
            return false
        }
        val conversionService = conversionService
        propertyAccessor.setProperty(idProperty, conversionService.convert(generatedIdValue, idProperty.type))
        return true
    }
}