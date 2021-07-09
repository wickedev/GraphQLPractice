package org.example.configuration.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.introspection.Introspection
import org.example.entity.User

const val IsAuthenticatedDirectiveName = "isAuthenticated"

const val HasRoleDirectiveName = "hasRole"

@GraphQLDirective(
    name = IsAuthenticatedDirectiveName,
    locations = [
        Introspection.DirectiveLocation.FIELD,
        Introspection.DirectiveLocation.FIELD_DEFINITION
    ]
)
annotation class IsAuthenticated

@GraphQLDirective(
    name = HasRoleDirectiveName,
    locations = [
        Introspection.DirectiveLocation.FIELD,
        Introspection.DirectiveLocation.FIELD_DEFINITION
    ]
)
annotation class HasRole(val role: User.Role)

class CustomDirectiveWiringFactory(
    authSchemaDirectiveWiring: AuthSchemaDirectiveWiring
) : KotlinDirectiveWiringFactory(
    manualWiring = mapOf<String, KotlinSchemaDirectiveWiring>(
        IsAuthenticatedDirectiveName to authSchemaDirectiveWiring,
        HasRoleDirectiveName to authSchemaDirectiveWiring
    )
)
