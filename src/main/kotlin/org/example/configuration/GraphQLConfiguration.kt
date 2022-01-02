package org.example.configuration

import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.zhokhov.graphql.datetime.LocalDateTimeScalar
import io.github.wickedev.graphql.scalars.CustomScalars
import io.github.wickedev.graphql.scalars.GraphQLIDScalar
import io.github.wickedev.graphql.types.ID
import org.example.configuration.graphql.CustomSchemaGeneratorHooks
import org.example.scalars.CollectionScalar
import org.example.scalars.SkipScalar
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Configuration
class GraphQLConfiguration {

    @Bean
    fun customScalars(): CustomScalars {
        return CustomScalars.of(
            ID::class to GraphQLIDScalar,
            Collection::class to CollectionScalar,
            UserDetails::class to SkipScalar,
            LocalDateTime::class to LocalDateTimeScalar.create(null, true, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        )
    }

    @Bean
    fun customSchemaGeneratorHooks(
        resolvers: Optional<List<FederatedTypeResolver<*>>>,
        customScalars: CustomScalars
    ) = CustomSchemaGeneratorHooks(resolvers.orElse(emptyList()), customScalars)
}

