package org.example.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:/application.yml")
class AuthConfig(
    @Value("\${auth.jwt.issuer}") val jwtIssuer: String = ""
)