package org.example.configuration.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.example.entity.User
import org.example.service.AuthService

class AuthDataFetcher(
    private val authService: AuthService,
    private val originalDataFetcher: DataFetcher<*>,
    private val directiveEnv: KotlinFieldDirectiveEnvironment
) : DataFetcher<Any> {
    override fun get(environment: DataFetchingEnvironment?): Any {
        if (environment != null) {
            val context = environment.getContext<GraphQLCustomContext>()

            if (context.jwt == null) {
                throw TokenNotExistError()
            }

            if (directiveEnv.isAuthenticated && !authService.isAuthenticated(context.jwt)) {
                throw AuthenticationError()
            }

            val requiredRole = if (directiveEnv.hasRole)
                directiveEnv.directive.getArgument("role").value
            else
                null

            if (requiredRole is User.Role && !authService.isAuthorized(requiredRole, context.jwt)) {
                throw ForbiddenError()
            }
        }

        return originalDataFetcher.get(environment)
    }
}

private val KotlinFieldDirectiveEnvironment.isAuthenticated: Boolean
    get() = directive.name == IS_AUTHENTICATED_DIRECTIVE_NAME

private val KotlinFieldDirectiveEnvironment.hasRole: Boolean
    get() = directive.name == HAS_ROLE_DIRECTIVE_NAME