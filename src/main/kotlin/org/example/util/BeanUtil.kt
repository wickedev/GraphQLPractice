package org.example.util

import com.expediagroup.graphql.server.execution.KotlinDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass


@Component
class BeanUtil : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    @Suppress("unused")
    companion object {
        lateinit var context: ApplicationContext
            private set

        fun <T : Any> getBean(type: KClass<T>): T {
            return context.getBean(type.java)
        }

        inline fun <reified T> getBean(): T {
            return context.getBean(T::class.java)
        }

        fun <T> getBean(type: Class<T>): T {
            return context.getBean(type)
        }

        fun <T> getBean(name: String, beanClass: Class<T>): T {
            return context.getBean(name, beanClass)
        }

        fun <T> getBeanOrNull(type: Class<T>): T? {
            val beanNames = context.getBeanNamesForType(type)
            return if (beanNames.size != 1) {
                null
            } else context.getBean(beanNames[0], type)
        }
    }
}


@Suppress("UNUSED_PARAMETER")
inline fun <reified L : KotlinDataLoader<K, V>, K, V> DataFetchingEnvironment.getDataLoader(loaderClass: KClass<L>): DataLoader<K, V> {
    val loader = BeanUtil.getBean<L>()
    return this.getDataLoader(loader.dataLoaderName)
}

@Suppress("UNUSED_PARAMETER")
inline fun <reified L : KotlinDataLoader<K, V>, K, V> DataFetchingEnvironment.getValueFromDataLoader(loaderClass: KClass<L>, key: K): CompletableFuture<V> {
    val loader = BeanUtil.getBean<L>()
    return this.getValueFromDataLoader(loader.dataLoaderName, key)
}