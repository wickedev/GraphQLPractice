package org.example.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@GraphQLIgnore
interface SimpleUserDetails : UserDetails {

    @GraphQLIgnore
    override fun getUsername(): String = ""

    @GraphQLIgnore
    override fun getPassword(): String = ""

    @GraphQLIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    @GraphQLIgnore
    override fun isAccountNonExpired(): Boolean = true

    @GraphQLIgnore
    override fun isAccountNonLocked(): Boolean = true

    @GraphQLIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @GraphQLIgnore
    override fun isEnabled(): Boolean = true
}