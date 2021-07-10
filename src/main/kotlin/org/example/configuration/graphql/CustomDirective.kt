package org.example.configuration.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.introspection.Introspection
import org.example.entity.User

const val IS_AUTHENTICATED_DIRECTIVE_NAME = "isAuthenticated"

const val HAS_ROLE_DIRECTIVE_NAME = "hasRole"

@GraphQLDirective(
    name = IS_AUTHENTICATED_DIRECTIVE_NAME,
    locations = [
        Introspection.DirectiveLocation.FIELD,
        Introspection.DirectiveLocation.FIELD_DEFINITION
    ]
)
annotation class IsAuthenticated

@GraphQLDirective(
    name = HAS_ROLE_DIRECTIVE_NAME,
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
        IS_AUTHENTICATED_DIRECTIVE_NAME to authSchemaDirectiveWiring,
        HAS_ROLE_DIRECTIVE_NAME to authSchemaDirectiveWiring
    )
)
