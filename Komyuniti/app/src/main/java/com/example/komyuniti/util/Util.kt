package com.example.komyuniti.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import java.security.*

fun generateKeyPair(userId: String): KeyPair {
    /*
    * Generate a new EC key pair entry in the Android Keystore by
    * using the KeyPairGenerator API. The private key can only be
    * used for signing or verification and only with SHA-256 or
    * SHA-512 as the message digest.
    */
    val keyStoreProvider = "AndroidKeyStore"
    val keyAlias = "keypair-$userId"
    val generator: KeyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_EC,
        keyStoreProvider
    )
    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
        keyAlias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    ).run {
        setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        build()
    }
    generator.initialize(parameterSpec)

    // generate and save key pair in key store by its alias
    return generator.genKeyPair()
}

fun loadKeyPair(userId: String): KeyPair? {
    val keyAlias = "keypair-$userId"
    val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    val entry: KeyStore.Entry = keyStore.getEntry(keyAlias, null)
    if (entry !is KeyStore.PrivateKeyEntry) {
        Log.w("PROFILE", "Not an instance of a PrivateKeyEntry")
        return null
    }
    val privateKey: PrivateKey = entry.privateKey
    val publicKey: PublicKey = keyStore.getCertificate(keyAlias).publicKey
    return KeyPair(publicKey, privateKey)
}
