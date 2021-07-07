package org.example.util

import com.expediagroup.graphql.server.execution.KotlinDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

typealias CoroutineBatchLoader<K, V> = suspend (List<K>) -> List<V>

typealias CoroutineWithContextBatchLoader<K, V> = suspend (List<K>, environment: BatchLoaderEnvironment) -> List<V>

abstract class CoroutineDataLoader<K, V> : KotlinDataLoader<K, V> {


    companion object {
        fun <K, V> newDataLoader(
            batchLoader: CoroutineBatchLoader<K, V>,
        ): DataLoader<K, V> {
            return DataLoader.newDataLoader { keys ->
                CoroutineScope(Dispatchers.Unconfined).future {
                    batchLoader(keys)
                }
            }
        }

        fun <K, V> newDataLoader(
            batchLoader: CoroutineBatchLoader<K, V>,
            options: DataLoaderOptions
        ): DataLoader<K, V> {
            return DataLoader.newDataLoader(
                { keys ->
                    CoroutineScope(Dispatchers.Unconfined).future {
                        batchLoader(keys)
                    }
                }, options
            )
        }
    }

    override val dataLoaderName: String = this::class.java.name

    open val option: DataLoaderOptions? = null

    final override fun getDataLoader(): DataLoader<K, V> {
        return if (option == null) newDataLoader(::batchLoad) else newDataLoader(::batchLoad, option!!)
    }

    abstract suspend fun batchLoad(keys: List<K>): List<V>
}


abstract class CoroutineWithContextDataLoader<K, V> : KotlinDataLoader<K, V> {

    companion object {
        fun <K, V> newDataLoader(
            batchLoader: CoroutineWithContextBatchLoader<K, V>,
        ): DataLoader<K, V> {
            return DataLoader.newDataLoader { keys, environment ->
                CoroutineScope(Dispatchers.Unconfined).future {
                    batchLoader(keys, environment)
                }
            }
        }

        inline fun <K, V> newDataLoader(
            crossinline batchLoader: CoroutineWithContextBatchLoader<K, V>,
            options: DataLoaderOptions
        ): DataLoader<K, V> {
            return DataLoader.newDataLoader(
                { keys, environment ->
                    CoroutineScope(Dispatchers.Unconfined).future {
                        batchLoader(keys, environment)
                    }
                }, options
            )
        }
    }


    override val dataLoaderName: String = this::class.java.name

    open val option: DataLoaderOptions? = null

    final override fun getDataLoader(): DataLoader<K, V> {
        return if (option == null) newDataLoader(::batchLoad) else newDataLoader(::batchLoad, option!!)
    }

    abstract suspend fun batchLoad(keys: List<K>, environment: BatchLoaderEnvironment): List<V>
}


@Suppress("UNUSED_PARAMETER")
inline fun <reified L : CoroutineDataLoader<K, V>, K, V> DataFetchingEnvironment.getDataLoader(loaderClass: KClass<L>): DataLoader<K, V> {
    val loader = BeanUtil.getBean<L>()
    return this.getDataLoader(loader.dataLoaderName)
}

@Suppress("UNUSED_PARAMETER")
inline fun <reified L : CoroutineDataLoader<K, V>, K, V> DataFetchingEnvironment.getValueFromDataLoader(
    loaderClass: KClass<L>,
    key: K
): CompletableFuture<V> {
    val loader = BeanUtil.getBean<L>()
    return this.getValueFromDataLoader(loader.dataLoaderName, key)
}

inline fun <K, E> List<E>.orderBy(keys: List<K>, keyExtractor: E.() -> K): List<E> {
    return keys.map { k -> find { e -> k == keyExtractor(e) }!! }
}
