package org.example.scalars

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType

val CollectionScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("SetScalar")
    .description("Collection of elements that does not support duplicate elements")
    .coercing(object : Coercing<Collection<*>, List<*>> {
        override fun serialize(dataFetcherResult: Any): List<*> {
            return if (dataFetcherResult is Collection<*>) {
                dataFetcherResult.toList()
            } else {
                emptyList<Any>()
            }
        }

        override fun parseValue(input: Any): Collection<*> {
            return if (input is List<*>) {
                input.toSet()
            } else {
                emptySet<Any>()
            }
        }

        override fun parseLiteral(input: Any): Collection<*> {
            return if (input is List<*>) {
                input.toSet()
            } else {
                emptySet<Any>()
            }
        }
    })
    .build()