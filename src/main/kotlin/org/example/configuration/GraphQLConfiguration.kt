package org.example.configuration

import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import io.github.wickedev.graphql.scalars.CustomScalars
import io.github.wickedev.graphql.scalars.GraphQLIDScalar
import io.github.wickedev.graphql.types.ID
import org.example.configuration.graphql.CustomSchemaGeneratorHooks
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class GraphQLConfiguration {

    @Bean
    fun customScalars(): CustomScalars {
        return CustomScalars.of(
            ID::class to GraphQLIDScalar
        )
    }

    @Bean
    fun customSchemaGeneratorHooks(
        resolvers: Optional<List<FederatedTypeResolver<*>>>,
        customScalars: CustomScalars
    ) = CustomSchemaGeneratorHooks(resolvers.orElse(emptyList()), customScalars)
}

