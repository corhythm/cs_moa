package com.mju.csmoa.common.util.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.mju.csmoa.JwtTokenInfo
import java.io.InputStream
import java.io.OutputStream

object JwtTokenInfoSerializer : Serializer<JwtTokenInfo> {

    override val defaultValue: JwtTokenInfo = JwtTokenInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): JwtTokenInfo {
        try {
            return JwtTokenInfo.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: JwtTokenInfo, output: OutputStream) = t.writeTo(output)

}