package org.example.configuration

import com.auth0.jwt.algorithms.Algorithm
import com.google.crypto.tink.subtle.EllipticCurves
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

@Configuration
class SecurityConfiguration {

    @Bean
    fun algorithm(): Algorithm {
        val gen = KeyPairGenerator.getInstance("EC").apply {
            initialize(EllipticCurves.getNistP256Params())
        }

        val kayPair = gen.generateKeyPair()
        val publicKey = kayPair.public as ECPublicKey
        val privateKey = kayPair.private as ECPrivateKey
        return Algorithm.ECDSA256(publicKey, privateKey)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder()
}