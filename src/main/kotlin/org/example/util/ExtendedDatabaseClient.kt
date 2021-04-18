package org.example.util

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import io.r2dbc.spi.Statement
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.util.ClassTypeInformation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import org.springframework.r2dbc.core.StatementFilterFunction
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier
import kotlin.reflect.KClass

interface ExtendedDatabaseClient : DatabaseClient {
    override fun sql(sql: String): GenericExecuteSpec
    override fun sql(sqlSupplier: Supplier<String>): GenericExecuteSpec

    interface GenericExecuteSpec : DatabaseClient.GenericExecuteSpec {

        override fun bind(index: Int, value: Any?): GenericExecuteSpec

        override fun bindNull(index: Int, type: Class<*>): GenericExecuteSpec

        override fun bind(name: String, value: Any?): GenericExecuteSpec

        override fun bindNull(name: String, type: Class<*>): GenericExecuteSpec

        override fun filter(filterFunction: Function<in Statement, out Statement>): GenericExecuteSpec

        override fun filter(filter: StatementFilterFunction): GenericExecuteSpec

        fun <T : Any> `as`(returnType: KClass<T>): RowsFetchSpec<T>
    }
}

fun MappingR2dbcConverter.writeValue(value: Any?): Any {
    return this.writeValue(value, ClassTypeInformation.OBJECT) as Any
}

fun <T : Any> MappingR2dbcConverter.getRowMapper(clazz: KClass<T>): BiFunction<Row, RowMetadata, T> {
    return BiFunction { row, meta -> read(clazz.java, row, meta) }
}

class DefaultExtendedDatabaseClient(
    private val databaseClient: DatabaseClient,
    private val converter: MappingR2dbcConverter,
) : ExtendedDatabaseClient, DatabaseClient by databaseClient {

    inner class DefaultGenericExecuteSpec(
        private val executeSpec: DatabaseClient.GenericExecuteSpec
    ) : ExtendedDatabaseClient.GenericExecuteSpec, DatabaseClient.GenericExecuteSpec by executeSpec {

        override fun bind(index: Int, value: Any?): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.bind(index, converter.writeValue(value)))
        }

        override fun bindNull(index: Int, type: Class<*>): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.bindNull(index, type))
        }

        override fun bind(name: String, value: Any?): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.bind(name, converter.writeValue(value)))
        }

        override fun bindNull(name: String, type: Class<*>): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.bindNull(name, type))
        }

        override fun filter(filterFunction: Function<in Statement, out Statement>): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.filter(filterFunction))
        }

        override fun filter(filter: StatementFilterFunction): ExtendedDatabaseClient.GenericExecuteSpec {
            return DefaultGenericExecuteSpec(executeSpec.filter(filter))
        }

        override fun <T : Any> `as`(returnType: KClass<T>): RowsFetchSpec<T> {
            return map(converter.getRowMapper(returnType))
        }
    }

    override fun sql(sql: String): ExtendedDatabaseClient.GenericExecuteSpec {
        return DefaultGenericExecuteSpec(databaseClient.sql(sql))
    }

    override fun sql(sqlSupplier: Supplier<String>): ExtendedDatabaseClient.GenericExecuteSpec {
        return DefaultGenericExecuteSpec(databaseClient.sql(sqlSupplier))
    }
}