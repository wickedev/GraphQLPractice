package org.example.interfaces

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import org.springframework.security.core.userdetails.UserDetails

@GraphQLIgnore
interface SimpleUserDetails : UserDetails {
    @GraphQLIgnore
    override fun isAccountNonExpired(): Boolean = true

    @GraphQLIgnore
    override fun isAccountNonLocked(): Boolean = true

    @GraphQLIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @GraphQLIgnore
    override fun isEnabled(): Boolean = true
}