package org.example

import io.github.wickedev.graphql.spring.data.r2dbc.configuration.EnableGraphQLR2dbcRepositories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.tools.agent.ReactorDebugAgent


@SpringBootApplication
@EnableGraphQLR2dbcRepositories
class Application


@OptIn(ExperimentalCoroutinesApi::class)
fun main(args: Array<String>) {
    ReactorDebugAgent.init()
    DebugProbes.enableCreationStackTraces = true
    runApplication<Application>(*args, "--debug")
}