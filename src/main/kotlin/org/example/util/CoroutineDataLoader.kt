package org.example.util

import com.expediagroup.graphql.server.execution.KotlinDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

typealias CoroutineBatchLoader<K, V> = suspend (List<K>) -> List<V>

abstract class CoroutineDataLoader<K, V> : KotlinDataLoader<K, V> {

    companion object {
        inline fun <K, V> newDataLoader(crossinline batchLoader: CoroutineBatchLoader<K, V>): DataLoader<K, V> {
            return DataLoader.newDataLoader { keys ->
                CoroutineScope(Dispatchers.Unconfined).future {
                    batchLoader(keys)
                }
            }
        }
    }

    override val dataLoaderName: String = this::class.java.name

    final override fun getDataLoader(): DataLoader<K, V> = newDataLoader(::getDataLoader)

    abstract suspend fun getDataLoader(keys: List<K>): List<V>
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