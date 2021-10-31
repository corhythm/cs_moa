package com.mju.csmoa.util.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.mju.csmoa.UserInfo
import java.io.InputStream
import java.io.OutputStream

object UserInfoSerializer : Serializer<UserInfo> {

    override val defaultValue: UserInfo = UserInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserInfo {
        try {
            return UserInfo.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: UserInfo, output: OutputStream) = t.writeTo(output)

}