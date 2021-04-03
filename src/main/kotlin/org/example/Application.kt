package org.example

import org.example.configuration.r2dbc.CustomR2dbcRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories


@SpringBootApplication
@EnableR2dbcRepositories(repositoryFactoryBeanClass = CustomR2dbcRepositoryFactoryBean::class)
class Application


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}