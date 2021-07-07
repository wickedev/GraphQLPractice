package org.exmaple

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.crypto.tink.subtle.EllipticCurves
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey


class JwtTest : Spek({
    test("generate JWT") {
        val gen = KeyPairGenerator.getInstance("EC").apply {
            initialize(EllipticCurves.getNistP256Params())
        }

        val kayPair = gen.generateKeyPair()
        val publicKey = kayPair.public as ECPublicKey
        val privateKey = kayPair.private as ECPrivateKey
        val algorithm: Algorithm = Algorithm.ECDSA256(publicKey, privateKey)

        val token = JWT.create()
            .withIssuer("issuer")
            .sign(algorithm)

        val jwt = JWT.decode(token)

        jwt.issuer.should.be.equal("issuer")
        jwt.algorithm.should.be.equal(algorithm.name)
    }
})