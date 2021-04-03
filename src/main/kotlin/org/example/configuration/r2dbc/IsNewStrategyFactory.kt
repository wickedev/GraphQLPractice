package org.example.configuration.r2dbc

import com.expediagroup.graphql.generator.scalars.ID

interface IsNewEntityStrategy {
    fun isNew(type: Class<*>?, value: Any?): Boolean
}

class CustomIsNewEntityStrategy : IsNewEntityStrategy {

    override fun isNew(type: Class<*>?, value: Any?): Boolean {
        return if (type == ID::class.java)
            (value as ID).value.isEmpty()
        else false
    }
}