package org.example.configuration

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.example.util.Identifier
import kotlin.reflect.KType


class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (Identifier::class.qualifiedName?.let { type.toString().contains(it) }) {
            true -> graphqlLongType
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