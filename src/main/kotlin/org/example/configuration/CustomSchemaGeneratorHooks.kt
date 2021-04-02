package org.example.configuration

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.zhokhov.graphql.datetime.*
import graphql.language.StringValue
import graphql.scalars.ExtendedScalars
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.example.util.Identifier
import java.net.URL
import java.time.*
import java.util.*
import kotlin.reflect.KType

class DatetimeScalars {
    companion object {
        val Date = GraphQLDate()
        val Duration = GraphQLDuration()
        val LocalDate = GraphQLLocalDate()
        val LocalDateTime = GraphQLLocalDateTime()
        val LocalTime = GraphQLLocalTime()
        val OffsetDateTime = GraphQLOffsetDateTime()
        val YearMonth = GraphQLYearMonth()
    }
}

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {;
        val isIdentifier = Identifier::class.qualifiedName?.let { type.toString().contains(it) } ?: false

        if (isIdentifier) {
            return graphqlLongType
        }

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
            else -> null
        }
    }
}

val graphqlLongType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("ID")
    .description("A type representing a formatted java.util.Long")
    .coercing(LongCoercing)
    .build()

object LongCoercing : Coercing<Long, String> {
    override fun parseValue(input: Any?): Long = serialize(input).toLong()

    override fun parseLiteral(input: Any?): Long? {
        val uuidString = (input as? StringValue)?.value
        return uuidString?.toLong()
    }

    override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
}