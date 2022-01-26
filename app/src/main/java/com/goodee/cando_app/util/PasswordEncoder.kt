package com.goodee.cando_app.util

import com.google.android.gms.common.util.Base64Utils
import java.security.DigestException
import java.security.MessageDigest

class PasswordEncoder {
    companion object {
        @JvmStatic fun endcodePassword(password: String): String {
            val hash: ByteArray
            try {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(password.trim().toByteArray())
                hash = md.digest()
            } catch (e: CloneNotSupportedException) {
                throw DigestException("couldn't make digest of partial content")
            }

            return Base64Utils.encode(hash)
        }
    }
}