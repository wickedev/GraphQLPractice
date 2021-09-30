package org.example

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.example.configuration.r2dbc.CustomR2dbcRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.tools.agent.ReactorDebugAgent


@SpringBootApplication
@EnableR2dbcRepositories(repositoryFactoryBeanClass = CustomR2dbcRepositoryFactoryBean::class)
class Application


@OptIn(ExperimentalCoroutinesApi::class)
fun main(args: Array<String>) {
    ReactorDebugAgent.init()
    DebugProbes.enableCreationStackTraces = true
    runApplication<Application>(*args)
}