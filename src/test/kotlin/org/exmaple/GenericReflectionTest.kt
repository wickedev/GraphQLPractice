package org.exmaple

import net.jodah.typetools.TypeResolver
import org.spekframework.spek2.Spek
import java.lang.reflect.ParameterizedType


open class Resource<T>(public val value: T)
class Target(value: String) : Resource<String>(value)

class GenericReflectionTest : Spek({
    test("generics") {

        val type = TypeResolver.resolveGenericType(
            Target::class.java.superclass,
            Target::class.java
        )

        Target::class.java.declaredFields.forEach {
            println("${it.name}, ${it.genericType}")
        }

        if (type is ParameterizedType) {
            println(type.rawType?.typeName)
            println(type.ownerType?.typeName)
            type.actualTypeArguments.forEach {
                println(it.typeName)
            }
        }
    }
})