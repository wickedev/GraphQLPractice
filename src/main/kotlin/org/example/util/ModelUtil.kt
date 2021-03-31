package org.example.util

import com.expediagroup.graphql.generator.scalars.ID

typealias Identifier = ID?

val DEFAULT_ID_VALUE = null

val Identifier.exist
    get(): Boolean {
        return this?.value?.isNotEmpty() ?: false
    }

val Identifier.notExist
    get(): Boolean {
        return !this.exist
    }
