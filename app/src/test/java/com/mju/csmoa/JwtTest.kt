package com.mju.csmoa

import com.mju.csmoa.common.util.secret.Secret
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.junit.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.SecretKey


class JwtTest {

    @Test
    fun jwtTest() {
        val jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQsImlhdCI6MTYzNjQzODg5MSwiZXhwIjoxNjM2NTI1MjkxfQ.CzJb6fudmBLEHZy0USlKIlbCTLmMbeSjTf31-ghjFjk"

        val key: SecretKey =
            Keys.hmacShaKeyFor(Secret.JWT_SECRET_KEY.toByteArray(StandardCharsets.UTF_8))

        // 2. JWT parsing
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwtToken);

        // 3. expiration 추출 (KST: Korea Standard Time)
        val simpleDateFormat = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val expiration: Date = claims.body.expiration
        val paredExpiration: Date? = simpleDateFormat.parse(expiration.toString())
        val now: Long = Date().time

        println("expiration = ${paredExpiration?.time}")
        println("now = $now")
        println("${paredExpiration?.time!! > now}")

    }

    @Test
    fun hs256() {
        println(SignatureAlgorithm.HS256)
    }

    @Test
    fun typeArray() {
        val arr = mutableListOf<String>("Hello", "World", "Kang", "SeongUk")
        println(arr.toTypedArray()[0])
    }

    @Test
    fun fileTest() {
        val file = File("app/src/test/java/com/mju/csmoa/ExampleUnitTest.java")
        println(file.length())
    }

    @Test
    fun logicalOperatorTest() {

        println(true||false)
        println(false||true)

    }
}