package org.example.configuration.repository.interfaces

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveSortingRepository

@NoRepositoryBean
interface SoftDeleteRepository<T, ID> : ReactiveSortingRepository<T, ID>