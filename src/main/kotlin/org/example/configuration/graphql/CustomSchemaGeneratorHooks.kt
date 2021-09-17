package org.example.configuration.graphql

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.zhokhov.graphql.datetime.*
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import org.springframework.data.annotation.Id
import java.net.URL
import java.time.*
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

class DatetimeScalars {
    companion object {
        val Date = GraphQLDate()
        val Duration = GraphQLDuration()
        val LocalDate = GraphQLLocalDate(true)
        val LocalDateTime = GraphQLLocalDateTime(true)
        val LocalTime = GraphQLLocalTime()
        val OffsetDateTime = GraphQLOffsetDateTime()
        val YearMonth = GraphQLYearMonth()
    }
}

class CustomSchemaGeneratorHooks(resolvers: List<FederatedTypeResolver<*>>) :
    FederatedSchemaGeneratorHooks(resolvers) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (type.classifier) {
            // DatetimeScalars
            Date::class -> DatetimeScalars.Date
            Duration::class -> DatetimeScalars.Duration
            LocalDate::class -> DatetimeScalars.LocalDate
            LocalDateTime::class -> DatetimeScalars.LocalDateTime
            LocalTime::class -> DatetimeScalars.LocalTime
            OffsetDateTime::class -> DatetimeScalars.OffsetDateTime
            YearMonth::class -> DatetimeScalars.YearMonth

            // ExtendedScalars
            ZonedDateTime::class -> ExtendedScalars.DateTime
            OffsetTime::class -> ExtendedScalars.Time
            URL::class -> ExtendedScalars.Url
            Locale::class -> ExtendedScalars.Locale
            else -> super.willGenerateGraphQLType(type)
        }
    }
}