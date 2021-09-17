package org.example.configuration.graphql

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider


@Suppress("unused")
class CustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = CustomSchemaGeneratorHooks(emptyList())
}